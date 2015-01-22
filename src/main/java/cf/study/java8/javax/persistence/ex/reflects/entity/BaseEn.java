package cf.study.java8.javax.persistence.ex.reflects.entity;

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

import java.util.LinkedHashSet;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import misc.Jsons;

@Entity
@Table(name = "base_en", indexes = { @Index(columnList = "name") })
@Inheritance(strategy = InheritanceType.JOINED)
public class BaseEn {
	@Id
	public long id;

	@Basic
	@Column(name="name", length=512)
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
		return Jsons.toString(this);
	}
}
