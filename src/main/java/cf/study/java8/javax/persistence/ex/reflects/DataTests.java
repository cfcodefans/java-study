package cf.study.java8.javax.persistence.ex.reflects;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import cf.study.java8.javax.cdi.weld.WeldTest;
import cf.study.java8.javax.persistence.ex.reflects.entity.BaseEn;
import cf.study.java8.javax.persistence.ex.reflects.entity.ClassEn;
import cf.study.java8.javax.persistence.ex.reflects.entity.FieldEn;
import cf.study.java8.javax.persistence.ex.reflects.entity.MethodEn;
import cf.study.java8.javax.persistence.ex.reflects.entity.PackageEn;
import cf.study.java8.javax.persistence.ex.reflects.entity.ParameterEn;
import cf.study.java8.lang.reflect.Reflects;



public class DataTests {
	
	@BeforeClass
	public static void setUp() {
		WeldTest.setUp();
	}
	
	@Test
	public void test() {
		EntryLoader el = new EntryLoader();
		try {
			el.extractJarStructure("junit");
			el.roots.stream().forEach((baseEn)->{System.out.println(baseEn.name);});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	    System.out.println(el.classEnPool.size());
	}
	
	@Test
	public void testWithObject() {
		EntryLoader el = new EntryLoader();
		ClassEn ce = el.getClassEnByClz(Object.class);
		
//		el.roots.forEach((en) -> {
//			EntryLoader.traverse(en, act);
//		});
		
		ReflectDao dao = ReflectDao.threadLocal.get();
		dao.beginTransaction();
		el.roots.forEach((en)->{
			dao.persist(en);
		});
		dao.endTransaction();
	}
	
    static	Consumer<BaseEn> act = (BaseEn)-> {
		ReflectDao dao = ReflectDao.threadLocal.get();
		dao.beginTransaction();
		dao.create(BaseEn);
		dao.getEm().flush();
		dao.endTransaction();
	};
	
	@Test
	public void testRuntimeJar() {
		File _f = new File(String.format("%s/lib/rt.jar", SystemUtils.JAVA_HOME));
		
		EntryLoader el = new EntryLoader();
		try {
			el.extractJarStructure(_f);
//			el.roots.stream().forEach((baseEn)->{System.out.println(baseEn.name);});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		el.roots.parallelStream().forEach((be)->{
			ReflectDao dao = ReflectDao.threadLocal.get();
			dao.beginTransaction();
			EntryLoader.traverse(be, (_be)->{dao.create(_be);}, ()->{dao.getEm().flush();});
			dao.endTransaction();
		});
	}
	
	@Test
	public void testTraverse() {
		EntryLoader el = new EntryLoader();
		try {
			el.extractJarStructure("junit");
//			el.roots.stream().forEach((baseEn)->{System.out.println(baseEn.name);});
		} catch (Exception e) {
			e.printStackTrace();
		}
		el.roots.parallelStream().forEach((be)->{
			ReflectDao dao = ReflectDao.threadLocal.get();
			dao.beginTransaction();
			EntryLoader.traverse(be, (_be)->{dao.create(_be);}, ()->{dao.getEm().flush();});
			dao.endTransaction();
		});

		el.classEnPool.values().forEach((ce) -> {
			System.out.println(String.format("%s\t%s", ce.id, ce.name));
		});
	}
	
	static class PersistActor implements Runnable {
		final List<BaseEn> stuff;
		
		public static void persist(Collection<BaseEn> beCol) {
			if (CollectionUtils.isEmpty(beCol)) return;
			
			
		}
		
		public PersistActor(List<BaseEn> stuff) {
			super();
			this.stuff = stuff;
		}

		public void run() {
			ReflectDao dao = ReflectDao.threadLocal.get();
			dao.beginTransaction();
			stuff.forEach((be)->{dao.persist(be);});
			dao.endTransaction();
		}
	}
	
	@AfterClass
	public static void tearDown() {
		WeldTest.tearDown();
	}

	static class EntryLoader {
		
		public static void traverse(BaseEn be, Consumer<BaseEn> act, Runnable interAct) {
			act.accept(be);
//			be.children.forEach(act);
			be.children.forEach((en)->{traverse(en, act, interAct);});
			interAct.run();
		}
		
		Map<String, PackageEn> packageEnPool = MapUtils.synchronizedMap(new LinkedHashMap<String, PackageEn>());
		Map<String, ClassEn> classEnPool = MapUtils.synchronizedMap(new LinkedHashMap<String, ClassEn>());
		Collection<BaseEn> roots = CollectionUtils.synchronizedCollection(new LinkedHashSet<BaseEn>());

		private final ReentrantLock lock = new ReentrantLock();
		
		public void extractJarStructure(String libName) throws Exception {
		
			final File f = Reflects.getJarFileInClassPath(libName);
			if (f == null || !f.canRead())
				return ;
		
			extractJarStructure(f);
		}

		private void extractJarStructure(final File f) throws MalformedURLException, IOException {
			JarFileClassLoader cl = new JarFileClassLoader("jfcl", 
														   new URL[] { f.toURI().toURL() }, 
														   ClassLoader.getSystemClassLoader());
		
			try {
				try (JarFile jf = new JarFile(f)) {
					System.out.println(String.format("entries: %d", jf.size()));
		
					try (Stream<JarEntry> entryStream = jf.stream()) {
		
						entryStream.filter((je) -> {
							return !je.isDirectory() || "META-INF".equals(je.getName()) || je.getName().endsWith("package-info");
						}).forEach((je) -> {
							String enName = je.getName();
//							System.out.println(enName);
							if (StringUtils.endsWith(enName, "/")) {
								enName = StringUtils.removeEnd(enName, "/").replace('/', '.');
								PackageEn pe = getPackageEnByName(enName);
								return;
							}
							if (StringUtils.endsWith(enName, ".class")) {
								enName = enName.replace('/', '.');
								getClassEnByName(StringUtils.substringBeforeLast(enName, ".class"));
							}
						});
					}
				}
			} finally {
				cl.close();
			}
		}

//		public PackageEn getPackageEnByPackage(Package pkg) {
//			if (pkg == null) return null;
//		}
		
		public PackageEn getPackageEnByName(String name) {
			if (StringUtils.isBlank(name))
				return null;

			PackageEn pe = null;

			lock.lock();
			try {
				pe = packageEnPool.get(name);
				if (pe != null)
					return pe;
	
				Package pkg = Package.getPackage(name);
				if (pkg == null) return null;
				
				PackageEn enclosing = StringUtils.contains(name, '.') ? getPackageEnByName(StringUtils.substringBeforeLast(name, ".")) : null;
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
		
		public ClassEn getClassEnByClz(Class<?> cls) {
			if (cls == null)
				return null;
			
			if (cls.isArray()) {
				cls = cls.getComponentType();
			}
			
			ClassEn ce = null;
			lock.lock();
			try {
				ce = classEnPool.get(cls.getName());
				if (ce != null)
					return ce;
	
//				BaseEn enclosing = null;
//				Class<?> enclosingClass = cls.getEnclosingClass();
//				if (enclosingClass != null) {
//					enclosing = getClassEnByClz(enclosingClass);
//				}
				
				ce = new ClassEn(cls);
				classEnPool.put(cls.getName(), ce);
				final ClassEn _ce = ce;
				
				Package pkg = cls.getPackage();
				_ce.pkg = (pkg == null) ? null : getPackageEnByName(pkg.getName());
				if (_ce.pkg != null) {
					_ce.pkg.children.add(_ce);
				}
				
//				_ce.superClz = 
						getClassEnByClz(cls.getSuperclass());
				
				Stream.of(cls.getInterfaces()).forEach((inf) -> {
//					_ce.infs.add(getClassEnByClz(inf));
					getClassEnByClz(inf);
				});
				
				Stream.of(cls.getDeclaredFields()).forEach((field) -> {
					getFieldEnByFiled(field, _ce);
				});
				
				Stream.of(cls.getDeclaredMethods()).forEach((method) -> {
					getMethodEnByMethod(method, _ce);
				});
				
				Stream.of(cls.getConstructors()).forEach((method) -> {
					getMethodEnByMethod(method, _ce);
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
		
		public ClassEn getClassEnByName(String name) {
			Class<?> cls = null;
			try {
				cls = Class.forName(name, false, ClassLoader.getSystemClassLoader());
				return getClassEnByClz(cls);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		public FieldEn getFieldEnByFiled(Field field, ClassEn enclosing) {
			if (field == null)
				return null;

			FieldEn fe = null;// getEnByField(field, _enclosing);
			// if (fe != null) return fe;

			fe = new FieldEn(field, enclosing);
//			Type genericType = field.getGenericType();
//
//			Class genericClass = Object.class;
//			if (genericType instanceof Class) {
//				genericClass = (Class) genericType;
//			}
//
//			fe.fieldType = getClassEnByClz(genericClass);
			return fe;
		}
		
		public MethodEn getMethodEnByMethod(Executable method, ClassEn _enclosing) {
			if (method == null || _enclosing == null)
				return null;

			MethodEn me = null;
			// MethodEn me = getEnByMethod(method, _enclosing);
			// if (me != null) return me;

			// ClassEn ce = create(method.getDeclaringClass());
			MethodEn _me = new MethodEn(method, _enclosing);

//			_me.returnClass = getClassEnByClz(method.getReturnType());
//			Stream.of(method.getParameters()).forEach((param) -> {
//				getParamEnByParam(param, _me);
//			});
//			Stream.of(method.getExceptionTypes()).forEach((clz) -> {
//				_me.exceptionClzz.add(getClassEnByClz(clz));
//			});

			return me;
		}
		
		public ParameterEn getParamEnByParam(Parameter param, MethodEn me) {
			if (param == null || me == null) {
				return null;
			}
			
			ParameterEn pe = new ParameterEn(param, me);
			pe.paramType = getClassEnByClz(param.getType());
			return pe;
		}
	}
}
