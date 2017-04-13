package cf.study.java.lang;

import org.junit.Test;

public class ObjectTests {
	@Test 
	public void testObject() {
		Object obj = new Object();
		
		System.out.println(obj);
		System.out.println(obj.hashCode());
		System.out.println(obj.toString());
		System.out.println(obj.getClass());
		System.out.println(obj.hashCode());
	}
	
	@Test
    public void callManyNPEInLoop() {
        for (int i = 0; i < 100000; i++) {
            try {
                ((Object)null).getClass();
            } catch (Exception e) {
                // This will switch from 2 to 0 (indicating our problem is happening)
                System.out.println(e.getStackTrace().length);
            }
        }
    }
}
