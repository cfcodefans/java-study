package cf.study.java.javax.persistence.jpa.ex.reflects.v1;

import java.io.File;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.hibernate.Session;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cf.study.java.javax.cdi.weld.WeldTest;
import cf.study.java.javax.persistence.dao.JpaModule;
import cf.study.java.javax.persistence.jpa.ex.reflects.v1.entity.BaseEn;
import cf.study.java.javax.persistence.jpa.ex.reflects.v1.entity.ClassEn;
import cf.study.java.javax.persistence.jpa.ex.reflects.v1.entity.FieldEn;
import cf.study.java.javax.persistence.jpa.ex.reflects.v1.entity.JarEn;
import cf.study.java.javax.persistence.jpa.ex.reflects.v1.entity.MethodEn;
import cf.study.java.javax.persistence.jpa.ex.reflects.v1.entity.PackageEn;
import cf.study.java.javax.persistence.jpa.ex.reflects.v1.entity.ParameterEn;
import cf.study.java.javax.persistence.jpa.ex.reflects.v1.entity.SourceEn;
import cf.study.java.lang.reflect.Reflects;



public class DataCollector {
	public static DataCollector _base = new DataCollector();
	
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUp() {
		WeldTest.setUp();
		JpaModule.instance();
		
		{
			ReflectDao dao = ReflectDao.threadLocal.get();
			List<ClassEn> ceList = (List<ClassEn>) dao.queryEntity("select ce from ClassEn ce join fetch ce.source");
			ceList.parallelStream().forEach(ce -> _base.classEnPool.put(ce.name, new AtomicReference<ClassEn>(ce)));

			List<PackageEn> peList = (List<PackageEn>) dao.queryEntity("select pe from PackageEn pe");
			peList.parallelStream().forEach(pe -> _base.packageEnPool.put(pe.name, pe));
			
//			Object delegate = dao.getEm().getDelegate();
//			System.out.println(delegate);
		}
	}
	
	private static final Logger log = LoggerFactory.getLogger(DataCollector.class);

	public final Map<String, PackageEn> packageEnPool = new ConcurrentHashMap<String, PackageEn>(1000);
	public final ConcurrentHashMap<String, AtomicReference<ClassEn>> classEnPool = new ConcurrentHashMap<String, AtomicReference<ClassEn>>(21000);
	public final Collection<BaseEn> roots = Collections.synchronizedCollection(new LinkedHashSet<BaseEn>());
	public final DataCollector base;
	
	public DataCollector() {
		this.base = null;
//		this(null);
	}
	
//	public DataCollector(final DataCollector _base) {
//		this.base = _base;
//	}
	
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
			.filter(ac-> {
				if (be instanceof ClassEn) {
					ClassEn ce = (ClassEn)be;
					return ac != ce.clazz;
				}
				return true;
			})
			.map(this::processClass)
			.filter(ce -> ce != null)
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
			final String clzName = Reflects.checkClzName(clz);
			
			_ce = getClassEnFromCache(clzName);
			if (_ce != null) {
				return _ce;
			}
			
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
				if (_ce.enclosing != null)
					_ce.enclosing.children.add(_ce);
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
	
	public static Map<String, SourceEn> loadSource(final File srcZip) {
		Map<String, SourceEn> reMap = new HashMap<String, SourceEn>();
		if (!(srcZip != null && srcZip.isFile() && srcZip.canRead())) {
			return reMap;
		}
		
		final JarEn je = new JarEn();
		je.name = srcZip.getName();
		
		try (ZipFile zf = new ZipFile(srcZip)) {
			zf.stream()
				.filter(ze->!ze.isDirectory())
				.filter(ze->ze.getName().endsWith("java"))
				.parallel()
				.forEach(ze -> {
					try {
						SourceEn src = new SourceEn(ze.getName());
						InputStream is = zf.getInputStream(ze);
						src.source = IOUtils.toString(is);
						src.jar = je;
						reMap.put(src.clzName, src);
					} catch (Exception e) {
						e.printStackTrace();
					} 
				} );
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return reMap;
	}

	public static List<String> associateByNativeSql(BaseEn be) {
		if (be == null) return Collections.emptyList();
		
		List<String> sb = new LinkedList<String>();
		
		if (be instanceof ClassEn) {
			ClassEn ce = (ClassEn) be;
			ClassEn superClzEn = ce.superClz;
			if (superClzEn != null) {
				sb.add(String.format("update class_en set super=%d where id=%d;", superClzEn.id, ce.id));
			}
			
			ce.annotations.forEach(ae->{
				sb.add(String.format("insert into annotations (base_en_id, annotation_en_id) values (%d, %d);", be.id, ae.id));
			});
			
			ce.infs.forEach((inf) -> {
				sb.add(String.format(
						"insert into interfaces (implement_en_id, interface_en_id) values (%d,%d);",
						ce.id, inf.id));
			});
		}
		
		if (be instanceof MethodEn) {
			MethodEn me = (MethodEn) be;
			if (me.method instanceof Method) {
				Method md = (Method)me.method;
				if (md.getReturnType() != null) {
					sb.add(String.format("update method_en set return_clz_id=%d where id=%d;",
							me.returnClass.id, 
							me.id));
				}
				
			}
			
			me.annotations.forEach(ae->{
				sb.add(String.format("insert into annotations (base_en_id, annotation_en_id) values (%d, %d);", be.id, ae.id));
			});
			
			me.exceptionClzz.forEach(exClz->{
				sb.add(String.format("insert into exceptions (method_en_id, exception_en_id) values (%d,%d);", 
						me.id, 
						exClz.id));
			});
		}
		
		if (be instanceof FieldEn) {
			FieldEn fe = (FieldEn) be;
			sb.add(String.format("update field_en set field_clz_id=%d where id=%d;", 
					fe.fieldType.id, 
					fe.id));
			
			fe.annotations.forEach(ae->{
				sb.add(String.format("insert into annotations (base_en_id, annotation_en_id) values (%d, %d);", be.id, ae.id));
			});
		}
		
		if (be instanceof ParameterEn) {
			ParameterEn pe = (ParameterEn) be;
			sb.add(String.format("update param_en set param_clz_id=%d where id=%d;", 
					pe.paramType.id, 
					pe.id));
			
			pe.annotations.forEach(ae->{
				sb.add(String.format("insert into annotations (base_en_id, annotation_en_id) values (%d, %d);", be.id, ae.id));
			});
		}
		
		return sb;
	}

	public static void traverse(BaseEn be,
			Predicate<BaseEn> threshold,
			Consumer<BaseEn> act, 
			Consumer<BaseEn> _act) {

		System.out.println(be);
		if (!threshold.test(be)) return;
		
		act.accept(be);
		be.children.forEach(en -> traverse(en, threshold, act, _act));
		_act.accept(be);
	}
	
	public static void traverse(BaseEn be,
			Predicate<BaseEn> threshold,
			Function<BaseEn, Collection<? extends BaseEn>> to,
			Consumer<BaseEn> act, 
			Consumer<BaseEn> _act) {

		if (threshold != null && !threshold.test(be)) {
			System.out.println(be);
			return;
		}
		
		if (act != null) act.accept(be);
		
		to.apply(be).stream()
			.filter(_be->_be != be)
			.forEach(en -> traverse(en, threshold, to, act, _act));
		
		if (_act != null) _act.accept(be);
	}
	
	@Test
	public void testObject() {
		DataCollector dc = new DataCollector();
		dc.processClass(Target.class);
		ClassEn ce = dc.getClassEnFromCache(Target.class.getName());
		log.info(ce.toString());
	}

	@Test
	public void testCollectRawData() {
		List<Class<?>> clzzList = new LinkedList<>();
		File _f = new File(String.format("%s/lib/rt.jar", SystemUtils.JAVA_HOME));
		clzzList.addAll(Reflects.extractClazz(_f));

		System.out.println("load source for each class");
		final Map<String, SourceEn> srcEnPool = loadSource(
				Paths.get(SystemUtils.JAVA_HOME).getParent().resolve("src.zip").toFile());

		System.out.println(clzzList.size());
		System.out.println(srcEnPool.size());

		clzzList.stream().parallel().forEach(this::processClass);

		this.classEnPool.values().stream().parallel().map(AtomicReference<ClassEn>::get).forEach(ce -> {
			ce.source = srcEnPool.get(ce.name);
		} );

		System.out.println(this.classEnPool.get(Object.class.getName()).get().source);
		final Set<BaseEn> queue = new LinkedHashSet<BaseEn>();
		
		
		List<ClassEn> ceList = classEnPool.values().stream().map(AtomicReference::get).collect(Collectors.toList());

		ceList.stream().filter(ce->ce.clazz.isAnnotation()).forEach(ce->{
			organizeClasses(ce, queue);
		});
		System.out.println("annotations are stored");
		
		roots.forEach(root->{
			organizeClasses(root, queue);
		});
		System.out.println("roots are stored");
		
		ceList.forEach(ce->{
			organizeClasses(ce, queue);
		});
		System.out.println("classes are stored");
		
		ceList.forEach(ce->{
			organizeMembers(ce, queue);
		});
		
//		queue.forEach(System.out::println);
		System.out.println(roots.size());
		System.out.println(queue.size());

		ReflectDao dao = ReflectDao.threadLocal.get();
		dao.beginTransaction();
		EntityManager em = dao.getEm();
		Session session = (Session) em.getDelegate();
		queue.forEach(be -> {
			System.out.println(be.id + ":\t" + be);
			session.save(be);
			session.flush();
		} );
		dao.endTransaction();
	}
	
	public void organizeClasses(final BaseEn be, final Set<BaseEn> queue) {
		if (be == null || queue.contains(be)) return;
		
		System.out.println(be.id + ":\t" + be);
		
		if (be instanceof PackageEn) {
			be.annotations.forEach(_be->organizeClasses(_be, queue));
			PackageEn pe = (PackageEn) be;
			organizeClasses(pe.enclosing, queue);
			queue.add(pe);
			pe.children.forEach(_be->organizeClasses(_be, queue));
		}
		
		if (be instanceof ClassEn) {
			ClassEn ce = (ClassEn) be;

			organizeClasses(ce.enclosing, queue);
			
			if (ce.clazz.isAnnotation()) {
				queue.add(ce);
				return;
			}
			
			be.annotations.forEach(_be->organizeClasses(_be, queue));
			organizeClasses(ce.superClz, queue);

			ce.infs.forEach(_be->organizeClasses(_be, queue));
			queue.add(ce);
//			ce.children.forEach(_be->organize(_be, queue));
		}
	}
	
	public void organizeMembers(final BaseEn be, final Set<BaseEn> queue) {
		if (be == null || queue.contains(be)) return;
		queue.add(be);
		be.children.forEach(_be->organizeMembers(_be, queue));
	}
}
