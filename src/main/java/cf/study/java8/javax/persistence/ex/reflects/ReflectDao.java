package cf.study.java8.javax.persistence.ex.reflects;

import static cf.study.java8.javax.persistence.ex.reflects.entity.CategoryEn.CLASS;
import static cf.study.java8.javax.persistence.ex.reflects.entity.CategoryEn.FIELD;
import static cf.study.java8.javax.persistence.ex.reflects.entity.CategoryEn.METHOD;
import static cf.study.java8.javax.persistence.ex.reflects.entity.CategoryEn.PACKAGE;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;

import misc.MiscUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import cf.study.java8.javax.cdi.weld.WeldTest;
import cf.study.java8.javax.persistence.dao.BaseDao;
import cf.study.java8.javax.persistence.ex.reflects.entity.BaseEn;
import cf.study.java8.javax.persistence.ex.reflects.entity.ClassEn;
import cf.study.java8.javax.persistence.ex.reflects.entity.FieldEn;
import cf.study.java8.javax.persistence.ex.reflects.entity.MethodEn;
import cf.study.java8.javax.persistence.ex.reflects.entity.PackageEn;
import cf.study.java8.javax.persistence.ex.reflects.entity.ParameterEn;

@RequestScoped
public class ReflectDao extends BaseDao<Object> {

	private static final Logger log = Logger.getLogger(ReflectDao.class);

	public static final Map<Object, BaseEn> lockSet = new ConcurrentHashMap<Object, BaseEn>();

	public static final ThreadLocal<ReflectDao> threadLocal = new ThreadLocal<ReflectDao>() {
		protected ReflectDao initialValue() {
			return WeldTest.getBeanInReqScope(ReflectDao.class);
		};
	};
	
	public ReflectDao() {
		// super(JpaModule.getEntityManager());
		log.info(MiscUtils.invocationInfo());
	}
	
	public ReflectDao(EntityManager _em) {
		super(_em);
	}

	public ClassEn create(Class<?> cls) {
		if (cls == null)
			return null;

		ClassEn ce = getEnByClass(cls);
		if (ce != null)
			return ce;

		BaseEn enclosing = null;
		Class<?> enclosingClass = cls.getEnclosingClass();
		if (enclosingClass != null) {
			enclosing = create(enclosingClass);
		}

		ClassEn _ce = new ClassEn(cls, enclosing);
		_ce.pkg = create(cls.getPackage());

		_ce.superClz = create(cls.getSuperclass());
		Stream.of(cls.getInterfaces()).forEach((inf) -> {
			_ce.infs.add(create(inf));
		});

		ce = (ClassEn) super.create(_ce);
		// em.lock(_ce, LockModeType.PESSIMISTIC_READ);
		em.flush();
		return ce;
	}

	public FieldEn create(Field field, ClassEn _enclosing) {
		if (field == null)
			return null;

		FieldEn fe = null;// getEnByField(field, _enclosing);
		// if (fe != null) return fe;

		fe = new FieldEn(field, _enclosing);
		Type genericType = field.getGenericType();

		Class genericClass = Object.class;
		if (genericType instanceof Class) {
			genericClass = (Class) genericType;
		}

		fe.fieldType = create(genericClass);
		fe = (FieldEn) super.create(fe);
		em.flush();
		return fe;
	}

	public MethodEn create(Method method, ClassEn _enclosing) {
		if (method == null)
			return null;

		MethodEn me = null;
		// MethodEn me = getEnByMethod(method, _enclosing);
		// if (me != null) return me;

		// ClassEn ce = create(method.getDeclaringClass());
		MethodEn _me = new MethodEn(method, _enclosing);
		_me.paramsHash = Objects.hash(method.getParameters());

		_me.returnClass = create(method.getReturnType());
//		Stream.of(method.getParameterTypes()).forEach((clz) -> {
//			_me.paramsClzz.add(create(clz));
//		});
		Stream.of(method.getExceptionTypes()).forEach((clz) -> {
			_me.exceptionClzz.add(create(clz));
		});
		me = (MethodEn) super.create(_me);
		em.flush();

		return me;
	}

	public PackageEn create(Package pkg) {
		if (pkg == null)
			return null;

		PackageEn _pkg = getEnByPackage(pkg);
		if (_pkg == null) {
			PackageEn enclosing = null;
			String parentPkgName = StringUtils.substringBeforeLast(pkg.getName(), ".");
			if (StringUtils.isNotBlank(parentPkgName)) {
				enclosing = create(Package.getPackage(parentPkgName));
			}

			log.info(String.format("Package: %s", pkg.getName()));
			PackageEn created = (PackageEn) super.create(new PackageEn(pkg, enclosing));
			em.flush();
			return created;
		}

		return _pkg;
	}

	public ClassEn getEnByClass(Class<?> cls) {
		String hql = "select ce from ClassEn ce where ce.name=?1 and ce.category=?2";
		return (ClassEn) SimpleQueryBuilder.byHQL(hql, em).reqOne()// .setLockType(LockModeType.PESSIMISTIC_READ)
				.withPositionedParams(cls.getName(), CLASS).getOne();
	}

	public FieldEn getEnByField(Field field) {
		String hql = "select fe from FieldEn fe where fe.name=?1 and fe.category=?2 and fe.enclosing.name=?3";
		FieldEn fe = (FieldEn) SimpleQueryBuilder.byHQL(hql, em).reqOne()
				.withPositionedParams(field.getName(), FIELD, field.getDeclaringClass().getName()).getOne();
		return fe;
	}

	public FieldEn getEnByField(Field field, ClassEn _enclosing) {
		String hql = "select fe from FieldEn fe where fe.name=?1 and fe.category=?2 and fe.enclosing=?3";
		FieldEn fe = (FieldEn) SimpleQueryBuilder.byHQL(hql, em).reqOne()
				.withPositionedParams(field.getName(), FIELD, _enclosing).getOne();
		return fe;
	}

	public MethodEn getEnByMethod(Method method) {
		String hql = "select me from MethodEn me where me.name=?1 and me.category=?2 and me.enclosing.name=?3";
		MethodEn me = (MethodEn) SimpleQueryBuilder.byHQL(hql, em).reqOne()
				.withPositionedParams(method.getName(), METHOD, method.getDeclaringClass().getName()).getOne();
		return me;
	}

	public MethodEn getEnByMethod(Method method, ClassEn _enclosing) {
		String hql = "select me from MethodEn me where me.name=?1 and me.category=?2 and me.enclosing=?3";
		MethodEn me = (MethodEn) SimpleQueryBuilder.byHQL(hql, em).reqOne()
				.withPositionedParams(method.getName(), METHOD, _enclosing).getOne();
		return me;
	}

	public PackageEn getEnByPackage(Package pkg) {
		String hql = "select pe from PackageEn pe where pe.name=?1 and pe.category=?2";
		PackageEn _pkg = (PackageEn) SimpleQueryBuilder.byHQL(hql, em).reqOne()
				.withPositionedParams(pkg.getName(), PACKAGE).getOne();
		return _pkg;
	}

	public void createClazz(Class<?> clz) {
		beginTransaction();
		ClassEn _created = create(clz);
		endTransaction();
		beginTransaction();
		final ClassEn created = (ClassEn)super.refresh(_created);
		Stream.of(clz.getDeclaredFields()).forEach((field) -> {
			create(field, created);
		});
		Stream.of(clz.getDeclaredMethods()).forEach((method) -> {
			create(method, created);
		});
		em.flush();
		endTransaction();
	}
	
	public void persist(BaseEn be) {
		if (be == null) return;
		
		create(be.enclosing);
//		em.flush();
		
		if (be instanceof PackageEn) {
			PackageEn pkgEn = (PackageEn) be;
			create(pkgEn);
		}
		
		if (be instanceof ClassEn) {
			ClassEn ce = (ClassEn) be;
			create(ce.pkg);
			create(ce.superClz);
			ce.infs.forEach((_be)->{persist(_be);});
		}
		
		if (be instanceof FieldEn) {
			FieldEn fe = (FieldEn) be;
			create(fe.fieldType);
		}
		
		if (be instanceof MethodEn) {
			MethodEn me = (MethodEn) be;
			create(me.returnClass);
			me.exceptionClzz.forEach((_be)->{persist(_be);});
		}
		
		if (be instanceof ParameterEn) {
			ParameterEn pe = (ParameterEn) be;
			create(pe.paramType);
		}
		
		create(be);
//		em.flush();
		be.children.forEach((_be)->{persist(_be);});
	}
}
