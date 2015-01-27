package cf.study.java8.javax.persistence.ex.reflects;

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
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;
import org.apache.xbean.classloader.JarFileClassLoader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import cf.study.java8.javax.cdi.weld.WeldTest;
import cf.study.java8.javax.persistence.dao.JpaModule;
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
//		ReflectDao dao = ReflectDao.threadLocal.get();
		JpaModule.instance();
	}

	private static final Logger log = Logger.getLogger(DataTests.class);
	
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
		ClassEn ce = el.preloadClassEnByClz(Object.class);
		
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
	public void testTraverseWithObject() {
		EntryLoader el = new EntryLoader();
		el.preloadClassEnByClz(Object.class);
		
		el.roots.parallelStream().forEach((be)->{
			ReflectDao dao = ReflectDao.threadLocal.get();
			dao.beginTransaction();
			EntryLoader.traverse(be, (_be)->{dao.create(_be);}, ()->{dao.getEm().flush();});
			dao.endTransaction();
		});

		System.out.println("preload is done");
//		
		el.classEnPool.values().parallelStream().forEach((ce) -> {
			el.inflateClassEnByClz(ce);
		});
//		
		System.out.println("inflation is done");
//		
		ReflectDao dao = ReflectDao.threadLocal.get();//new ReflectDao(JpaModule.getEntityManager());
		dao.beginTransaction();
		el.classEnPool.values().forEach((ce)->{
			dao.refresh(ce);
		});
		el.classEnPool.values().forEach((be)->{
			EntryLoader.traverse(be, (_be)->{dao.edit(_be);}, ()->{});
		});
		dao.getEm().flush();
		dao.endTransaction();
		System.out.println("merge is done");
	}
	
	@Test
	public void verifyDatabase() {
		ReflectDao dao = ReflectDao.threadLocal.get();
		System.out.println(dao.queryCount("select count(1) from BaseEn be"));
	}
	
	@Test
	public void prepareData() {
		EntryLoader el = new EntryLoader();
		el.preloadClassEnByClz(com.sun.awt.AWTUtilities.class);
		
		el.classEnPool.keySet().parallelStream().forEach((key) -> {
			ClassEn ce = el.classEnPool.get(key);
			el.inflateClassEnByClz(ce);
			System.out.println(key + ": \t" + ce.id + ": \t" + ce.name);
		});
		
		el.classEnPool.values().parallelStream().filter((ce)->(ce.infs.size() > 1)).forEach((ce)->{
			StringBuilder sb = new StringBuilder();
			sb.append(ce.name).append(".infs: \n");
			ce.infs.forEach((ie)->{sb.append("\t").append(ie.name).append("\n");});
			System.out.println(sb);
		});
	}
	
	@Test
	public void testTraverseWithObject1() {
		EntryLoader el = new EntryLoader();
		el.preloadClassEnByClz(Object.class);
		
		el.roots.parallelStream().forEach((be)->{
			ReflectDao dao = ReflectDao.threadLocal.get();
			dao.beginTransaction();
			EntryLoader.traverse(be, (_be)->{dao.create(_be);}, ()->{dao.getEm().flush();});
			dao.endTransaction();
		});
		System.out.println("Preload class: " + el.classEnPool.size());
		
//		el.classEnPool.clear();
		
		{
			ReflectDao dao = ReflectDao.threadLocal.get();
			List<Object[]> idAndNames = dao.queryEntity("select id, name from ClassEn ce");
			idAndNames.parallelStream().forEach((idAndName) -> {
				Long id = (Long) idAndName[0];
				String name = (String) idAndName[1];
				
				ClassEn ce = el.classEnPool.get(name);
				if (ce != null)
					ce.id = id;
			});
		}
		
		el.classEnPool.keySet().parallelStream().forEach((key) -> {
			ClassEn ce = el.classEnPool.get(key);
			el.inflateClassEnByClz(ce);
			System.out.println(key + ": \t" + ce.id + ": \t" + ce.name);
		});
		System.out.println("Inflate class: " + el.classEnPool.size());
		
		ReflectDao dao = ReflectDao.threadLocal.get();
		el.classEnPool.keySet().forEach((key) -> {
			dao.beginTransaction();
			ClassEn ce = el.classEnPool.get(key);
			dao.inflateClassEnByNativeSql(ce);
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

		System.out.println("preload is done");
		
		el.classEnPool.values().parallelStream().forEach((ce) -> {
			el.inflateClassEnByClz(ce);
		});
		
		System.out.println("inflation is done");
		
		ReflectDao dao = ReflectDao.threadLocal.get();
		dao.beginTransaction();
		el.roots.parallelStream().forEach((be)->{
			EntryLoader.traverse(be, (_be)->{dao.edit(_be);}, ()->{});
		});
		System.out.println("merge is done");
		dao.getEm().flush();
		dao.endTransaction();
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
			be.children.forEach((en)->{traverse(en, act, interAct);});
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
							return !(je.isDirectory() || "META-INF".equals(je.getName()) || je.getName().endsWith("package-info"));
						}).forEach((je) -> {
							String enName = je.getName();
//							System.out.println(enName);
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
				if (pkg == null) return null;
				
				PackageEn enclosing = StringUtils.contains(name, '.') ? preloadPackageEnByName(StringUtils.substringBeforeLast(name, ".")) : null;
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
	
				BaseEn enclosing = null;
				Class<?> enclosingClass = cls.getEnclosingClass();
				if (enclosingClass != null) {
//					enclosing = 
							preloadClassEnByClz(enclosingClass);
				}
				
				ce = new ClassEn(cls, enclosing);
				classEnPool.put(cls.getName(), ce);
				final ClassEn _ce = ce;
				
				Package pkg = cls.getPackage();
				_ce.pkg = (pkg == null) ? null : preloadPackageEnByName(pkg.getName());
				if (_ce.pkg != null) {
					_ce.pkg.children.add(_ce);
				}
				
//				_ce.superClz = 
						preloadClassEnByClz(cls.getSuperclass());
				
				Stream.of(cls.getAnnotations()).forEach((an)-> {
					preloadClassEnByClz(an.annotationType());
				});		
						
				Stream.of(cls.getInterfaces()).forEach((inf) -> {
//					_ce.infs.add(getClassEnByClz(inf));
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
			
			Stream.of(field.getAnnotations()).forEach((an)-> {
				preloadClassEnByClz(an.annotationType());
			});	
//
//			fe.fieldType = 
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

//			_me.returnClass =
			if (method instanceof Method) 
					preloadClassEnByClz(((Method)method).getReturnType());
			
			Stream.of(method.getAnnotations()).forEach((an)-> {
				preloadClassEnByClz(an.annotationType());
			});	
			
			Stream.of(method.getParameters()).forEach((param) -> {
				preloadParamEnByParam(param, _me);
			});
			Stream.of(method.getExceptionTypes()).forEach((clz) -> {
//				_me.exceptionClzz.add(getClassEnByClz(clz));
				preloadClassEnByClz(clz);
			});

			return me;
		}
		
		public ParameterEn preloadParamEnByParam(Parameter param, MethodEn me) {
			if (param == null || me == null) {
				return null;
			}
			
			ParameterEn pe = new ParameterEn(param, me);
			
			Stream.of(param.getAnnotations()).forEach((an)-> {
				preloadClassEnByClz(an.annotationType());
			});	
			
			//pe.paramType = 
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
						
				Class<?> enclosingClass = cls.getEnclosingClass();
				if (enclosingClass != null) {
					ce.enclosing = inflateClassEnByClz(enclosingClass);
				}
				
				ce.superClz = inflateClassEnByClz(cls.getSuperclass());
				
				Stream.of(cls.getAnnotations()).forEach((an)-> {
					ce.annotations.add(inflateClassEnByClz(an.annotationType()));
				});		
						
				Stream.of(cls.getInterfaces()).forEach((inf) -> {
					ce.infs.add(inflateClassEnByClz(inf));
				});
				
				ce.children.stream().forEach((be)->{
					if (be instanceof FieldEn) {
						FieldEn fe = (FieldEn) be;
						inflateFieldEnByFiled(fe, ce);
					} else if (be instanceof MethodEn) {
						inflateMethodEnByMethod((MethodEn)be, ce);
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
					inflateClassEnByClz(((Method)me.method).getReturnType());

			me.children.forEach((pe) -> {
				inflateParamEnByParam((ParameterEn)pe, me);
			});
			Stream.of(me.method.getExceptionTypes()).forEach((clz) -> {
				me.exceptionClzz.add(inflateClassEnByClz(clz));
			});

			return me;
		}
		
		public ParameterEn inflateParamEnByParam(ParameterEn pe, MethodEn me) {
			if (pe == null || me == null) {
				return null;
			}
			
			pe.paramType = inflateClassEnByClz(pe.parameter.getType());
			return pe;
		}
	}
	
	public static void main(String[] args) {
		WeldTest.setUp();
		JpaModule.instance();
		File _f = new File(String.format("%s/lib/rt.jar", SystemUtils.JAVA_HOME));
		
		EntryLoader el = new EntryLoader();
		try {
			el.extractJarStructure(_f);
//			el.extractJarStructure("junit");

			long size = el.classEnPool.size();
			
			{
				AtomicLong counter = new AtomicLong(0);
				el.roots.parallelStream().forEach((be) -> {
					ReflectDao dao = ReflectDao.threadLocal.get();
					dao.beginTransaction();
					EntryLoader.traverse(be, (_be) -> {
						dao.create(_be); 
						
						if (_be instanceof ClassEn){
							long _c = counter.incrementAndGet();
							log.info(String.format("%d/%d %f%% %s", _c, size, _c/(double)size * 100, _be.name));
						}
					}, () -> {
						dao.getEm().flush();
					});
					dao.endTransaction();
				});
				System.out.println("Preload class: " + el.classEnPool.size());
			}

			{
				ReflectDao dao = ReflectDao.threadLocal.get();
				List<Object[]> idAndNames = dao.queryEntity("select id, name from ClassEn ce");
				idAndNames.parallelStream().forEach((idAndName) -> {
					Long id = (Long) idAndName[0];
					String name = (String) idAndName[1];

					ClassEn ce = el.classEnPool.get(name);
					if (ce != null)
						ce.id = id;
				});
			}

			el.classEnPool.keySet().parallelStream().forEach((key) -> {
				ClassEn ce = el.classEnPool.get(key);
				el.inflateClassEnByClz(ce);
				System.out.println(key + ": \t" + ce.id + ": \t" + ce.name);
			});
			System.out.println("Inflate class: " + el.classEnPool.size());

			
			
			ReflectDao dao = ReflectDao.threadLocal.get();
			el.classEnPool.keySet().forEach((key) -> {
				dao.beginTransaction();
				
				ClassEn ce = el.classEnPool.get(key);
				log.info(String.format("%d/%d %f%% %s", ++counter, size, counter/(double)size * 100, ce.name));
				dao.inflateClassEnByNativeSql(ce);
				dao.endTransaction();
			});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JpaModule.instance().destory();
		}
	}
	static long counter = 0;
}
