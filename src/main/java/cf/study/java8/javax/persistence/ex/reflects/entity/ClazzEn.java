package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.LinkedList;
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
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class ClazzEn implements DeclaredType {
	
	public ClazzEn() {
	}
	
	public ClazzEn(Class<?> cls) {
		
	}

	@Id
	public long id;
	
	@OneToOne(cascade = { CascadeType.REFRESH })
	public TypeElementEn element;

	@Enumerated(EnumType.STRING)
	public TypeKind kind;
	
	@OneToOne
	public ClazzEn enclosingType;
	
	@OneToMany
	public List<ClazzEn> typeArguments = new LinkedList<ClazzEn>();
	
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
		return element;
	}

	public TypeMirror getEnclosingType() {
		return enclosingType;
	}

	public List<? extends TypeMirror> getTypeArguments() {
		return typeArguments;
	}
	
	public Class<?> getClazz() {
		try {
			return Class.forName(element.getQualifiedName().toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?>[] toArray(Collection<ClazzEn> col) {
		return col.stream().map((clazzEn)->clazzEn.getClazz()).toArray(Class<?>[]::new);
	}
	
//	public static List<ClazzEn> toList(Class<?>...clss) {
//		
//	}
}