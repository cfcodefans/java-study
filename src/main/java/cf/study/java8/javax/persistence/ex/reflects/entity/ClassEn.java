package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Modifier;
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

import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name = "class_en")
public class ClassEn extends BaseEn {

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
	
	@ManyToOne(cascade= {CascadeType.REFRESH, CascadeType.PERSIST})
	@JoinColumn(name="super", nullable=true)
	public ClassEn superClz;
	
	@ManyToMany(cascade= {CascadeType.REFRESH, CascadeType.PERSIST})
	@JoinTable(name="infs")
	public Set<ClassEn> infs = new HashSet<ClassEn>();
	
}
