package cf.study.oo;

import junit.framework.Assert;

import org.junit.Test;

public class InheritanceTest {

	@Test
	public void testBinding() {
		Base b = new Sub();
		Assert.assertEquals("base", b.name);
		
		Sub b1 = (Sub)b;
		Assert.assertEquals("sub", b1.name);
	}
	
	@Test
	public void testLoopAndFinally() {
		for (int i = 0; i < 10; i++) {
			try {
				System.out.println(i);
			} finally {
				return;
			}
		}
	}
};


class Base {
	protected String name = "base";
};

class Sub extends Base {
	protected String name = "sub";
}