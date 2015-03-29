package cf.study.java8.javax.persistence.cdi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

@Inherited
@Target({ ElementType.METHOD, ElementType.TYPE })
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {
	
}