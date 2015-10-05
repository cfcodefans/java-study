package cf.study.data.storage.jpa;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.ejb.Ejb3Configuration;
import org.junit.Test;

import cf.study.data.storage.entities.onetomany.Many;
import cf.study.data.storage.entities.onetomany.One;

public class JPATest extends JPATestCase {
	public static void main(String[] args) throws IOException {
		Ejb3Configuration cfg = new Ejb3Configuration();

		String un = "jpa_study";
		Ejb3Configuration configured = cfg.configure(un, null);
		configured.buildEntityManagerFactory();
	}
	
	@Test
	public void test1() {
		String un = "jpa_study";
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(un);
		EntityManager em = emf.createEntityManager();
		
		em.getTransaction().begin();
		One one = new One();
		
		em.persist(one);
//		em.flush();
		System.out.println(one.getId());
		
		for (int i = 0; i < 4; i++) {
			Many many = new Many();
			em.persist(many);
//			em.flush();
			one.getMany().add(many);
		}
		em.persist(one);
//		one = new One();
//		
//		em.persist(one);
		em.flush();
//		System.out.println(one.getId());
		
		
		em.getTransaction().commit();
	}
}
