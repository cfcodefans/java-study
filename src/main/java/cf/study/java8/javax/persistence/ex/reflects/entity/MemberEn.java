package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.lang.reflect.Member;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "member_en")
@Cacheable
public class MemberEn extends BaseEn {
	
	public MemberEn() {
		category = CategoryEn.MEMBER;
	}
	
	public MemberEn(Member m, BaseEn enclosed, CategoryEn cat) {
		super(m.getName(), enclosed, cat);
		modifiers.addAll(getModifiers(m.getModifiers()));
		synthetic = m.isSynthetic();
	}
	
	public MemberEn(Member m, BaseEn enclosed) {
		this(m, enclosed, CategoryEn.MEMBER);
	}

	@ElementCollection(targetClass = Modifier.class)
	@Enumerated(EnumType.STRING)
	public Set<Modifier> modifiers = new LinkedHashSet<Modifier>();
	
	@Basic
	public boolean synthetic;
}
