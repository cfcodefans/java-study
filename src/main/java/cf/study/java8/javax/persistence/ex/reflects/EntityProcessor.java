package cf.study.java8.javax.persistence.ex.reflects;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import junit.framework.Assert;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.internal.SessionImpl;
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


public class EntityProcessor {
	@BeforeClass
	public static void setUp() {
		WeldTest.setUp();
		JpaModule.instance();
	}
	
	@Test
	public void test1() {
		EntityProcessor ep = assembler();
		ep.processClassEn(Object.class);
		
		ep.roots.parallelStream().forEach((be)->{
			ReflectDao dao = ReflectDao.threadLocal.get();
			dao.beginTransaction();
			traverse(be, (_be)->{
				if (_be instanceof ClassEn) {
					dao.getEm().flush();
				}
				dao.create(_be);
			}, ()->{});
			dao.endTransaction();
		});
	}
	
	public EntityProcessor preload(EntityProcessor base, Class<?>...clzz) {
		if (ArrayUtils.isEmpty(clzz)) return null;
		
		this.base = base;
		Stream.of(clzz).forEach(clz->{
			processClassEn(clz);
		});
		
		roots.parallelStream().forEach((be)->{
			ReflectDao dao = ReflectDao.threadLocal.get();
			dao.beginTransaction();
			traverse(be, (_be)->{
				if (_be instanceof ClassEn) {
					dao.getEm().flush();
				}
				dao.create(_be);
			}, ()->{});
			dao.endTransaction();
		});
		
		return this;
	}
	
	public EntityProcessor process() {
		ConcurrentLinkedQueue<String> sqlQueue = new ConcurrentLinkedQueue<String>();

		classEnPool.values().stream().forEach(ce -> {
			traverse(ce, (_be) -> {
				List<String> sqlList = associateByNativeSql(_be);
				if (CollectionUtils.isEmpty(sqlList)) {
					return;
				}
				sqlQueue.addAll(sqlList);
			}, () -> {
			});
		});

		System.out.println(String.format("size of sql: %d", sqlQueue.size()));
		ReflectDao dao = ReflectDao.threadLocal.get();
		dao.beginTransaction();
		Connection conn = dao.getEm().unwrap(SessionImpl.class).connection();
		sqlQueue.stream().parallel().forEach(sql -> {
			System.out.println(sql);
			try {
				conn.createStatement().execute(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		dao.endTransaction();

		return this;
	}
	
	
	
	@Test
	public void test1_1() {
		EntityProcessor ep = assembler();
		ep.clazzProc = (clz) -> {return ep.processClassEn(clz);};
		ep.preload(null, Object.class).process();
		
//		EntityProcessor ep = assembler();
//		ep.processClassEn(Object.class);
//		
//		ep.roots.parallelStream().forEach((be)->{
//			ReflectDao dao = ReflectDao.threadLocal.get();
//			dao.beginTransaction();
//			traverse(be, (_be)->{
//				if (_be instanceof ClassEn) {
//					dao.getEm().flush();
//				}
//				dao.create(_be);
//			}, ()->{});
//			dao.endTransaction();
//		});
//		
////		EntityProcessor ep1 = associator();
////		ep1.base = ep.base;
////		ep.classEnPool.keySet().stream().parallel().forEach(key->{
////			ep1.classEnPool.put(key, ep.getClassEnFromCache(key));
////		});
////		
////		
////		ep1.classEnPool.values().stream().parallel().forEach((ce)->{
////			System.out.println(ce);
////			ep1.reprocessClassEn(ce.clazz);
////		});
////		
////		Assert.assertFalse(ep1.inflatedClassEnPool.isEmpty());
////		System.out.println("start....");
//		
//		ConcurrentLinkedQueue<String> sqlQueue = new ConcurrentLinkedQueue<String>();
//		
//		ep.classEnPool.values().stream().forEach(ce->{
//			traverse(ce, (_be)->{
//				List<String> sqlList = ep.associateByNativeSql(_be);
//				if (CollectionUtils.isEmpty(sqlList)) {
//					return;
//				}
//				sqlQueue.addAll(sqlList);
//			}, ()->{});
//		});
//		
//		System.out.println(String.format("size of sql: %d", sqlQueue.size()));
//		ReflectDao dao = ReflectDao.threadLocal.get();
//		dao.beginTransaction();
//		Connection conn = dao.getEm().unwrap(SessionImpl.class).connection();
//		sqlQueue.stream().parallel().forEach(sql->{
//			System.out.println(sql);
//			try {
//				conn.createStatement().execute(sql);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		});
//		
//		dao.endTransaction();
	}
	
	@Test
	public void test2() {
		EntityProcessor ep = associator();
		
		ReflectDao dao = ReflectDao.threadLocal.get();
		dao.beginTransaction();
		
		List<BaseEn> beList = dao.queryEntity("select distinct be from BaseEn be left join fetch be.children kids");
		
		System.out.println("loaded BaseEn: " + beList.size());
		
		beList.forEach(be -> {
			if (be instanceof PackageEn) {
				PackageEn pe = (PackageEn) be;
				ep.packageEnPool.put(pe.name, pe);
			} else if (be instanceof ClassEn) {
				ClassEn ce = (ClassEn) be;
				ep.classEnPool.put(ce.name, ce);
			}
		});
		
		ep.classEnPool.values().stream().parallel().forEach((ce) -> {
			ce.loadClass();
			System.out.println(ce);
			ep.reprocessClassEn(ce.clazz);
		});
		
		Assert.assertFalse(ep.inflatedClassEnPool.isEmpty());
		System.out.println("start....");
		
		ConcurrentLinkedQueue<String> sqlQueue = new ConcurrentLinkedQueue<String>();
		
		ep.classEnPool.values().stream().forEach(ce -> {
			traverse(ce, (_be) -> {
				List<String> sqlList = ep.associateByNativeSql(_be);
				if (CollectionUtils.isEmpty(sqlList)) {
					return;
				}
				sqlQueue.addAll(sqlList);
			}, () -> {
			});
		});
		
		System.out.println(String.format("size of sql: %d", sqlQueue.size()));
		
		Connection conn = dao.getEm().unwrap(SessionImpl.class).connection();
		sqlQueue.stream().parallel().forEach(sql -> {
			System.out.println(sql);
			try {
				conn.createStatement().execute(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		dao.endTransaction();
	}
	
	@Test
	public void test3() {
		test1();
		test2();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test4() {
		EntityProcessor ep = assembler();
		{
			ReflectDao dao = ReflectDao.threadLocal.get();
			dao.beginTransaction();

			List<BaseEn> beList = dao.queryEntity("select distinct be from BaseEn be left join fetch be.children kids");

			System.out.println("loaded BaseEn: " + beList.size());

			beList.forEach(be -> {
				if (be instanceof PackageEn) {
					PackageEn pe = (PackageEn) be;
					ep.packageEnPool.put(pe.name, pe);
				} else if (be instanceof ClassEn) {
					ClassEn ce = (ClassEn) be;
					ep.classEnPool.put(ce.name, ce);
				}

				if (be.enclosing == null) {
					ep.roots.add(be);
				}
			});

			dao.endTransaction();
			
			System.out.println("preload classes: " + ep.classEnPool.size());
		}
		
		Long cursor = 0l;
		{
			ReflectDao dao = ReflectDao.threadLocal.get();
			cursor = (Long) dao.findOneEntity("select be.id from BaseEn be order by be.id desc");
			if (cursor == null) {
				cursor = 0l;
			}
			System.out.println("current id cursor: " + cursor);
		}
		
		List<Class<?>> junitClzList = Reflects.extractClazz(Reflects.getJarFileInClassPath("junit"));
		
		{
			EntityProcessor _ep = assembler();
			_ep.base = ep;

			junitClzList.forEach((clz) -> {
				_ep.processClassEn(clz);
			});

			System.out.println("has processed: " + _ep.classEnPool.size());
			System.out.println(_ep.roots.size());

			_ep.roots.parallelStream().forEach((be) -> {
				ReflectDao dao = ReflectDao.threadLocal.get();
				dao.beginTransaction();
				traverse(be, (_be) -> {
					if (_be instanceof ClassEn) {
						dao.getEm().flush();
					}
					dao.create(_be);
				}, () -> {});
				dao.endTransaction();
			});
			
			ReflectDao dao = ReflectDao.threadLocal.get();
			dao.beginTransaction();
			List<BaseEn> beList = dao.queryEntity("select distinct be from BaseEn be left join fetch be.children kids where be.id > ?1", cursor);
			
			System.out.println("loaded BaseEn: " + beList.size());
			
			EntityProcessor ep1 = associator();
			ep1.base = ep;
			beList.forEach(be->{
				if (be instanceof PackageEn) {
					PackageEn pe = (PackageEn) be;
					ep1.packageEnPool.put(pe.name, pe);
				} else if (be instanceof ClassEn) {
					ClassEn ce = (ClassEn) be;
					ep1.classEnPool.put(ce.name, ce);
				}
			});
			
			ep1.classEnPool.values().stream().parallel().forEach((ce)->{
				ce.loadClass();
				System.out.println(ce);
				ep1.reprocessClassEn(ce.clazz);
			});
			
			Assert.assertFalse(ep1.inflatedClassEnPool.isEmpty());
			System.out.println("start....");
			
			ConcurrentLinkedQueue<String> sqlQueue = new ConcurrentLinkedQueue<String>();
			
			ep.classEnPool.values().stream().forEach(ce->{
				traverse(ce, (_be)->{
					List<String> sqlList = ep1.associateByNativeSql(_be);
					if (CollectionUtils.isEmpty(sqlList)) {
						return;
					}
					sqlQueue.addAll(sqlList);
				}, ()->{});
			});
			
			System.out.println(String.format("size of sql: %d", sqlQueue.size()));
			
			Connection conn = dao.getEm().unwrap(SessionImpl.class).connection();
			sqlQueue.stream().parallel().forEach(sql->{
				System.out.println(sql);
				try {
					conn.createStatement().execute(sql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			
			dao.endTransaction();
		}
		
	}
	
	public List<String> associateByNativeSql(BaseEn be) {
		if (be == null) return Collections.emptyList();
		
		List<String> sb = new LinkedList<String>();
		
//		be.annotations.forEach(anno->{
//			sb.add(String.format("insert into annotations (base_en_id, annotation_en_id) values (%d, %d);", be.id, anno.id));
//		});
		
		if (be instanceof ClassEn) {
			ClassEn ce = (ClassEn) be;
			Class<?> superClz = ce.clazz.getSuperclass();
			if (superClz != null) {
//				dao.executeNativeSqlUpdate("update class_en set super=? where id=?", loadClassEn(superClz).id, ce.id);
				sb.add(String.format("update class_en set super=%d where id=%d;\n", loadClassEn(superClz).id, ce.id));
			}
			
			Stream.of(ce.clazz.getAnnotations()).forEach(anClz->{
				ClassEn an = loadClassEn(anClz.annotationType());
				sb.add(String.format("insert into annotations (base_en_id, annotation_en_id) values (%d, %d);", be.id, an.id));
			});
			
			{
				final ClassEn _ce = ce;
				Stream.of(ce.clazz.getInterfaces()).forEach((_inf) -> {
					ClassEn inf = loadClassEn(_inf);
//					dao.executeNativeSqlUpdate(
//							"insert into interfaces (implement_en_id, interface_en_id) values (?,?) on duplicate key update interface_en_id=?; ",
//							_ce.id, inf.id, inf.id);
					sb.add(String.format(
							"insert into interfaces (implement_en_id, interface_en_id) values (%d,%d);\n",
							_ce.id, inf.id));
				});
			}
		}
		
		if (be instanceof MethodEn) {
			MethodEn me = (MethodEn) be;
			if (me.method instanceof Method) {
				Method md = (Method)me.method;
				if (md.getReturnType() != null) {
//					dao.executeNativeSqlUpdate("update method_en set return_clz_id=? where id=?", 
//						loadClassEn(md.getReturnType()).id, 
//						me.id);
					sb.add(String.format("update method_en set return_clz_id=%d where id=%d;\n",
							loadClassEn(md.getReturnType()).id, 
							me.id));
				}
				
			}
			
			Stream.of(me.method.getAnnotations()).forEach(anClz->{
				ClassEn an = loadClassEn(anClz.annotationType());
				sb.add(String.format("insert into annotations (base_en_id, annotation_en_id) values (%d, %d);", be.id, an.id));
			});
			
			Executable exe = me.method;
			Stream.of(exe.getExceptionTypes()).forEach(exClz->{
//				dao.executeNativeSqlUpdate("insert into exceptions (method_en_id, exception_en_id) values (?,?);", 
//						me.id, 
//						loadClassEn(exClz).id);
				sb.add(String.format("insert into exceptions (method_en_id, exception_en_id) values (%d,%d);\n", 
						me.id, 
						loadClassEn(exClz).id));
			});
		}
		
		if (be instanceof FieldEn) {
			FieldEn fe = (FieldEn) be;
//			dao.executeNativeSqlUpdate("update field_en set field_clz_id=? where id=?", 
//					loadClassEn(fe.field.getType()).id, 
//					fe.id);
			sb.add(String.format("update field_en set field_clz_id=%d where id=%d;\n", 
					loadClassEn(fe.field.getType()).id, 
					fe.id));
			
			Stream.of(fe.field.getAnnotations()).forEach(anClz->{
				ClassEn an = loadClassEn(anClz.annotationType());
				sb.add(String.format("insert into annotations (base_en_id, annotation_en_id) values (%d, %d);", be.id, an.id));
			});
		}
		
		if (be instanceof ParameterEn) {
			ParameterEn pe = (ParameterEn) be;
//			dao.executeNativeSqlUpdate("update param_en set param_clz_id=? where id=?", 
//					loadClassEn(pe.parameter.getType()).id, 
//					pe.id);
			sb.add(String.format("update param_en set param_clz_id=%d where id=%d;\n", 
					loadClassEn(pe.parameter.getType()).id, 
					pe.id));
			
			Stream.of(pe.parameter.getAnnotations()).forEach(anClz->{
				ClassEn an = loadClassEn(anClz.annotationType());
				sb.add(String.format("insert into annotations (base_en_id, annotation_en_id) values (%d, %d);", be.id, an.id));
			});
		}
		
		return sb;
	}
	
	public static EntityProcessor assembler() {
		EntityProcessor ep = new EntityProcessor();
		ep.clazzProc = (clz) -> {ep.processClassEn(clz); return null;};
		ep.clazzGetter = ep::getClassEn;
		return ep;
	}
	
	public static EntityProcessor associator() {
		EntityProcessor ep = new EntityProcessor();
		ep.clazzProc = (clz) -> {ep.reprocessClassEn(clz); return null;};
		ep.clazzGetter = ep::loadClassEn;
		return ep;
	}

	public static void traverse(BaseEn be, Consumer<BaseEn> act, Runnable interAct) {
		if (be instanceof ClassEn)
			System.out.println(be);
		// System.out.println("\t" + be.children);
		act.accept(be);
		be.children.forEach((en) -> {
			traverse(en, act, interAct);
		});
		interAct.run();
	}

	public EntityProcessor base;
	
	public final Map<String, ClassEn> classEnPool = MapUtils.synchronizedMap(new LinkedHashMap<String, ClassEn>());
	public Function<Class<?>, ClassEn> clazzProc = null;
	public Function<Class<?>, ClassEn> clazzGetter = null;

	public BiFunction<ClassEn, Field, FieldEn> fieldProc = null;
	public final Map<String, ClassEn> inflatedClassEnPool = MapUtils.synchronizedMap(new LinkedHashMap<String, ClassEn>());
	public final ReentrantLock lock = new ReentrantLock();
	public final Map<String, PackageEn> packageEnPool = MapUtils.synchronizedMap(new LinkedHashMap<String, PackageEn>());
	public final Collection<BaseEn> roots = CollectionUtils.synchronizedCollection(new LinkedHashSet<BaseEn>());

	public ClassEn getClassEnFromCache(String clzName) {
		if (StringUtils.isBlank(clzName)) return null;
		
		if (base != null) {
			ClassEn ce = base.getClassEnFromCache(clzName);
			if (ce != null) return ce;
		}
		
		return classEnPool.get(clzName);
	}
	
	public void processAnnotation(BaseEn be, AnnotatedElement ae) {
		if (be == null || ae == null)
			return;
		Stream.of(ae.getDeclaredAnnotations()).forEach((an) -> {
			ClassEn ce = clazzProc.apply(an.annotationType());
			if (ce != null)
				be.annotations.add(ce);
		});
	}

	public ClassEn processClassEn(Class<?> clz) {
		if (clz == null)
			return null;

		while (clz.isArray()) {
			clz = clz.getComponentType();
		}

		ClassEn _ce = null;
		try {
			PackageEn pe = null;
			pe = processPackageEn(clz.getPackage());
			String clzName = ClassEn.checkClzName(clz);
			
			lock.lockInterruptibly();
			_ce = getClassEnFromCache(clzName);
			if (_ce != null) {
				return _ce;
			}

			Class<?> enclosingClz = ClassEn.getEnclossingClz(clz);

			ClassEn enclosingClassEn = clazzProc.apply(enclosingClz);
			_ce = new ClassEn(clz, ObjectUtils.defaultIfNull(enclosingClassEn, pe));
			_ce.pkg = pe;

			classEnPool.put(clzName, _ce);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return _ce;
		}
		lock.unlock();

		return processClassEn(_ce);
	}

	public ClassEn processClassEn(final ClassEn ce) {
		if (ce == null) return ce;
		
		if (ce.enclosing == null) {
			ce.enclosing = ce.pkg;
		}

		if (ce.enclosing == null) {
			roots.add(ce);
		}
		
		Class<?> clz = ce.clazz;

		processAnnotation(ce, clz);

		ce.superClz = clazzProc.apply(clz.getSuperclass());

		Stream.of(clz.getInterfaces()).forEach((infClz) -> {
			ClassEn infCe = clazzProc.apply(infClz);
			if (infCe != null)
				ce.infs.add(infCe);
			});

		Stream.of(clz.getDeclaredFields()).forEach((fd) -> {
			processFieldEn(ce, fd);
		});

		Stream.of(clz.getDeclaredConstructors()).forEach((con) -> {
			processMethodEn(ce, con);
		});

		Stream.of(clz.getDeclaredMethods()).forEach((method) -> {
			processMethodEn(ce, method);
		});

		return ce;
	}

	private ClassEn loadClassEn(Class<?> clz) {
		ClassEn _ce = null;
		if (clz == null) return _ce;
		
		while (clz.isArray()) {
			clz = clz.getComponentType();
		}
		
		try {
			lock.lockInterruptibly();
			String clzName = ClassEn.checkClzName(clz);
			
			_ce = inflatedClassEnPool.get(clzName);
			if (_ce != null) {
				return _ce;
			}
			
			_ce = classEnPool.get(clzName);
			_ce.clazz = clz;
			inflatedClassEnPool.put(clzName, _ce);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		lock.unlock();
		return _ce;
	}
	
	private ClassEn getClassEn(Class<?> clz) {
		ClassEn _ce = null;
		try {
			PackageEn pe = null;
			pe = processPackageEn(clz.getPackage());
			String clzName = ClassEn.checkClzName(clz);
			
			lock.lockInterruptibly();
			_ce = getClassEnFromCache(clzName);
			if (_ce != null) {
				return _ce;
			}

			Class<?> enclosingClz = ClassEn.getEnclossingClz(clz);

			ClassEn enclosingClassEn = clazzProc.apply(enclosingClz);
			_ce = new ClassEn(clz, ObjectUtils.defaultIfNull(enclosingClassEn, pe));
			_ce.pkg = pe;

			classEnPool.put(clzName, _ce);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return _ce;
		}
		lock.unlock();
		return _ce;
	}

	public FieldEn processFieldEn(ClassEn ce, Field field) {
		if (field == null || ce == null)
			return null;

		FieldEn fe = FieldEn.instance(ce, field);
		processAnnotation(fe, field);
		fe.fieldType = clazzProc.apply(field.getType());

		return fe;
	}

	public MethodEn processMethodEn(ClassEn ce, Executable exe) {
		if (exe == null || ce == null)
			return null;

		MethodEn me = MethodEn.instance(ce, exe);

		processAnnotation(me, exe);

		if (exe instanceof Method) {
			Method method = (Method) exe;
			me.returnClass = clazzProc.apply(method.getReturnType());
		}

		Stream.of(exe.getExceptionTypes()).forEach((exClz) -> {
			ClassEn exce = clazzProc.apply(exClz);
			if (exce != null)
				me.exceptionClzz.add(exce);
			// methodExceptionTypesProc.apply(me, exClz);
			});

		Stream.of(exe.getParameters()).forEach((param) -> {
			processParameterEn(me, param);
		});

		return me;
	}

	public synchronized PackageEn processPackageEn(Package _package) {
		if (_package == null)
			return null;

		String pkgName = _package.getName();
		PackageEn _pe = packageEnPool.get(pkgName);
		if (_pe != null)
			return _pe;

		PackageEn pe = new PackageEn(_package, processPackageEn(PackageEn.getParentPkg(_package)));
		if (pe.enclosing == null) {
			roots.add(pe);
		}

		packageEnPool.put(pkgName, pe);
		processAnnotation(pe, _package);

		return pe;
	}

	public ParameterEn processParameterEn(MethodEn me, Parameter param) {
		if (param == null || me == null)
			return null;

		ParameterEn pe = ParameterEn.instance(me, param);
		processAnnotation(pe, param);
		pe.paramType = clazzProc.apply(param.getType());

		return pe;
	}

	public synchronized ClassEn reprocessClassEn(Class<?> clz) {
		if (clz == null)
			return null;
	
		while (clz.isArray()) {
			clz = clz.getComponentType();
		}
	
		ClassEn _ce = null;
		if (clz == null) return _ce;
		try {
			String clzName = ClassEn.checkClzName(clz);
			
			_ce = inflatedClassEnPool.get(clzName);
			if (_ce != null) {
				return _ce;
			}
			
			_ce = classEnPool.get(clzName);
			_ce.clazz = clz;
			inflatedClassEnPool.put(clzName, _ce);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		lock.unlock();
	
		return processClassEn(_ce);
	}
}
