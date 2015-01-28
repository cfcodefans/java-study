package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

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
	
	public synchronized FieldEn assembleWholeFieldEn(Field field) {
		if (field == null) return null;
		
		FieldEn fe = new FieldEn(field, assembleWholeClassEn(field.getDeclaringClass()));
		
		assembleWholeAnnotation(fe, field);
		
		fe.fieldType = assembleWholeClassEn(field.getType());
		
		return fe;
	}

	public synchronized ClassEn assembleWholeClassEn(Class<?> clz) {
		if (clz == null)
			return null;

		String clzName = clz.getName();

		ClassEn _ce = ctx.inflatedClassEnPool.get(clzName);
		if (_ce != null) {
			return _ce;
		}

		Class<?> enclosingClz = getEnclossingClz(clz);

		ClassEn ce = new ClassEn(clz, assembleWholeClassEn(enclosingClz));

		ce.pkg = assembleWholePackageEn(clz.getPackage());
		
		assembleWholeAnnotation(ce, clz);
		
		ce.superClz = assembleWholeClassEn(clz.getSuperclass());
		
		Stream.of(clz.getInterfaces()).forEach((infClz)->{
			ce.infs.add(assembleWholeClassEn(infClz));
		});
		
		Stream.of(clz.getDeclaredFields()).forEach((fd)->{
			assembleWholeFieldEn(fd);
		});

		Stream.of(clz.getDeclaredConstructors()).forEach((con)->{
			assembleWholeMethodEn(con);
		});
		
		Stream.of(clz.getDeclaredMethods()).forEach((method)->{
			assembleWholeMethodEn(method);
		});
		
		return ce;
	}
	
	public synchronized MethodEn assembleWholeMethodEn(Executable exe) {
		if (exe == null) return null;
		
		MethodEn me = new MethodEn(exe, assembleWholeClassEn(exe.getDeclaringClass()));
		
		assembleWholeAnnotation(me, exe);
		
		if (exe instanceof Method) {
			Method method = (Method) exe;
			me.returnClass = assembleWholeClassEn(method.getReturnType());
		} 
		
		Stream.of(exe.getExceptionTypes()).forEach((exClz)->{
			me.exceptionClzz.add(assembleWholeClassEn(exClz));
		});
		
		Stream.of(exe.getParameters()).forEach((param)->{
			assembleWholeParameterEn(param, me);
		});
		
		return me;
	}
	
	public synchronized ParameterEn assembleWholeParameterEn(Parameter param, MethodEn me) {
		if (param == null || me == null) return null;
		
		ParameterEn pe = new ParameterEn(param, me);
		
		assembleWholeAnnotation(pe, param);
		
		pe.paramType = assembleWholeClassEn(param.getType());
		
		return pe;
	}

	public synchronized PackageEn assembleWholePackageEn(Package _package) {
		if (_package == null)
			return null;

		String pkgName = _package.getName();

		PackageEn _pe = ctx.packageEnPool.get(pkgName);
		if (_pe != null)
			return _pe;

		PackageEn pe = new PackageEn(_package, assembleWholePackageEn(getParentPkg(_package)));
		
		ctx.packageEnPool.put(pkgName, pe);

		assembleWholeAnnotation(pe, _package);

		return pe;
	}

	public synchronized void assembleWholeAnnotation(BaseEn be, AnnotatedElement ae) {
		if (be == null || ae == null)
			return;
		
		Stream.of(ae.getDeclaredAnnotations()).forEach((an) -> {
			be.annotations.add(assembleWholeClassEn(an.annotationType()));
		});
	}

	public static Package getParentPkg(Package _package) {
		if (_package == null)
			return null;
		
		return Package.getPackage(StringUtils.substringBeforeLast(_package.getName(), "."));
	}
}
