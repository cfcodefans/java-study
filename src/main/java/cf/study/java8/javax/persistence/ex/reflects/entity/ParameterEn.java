package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.lang.reflect.Parameter;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "param_en")
public class ParameterEn extends MemberEn {

	@ManyToOne(cascade = { CascadeType.REFRESH })
	public ClassEn paramType;
	
	@Basic
	public boolean isArray;
	
	@Basic
	public Boolean isVarArgs;
	
	@Transient transient public Parameter parameter;
	
	public ParameterEn() {
		category = CategoryEn.PARAMETER;
	}
	
	public ParameterEn(Parameter param) {
		this();
		this.parameter = param;
		
		name = param.getName();
		modifiers.addAll(getModifiers(param.getModifiers()));
		synthetic = param.isSynthetic();
		isVarArgs = param.isVarArgs();
		
		isArray = param.getType().isArray();
	}
	
	public ParameterEn(Parameter param, MethodEn _enclosing) {
		this(param);
		this.enclosing = _enclosing;
		if (_enclosing != null)
			_enclosing.children.add(this);	
	}
}
