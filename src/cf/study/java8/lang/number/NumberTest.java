package cf.study.java8.lang.number;


import static misc.MiscUtils.toBinStr;

import org.junit.Test;

public class NumberTest {

	@Test
	public void testMod() {
		System.out.println(-3 % 2); //-1
		System.out.println(3 % -2); //1
	}
	
	@Test
	public void testMax() {
		System.out.println(Integer.MAX_VALUE);
		System.out.println(-Integer.MAX_VALUE);
		System.out.println(Integer.MAX_VALUE + (-Integer.MAX_VALUE));
		
		System.out.println(Long.MAX_VALUE);
		System.out.println(-Long.MAX_VALUE);
		System.out.println(Long.MAX_VALUE + (-Long.MAX_VALUE));
	}
	
	
	@Test
	public void testMaxAndMin() {
		System.out.println(Integer.MAX_VALUE);
		System.out.println(Integer.MIN_VALUE);
		System.out.println(Integer.MAX_VALUE + Integer.MIN_VALUE);
		
		System.out.println(Long.MAX_VALUE);
		System.out.println(Long.MIN_VALUE);
		System.out.println(Long.MAX_VALUE + Long.MIN_VALUE);
	}
	
	@Test
	public void testBinary() {
		byte _b = 1;
		short _s = 1;
		int _i = 1;
		long _l = 1;
		System.out.println(toBinStr(_b));
		System.out.println(toBinStr(_b >> 1));
		System.out.println(toBinStr((byte)_b >> 1));
		System.out.println(toBinStr((byte)(_b >> 1)));
		
		//every number operation between byte, short, integer, produces result in integer
		System.out.println();
		System.out.println("every number operation between byte, short, integer, produces result in integer");
		System.out.println(toBinStr(_b + _b));
		System.out.println(toBinStr(_b + _s));
		System.out.println(toBinStr(_b + _i));
		System.out.println(toBinStr(_s + _i));
		System.out.println(toBinStr((byte)(_b + _b)));
		
		//every number operation between byte, short, integer, involving long, produces result in long
		System.out.println();
		System.out.println("every number operation between byte, short, integer, produces result in long");
		System.out.println(toBinStr(_b + _l));
		System.out.println(toBinStr(_i + _l));
		System.out.println(toBinStr(_b + _l));
		System.out.println(toBinStr(_l + _l));
		System.out.println(toBinStr((byte)(_l + _l)));
		
		System.out.println();
		System.out.println("two's compelment");
		System.out.println(toBinStr((byte)-_b));
		System.out.println(toBinStr((byte)-(_b+_b)));
		System.out.println(toBinStr(-_l));
//		System.out.println(toBinStr((byte)-_b));
		
		System.out.println();
		System.out.println("bit order in java is big endian");
		System.out.println(toBinStr((byte)16));
		
		System.out.println();
		System.out.println(Byte.MAX_VALUE + " = " + toBinStr(Byte.MAX_VALUE));
		System.out.println(Byte.MIN_VALUE + " = " + toBinStr(Byte.MIN_VALUE));
		System.out.println(((byte)-1));
		System.out.println(((byte)-1) & Byte.MAX_VALUE);
	}
}
