package cf.study.data.storage.jpa;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

public class JPATestCase {
	protected Logger log = Logger.getLogger("JpaTestNg");

	protected static EntityManagerFactory emf;
	protected static EntityManager em;

	public Query getQueryWithParams(String sql, Object... params) {
		if (StringUtils.isEmpty(sql)) {
			return null;
		}

		Query q = em.createQuery(sql);
		if (ArrayUtils.isEmpty(params)) {
			return q;
		}

		for (int i = 0; i < params.length; i++) {
			q.setParameter(i + 1, params[i]);
		}

		return q;
	}

	public List queryWithParams(String sql, Object... params) {
		Query q = getQueryWithParams(sql, params);
		if (q != null) {
			return q.getResultList();
		}
		return null;
	}

	public Object getObjWithParams(String sql, Object... params) {
		Query q = getQueryWithParams(sql, params);
		if (q != null) {
			return q.getSingleResult();
		}
		return null;
	}

	@BeforeClass
	public static void initEntityManager() {
		emf = Persistence.createEntityManagerFactory("jpa_study");
		em = emf.createEntityManager();
	}

	@Before
	public void printHeader() {
		System.out.println(StringUtils.repeat("=", 40));
	}

	/*  */

	@Before
	public void startTrans() {
		System.out.println("Transaction starts");
		if (!em.isOpen()) {
			em = emf.createEntityManager();
		}
		em.getTransaction().begin();
	}

	@After
	public void closeTrans() {
		EntityTransaction transaction = em.getTransaction();
		if (!transaction.isActive()) {
			return;
		}

		if (!transaction.getRollbackOnly()) {
			transaction.commit();
		}
		em.close();
		System.out.println("Transaction ends");
	}

	public static <T> void printResultSets(Collection<T> records) {
		if (CollectionUtils.isEmpty(records)) {
			System.out.println("Nothing found");
		}

		StringBuilder sb = new StringBuilder();

		T sample = records.iterator().next();
		try {
			Map map = BeanUtils.describe(sample);
			for (Object propertyName : map.keySet()) {
				if ("class".equals(propertyName)) {
					continue;
				}
				sb.append(propertyName).append('\t');
			}
			sb.append('\n');
			next_record: for (T record : records) {
				map = BeanUtils.describe(record);
				for (Object propertyName : map.keySet()) {
					if ("class".equals(propertyName)) {
						continue;
					}
					Object value = map.get(propertyName);
					if (value == record) {
						continue next_record;
					}
					sb.append(value).append('\t');
				}
				sb.append('\n');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		sb.append('\n');
		System.out.println(sb.toString());
	}

	protected Session getSession() {
		Object delegate = em.getDelegate();
		if (!(delegate instanceof Session)) {
			return null;
		}

		Session sess = (Session) delegate;
		return sess;
	}

	public void persistEntities(Serializable... entities) {
		if (ArrayUtils.isEmpty(entities)) {
			return;
		}
		for (Serializable entity : entities) {
			if (entity == null) {
				continue;
			}
			em.persist(entity);
		}
	}
}
