package cf.study.java8.javax.persistence.jpa.ex.reflects.v1;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Stream;

import misc.MiscUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity.BaseEn;
import cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity.ClassEn;
import cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity.FieldEn;
import cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity.MethodEn;
import cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity.PackageEn;
import cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity.ParameterEn;
import cf.study.java8.lang.reflect.Reflects;



public class DataCollector {
	private static final Logger log = Logger.getLogger(DataCollector.class);

	public final Map<String, PackageEn> packageEnPool = new ConcurrentHashMap<String, PackageEn>(1000);
	public final ConcurrentHashMap<String, AtomicReference<ClassEn>> classEnPool = new ConcurrentHashMap<String, AtomicReference<ClassEn>>(21000);
	public final Collection<BaseEn> roots = CollectionUtils.synchronizedCollection(new LinkedHashSet<BaseEn>());
	public final DataCollector base;
	
	
	
	
	
//	public DataCollector(final DataCollector _base) {
//		this.base = _base;
//	}
	
	public DataCollector() {
		base = null;
	}
	
	public ClassEn getClassEnFromCache(String clzName) {
		if (StringUtils.isBlank(clzName)) return null;
		
		if (base != null) {
			ClassEn ce = base.getClassEnFromCache(clzName);
			if (ce != null) return ce;
		}
		
		AtomicReference<ClassEn> ref = classEnPool.get(clzName);
		return ref == null ? null : ref.get();
	}
	
	public PackageEn getPackageEnFromCache(String pkgName) {
		if (StringUtils.isBlank(pkgName)) return null;
		
		if (base != null) {
			PackageEn pe = base.getPackageEnFromCache(pkgName);
			if (pe != null) return pe;
		}
		
		return packageEnPool.get(pkgName);
	}
	
	public void processAnnotation(BaseEn be, AnnotatedElement ae) {
		if (be == null || ae == null) return;
		Stream.of(ae.getDeclaredAnnotations())
//			.map((an)->clazzProc.apply(an.annotationType()))
			.map(Annotation::annotationType)
			.map(this::processClass)
			.filter(ce->ce != null)
			.forEach(be.annotations::add);
	}
	
	public synchronized PackageEn processPackageEn(Package _package) {
		if (_package == null)
			return null;

		String pkgName = _package.getName();
		
		PackageEn _pe = getPackageEnFromCache(pkgName);
		
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
	
	public ClassEn processClass(Class<?> clz) {
		if (clz == null)
			return null;

//		System.out.println(MiscUtils.invocationInfo() + " clz:\t" + clz);
		
		while (clz.isArray()) {
			clz = clz.getComponentType();
		}

		ClassEn _ce = null;
		try {
			final PackageEn pe = processPackageEn(clz.getPackage());
			final String clzName = ClassEn.checkClzName(clz);
			
			_ce = getClassEnFromCache(clzName);
			if (_ce != null) {
				return _ce;
			}
			
//			while (!(lock.tryLock() || lock.tryLock(1, TimeUnit.SECONDS))) {
//				log.warn("wait for lock to process: " + clz);
//			}
			
			
			
			if (classEnPool.putIfAbsent(clzName, new AtomicReference<ClassEn>()) == null) {
				AtomicReference<ClassEn> ref = classEnPool.get(clzName);
				

				_ce = new ClassEn(clz, null);//ObjectUtils.defaultIfNull(enclosingClassEn, pe));
				if (ref.getAndSet(_ce) != null) {
					log.warn("found repeated: " + clzName);
				}
				
				Class<?> enclosingClz = ClassEn.getEnclossingClz(clz);
				ClassEn enclosingClassEn = processClass(enclosingClz);
				_ce.pkg = pe;
				_ce.enclosing = ObjectUtils.defaultIfNull(enclosingClassEn, pe);
			} else {
				AtomicReference<ClassEn> ref = classEnPool.get(clzName);
				while ((_ce = ref.get()) == null) {
					Thread.sleep(1);
				};
			}			
		} catch (Exception e) {
			e.printStackTrace();
			return _ce;
		} finally {
//			if (lock.isHeldByCurrentThread())
//				lock.unlock();
		}

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

		ce.superClz = processClass(clz.getSuperclass()); 
				//clazzProc.apply(clz.getSuperclass());

		Stream.of(clz.getInterfaces())
			.map(this::processClass)
//			.map(this::processClassEn)
			.filter(ice-> ice != null)
			.forEach(ce.infs::add);

		Stream.of(clz.getDeclaredFields()).forEach((fd) -> processFieldEn(ce, fd));
		Stream.of(clz.getDeclaredConstructors()).forEach((con) -> processMethodEn(ce, con));
		Stream.of(clz.getDeclaredMethods()).forEach(method -> processMethodEn(ce, method));

		return ce;
	}
	
	public FieldEn processFieldEn(ClassEn ce, Field field) {
		if (field == null || ce == null)
			return null;

		FieldEn fe = FieldEn.instance(ce, field);
		processAnnotation(fe, field);
//		fe.fieldType = clazzProc.apply(field.getType());
		fe.fieldType = processClass(field.getType());

		return fe;
	}

	public MethodEn processMethodEn(ClassEn ce, Executable exe) {
		if (exe == null || ce == null)
			return null;

		MethodEn me = MethodEn.instance(ce, exe);

		processAnnotation(me, exe);

		if (exe instanceof Method) {
			Method method = (Method) exe;
//			me.returnClass = clazzProc.apply(method.getReturnType());
			me.returnClass = processClass(method.getReturnType());
		}

		Stream.of(exe.getExceptionTypes())
			.map(this::processClass)
//			.map(this::processClassEn)
			.filter(ece-> ece != null)
			.forEach(me.exceptionClzz::add);
		
//		.forEach((exClz) -> {
//			ClassEn exce = clazzProc.apply(exClz);
//			if (exce != null)
//				me.exceptionClzz.add(exce);
//			});

		Stream.of(exe.getParameters()).forEach(param -> processParameterEn(me, param));

		return me;
	}
	
	public ParameterEn processParameterEn(MethodEn me, Parameter param) {
		if (param == null || me == null)
			return null;

		ParameterEn pe = ParameterEn.instance(me, param);
		processAnnotation(pe, param);
//		pe.paramType = clazzProc.apply(param.getType());
		pe.paramType = processClass(param.getType());

		return pe;
	}
	
	public DataCollector collect(final Class<?>...clzz) {
		if (ArrayUtils.isEmpty(clzz)) return this;
		
		Stream.of(clzz).forEach(this::processClass);
		
		return this;
	}
	
	@Test
	public void test1() {
		DataCollector dc = new DataCollector();
		dc.processClass(Object.class);
//		dc.classEnPool.keySet().forEach(log::info);
		ClassEn ce = getClassEnFromCache(Object.class.getName());
		log.info(ce);
	}
	
	@Test
	public void testCollectRawData() {
		List<Class<?>> clzzList = new LinkedList<>();
		File _f = new File(String.format("%s/lib/rt.jar", SystemUtils.JAVA_HOME));
		clzzList.addAll(Reflects.extractClazz(_f));

		System.out.println(clzzList.size());
		
		clzzList.stream().parallel().forEach(this::processClass);
		
//		System.out.println(this.classEnPool.size());
//		ClassEn objEn = this.classEnPool.get(Object.class.getName());
//		
//		System.out.println(objEn);
//		
//		this.roots.stream().forEach(en -> traverse(en, System.out::println, ()->{}));
	}

	public static void traverse(BaseEn be, Consumer<BaseEn> act, Runnable interAct) {
		if (be instanceof ClassEn) {
			System.out.println(be);
		}
		act.accept(be);
		be.children.forEach(en -> traverse(en, act, interAct));
		interAct.run();
	}
}
