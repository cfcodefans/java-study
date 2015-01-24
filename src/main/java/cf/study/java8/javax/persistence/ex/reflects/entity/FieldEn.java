package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.lang.reflect.Field;

import javax.persistence.Entity;

@Entity
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

}
