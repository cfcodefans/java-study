package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.lang.reflect.Member;

import javax.lang.model.element.Modifier;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class MemberEn implements Member {
	
	@Id
	public long id;
	
	@ManyToOne
	public ClazzEn declaringClass;
	
	@Basic
	public String name;
	
	@Enumerated(EnumType.STRING)
	public Modifier modifier;
	
	@Basic
	public boolean synthetic;
	
	public Class<?> getDeclaringClass() {
		return declaringClass.getClazz();
	}
	
	public String getName() {
		return name;
	}
	
	public int getModifiers() {
		return modifier.ordinal();
	}
	
	public boolean isSynthetic() {
		return synthetic;
	}

}
