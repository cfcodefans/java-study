package cf.study.java.lang;

import misc.MiscUtils;
import org.junit.Test;

/**
 * Created by fan on 2016/11/3.
 */
public class InnerClassTests {
	class InnerClass {
		InnerClass() {
			System.out.println(MiscUtils.invocationInfo());
		}
	}

	@Test
	public void testInnerClassNew() {
		this.new InnerClass();
	}
}
