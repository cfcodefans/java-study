package cf.study.java8.lang.keywords;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Test;

public class InstanceofTests {
	@Test
	public void testInstanceof_1() {
		final Integer _int = Integer.valueOf(1);
		
		Assert.assertTrue(_int instanceof Integer);
		Assert.assertTrue(_int instanceof Number);
		Assert.assertTrue(_int instanceof Object);
		Assert.assertTrue(_int instanceof Serializable);
		Assert.assertTrue(_int instanceof Comparable);
//		Assert.assertTrue(1 instanceof Integer); // compile error
		
		Assert.assertTrue((Integer)1 instanceof Integer);
		
		Assert.assertFalse(null instanceof Integer);
	}
	
	@Test
	public void testInstanceof_2() {
		Cls_1 obj_1 = new Cls_1();
		Cls_1 obj_null = null;
		Inf_1 inf_1 = obj_1;
		
		Assert.assertTrue(obj_1 instanceof Cls_1);
		Assert.assertTrue(obj_1 instanceof Inf_1);
		
		Assert.assertFalse(obj_null instanceof Cls_1);
		Assert.assertFalse(obj_null instanceof Inf_1);
		
		Assert.assertTrue(inf_1 instanceof Cls_1);
		Assert.assertTrue(inf_1 instanceof Inf_1);
		
		Cls_2 obj_2 = new Cls_2();
		Cls_2 obj_2_null = null;
		Inf_2 inf_2 = obj_2;
		
		Assert.assertTrue(obj_2 instanceof Cls_2);
		Assert.assertTrue(obj_2 instanceof Inf_2);
		
		Assert.assertFalse(obj_2_null instanceof Cls_2);
		Assert.assertFalse(obj_2_null instanceof Inf_2);
		
		Assert.assertTrue(inf_2 instanceof Cls_2);
		Assert.assertTrue(inf_2 instanceof Inf_2);
		
		Cls_1_2 obj_1_2 = new Cls_1_2();
		Assert.assertTrue(obj_1_2 instanceof Inf_1);
		Assert.assertTrue(obj_1_2 instanceof Inf_2);
		Assert.assertFalse(obj_1_2 instanceof Inf_12);
		
		Cls_12 obj_12 = new Cls_12();
		Assert.assertTrue(obj_12 instanceof Inf_1);
		Assert.assertTrue(obj_12 instanceof Inf_2);
		Assert.assertTrue(obj_12 instanceof Inf_12);
		
	}
	
	
}

interface Inf_1 {}
interface Inf_2 {}

interface Inf_12 extends Inf_1, Inf_2 {}

class Cls_1 implements Inf_1 {}
class Cls_2 implements Inf_2 {}

class Cls_1_2 implements Inf_1, Inf_2 {}
class Cls_12 implements Inf_12 {}