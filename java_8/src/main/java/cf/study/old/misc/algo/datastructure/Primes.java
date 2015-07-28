package cf.study.misc.algo.datastructure;

import org.junit.Test;

public class Primes {

	@Test
	public void test() {
		main(new String[] {"2000"});
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int N = Integer.parseInt(args[0]);
		boolean[] a = new boolean[N];
		for (int i = 2; i < N; i++)
			a[i] = true;
		for (int i = 2; i < N; i++) {
			if (a[i]) {
				for (int j = i; j * i < N; j++) {
					a[i * j] = false;
				}
			}
		}
		for (int i = 2; i < N; i++) {
			if (i > N - 100) {
				if (a[i])
					System.out.print(" " + i);
			}
		}
	}

}
