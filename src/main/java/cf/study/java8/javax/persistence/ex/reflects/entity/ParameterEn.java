package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.lang.reflect.Parameter;
import java.util.Optional;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "param_en")
@Cacheable(false)
public class ParameterEn extends MemberEn {

	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name="param_clz_id", nullable=true)
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
	
	public static ParameterEn instance(MethodEn me, Parameter param) {
		if (me == null || param == null) return null;
		
		Optional<BaseEn> peOpt = me.children.stream().filter((pe)->(param.getName().equals(pe.name))).findFirst();
		ParameterEn pe = peOpt.isPresent() ? (ParameterEn) peOpt.get() : new ParameterEn(param, me);
		pe.parameter = param;
		return pe; 
	}
	
	public ParameterEn clone() {
		return clone(null);
	}
	
	public ParameterEn clone(ParameterEn _pe) {
		if (_pe == null) {
			_pe = new ParameterEn();
		}
		
		_pe = (ParameterEn)super.clone(_pe);
		
		_pe.parameter = parameter;
		_pe.isArray = isArray;
		_pe.isVarArgs = isVarArgs;
		
//		_pe.paramType = paramType.clone();
		return _pe;
	}
}
