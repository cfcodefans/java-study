package cf.study.java8.javax.persistence.ex.reflects;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;

import cf.study.java8.javax.persistence.dao.BaseDao;
import cf.study.java8.javax.persistence.ex.reflects.entity.BaseEn;
import cf.study.java8.javax.persistence.ex.reflects.entity.ClassEn;
import cf.study.java8.javax.persistence.ex.reflects.entity.FieldEn;
import cf.study.java8.javax.persistence.ex.reflects.entity.MethodEn;
import cf.study.java8.javax.persistence.ex.reflects.entity.PackageEn;

@ApplicationScoped
public class ReflectDao extends BaseDao<Object> {

	public ReflectDao() {
//		super(JpaModule.getEntityManager());
	}
	
	
	public ClassEn create(Class<?> cls) {
		if (cls == null) return null;
		
		ClassEn ce = getEnByClass(cls);
		if (ce != null) return ce;
		
		BaseEn enclosing = null;
		Class<?> enclosingClass = cls.getEnclosingClass();
		if (enclosingClass != null) {
			enclosing = create(enclosingClass);
		}
		
		ClassEn _ce = new ClassEn(cls, enclosing);
		_ce.pkg = create(cls.getPackage());
		
		System.out.println("creating " + cls.getSimpleName());
		ce = (ClassEn)super.create(_ce);
		//em.flush();
		return ce;
	}
	
	
	public FieldEn create(Field field) {
		if (field == null) return null;
		
		FieldEn fe = getEnByField(field);
		if (fe != null) return fe;
	
		fe = new FieldEn(field);
		fe.enclosd = getEnByClass(field.getDeclaringClass());
		fe = (FieldEn) super.create(fe);
		//em.flush();
		return fe;
	}
	
	
	public MethodEn create(Method method) {
		if (method == null) return null;
		
		MethodEn me = getEnByMethod(method);
		if (me != null) return me;
		
		ClassEn ce = create(method.getDeclaringClass());
		MethodEn _me = new MethodEn(method, ce);
		
		_me.returnClass = create(method.getReturnType());
		Stream.of(method.getParameterTypes()).forEach((clz)->{_me.paramsClzz.add(create(clz));});
		Stream.of(method.getExceptionTypes()).forEach((clz)->{_me.exceptionClzz.add(create(clz));});
		me = (MethodEn) super.create(_me);
		//em.flush();
		
		return me;
	}

	
	public PackageEn create(Package pkg) {
		if (pkg == null) return null;
		
		PackageEn _pkg = getEnByPackage(pkg);
		if (_pkg == null) {
			PackageEn created = (PackageEn)super.create(new PackageEn(pkg));
			//em.flush();
			return created;
		}
		
		return _pkg;
	}
	
	public ClassEn getEnByClass(Class<?> cls) {
		return (ClassEn)super.findOneEntity("select ce from ClassEn ce where ce.name=?1", cls.getName());
	}
	
	public FieldEn getEnByField(Field field) {
		FieldEn fe = (FieldEn)super.findOneEntity("select fe from FieldEn fe where fe.name=?1", field.getName());
		return fe;
	}
	
	public MethodEn getEnByMethod(Method method) {
		MethodEn me = (MethodEn)super.findOneEntity("select me from MethodEn me where me.name=?1", method.getName());
		return me;
	}
	
	public PackageEn getEnByPackage(Package pkg) {
		PackageEn _pkg = (PackageEn)super.findOneEntity("select pe from PackageEn pe where pe.name=?1", pkg.getName());
		return _pkg;
	}


	public void createClazz(Class<?> clz) {
		beginTransaction();
		create(clz);
		Stream.of(clz.getFields()).forEach((field)->{create(field);});
		Stream.of(clz.getMethods()).forEach((method)->{create(method);});
		em.flush();
		endTransaction();
	}
}
