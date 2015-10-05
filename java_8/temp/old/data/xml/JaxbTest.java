package cf.study.data.xml;

import javax.xml.bind.JAXB;

import org.junit.Test;

import cf.study.data.TestPojo;

public class JaxbTest {

	@Test
	public void testMarshaller() {
		JAXB.marshal(new TestPojo(), System.out);

		final TestPojo me = new TestPojo();
		me.setStrAttr("me");

		final TestPojo you = new TestPojo();
		you.setStrAttr("you");

		you.getMapAttr().put(me.getStrAttr(), me);

		me.getMapAttr().put(you.getStrAttr(), you);

		System.out.println();

		JAXB.marshal(me, System.out);
	}

}
