package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.Name;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class TypeElementEn extends ElementEn implements TypeElement {
	
	public TypeElementEn() {
	}
	
	public TypeElementEn(Class<?> clz) {
		qualifiedName = clz.getName();
	}
	
	@Basic
	public String qualifiedName; 

	@OneToOne
	public ClazzEn clazz;
	
	@OneToOne(cascade= {CascadeType.REFRESH})
	public ClazzEn superClazz;
	
	@OneToMany(cascade= {CascadeType.REFRESH})
	public List<ClazzEn> interfaces = new LinkedList<ClazzEn>();

	@Override
	public NestingKind getNestingKind() {
		return null;
	}

	@Override
	public Name getQualifiedName() {
		return new NameImpl(qualifiedName);
	}

	@Override
	public TypeMirror getSuperclass() {
		return superClazz;
	}

	@Override
	public List<? extends TypeMirror> getInterfaces() {
		return interfaces;
	}

	@Override
	public List<? extends TypeParameterElement> getTypeParameters() {
		return null;
	}
}
