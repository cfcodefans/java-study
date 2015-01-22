package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class ClassEn extends BaseEn {

	public ClassEn() {
	}
	
	public ClassEn(Class<?> clz, BaseEn enclosing) {
		super(clz.getName(), enclosing);
		types.addAll(TypeEn.by(clz));
		modifiers.addAll(getModifiers(clz.getModifiers()));
	}
	
	public ClassEn(Class<?> clz) {
		this(clz, null);
	}
	
	@ElementCollection(targetClass = TypeEn.class)
//	@CollectionTable(name = "days", joinColumns = @JoinColumn(name = "rule_id"))
//	@Column(name = "daysOfWeek", nullable = false)
	@Enumerated(EnumType.STRING)
	public Set<TypeEn> types = new HashSet<TypeEn>();
	
	@ElementCollection(targetClass = Modifier.class)
	@Enumerated(EnumType.STRING)
	public Set<Modifier> modifiers = new HashSet<Modifier>();
	
	@ManyToOne(cascade= {CascadeType.REFRESH})
	public PackageEn pkg;
	
	@ManyToOne(cascade= {CascadeType.REFRESH})
	public ClassEn superClz;
	
	@ManyToMany(cascade= {CascadeType.REFRESH})
	@JoinTable(name="infs")
	public Set<ClassEn> infs = new HashSet<ClassEn>();
	
}
