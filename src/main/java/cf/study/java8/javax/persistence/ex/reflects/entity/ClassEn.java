package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import misc.MiscUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

@Entity
@Table(name = "class_en")
@Cacheable
public class ClassEn extends BaseEn {

	public static Class<?> getEnclossingClz(Class<?> clz) {
		if (clz == null) return null;
		if (clz.getEnclosingClass() != null) return clz.getEnclosingClass();
		if (clz.getEnclosingMethod() != null) return clz.getEnclosingMethod().getDeclaringClass();
		if (clz.getEnclosingConstructor() != null) return clz.getEnclosingConstructor().getDeclaringClass();
		
		return null;
	}
	
	public static String checkClzName(Class<?> clz) {
		if (clz == null) return StringUtils.EMPTY;
		String clzName = clz.getName();
		
		if (primitives.containsKey(clzName)) {
			clz = primitives.get(clzName);
			clzName = clz.getName();
		}
		return clzName;
	}
	
	
	@Transient
	public transient Class<?> clazz;
	
	public ClassEn() {
		category = CategoryEn.CLASS;
		if (StringUtils.isNotBlank(name)) {
			try {
				clazz = Class.forName(name, false, ClassLoader.getSystemClassLoader());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public ClassEn(Class<?> clz, BaseEn enclosing) {
		super(clz.isArray() ? clz.getComponentType().getName() : clz.getName(), enclosing, CategoryEn.CLASS);
		clazz = clz;
		types.addAll(TypeEn.by(clz));
		modifiers.addAll(getModifiers(clz.getModifiers()));
	}
	
	@ElementCollection(targetClass = TypeEn.class)
	@Enumerated(EnumType.STRING)
	public Set<TypeEn> types = new HashSet<TypeEn>();
	
	@ElementCollection(targetClass = Modifier.class)
	@Enumerated(EnumType.STRING)
	public Set<Modifier> modifiers = new HashSet<Modifier>();
	
	@ManyToOne(cascade= {CascadeType.REFRESH})
	@JoinColumn(name="package", nullable=true)
	public PackageEn pkg;
	
	@ManyToOne(cascade= {CascadeType.REFRESH})
	@JoinColumn(name="super", nullable=true)
	public ClassEn superClz;
	
	@ManyToMany(cascade= {CascadeType.REFRESH})
	@JoinTable(name="interfaces", joinColumns = {@JoinColumn(name="implement_en_id", referencedColumnName="id")},
	                              inverseJoinColumns = {@JoinColumn(name="interface_en_id", referencedColumnName="id")})
	public Set<ClassEn> infs = new HashSet<ClassEn>();

	private static final Logger log = Logger.getLogger(ClassEn.class);

	@SuppressWarnings("unchecked")
	public static final Map<String, Class<?>> primitives = MiscUtils.map(
				"void", Void.class,
				"long", long.class,
				"int", int.class,
				"char", char.class,
				"boolean", boolean.class,
				"byte", byte.class,
				"double", double.class,
				"short", short.class,
				"float", float.class
			);

	public void loadClass() {
		if (primitives.containsKey(name)) {
			clazz = primitives.get(name);
		} else {
			try {
				clazz = Class.forName(name, false, ClassLoader.getSystemClassLoader());
			} catch (ClassNotFoundException e) {
				log.error("class not found: " + name);
			}
		}
	}
	
	public ClassEn clone() {
		return clone(null);
	}
	
	public ClassEn clone(ClassEn _ce) {
		if (_ce == null) {
			_ce = new ClassEn();
		}
		
		_ce = (ClassEn)super.clone(_ce);
		
		_ce.clazz = clazz;
		_ce.types.addAll(types);
		_ce.modifiers.addAll(modifiers);
		
//		if (pkg != null)
//			_ce.pkg = pkg.clone();
//		
//		_ce.superClz = superClz.clone();
//		
//		Set<ClassEn> infs2 = _ce.infs;
//		infs.stream().forEach(inf->{
//			infs2.add(inf.clone());
//		});
		
		return _ce;
	}
}
