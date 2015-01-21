package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.util.List;

import javax.lang.model.element.Name;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class TypeElementEn extends ElementEn implements TypeElement {
	
	@Basic
	public String qualifiedName; 

	@OneToOne
	public ClazzEn clazz;

	@Id
	public long id;
	
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
		return null;
	}

	@Override
	public List<? extends TypeMirror> getInterfaces() {
		return null;
	}

	@Override
	public List<? extends TypeParameterElement> getTypeParameters() {
		return null;
	}
}
