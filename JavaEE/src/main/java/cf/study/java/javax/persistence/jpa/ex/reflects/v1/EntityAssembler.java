package cf.study.java.javax.persistence.jpa.ex.reflects.v1;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cf.study.java.javax.cdi.weld.WeldTest;
import cf.study.java.javax.persistence.dao.JpaModule;
import cf.study.java.javax.persistence.jpa.ex.reflects.v1.entity.BaseEn;
import cf.study.java.javax.persistence.jpa.ex.reflects.v1.entity.ClassEn;
import cf.study.java.javax.persistence.jpa.ex.reflects.v1.entity.FieldEn;
import cf.study.java.javax.persistence.jpa.ex.reflects.v1.entity.MethodEn;
import cf.study.java.javax.persistence.jpa.ex.reflects.v1.entity.PackageEn;
import cf.study.java.javax.persistence.jpa.ex.reflects.v1.entity.ParameterEn;
import cf.study.java.lang.reflect.Reflects;
import misc.MiscUtils;

public class EntityAssembler {

	public static class Context {
		public Map<String, PackageEn> packageEnPool = MapUtils.synchronizedMap(new LinkedHashMap<String, PackageEn>());
		public Map<String, ClassEn> classEnPool = MapUtils.synchronizedMap(new LinkedHashMap<String, ClassEn>());
		public Collection<BaseEn> roots = Collections.synchronizedCollection(new LinkedHashSet<BaseEn>());
		public Map<String, ClassEn> inflatedClassEnPool = MapUtils.synchronizedMap(new LinkedHashMap<String, ClassEn>());

		public void reset() {
			packageEnPool.clear();
			classEnPool.clear();
			roots.clear();
			inflatedClassEnPool.clear();
		}
	}

	public static Class<?> getEnclossingClz(Class<?> clz) {
		if (clz == null)
			return null;
		if (clz.getEnclosingClass() != null)
			return clz.getEnclosingClass();
		if (clz.getEnclosingMethod() != null)
			return clz.getEnclosingMethod().getDeclaringClass();
		if (clz.getEnclosingConstructor() != null)
			return clz.getEnclosingConstructor().getDeclaringClass();

		return null;
	}

	public final Context ctx = new Context();
	private static final Logger log = LoggerFactory.getLogger(EntityAssembler.class);

	public synchronized FieldEn assembleFieldEn(Field field) {
		if (field == null)
			return null;

		FieldEn fe = new FieldEn(field, assembleClassEn(field.getDeclaringClass()));

		assembleAnnotations(fe, field);

		fe.fieldType = assembleClassEn(field.getType());

		return fe;
	}

	public synchronized ClassEn assembleClassEn(Class<?> clz) {
		if (clz == null)
			return null;

		while (clz.isArray()) {
			clz = clz.getComponentType();
		}
		
		String clzName = clz.getName();

		ClassEn _ce = ctx.classEnPool.get(clzName);
		if (_ce != null) {
			return _ce;
		}

		Class<?> enclosingClz = getEnclossingClz(clz);

		PackageEn pe = assemblePackageEn(clz.getPackage());
		ClassEn enclosingClassEn = assembleClassEn(enclosingClz);
		ClassEn ce = new ClassEn(clz, ObjectUtils.defaultIfNull(enclosingClassEn, pe));
		
		ctx.classEnPool.put(clzName, ce);

		ce.pkg = pe;

		if (ce.enclosing == null) {
			ce.enclosing = ce.pkg;
		}

		if (ce.enclosing == null) {
			ctx.roots.add(ce);
		}

		assembleAnnotations(ce, clz);

		ce.superClz = assembleClassEn(clz.getSuperclass());

		Stream.of(clz.getInterfaces()).forEach((infClz) -> {
			ce.infs.add(assembleClassEn(infClz));
		});

		Stream.of(clz.getDeclaredFields()).forEach((fd) -> {
			assembleFieldEn(fd);
		});

		Stream.of(clz.getDeclaredConstructors()).forEach((con) -> {
			assembleMethodEn(con);
		});

		Stream.of(clz.getDeclaredMethods()).forEach((method) -> {
			assembleMethodEn(method);
		});

		return ce;
	}

	public synchronized MethodEn assembleMethodEn(Executable exe) {
		if (exe == null)
			return null;

		MethodEn me = new MethodEn(exe, assembleClassEn(exe.getDeclaringClass()));

		assembleAnnotations(me, exe);

		if (exe instanceof Method) {
			Method method = (Method) exe;
			me.returnClass = assembleClassEn(method.getReturnType());
		}

		Stream.of(exe.getExceptionTypes()).forEach((exClz) -> {
			me.exceptionClzz.add(assembleClassEn(exClz));
		});

		Stream.of(exe.getParameters()).forEach((param) -> {
			assembleParameterEn(param, me);
		});

		return me;
	}

	public synchronized ParameterEn assembleParameterEn(Parameter param, MethodEn me) {
		if (param == null || me == null)
			return null;

		ParameterEn pe = new ParameterEn(param, me);

		assembleAnnotations(pe, param);

		pe.paramType = assembleClassEn(param.getType());

		return pe;
	}

	public synchronized PackageEn assemblePackageEn(Package _package) {
		if (_package == null)
			return null;

		String pkgName = _package.getName();

		PackageEn _pe = ctx.packageEnPool.get(pkgName);
		if (_pe != null)
			return _pe;

		PackageEn pe = new PackageEn(_package, assemblePackageEn(getParentPkg(_package)));

		if (pe.enclosing == null) {
			ctx.roots.add(pe);
		}
		
		ctx.packageEnPool.put(pkgName, pe);

		assembleAnnotations(pe, _package);

		return pe;
	}

	public synchronized void assembleAnnotations(BaseEn be, AnnotatedElement ae) {
		if (be == null || ae == null)
			return;

		Stream.of(ae.getDeclaredAnnotations()).forEach((an) -> {
			be.annotations.add(assembleClassEn(an.annotationType()));
		});
	}

	public synchronized void inflateAnnotations(BaseEn be, AnnotatedElement ae) {
		if (be == null || ae == null)
			return;

		Stream.of(ae.getDeclaredAnnotations()).forEach((an) -> {
			be.annotations.add(inflateClassEn(an.annotationType()));
		});
	}

	
	public static Package getParentPkg(Package _package) {
		if (_package == null)
			return null;

		return Package.getPackage(StringUtils.substringBeforeLast(_package.getName(), "."));
	}

	public PackageEn inflatePackageEn(PackageEn pe) {
		if (pe == null)
			return pe;

		pe._package = Package.getPackage(pe.name);

		inflateAnnotations(pe);

		return pe;
	}

	public synchronized ClassEn inflateClassEn(Class<?> clz) {
		if (clz == null) return null;
		ClassEn ce = ctx.classEnPool.get(clz.getName());
		if (ce == null) {
			Class<?> clz1 = Reflects.loadClass(clz.getName());
			if (clz1 == null) {
				return null;
			}
			ce = ctx.classEnPool.get(clz1.getName());
		}
		return inflateClassEn(ce);
	}
	
	public synchronized ClassEn inflateClassEn(ClassEn ce) {
		if (ce == null)
			return ce;

		ClassEn _ce = ctx.inflatedClassEnPool.get(ce.name);
		if (_ce != null) {
			return _ce;
		}
		
		ctx.inflatedClassEnPool.put(ce.name, ce);
		
		ce.loadClass();

		inflateAnnotations(ce, ce.clazz);

		if (ce.clazz.getSuperclass() != null)
			ce.superClz = inflateClassEn(ce.clazz.getSuperclass());

		Stream.of(ce.clazz.getInterfaces()).forEach((inf) -> {
			ClassEn interfaceClassEn = inflateClassEn(inf);
			if (interfaceClassEn != null) {
				ce.infs.add(interfaceClassEn);
			} else {
				log.error("interface is null : " + inf);
			}
		});

		if (ce.enclosing instanceof ClassEn) {
			ClassEn ec = (ClassEn) ce.enclosing;
		 	ce.enclosing = inflateClassEn(ec);
		}

		ce.pkg = inflatePackageEn(ce.pkg);

		ce.children.stream().filter(child -> (child instanceof FieldEn)).forEach((child) -> {
			FieldEn fe = (FieldEn) child;
			inflateFieldEn(fe);
		});

		MethodEn[] meAry = ce.children.stream().filter(child -> (child instanceof MethodEn)).toArray(MethodEn[]::new);

		Stream.of(ce.clazz.getDeclaredMethods()).forEach(method -> {
//			Stream<MethodEn> meStream = Stream.of(meAry);
			Optional<MethodEn> findFirst = Stream.of(meAry).filter(me -> (me.isMatch(method))).findFirst();
			if (!findFirst.isPresent()) {
				log.error(method.getName() + " isn't found!");
				return;
			}
			MethodEn _me = findFirst.get();
			inflateMethodEn(_me, method);
		});

		Stream.of(ce.clazz.getDeclaredConstructors()).forEach(method -> {
			Optional<MethodEn> findFirst = Stream.of(meAry).filter(me -> (me.isMatch(method))).findFirst();
			if (!findFirst.isPresent()) {
				log.error(method.getName() + " isn't found!");
				return;
			}
			MethodEn _me = findFirst.get();
			inflateMethodEn(_me, method);
		});

		return ce;
	}

	private MethodEn inflateMethodEn(MethodEn me, Executable method) {
		if (me == null)
			return me;

		inflateAnnotations(me, method);

		if (method instanceof Method) {
			me.returnClass = inflateClassEn(((Method)method).getReturnType());
		}

		Stream.of(method.getExceptionTypes()).forEach((ex) -> {
			me.exceptionClzz.add(inflateClassEn(ex));
		});

		me.method = method;

//		List<Class<?>> paramClzz = new ArrayList<Class<?>>(me.children.size());
		me.children.forEach(child -> {
//			paramClzz.add(
					inflateParameterEn((ParameterEn) child);
//					.paramType.clazz);
		});
//		Class<?> paramClzAry[] = paramClzz.toArray(new Class<?>[0]);
//
//		Class<?> declared = ((ClassEn) me.enclosing).clazz;

		// try {
		// me.method = me.name.equals(declared.getName()) ?
		// declared.getConstructor(paramClzAry) :
		// declared.getDeclaredMethod(me.name, paramClzAry);
		// } catch (NoSuchMethodException | SecurityException e) {
		// e.printStackTrace();
		// }

		return me;
	}

	private ParameterEn inflateParameterEn(ParameterEn pe) {
		if (pe == null)
			return pe;


		MethodEn me = (MethodEn) pe.enclosing;

		pe.parameter = Stream.of(me.method.getParameters()).filter(param -> pe.name.equals(param.getName()))
				.findFirst().get();

		inflateAnnotations(pe, pe.parameter);
		pe.paramType = inflateClassEn(pe.parameter.getType());
		
		return pe;
	}

	private FieldEn inflateFieldEn(FieldEn fe) {
		if (fe == null)
			return fe;

		ClassEn ce = (ClassEn) fe.enclosing;

		try {
			fe.field = ce.clazz.getDeclaredField(fe.name);
		} catch (NoSuchFieldException | SecurityException e) {
			log.error(String.format("%s has not field %s", ce.name, fe.name));
		}

		inflateAnnotations(fe, fe.field);

		fe.fieldType = inflateClassEn(fe.field.getType());

		return fe;
	}

	private BaseEn inflateAnnotations(BaseEn be) {
		if (be == null)
			return be;
		be.annotations.stream().forEach((an) -> {
			inflateClassEn(an);
		});
		return be;
	}
	
	@Test
	public void test() {
		EntityAssembler ae = new EntityAssembler();
		ClassEn ce = ae.assembleClassEn(Object.class);
		
		ae.ctx.classEnPool.keySet().forEach(key->{
			System.out.println(key);
		});
		
		inflateClassEn(ce);
	}
	
	public static BaseEn disassemble(BaseEn be) {
		if (be == null) return be;
		
		be.annotations.clear();
		
		if (be instanceof ClassEn) {
			ClassEn ce = (ClassEn) be;
			ce.superClz = null;
			ce.infs.clear();
		} else if (be instanceof FieldEn) {
			FieldEn fe = (FieldEn)be;
			fe.fieldType = null;
		} else if (be instanceof MethodEn) {
			MethodEn me = (MethodEn)be;
			me.returnClass = null;
			me.exceptionClzz = null;
		} else if (be instanceof ParameterEn) {
			ParameterEn pe = (ParameterEn)be;
			pe.paramType = null;
		} 
		
		return be;
	}
	
	@Test
	public void test1() {
		EntityAssembler ae = new EntityAssembler();
		ae.assembleClassEn(Object.class);
		
		ae.ctx.classEnPool.values().stream().parallel().forEach(ce->{
			traverse(ce, 
					EntityAssembler::disassemble, 
					()->{});
		});
		
		ae.ctx.roots.parallelStream().forEach((be)->{
			ReflectDao dao = ReflectDao.threadLocal.get();
			dao.beginTransaction();
			traverse(be, (_be)->{dao.create(_be);}, ()->{dao.getEm().flush();});
			dao.endTransaction();
		});
	}
	
	@Test
	public void test2() {
		int threadCount = MiscUtils.AVAILABLE_PROCESSORS;
		ExecutorService tp = Executors.newFixedThreadPool(threadCount);
		for (int i = 0; i < threadCount; i++) {
			final int _i = i + 1;
			tp.submit(() -> {
				EntityAssembler.inflate(_i);
			});
		}
		
		try {
			while (!tp.awaitTermination(5, TimeUnit.SECONDS)) {
				log.info("ongoing......");
			};
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test3() {
		EntityAssembler ea = new EntityAssembler();
		
		try {
			ReflectDao dao = ReflectDao.threadLocal.get();
			dao.beginTransaction();
			
			List<BaseEn> beList = dao.queryEntity("select distinct be from BaseEn be left join fetch be.children kids");
			
			System.out.println("loaded BaseEn: " + beList.size());
			
			beList.forEach(be->{
				if (be instanceof PackageEn) {
					PackageEn pe = (PackageEn) be;
					ea.ctx.packageEnPool.put(pe.name, pe);
				} else if (be instanceof ClassEn) {
					ClassEn ce = (ClassEn) be;
					System.out.println(ce);
					ea.ctx.classEnPool.put(ce.name, ce);
				}
			});
			
			ea.ctx.classEnPool.values().stream().forEach(ea::associate);
			
			ea.ctx.classEnPool.values().stream().forEach(ce->{
				traverse(ce, (_be)->{dao.edit(_be);}, ()->{dao.getEm().flush();});
			});
			
			dao.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("finished!!!");
	}
	
	@SuppressWarnings("unchecked")
	public static void inflate(int i) {
		EntityAssembler ea = new EntityAssembler();
		
		try {
			ReflectDao dao = ReflectDao.threadLocal.get();
			
			List<BaseEn> beList = dao.queryEntity("select distinct be from BaseEn be left join fetch be.children kids");
			
			System.out.println("loaded BaseEn: " + beList.size());
			
			beList.forEach(be->{
				if (be instanceof PackageEn) {
					PackageEn pe = (PackageEn) be;
					ea.ctx.packageEnPool.put(pe.name, pe);
				} else if (be instanceof ClassEn) {
					ClassEn ce = (ClassEn) be;
					System.out.println(ce);
					ea.ctx.classEnPool.put(ce.name, ce);
				}
			});
			
			ea.ctx.classEnPool.values().stream().forEach(ea::inflateClassEn);
			
			dao.beginTransaction();
			ea.ctx.classEnPool.values().stream().filter(ce->ce.id % i == 0).forEach(ce->{
				traverse(ce, (_be)->{dao.edit(_be);}, ()->{dao.getEm().flush();});
			});
			
			dao.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("finished!!!");
	}

	@BeforeClass
	public static void setUp() {
		WeldTest.setUp();
		JpaModule.instance();
	}

	public static void traverse(BaseEn be, Consumer<BaseEn> act, Runnable interAct) {
		if (be instanceof ClassEn)
		System.out.println(be + ": " + be.children);
//		System.out.println("\t" + be.children);
		act.accept(be);
		be.children.forEach((en) -> {
			traverse(en, act, interAct);
		});
		interAct.run();
	}
	
	
	public void associateAnnotations(BaseEn be, AnnotatedElement ae) {
		if (be == null || ae == null)
			return;

		Stream.of(ae.getDeclaredAnnotations()).forEach((an) -> {
			ClassEn anno = ctx.classEnPool.get(an.annotationType().getName());
			if (anno != null)
				be.annotations.add(anno);
		});
	}
	
	public ClassEn associate(ClassEn ce) {
		if (ce == null) return ce;
		
		log.info(ce.toString());
		ce.loadClass();
		
		associateAnnotations(ce, ce.clazz);
		
		if (ce.clazz.getSuperclass() != null)
			ce.superClz = ctx.classEnPool.get(ce.clazz.getSuperclass().getName());

		Stream.of(ce.clazz.getInterfaces()).forEach((inf) -> {
			ClassEn interfaceClassEn = ctx.classEnPool.get(inf.getName());
			if (interfaceClassEn != null) {
				ce.infs.add(interfaceClassEn);
			} else {
				log.error("interface is null : " + inf);
			}
		});
		
		ce.pkg = inflatePackageEn(ce.pkg);
		
		ce.children.stream().filter(child -> (child instanceof FieldEn)).forEach((child) -> {
			FieldEn fe = (FieldEn) child;
			associateFieldEn(fe);
		});

		MethodEn[] meAry = ce.children.stream().filter(child -> (child instanceof MethodEn)).toArray(MethodEn[]::new);

		Stream.of(ce.clazz.getDeclaredMethods()).forEach(method -> {
//			Stream<MethodEn> meStream = Stream.of(meAry);
			Optional<MethodEn> findFirst = Stream.of(meAry).filter(me -> (me.isMatch(method))).findFirst();
			if (!findFirst.isPresent()) {
				log.error(method.getName() + " isn't found!");
				return;
			}
			MethodEn _me = findFirst.get();
			associateMethodEn(_me, method);
		});

		Stream.of(ce.clazz.getDeclaredConstructors()).forEach(method -> {
			Optional<MethodEn> findFirst = Stream.of(meAry).filter(me -> (me.isMatch(method))).findFirst();
			if (!findFirst.isPresent()) {
				log.error(method.getName() + " isn't found!");
				return;
			}
			MethodEn _me = findFirst.get();
			associateMethodEn(_me, method);
		});
		
		return ce;
	}
	
	public FieldEn associateFieldEn(FieldEn fe) {
		if (fe == null) return fe;
		
		ClassEn ce = (ClassEn) fe.enclosing;

		try {
			fe.field = ce.clazz.getDeclaredField(fe.name);
		} catch (NoSuchFieldException | SecurityException e) {
			log.error(String.format("%s has not field %s", ce.name, fe.name));
		}

		associateAnnotations(fe, fe.field);

		fe.fieldType = ctx.classEnPool.get(fe.field.getType().getName());
		
		return fe;
	}
	
	private MethodEn associateMethodEn(MethodEn me, Executable method) {
		if (me == null)
			return me;

		associateAnnotations(me, method);

		if (method instanceof Method) {
			me.returnClass = ctx.classEnPool.get(((Method)method).getReturnType().getName()); 
		}

		Stream.of(method.getExceptionTypes()).forEach((ex) -> {
			me.exceptionClzz.add(ctx.classEnPool.get(ex.getName()));
		});

		me.method = method;

//		List<Class<?>> paramClzz = new ArrayList<Class<?>>(me.children.size());
		me.children.forEach(child -> {
//			paramClzz.add(
					associateParameterEn((ParameterEn) child);
//					.paramType.clazz);
		});
//		Class<?> paramClzAry[] = paramClzz.toArray(new Class<?>[0]);
//
//		Class<?> declared = ((ClassEn) me.enclosing).clazz;

		// try {
		// me.method = me.name.equals(declared.getName()) ?
		// declared.getConstructor(paramClzAry) :
		// declared.getDeclaredMethod(me.name, paramClzAry);
		// } catch (NoSuchMethodException | SecurityException e) {
		// e.printStackTrace();
		// }

		return me;
	}
	
	private ParameterEn associateParameterEn(ParameterEn pe) {
		if (pe == null)
			return pe;


		MethodEn me = (MethodEn) pe.enclosing;

		pe.parameter = Stream.of(me.method.getParameters()).filter(param -> pe.name.equals(param.getName()))
				.findFirst().get();

		associateAnnotations(pe, pe.parameter);
		pe.paramType = ctx.classEnPool.get(pe.parameter.getType().getName());
		
		return pe;
	}
}
