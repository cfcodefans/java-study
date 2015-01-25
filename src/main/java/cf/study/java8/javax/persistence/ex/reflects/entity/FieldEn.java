package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.lang.reflect.Field;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "field_en")
public class FieldEn extends MemberEn {
	
	@Transient
	transient public Field field;
	
	public FieldEn() {
		category = CategoryEn.FIELD;
	}
	
	public FieldEn(Field _field) {
		this();
		this.field = _field;
		name = _field.getName();
		modifiers = BaseEn.getModifiers(_field.getModifiers());
		synthetic = _field.isSynthetic();
	}

	public FieldEn(Field field, ClassEn _enclosing) {
		this(field);
		super.enclosing = _enclosing;
	}
	
	@ManyToOne(cascade = { CascadeType.REFRESH })
	public ClassEn fieldType;
}
