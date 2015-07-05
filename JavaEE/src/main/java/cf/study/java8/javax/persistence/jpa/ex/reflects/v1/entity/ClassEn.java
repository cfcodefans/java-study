package cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.persistence.CascadeType;
import javax.persistence.Column;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import cf.study.java8.lang.reflect.Reflects;

@Entity
@Table(name = "class_en")
//@Cacheable(false)
public class ClassEn extends BaseEn {

	public static Class<?> getEnclossingClz(Class<?> clz) {
		if (clz == null) return null;
		if (clz.getEnclosingClass() != null) return clz.getEnclosingClass();
		if (clz.getEnclosingMethod() != null) return clz.getEnclosingMethod().getDeclaringClass();
		if (clz.getEnclosingConstructor() != null) return clz.getEnclosingConstructor().getDeclaringClass();
		
		return null;
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
	@JoinTable(name="clz_types", joinColumns = {@JoinColumn(name="clz_en_id", referencedColumnName="id")})
	@Column(name="type")
	public Set<TypeEn> types = new HashSet<TypeEn>();
	
	@ElementCollection(targetClass = Modifier.class)
	@Enumerated(EnumType.STRING)
	@JoinTable(name="clz_modifiers", joinColumns = {@JoinColumn(name="clz_en_id", referencedColumnName="id")})
	@Column(name="modifier")
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
	
	@ManyToOne(cascade= {CascadeType.REFRESH, CascadeType.PERSIST})
	@JoinColumn(name="source", nullable=true)
	public SourceEn source;

	private static final Logger log = Logger.getLogger(ClassEn.class);

	public void loadClass() {
		clazz = Reflects.loadClass(name);
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
		// });

		return _ce;
	}
}
