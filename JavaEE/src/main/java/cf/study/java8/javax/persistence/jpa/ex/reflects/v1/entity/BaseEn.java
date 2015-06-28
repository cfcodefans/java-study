package cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity;

import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isNative;
import static java.lang.reflect.Modifier.isPrivate;
import static java.lang.reflect.Modifier.isProtected;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.lang.reflect.Modifier.isStrict;
import static java.lang.reflect.Modifier.isSynchronized;
import static java.lang.reflect.Modifier.isTransient;
import static java.lang.reflect.Modifier.isVolatile;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.collections4.CollectionUtils;

@Entity
@Table(name = "base_en", indexes = { @Index(name="name_idx", columnList = "name"), @Index(columnList="category", name="cat_idx") })
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable(false)
//@DiscriminatorColumn(name="category", discriminatorType = DiscriminatorType.STRING)
public class BaseEn {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;

	@Basic
	@Column(name="name", length=512)
	public String name;

	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name="enclosing", nullable=true)
	public BaseEn enclosing;
	
//	@Transient
	@OneToMany(cascade= {CascadeType.REFRESH}, mappedBy="enclosing")
	public Collection<BaseEn> children = CollectionUtils.synchronizedCollection(new LinkedHashSet<BaseEn>());

	@Enumerated(EnumType.STRING)
	@Column(name="category")
	public CategoryEn category = CategoryEn.DEFAULT;
	
	@ManyToMany(cascade = { CascadeType.REFRESH })
	@JoinTable(name = "annotations", 
				joinColumns = {@JoinColumn(name="base_en_id", referencedColumnName="id")},
				inverseJoinColumns = {@JoinColumn(name="annotation_en_id", referencedColumnName="id")})
	public List<ClassEn> annotations = new LinkedList<ClassEn>();
	
	@Version
	public long version;
	
	public BaseEn(String qualifiedName, BaseEn enclosing, CategoryEn cat) {
		super();
		this.name = qualifiedName;
		this.enclosing = enclosing;
		this.category = cat;
		
		if (enclosing != null)
			enclosing.children.add(this);
	}

	public BaseEn() {
	}

	public static Set<Modifier> getModifiers(int mod) {
		Set<Modifier> ms = new LinkedHashSet<Modifier>();

		if (isAbstract(mod))
			ms.add(Modifier.ABSTRACT);
		if (isFinal(mod))
			ms.add(Modifier.FINAL);
		if (isNative(mod))
			ms.add(Modifier.NATIVE);
		if (isPrivate(mod))
			ms.add(Modifier.PRIVATE);
		if (isProtected(mod))
			ms.add(Modifier.PROTECTED);
		if (isPublic(mod))
			ms.add(Modifier.PUBLIC);
		if (isStatic(mod))
			ms.add(Modifier.STATIC);
		if (isStrict(mod))
			ms.add(Modifier.STRICTFP);
		if (isSynchronized(mod))
			ms.add(Modifier.SYNCHRONIZED);
		if (isTransient(mod))
			ms.add(Modifier.TRANSIENT);
		if (isVolatile(mod))
			ms.add(Modifier.VOLATILE);

		return ms;
	}
	
	@Override
	public String toString() {
//		return Jsons.toString(this);
		return category + ": " + name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		if (id != null) return result;
		
		result = prime * result + (int) (version ^ (version >>> 32));
		
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((enclosing == null) ? 0 : enclosing.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		BaseEn other = (BaseEn) obj;
		
		if (id != null && id.equals(other.id)) {
			return true;
		}
		
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		
		if (category != other.category)
			return false;
		if (enclosing == null) {
			if (other.enclosing != null)
				return false;
		} else if (!enclosing.equals(other.enclosing))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (version != other.version)
			return false;
		return true;
	}
	
	public BaseEn clone() {
		return clone(null);
	}
	
	public BaseEn clone(BaseEn _be) {
		if (_be == null) {
			_be = new BaseEn();
		}
		
		_be.id = id;
		_be.name = name;
		_be.category = category;
		_be.version = version;
		
//		List<ClassEn> ans = _be.annotations;
//		annotations.forEach(an->{
//			ans.add(an.clone());
//		});
//		
		Collection<BaseEn> children2 = _be.children;
		children.stream().map(BaseEn::clone).forEach(children2::add);
//		
//		if (enclosing != null)
//			_be.enclosing = enclosing.clone();
		
		return _be;
	}
}
