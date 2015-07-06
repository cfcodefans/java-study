package cf.study.data.mining.entity;

import java.lang.reflect.Member;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

@Entity
@Table(name = "member_en")
//@Cacheable(false)
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
	@JoinTable(name="member_modifiers", joinColumns = {@JoinColumn(name="member_en_id", referencedColumnName="id")})
	@Column(name="modifier")
	public Set<Modifier> modifiers = new LinkedHashSet<Modifier>();
	
	@Basic
	public boolean synthetic;
	
	public MemberEn clone() {
		return clone(null);
	}
	
	public MemberEn clone(MemberEn _me) {
		if (_me == null) {
			_me = new FieldEn();
		}
		
		_me = (MemberEn)super.clone(_me);
		_me.modifiers.addAll(modifiers);
		_me.synthetic = synthetic;
		
		return _me;
	}
}
