package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class ElementEn implements Element {

	@Id
	public long id;
	
	@Basic
	public String simpleName;

	@Basic
	public String modifierStr;

	@Enumerated(EnumType.STRING)
	public ElementKind kind;

	@ManyToOne(cascade = { CascadeType.REFRESH }, optional = true)
	public ElementEn enclosingElement;

	@OneToMany(cascade = { CascadeType.REFRESH })
	public List<ElementEn> enclosedElements = new LinkedList<ElementEn>();

	public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
		return null;
	}

	public TypeMirror asType() {
		return null;
	}

	public ElementKind getKind() {
		return kind;
	}

	public Set<Modifier> getModifiers() {
		return null;
	}

	public Name getSimpleName() {
		return new NameImpl(simpleName);
	}

	public Element getEnclosingElement() {
		return enclosingElement;
	}

	public List<? extends Element> getEnclosedElements() {
		return enclosedElements;
	}

	public List<? extends AnnotationMirror> getAnnotationMirrors() {
		return null;
	}

	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		return null;
	}

	public <R, P> R accept(ElementVisitor<R, P> v, P p) {
		return null;
	}

}
