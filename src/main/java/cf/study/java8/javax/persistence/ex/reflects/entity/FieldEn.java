package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.lang.reflect.Field;
import java.util.Optional;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "field_en")
@Cacheable(false)
public class FieldEn extends MemberEn {
	
	@Transient
	transient public Field field;
	
	public FieldEn() {
		category = CategoryEn.FIELD;
	}
	
	public FieldEn(Field _field, ClassEn _enclosing) {
		super(_field, _enclosing, CategoryEn.FIELD);
		this.field = _field;
	}
	
	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name="field_clz_id", nullable=true)
	public ClassEn fieldType;
	
	public static FieldEn instance(ClassEn ce, Field field) {
		if (ce == null || field == null) return null;
		
		Optional<BaseEn> fieldEnOpt = ce.children.stream().filter(be->(be instanceof FieldEn && field.getName().equals(be.name))).findFirst();
		FieldEn fe = fieldEnOpt.isPresent() ? (FieldEn)fieldEnOpt.get() : new FieldEn(field, ce);
		fe.field = field;
		return fe;
	}
	
	public FieldEn clone() {
		return clone(null);
	}
	
	public FieldEn clone(FieldEn _fe) {
		if (_fe == null) {
			_fe = new FieldEn();
		}
		
		_fe = (FieldEn)super.clone(_fe);
		
		_fe.field = field;
//		_fe.fieldType = fieldType.clone();
		
		return _fe;
	}
}
