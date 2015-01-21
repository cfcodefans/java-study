package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.TypeVariable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class MethodEn extends MemberEn {

	@OneToMany
	public List<ClazzEn> parameterTypes = new LinkedList<ClazzEn>();
	
	@OneToMany
	public List<ClazzEn> exceptionTypes = new LinkedList<ClazzEn>();
	
	public TypeVariable<?>[] getTypeParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Class<?>[] getParameterTypes() {
		return ClazzEn.Factory.toArray(parameterTypes); 
	}

	
	public Class<?>[] getExceptionTypes() {
		return ClazzEn.Factory.toArray(exceptionTypes); 
	}

	
	public String toGenericString() {
		return null;
	}
	
	public Annotation[][] getParameterAnnotations() {
		return null;
	}

	public AnnotatedType getAnnotatedReturnType() {
		// TODO Auto-generated method stub
		return null;
	}

}
