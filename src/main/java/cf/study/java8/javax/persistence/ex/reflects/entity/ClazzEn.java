package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;

@Entity
public class ClazzEn implements DeclaredType {

	@OneToOne(cascade = { CascadeType.REFRESH })
	public ElementEn element;

	@Enumerated(EnumType.STRING)
	public TypeKind kind;
	
	public TypeKind getKind() {
		return kind;
	}

	public <R, P> R accept(TypeVisitor<R, P> v, P p) {
		return null;
	}

	public List<? extends AnnotationMirror> getAnnotationMirrors() {
		return null;
	}

	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		return null;
	}

	public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
		return null;
	}

	public Element asElement() {
		return null;
	}

	public TypeMirror getEnclosingType() {
		return null;
	}

	public List<? extends TypeMirror> getTypeArguments() {
		return null;
	}

}
