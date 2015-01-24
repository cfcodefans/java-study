package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.lang.reflect.Field;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "field_en")
public class FieldEn extends MemberEn {
	public FieldEn() {
		category = CategoryEn.FIELD;
	}
	
	public FieldEn(Field field) {
		this();
		name = field.getName();
		modifiers = BaseEn.getModifiers(field.getModifiers());
		synthetic = field.isSynthetic();
	}

	public FieldEn(Field field, ClassEn _enclosing) {
		this(field);
		super.enclosing = _enclosing;
	}
	
	@ManyToOne(cascade = { CascadeType.REFRESH })
	public ClassEn fieldType;
}
