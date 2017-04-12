package cf.study.data.storage.jpa;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.Test;

import cf.study.data.storage.entities.onetomany.Many;

public class CriteriaTest extends JPATestCase {
	@Test
	public void testSimpleQuery() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Many> cq = cb.createQuery(Many.class);
		cq.from(Many.class);
		List<Many> reList = em.createQuery(cq).getResultList();
		System.out.println(reList);
	}
	
	@Test
	public void testCondition() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Many> cq = cb.createQuery(Many.class);
		Root<Many> root = cq.from(Many.class);
		Predicate eqPred = cb.equal(root.get("id"), 50);
		cq.where(eqPred);

		List<Many> reList = em.createQuery(cq).getResultList();
		System.out.println(reList);
	}
	
	@Test
	public void testAndConditions() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Many> cq = cb.createQuery(Many.class);
		Root<Many> root = cq.from(Many.class);
		Predicate eqPred = cb.equal(root.get("id"), 50);
		Predicate eqPred1 = cb.equal(root.get("id"), 51);
		Predicate eqPred2 = cb.equal(root.get("id"), 51);
		
		cq.where(eqPred, eqPred1, eqPred2);
		
		cb.and(eqPred1, eqPred2);

		List<Many> reList = em.createQuery(cq).getResultList();
		System.out.println(reList);
	}
	
	@Test
	public void testAndConditions1() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Many> cq = cb.createQuery(Many.class);
		Root<Many> root = cq.from(Many.class);
		Predicate eqPred = cb.equal(root.get("id"), 50);
		Predicate eqPred1 = cb.equal(root.get("id"), 51);
		Predicate eqPred2 = cb.equal(root.get("id"), 51);
		
		cq.where(eqPred, cb.and(eqPred1, eqPred2));

		List<Many> reList = em.createQuery(cq).getResultList();
		System.out.println(reList);
	}
	
	@Test
	public void testAndOrConditions() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Many> cq = cb.createQuery(Many.class);
		Root<Many> root = cq.from(Many.class);
		Predicate eqPred = cb.equal(root.get("id"), 50);
		Predicate eqPred1 = cb.equal(root.get("id"), 51);
		Predicate eqPred2 = cb.equal(root.get("id"), 51);
		
		cq.where(eqPred, cb.or(eqPred1, eqPred2));

		List<Many> reList = em.createQuery(cq).getResultList();
		System.out.println(reList);
	}
	
	@Test
	public void testOrConditions() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Many> cq = cb.createQuery(Many.class);
		Root<Many> root = cq.from(Many.class);
		Predicate eqPred = cb.equal(root.get("id"), 50);
		Predicate eqPred1 = cb.equal(root.get("id"), 51);
		Predicate eqPred2 = cb.equal(root.get("id"), 52);
		
		cq.where(cb.or(eqPred, eqPred1, eqPred2));

		List<Many> reList = em.createQuery(cq).getResultList();
		System.out.println(reList);
	}
	
	@Test
	public void testInConditions() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Many> cq = cb.createQuery(Many.class);
		Root<Many> root = cq.from(Many.class);
		
		cq.where(root.get("id").in(50, 51, 52));

		List<Many> reList = em.createQuery(cq).getResultList();
		System.out.println(reList);
	}
	
	@Test
	public void testOrder() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Many> cq = cb.createQuery(Many.class);
		Root<Many> root = cq.from(Many.class);
		
		cq.orderBy(cb.asc(root.get("id")));
		cq.orderBy(cb.desc(root.get("id")));
		
		List<Many> reList = em.createQuery(cq).getResultList();
		System.out.println(reList);
	}
}
