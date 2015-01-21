package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.lang.reflect.Member;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class MemberEn extends BaseEn {
	
	public MemberEn() {
	}
	
	public MemberEn(Member m, BaseEn enclosed) {
		super(m.getName(), enclosed);
		modifiers.addAll(getModifiers(m.getModifiers()));
		synthetic = m.isSynthetic();
	}

	@ElementCollection(targetClass = Modifier.class)
	@Enumerated(EnumType.STRING)
	public Set<Modifier> modifiers;
	
	@Basic
	public boolean synthetic;
}
