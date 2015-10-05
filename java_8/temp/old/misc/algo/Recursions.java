package cf.study.misc.algo;

import org.junit.Test;

public class Recursions {
	int factorial(final int n) {
		if (n == 0) return 1;
		return n * factorial(n - 1);
	}
	
	int factorial_(final int n) {
		int t = 1;
		for (int i = 1; i <= n; i++) {
			t *= i;
		}
		return t;
	}
	
	@Test
	public void testFactorial() {
		System.out.println(factorial(10));
		System.out.println(factorial_(10));
	}
}

