package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import cf.study.java8.javax.persistence.ex.reflects.DataTests;

public class EntityAssembler {

	public static class Context {
		Map<String, PackageEn> packageEnPool = MapUtils.synchronizedMap(new LinkedHashMap<String, PackageEn>());
		Map<String, ClassEn> classEnPool = MapUtils.synchronizedMap(new LinkedHashMap<String, ClassEn>());
		Collection<BaseEn> roots = CollectionUtils.synchronizedCollection(new LinkedHashSet<BaseEn>());
		Map<String, ClassEn> inflatedClassEnPool = MapUtils.synchronizedMap(new LinkedHashMap<String, ClassEn>());

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
	private static final Logger log = Logger.getLogger(EntityAssembler.class);

	public synchronized FieldEn assembleFieldEn(Field field) {
		if (field == null)
			return null;

		FieldEn fe = new FieldEn(field, assembleClassEn(field.getDeclaringClass()));

		assembleAnnotation(fe, field);

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

		ClassEn ce = new ClassEn(clz, assembleClassEn(enclosingClz));
		
		ctx.classEnPool.put(clzName, ce);

		ce.pkg = assemblePackageEn(clz.getPackage());

		if (ce.enclosing == null) {
			ce.enclosing = ce.pkg;
		}

		if (ce.enclosing == null) {
			ctx.roots.add(ce);
		}

		assembleAnnotation(ce, clz);

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

		assembleAnnotation(me, exe);

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

		assembleAnnotation(pe, param);

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

		ctx.packageEnPool.put(pkgName, pe);

		assembleAnnotation(pe, _package);

		return pe;
	}

	public synchronized void assembleAnnotation(BaseEn be, AnnotatedElement ae) {
		if (be == null || ae == null)
			return;

		Stream.of(ae.getDeclaredAnnotations()).forEach((an) -> {
			be.annotations.add(assembleClassEn(an.annotationType()));
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

	public ClassEn inflateClassEn(ClassEn ce) {
		if (ce == null)
			return ce;

		try {
			ce.clazz = Class.forName(ce.name, false, ClassLoader.getSystemClassLoader());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		inflateAnnotations(ce);

		inflateClassEn(ce.superClz);

		ce.infs.forEach((inf) -> {
			inflateClassEn(inf);
		});

		if (ce.enclosing instanceof ClassEn) {
			ClassEn ec = (ClassEn) ce.enclosing;
			inflateClassEn(ec);
		}

		inflatePackageEn(ce.pkg);

		ce.children.stream().filter(child -> (child instanceof FieldEn)).forEach((child) -> {
			FieldEn fe = (FieldEn) child;
			inflateFieldEn(fe);
		});

		MethodEn[] meAry = ce.children.stream().filter(child -> (child instanceof MethodEn)).toArray(MethodEn[]::new);
		Stream<MethodEn> meStream = Stream.of(meAry);

		Stream.of(ce.clazz.getDeclaredMethods()).forEach(method -> {
			MethodEn _me = meStream.filter(me -> (me.isMatch(method))).findFirst().get();
			inflateMethodEn(_me, method);
		});

		Stream.of(ce.clazz.getDeclaredConstructors()).forEach(method -> {
			MethodEn _me = meStream.filter(me -> (me.isMatch(method))).findFirst().get();
			inflateMethodEn(_me, method);
		});

		return ce;
	}

	private MethodEn inflateMethodEn(MethodEn me, Executable method) {
		if (me == null)
			return me;

		inflateAnnotations(me);

		inflateClassEn(me.returnClass);

		me.exceptionClzz.forEach((ex) -> {
			inflateClassEn(ex);
		});

		me.method = method;

		List<Class<?>> paramClzz = new ArrayList<Class<?>>(me.children.size());
		me.children.forEach(child -> {
			paramClzz.add(inflateParameterEn((ParameterEn) child).paramType.clazz);
		});
		Class<?> paramClzAry[] = paramClzz.toArray(new Class<?>[0]);

		Class<?> declared = ((ClassEn) me.enclosing).clazz;

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

		inflateAnnotations(pe);

		inflateClassEn(pe.paramType);

		MethodEn me = (MethodEn) pe.enclosing;

		pe.parameter = Stream.of(me.method.getParameters()).filter(param -> pe.name.equals(param.getName()))
				.findFirst().get();

		return pe;
	}

	private FieldEn inflateFieldEn(FieldEn fe) {
		if (fe == null)
			return fe;

		ClassEn ce = (ClassEn) fe.enclosing;

		try {
			fe.field = ce.clazz.getField(fe.name);
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}

		inflateAnnotations(fe);

		inflateClassEn(fe.fieldType);

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
		ae.assembleClassEn(Object.class);
		
		ae.ctx.classEnPool.keySet().forEach(key->{
			System.out.println(key);
		});
	}
}
