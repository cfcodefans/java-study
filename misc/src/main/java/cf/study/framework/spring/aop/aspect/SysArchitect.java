package cf.study.framework.spring.aop.aspect;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import misc.MiscUtils;

@Aspect
public class SysArchitect {
	
	@PostConstruct
	public void init() {
		log.info(MiscUtils.invocationInfo());
	}
	
	public final static Logger log = LogManager.getLogger(SysArchitect.class);

	/**
	 * A join point is in the web layer if the method is defined
	 * in a type in the cf.study.framework.spring.aop.web package of any sub-package 
	 * under that
	 */
	@Pointcut("within(cf.study.framework.spring.aop.web..*)")
	public void inWebLayer() {log.info(MiscUtils.invocationInfo());}
	
	/**
	 * A join point is in the service layer if the method is defined
	 * in a type in the cf.study.framework.spring.aop.service package of any sub-package 
	 * under that
	 */
	@Pointcut("within(cf.study.framework.spring.aop.service.*.*)")
	public void inServiceLayer() {log.info(MiscUtils.invocationInfo());}
	
	/**
	 * A join point is in the service layer if the method is defined
	 * in a type in the cf.study.framework.spring.aop.dao package of any sub-package 
	 * under that
	 */
	@Pointcut("within(cf.study.framework.spring.aop.dao..*)")
	public void inDataAccessLayer() {log.info(MiscUtils.invocationInfo());}
	
	/**
	 * A business service is the execution of any method defined on a service
	 * interface. This definition assumes that interfaces are placed in the 
	 * "service" package, and that implementation types are in the sub-packages.
	 * 
	 * If you group service interfaces by functional area (for example,
	 * in packages cf.study.framework.spring.aop.def.service and cf.study.framework.spring.aop.abc.service) then
	 * the pointcut expression "execution(* cf.study.framework.spring.aop..serice.*.*(..))"
	 * could be used instead
	 * 
	 *  Alternatively, you can write the expression using the 'bean'
	 *  PCD, like so "bean(*Service)". (This assumes that you have
	 *  named your Spring service beans in a consistent fashion.)
	 */
	@Pointcut("execution(* cf.study.framework.spring..service.*.*(..))")
	public void businessService() {log.info(MiscUtils.invocationInfo());}
	
	@Pointcut("execution(* cf.study.framework.spring.service.*.*(..))")
	public void pointCutExecution() {log.info("\n\t" + MiscUtils.invocationInfo());}
	
	@Before("execution(* cf.study.framework.spring.aop.service.*.*(..))")
	public void before(JoinPoint jp) {
		log.info("\n\t" + MiscUtils.invocationInfo() + "\n\t" + jp);
	}
	
	@After("execution(* cf.study.framework.spring.aop.service.*.*(..))")
	public void after() {log.info("\n\t" + MiscUtils.invocationInfo());}
	
	/**
	 *	A data access operation is the execution of any method defined on a 
	 *	dao interface. This definition assumes that interfaces are placed in the 
	 *	"dao" package, and that implementation types are in sub-packages. 
	 */
	@Pointcut("execution(* cf.study.framework.spring.aop.dao.*.*(..))")
	public void dataAccessOper() {log.info(MiscUtils.invocationInfo());}
}