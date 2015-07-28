package cf.study.misc;

import org.junit.Test;

import cf.study.utils.Trace;
import cf.study.utils.Trace.TraceEntry;

public class JavaTest {
	private boolean foo() {
		System.out.println("return foo();");
		return true;
	}

	private boolean weird_1() {
		try {
			return foo();// this is called
		} finally {
			return false;// finally talks
		}
	}

	@Test
	public void test() {
		System.out.println(weird_1());
	}

	@Test
	public void testOS() {
		System.out.println(System.getProperty("os.name"));
	}
	
	private void apple() {
		Trace.start();
		
		Trace.ongoing("apple 1");
		Trace.ongoing("apple 2");
		Trace.ongoing("apple 3");
		
		Trace.end();
	}
	
	private void orange() {
		Trace.start();
		
		Trace.ongoing("orange 1");
		Trace.ongoing("orange 2");
		Trace.ongoing("orange 3");
		
		Trace.end();
	}
	
	private void banana() {
		Trace.start();
		Trace.ongoing("I want pear");
		pear();
		Trace.ongoing("banana ends");
		Trace.end();
	}
	
	private void pear() {
		TraceEntry te = Trace.start();
		Trace.ongoing("I want berry");
		try {
			berry();
		} catch (Exception e) {
			Trace.end(te, e);
			return;
		}
		Trace.end();
	}
	
	private void berry() {
		Trace.start();
		Trace.ongoing("I want coconut");
		coconut();
		Trace.end();
	}
	
	private void coconut() {
		Trace.start();
		Trace.ongoing("climb up on coconut tree");
		if (true)
			throw new RuntimeException("coconut drops");
		Trace.end();
	}
	
	@Test
	public void testCall() {
		try {
//			apple();
//			orange();
			banana();
//			pear();
//			berry();
		} catch (Exception e) {
			Trace.end();
		}

		System.out.println(Trace.flush());
	}
	
	private void f1_1_1() {
		Trace.start("1.1.1");
		
		Trace.end();
	}
	
	private void f1_1() {
		Trace.start("1.1");
		f1_1_1();
		Trace.end();
	}
	
	private void f2_1() {
		Trace.start("2.1");
		Trace.ongoing("2.1...");
		Trace.end();
	}
	
	@Test
	public void testCall1() {
		Trace.start();
		
		Trace.ongoing("1");
		f1_1();
		Trace.ongoing("2");
		for (int i = 0; i < 5; i++) {
			f2_1();
		}
		Trace.ongoing("3");
		Trace.ongoing("4");
		Trace.ongoing("5");
		
		Trace.end();
		System.out.println(Trace.flush());
	}
}
