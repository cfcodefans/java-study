package cf.study.data.mining;

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
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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

	public final Map<String, PackageEn> packageEnPool = MapUtils.synchronizedMap(new LinkedHashMap<String, PackageEn>(1000));
	public final Map<String, ClassEn> classEnPool = MapUtils.synchronizedMap(new LinkedHashMap<String, ClassEn>(21000));
	public final Collection<BaseEn> roots = CollectionUtils.synchronizedCollection(new LinkedHashSet<BaseEn>());
	public final DataCollector base;
	public final ReentrantLock lock = new ReentrantLock();
	
	
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
		
		return classEnPool.get(clzName);
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

			ClassEn enclosingClassEn = processClass(enclosingClz);
			_ce = new ClassEn(clz, ObjectUtils.defaultIfNull(enclosingClassEn, pe));
			_ce.pkg = pe;

			classEnPool.put(clzName, _ce);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return _ce;
		} finally {
			lock.unlock();
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
		ClassEn ce = dc.classEnPool.get(Object.class.getName());
		log.info(ce);
	}
	
	@Test
	public void testCollectRawData() {
		List<Class<?>> clzzList = new LinkedList<>();
		File _f = new File(String.format("%s/lib/rt.jar", SystemUtils.JAVA_HOME));
		clzzList.addAll(Reflects.extractClazz(_f));

		System.out.println(clzzList.size());
	}
}
