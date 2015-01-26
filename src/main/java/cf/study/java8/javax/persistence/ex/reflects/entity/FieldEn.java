package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.lang.reflect.Field;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
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
	
	public FieldEn(Field _field, ClassEn _enclosing) {
		super(_field, _enclosing, CategoryEn.FIELD);
		this.field = _field;
	}
	
	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name="field_clz_id", nullable=true)
	public ClassEn fieldType;
}
