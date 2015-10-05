package cf.study.jdk7.lang;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.junit.Test;

import cf.study.utils.MiscUtils;

public class JavaTest {
	
	
	@Test
	public void testHash() {
		System.out.println(new Object().hashCode());
		System.out.println(new Object().hashCode());
	}
	
	private Object varags(Object...ps) {
		if (ps == null) {
			return null;
		}
		System.out.println("args: " + Arrays.toString(ps));
		System.out.println("Conclusion: when you pass nothing to varargs, it is an empty array");
		return ps;
	}
	@Test
	public void testVarags() {
		System.out.println(varags());
	}
	
	@Test
	public void testAssignment1() {
		int i = 0;
		System.out.println(i=2);
		System.out.println((i=2));
		
		boolean b = true;
		if (b = false) {
			System.out.println("b = false");
		}
		if (b == false) {
			System.out.println("b == false");
		}
		
		Integer _i = null;
//		System.out.println(Integer.valueOf(0).compareTo( _i));
//		System.out.println(0 > _i);
//		System.out.println(0 < _i);
//		System.out.println(0 == _i);
	}
	
	@Test
	public void testPrivateInheritance() {
		Base obj = new Sub();
		Base.sfoo();
		obj.sfoo();
		
		Sub.sfoo();
		obj.pfoo();
		Sub obj1 = (Sub)obj;
		
		obj1.sfoo();
		obj1.pfoo();
		
		System.out.println("Conclustion: subclass's static methods are only called with subclass name, static methods are not overriden");
	}
	
	@Test
	public void testFinally() {
		try {
			System.out.println("try");
			if (true)
			return;
		} finally {
			System.out.println("finally");
		}
		
		System.out.println("after finally");
	}
	
	private void foo(int i) {
		int j = i;
//		String s = "abc".concat(Integer.toString(i)) ;
		Date d = new Date();
//		Object obj = null;
	}
	
	private void fooFinalParams(final int i) {
		final int j = i;
//		final String s = "abc".concat(Integer.toString(i)) ;
		final Date d = new Date();
//		final Object obj = null;
	}
	
	@Test
	public void testFinal() {
		long t = System.nanoTime();
		for (int i = 0; i < 300000; i++) {
			foo(i);
		}
		System.out.println(System.nanoTime() - t);
		
		t = System.nanoTime();
		for (int i = 0; i < 300000; i++) {
			fooFinalParams(i);
		}
		System.out.println(System.nanoTime() - t);
	}
	
	private long final_() {
		System.out.println("final_");
		return System.currentTimeMillis(); 
	}
	
	@Test
	public void testFinal_() {
		final long v = final_();
		System.out.println(v);
		System.out.println(v);
		System.out.println(v);
		System.out.println(v);
	}
	
	@Test
	public void testCopyArray() {
		int[] intA = new int[] {0,1,2,3};
		byte[] byteA = new byte[16];
		System.arraycopy(intA, 0, byteA, 0, 4);
		System.out.println(Arrays.toString(byteA));
	}
	
	@Test
	public void testNumber() {
		System.out.println(toBinStr(5));
		System.out.println(toBinStr(-5));
		
		System.out.println(toBinStr(7));
		System.out.println(toBinStr(-7));
		
		System.out.println(toBinStr(4));
		System.out.println(toBinStr(-4));
	}
	public String toBinStr(int iiv) {
		BitSet bs = BitSet.valueOf(new long[] {iiv});
		StringBuilder sb = new StringBuilder();
		for (int i = 0, j = bs.length(); i < j; i++) {
			sb.append(bs.get(i) ? 1 : 0);
		}
		return StringUtils.leftPad(sb.toString(), 64, '0');
	}
	
	@Test
	public void testFinalizer() {
		{
			Base b = new Base();
		}
//		System.gc();
		System.out.println("outside the inner scope");
		
		{
			Base b = new Base();
			b = null;
		}
//		System.gc();
		System.out.println("outside the inner scope");
		
		WeakReference<Base> wr = null;
		{
			Base b = new Base();
			wr = new WeakReference<Base>(b);
			b = null;
		}
		System.gc();
		System.out.println("outside the inner scope: " + wr.get());
	}
	
	@Test
	public void testDate() {
//		Locale.getDefault();
		System.out.println(Arrays.toString(TimeZone.getAvailableIDs()));
		final TimeZone timeZone = TimeZone.getTimeZone("GMT+0");
		System.out.println(timeZone);
		System.out.println(Calendar.getInstance(timeZone)); 
		System.out.println(DateFormatUtils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd hh:mm:ss"));
		System.out.println(new Date(System.currentTimeMillis() - TimeZone.getDefault().getRawOffset()));
	}
	
	@Test
	public void testOverrideAttr() {
		Base b = new Sub();
		System.out.println(b.getA());
		
		Sub s = (Sub)b;
		System.out.println(s.getA());
	}
}

class Base {
	
	public int getA() {
		return a;
	}
	
	protected int a = 1;
	
	static void sfoo() {
		new Base().foo();
	}
	
	public void pfoo() {
		foo();
	}
	
	private void foo() {
		System.out.println("Base.foo()");
	}
	
	protected void finalize() {
		System.out.println(MiscUtils.invocationInfo());
	}
}

class Sub extends Base {
	protected int a = 2;
	
	static void sfoo() {
		new Sub().foo();
	}
	
	public void foo() {
		System.out.println("Sub.foo()");
	}
	
	public void pfoo() {
		foo();
	}
}

