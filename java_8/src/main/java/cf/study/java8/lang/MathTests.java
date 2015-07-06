package cf.study.java8.lang;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.CharUtils;
import org.junit.Test;

public class MathTests {
	@Test
	public void testCeilAndFloor() {
		System.out.println(Math.ceil(0.05));
		System.out.println(Math.floor(0.05));
	}

	@Test
	public void testMathContext() {
		BigDecimal re = new BigDecimal(22).divide(new BigDecimal(7), new MathContext(100));
		System.out.println(re);
	}

	@Test
	public void testPi() {
		System.out.println(pi(20));
		System.out.println(pi2Longs(20));
	}

	public static BigDecimal pi(Integer n) {
		if (n == null) {
			n = 0;
		}

		MathContext mc = new MathContext(n);
		BigDecimal pi = new BigDecimal(BigInteger.ZERO, n, mc);
		BigDecimal one = new BigDecimal(BigInteger.ONE, n, mc);
		BigDecimal two = new BigDecimal(new BigInteger("2"), n, mc);
		BigDecimal four = new BigDecimal(new BigInteger("4"), n, mc);
		for (int k = 0; k < n; k++) {
			BigDecimal tmp1 = one.divide(new BigDecimal(16).pow(k, mc), mc);

			BigDecimal tmp2_1 = four.divide(new BigDecimal(8 * k + 1, mc), mc);
			BigDecimal tmp2_2 = two.divide(new BigDecimal(8 * k + 4, mc), mc);
			BigDecimal tmp2_3 = one.divide(new BigDecimal(8 * k + 5, mc), mc);
			BigDecimal tmp2_4 = one.divide(new BigDecimal(8 * k + 6, mc), mc);

			BigDecimal tmp2 = tmp2_1.subtract(tmp2_2, mc).subtract(tmp2_3, mc).subtract(tmp2_4, mc);
			pi = pi.add(tmp1.multiply(tmp2, mc));
		}

		// pi.setScale(n, BigDecimal.ROUND_UP);
		pi.scaleByPowerOfTen(n);

		return pi;
	}

	public static List<Long> pi2Longs(Integer n) {
		BigDecimal pi = pi(n);

		List<Long> list = new ArrayList<Long>(n);
		pi.toString().substring(0, n).chars().forEach((i) -> {
			char c = (char) i;
			if (CharUtils.isAsciiNumeric(c)) {
				list.add((long) (c - 48));
			}
		});

		return list;
	}

}
