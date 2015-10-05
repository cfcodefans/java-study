package cf.study.data.storage.jpa;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cf.study.data.storage.entities.onetomany.Many;
import cf.study.data.storage.entities.onetomany.One;

public class OneToManyTest extends JPATestCase {
	private One _o = null;
	private List<Many> _m = new ArrayList<Many>();
	
	@Test
	public void persistOneWithManyCascadePersist() {
		One one = new One();
		for (int i = 0; i < 4; i++) {
			Many many = new Many();
			one.getCascadePersistMany().add(many);
		}
		em.persist(one);
	}
	
	@Test
	public void persistOneWithManyCascadeAll() {
		One one = new One();
		em.persist(one);
		em.flush();
		
		List<Many> tempList = new ArrayList<Many>();
		for (int i = 0; i < 4; i++) {
			Many many = new Many();
			tempList.add(many);
			em.persist(many);
			em.flush();
			System.out.println(many.getId());
		}
		one.getMany().addAll(tempList);
		em.persist(one);
		em.flush();
	}
	
	@Test
	public void persistOneWithManyCascadeRefresh() {
		One one = new One();
		for (int i = 0; i < 4; i++) {
			Many many = new Many();
			em.persist(many);
			one.getCascadeRefreshMany().add(many);
		}
		em.persist(one);
	}
	
	@Test
	public void persistOneWithManyCascadeMerge() {
		One one = new One();
		for (int i = 0; i < 4; i++) {
			Many many = new Many();
			em.persist(many);
			one.getCascadeMergeMany().add(many);
		}
		em.persist(one);
		em.flush();
		
		em.detach(one);
		
		one.getCascadeMergeMany().remove(1);
		em.merge(one);
	}
	
	
}
