package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class BaseEn {
	@Id
	public long id;

	@Basic
	public String name;

	@ManyToOne(cascade = { CascadeType.REFRESH })
	public BaseEn enclosd;

	public BaseEn(String qualifiedName, BaseEn enclosd) {
		super();
		this.name = qualifiedName;
		this.enclosd = enclosd;
	}
	
	public BaseEn() {
	}
	
	public static Set<Modifier> getModifiers(int mod) {
		Set<Modifier> ms = new LinkedHashSet<Modifier>();
		
		java.lang.reflect.Modifier m = new java.lang.reflect.Modifier();
		if (m.isAbstract(mod)) ms.add(Modifier.ABSTRACT);
		if (m.isFinal(mod)) ms.add(Modifier.FINAL);
		if (m.isNative(mod)) ms.add(Modifier.NATIVE);
		if (m.isPrivate(mod)) ms.add(Modifier.PRIVATE);
		if (m.isProtected(mod)) ms.add(Modifier.PROTECTED);
		if (m.isPublic(mod)) ms.add(Modifier.PUBLIC);
		if (m.isStatic(mod)) ms.add(Modifier.STATIC);
		if (m.isStrict(mod)) ms.add(Modifier.STRICTFP);
		if (m.isSynchronized(mod)) ms.add(Modifier.SYNCHRONIZED);
		if (m.isTransient(mod)) ms.add(Modifier.TRANSIENT);
		if (m.isVolatile(mod)) ms.add(Modifier.VOLATILE);
		
		return ms;
	}
}
