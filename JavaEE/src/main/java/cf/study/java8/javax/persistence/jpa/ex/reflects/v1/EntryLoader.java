package cf.study.java8.javax.persistence.jpa.ex.reflects.v1;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.xbean.classloader.JarFileClassLoader;
import org.junit.Test;

import cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity.BaseEn;
import cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity.ClassEn;
import cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity.FieldEn;
import cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity.MethodEn;
import cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity.PackageEn;
import cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity.ParameterEn;
import cf.study.java8.lang.reflect.Reflects;

public class EntryLoader {

	public static void traverse(BaseEn be, Consumer<BaseEn> act, Runnable interAct) {
		act.accept(be);
		be.children.forEach((en) -> {
			traverse(en, act, interAct);
		});
		interAct.run();
	}

	Map<String, PackageEn> packageEnPool = MapUtils.synchronizedMap(new LinkedHashMap<String, PackageEn>());
	Map<String, ClassEn> classEnPool = MapUtils.synchronizedMap(new LinkedHashMap<String, ClassEn>());
	Collection<BaseEn> roots = CollectionUtils.synchronizedCollection(new LinkedHashSet<BaseEn>());
	Collection<String> inflatedClassSet = CollectionUtils.synchronizedCollection(new LinkedHashSet<String>());

	private final ReentrantLock lock = new ReentrantLock();

	public void extractJarStructure(String libName) throws Exception {

		final File f = Reflects.getJarFileInClassPath(libName);
		if (f == null || !f.canRead())
			return;

		extractJarStructure(f);
	}

	void extractJarStructure(final File f) throws MalformedURLException, IOException {
		JarFileClassLoader cl = new JarFileClassLoader("jfcl", new URL[] { f.toURI().toURL() }, ClassLoader.getSystemClassLoader());

		try {
			try (JarFile jf = new JarFile(f)) {
				System.out.println(String.format("entries: %d", jf.size()));

				try (Stream<JarEntry> entryStream = jf.stream()) {

					entryStream.filter((je) -> {
						return !(je.isDirectory() || "META-INF".equals(je.getName()) || je.getName().endsWith("package-info"));
					}).forEach((je) -> {
						String enName = je.getName();
						if (StringUtils.endsWith(enName, "/")) {
							enName = StringUtils.removeEnd(enName, "/").replace('/', '.');
							PackageEn pe = preloadPackageEnByName(enName);
							return;
						}
						if (StringUtils.endsWith(enName, ".class")) {
							enName = enName.replace('/', '.');
							preloadClassEnByName(StringUtils.substringBeforeLast(enName, ".class"));
						}
					});
				}
			}
		} finally {
			cl.close();
		}
	}

	public PackageEn preloadPackageEnByName(String name) {
		if (StringUtils.isBlank(name))
			return null;

		PackageEn pe = null;

		lock.lock();
		try {
			pe = packageEnPool.get(name);
			if (pe != null)
				return pe;

			Package pkg = Package.getPackage(name);
			if (pkg == null)
				return null;

			PackageEn enclosing = StringUtils.contains(name, '.') ? preloadPackageEnByName(StringUtils
					.substringBeforeLast(name, ".")) : null;
			pe = new PackageEn(pkg, enclosing);
			packageEnPool.put(name, pe);

			if (pe.enclosing == null) {
				roots.add(pe);
			}
		} finally {
			lock.unlock();
		}
		return pe;
	}

	public ClassEn preloadClassEnByClz(Class<?> cls) {
		if (cls == null)
			return null;

		while (cls.isArray()) {
			cls = cls.getComponentType();
		}

		ClassEn ce = null;
		lock.lock();
		try {
			ce = classEnPool.get(cls.getName());
			if (ce != null)
				return ce;

			// BaseEn enclosing = null;
			// Class<?> enclosingClass = cls.getEnclosingClass();
			// if (enclosingClass != null) {
			// // enclosing =
			// }
			preloadClassEnByClz(ClassEn.getEnclossingClz(cls));

			ce = new ClassEn(cls, null);
			classEnPool.put(cls.getName(), ce);
			final ClassEn _ce = ce;

			Package pkg = cls.getPackage();
			_ce.pkg = (pkg == null) ? null : preloadPackageEnByName(pkg.getName());
			if (_ce.pkg != null) {
				_ce.pkg.children.add(_ce);
			}

			// _ce.superClz =
			preloadClassEnByClz(cls.getSuperclass());

			Stream.of(cls.getAnnotations()).forEach((an) -> {
				preloadClassEnByClz(an.annotationType());
			});

			Stream.of(cls.getInterfaces()).forEach((inf) -> {
				// _ce.infs.add(getClassEnByClz(inf));
					preloadClassEnByClz(inf);
				});

			Stream.of(cls.getDeclaredFields()).forEach((field) -> {
				preloadFieldEnByFiled(field, _ce);
			});

			Stream.of(cls.getDeclaredMethods()).forEach((method) -> {
				preloadMethodEnByMethod(method, _ce);
			});

			Stream.of(cls.getConstructors()).forEach((method) -> {
				preloadMethodEnByMethod(method, _ce);
			});

			if (ce.enclosing == null && ce.pkg == null) {
				roots.add(ce);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return ce;
	}

	public ClassEn preloadClassEnByName(String name) {
		Class<?> cls = null;
		try {
			cls = Class.forName(name, false, ClassLoader.getSystemClassLoader());
			return preloadClassEnByClz(cls);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public FieldEn preloadFieldEnByFiled(Field field, ClassEn enclosing) {
		if (field == null)
			return null;

		FieldEn fe = null;// getEnByField(field, _enclosing);
		// if (fe != null) return fe;

		fe = new FieldEn(field, enclosing);
		Type genericType = field.getGenericType();
		//
		Class genericClass = Object.class;
		if (genericType instanceof Class) {
			genericClass = (Class) genericType;
		}

		Stream.of(field.getAnnotations()).forEach((an) -> {
			preloadClassEnByClz(an.annotationType());
		});
		//
		// fe.fieldType =
		preloadClassEnByClz(genericClass);
		return fe;
	}

	public MethodEn preloadMethodEnByMethod(Executable method, ClassEn _enclosing) {
		if (method == null || _enclosing == null)
			return null;

		MethodEn me = null;
		// MethodEn me = getEnByMethod(method, _enclosing);
		// if (me != null) return me;

		// ClassEn ce = create(method.getDeclaringClass());
		MethodEn _me = new MethodEn(method, _enclosing);

		// _me.returnClass =
		if (method instanceof Method)
			preloadClassEnByClz(((Method) method).getReturnType());

		Stream.of(method.getAnnotations()).forEach((an) -> {
			preloadClassEnByClz(an.annotationType());
		});

		Stream.of(method.getParameters()).forEach((param) -> {
			preloadParamEnByParam(param, _me);
		});
		Stream.of(method.getExceptionTypes()).forEach((clz) -> {
			// _me.exceptionClzz.add(getClassEnByClz(clz));
				preloadClassEnByClz(clz);
			});

		return me;
	}

	public ParameterEn preloadParamEnByParam(Parameter param, MethodEn me) {
		if (param == null || me == null) {
			return null;
		}

		ParameterEn pe = new ParameterEn(param, me);

		Stream.of(param.getAnnotations()).forEach((an) -> {
			preloadClassEnByClz(an.annotationType());
		});

		// pe.paramType =
		preloadClassEnByClz(param.getType());
		return pe;
	}

	public ClassEn inflateClassEnByClz(ClassEn ce) {
		if (ce == null)
			return null;

		lock.lock();
		try {
			if (inflatedClassSet.contains(ce.name)) {
				return ce;
			}

			inflatedClassSet.add(ce.name);

			Class<?> cls = ce.clazz;
			if (cls.getPackage() != null)
				ce.pkg = packageEnPool.get(cls.getPackage().getName());

			// Class<?> enclosingClass = cls.getEnclosingClass();
			// if (enclosingClass != null) {
			// ce.enclosing = inflateClassEnByClz(enclosingClass);
			// }
			ce.enclosing = preloadClassEnByClz(ClassEn.getEnclossingClz(cls));

			ce.superClz = inflateClassEnByClz(cls.getSuperclass());

			Stream.of(cls.getAnnotations()).forEach((an) -> {
				ce.annotations.add(inflateClassEnByClz(an.annotationType()));
			});

			Stream.of(cls.getInterfaces()).forEach((inf) -> {
				ce.infs.add(inflateClassEnByClz(inf));
			});

			ce.children.stream().forEach((be) -> {
				if (be instanceof FieldEn) {
					FieldEn fe = (FieldEn) be;
					inflateFieldEnByFiled(fe, ce);
				} else if (be instanceof MethodEn) {
					inflateMethodEnByMethod((MethodEn) be, ce);
				}
			});

			if (ce.enclosing == null && ce.pkg == null) {
				roots.add(ce);
			}

			return ce;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return null;
	}

	public ClassEn inflateClassEnByClz(Class<?> cls) {
		if (cls == null)
			return null;

		while (cls.isArray()) {
			cls = cls.getComponentType();
		}
		return inflateClassEnByClz(classEnPool.get(cls.getName()));
	}

	public FieldEn inflateFieldEnByFiled(FieldEn fe, ClassEn enclosing) {
		if (fe == null || fe.field == null)
			return null;

		Type genericType = fe.field.getGenericType();
		Class genericClass = Object.class;
		if (genericType instanceof Class) {
			genericClass = (Class) genericType;
		}
		fe.fieldType = inflateClassEnByClz(genericClass);
		return fe;
	}

	public MethodEn inflateMethodEnByMethod(MethodEn me, ClassEn _enclosing) {
		if (me == null || _enclosing == null)
			return null;

		if (me.method instanceof Method)
			me.returnClass = inflateClassEnByClz(((Method) me.method).getReturnType());

		me.children.forEach((pe) -> {
			inflateParamEnByParam((ParameterEn) pe);
		});
		Stream.of(me.method.getExceptionTypes()).forEach((clz) -> {
			me.exceptionClzz.add(inflateClassEnByClz(clz));
		});

		return me;
	}

	public ParameterEn inflateParamEnByParam(ParameterEn pe) {
		if (pe == null) {
			return null;
		}

		pe.paramType = inflateClassEnByClz(pe.parameter.getType());
		return pe;
	}
	
	@Test
	public void testLoading() throws Exception {
		File _f = new File(String.format("%s/lib/rt.jar", SystemUtils.JAVA_HOME));
		extractJarStructure(_f);
	}
}