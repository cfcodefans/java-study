package cf.study.java8.javax.persistence.cdi;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cf.study.java8.javax.persistence.dao.JpaModule;

@Transactional
@Interceptor
public class TransactionalInterceptor {
	
	private static Logger log = LoggerFactory.getLogger(TransactionalInterceptor.class);

	@AroundInvoke
	public Object withTransaction(InvocationContext ctx) throws Throwable {
		EntityManager em = JpaModule.getEntityManager();

		if (em == null) {
			log.warn("not EntityManager found! \n\t" + ctx.getTarget() + "." + ctx.getMethod());
			return ctx.proceed();
		}

		if (!em.isOpen()) {
			log.warn("not EntityManager Opened! \n\t" + ctx.getTarget() + "." + ctx.getMethod());
			return ctx.proceed();
		}

		if (em.isJoinedToTransaction()) {
			return ctx.proceed();
		}

		final EntityTransaction transaction = em.getTransaction();

		if (!transaction.isActive()) {
			transaction.begin();
		}
		Object returnValue = null;
		try {
			log.info("transaction start");
			returnValue = ctx.proceed();
//			em.flush();
			transaction.commit();
			log.info("transaction committed");
		} catch (Throwable t) {
			try {
				if (em.getTransaction().isActive()) {
					em.getTransaction().rollback();
					log.warn("Rolled back transaction");
				}
			} catch (HibernateException e1) {
				log.warn("Rollback of transaction failed -> " + e1);
			}
			throw t;
		}

		return returnValue;
	}
}
