package cf.study.misc.algo.datastructure;

import org.junit.Test;

public class CoinFlip {

	static boolean heads() {
		return Math.random() < 0.5f;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int i, j, cnt;
		int N = Integer.parseInt(args[0]);
		int M = Integer.parseInt(args[1]);

		int[] f = new int[N + 1];
		for (j = 0; j <= N; j++)
			f[j] = 0;
		for (i = 0; i < M; i++, f[cnt]++)
			for (cnt = 0, j = 0; j <= N; j++)
				if (heads())
					cnt++;
		
		for (j = 0; j <= N; j++) {
			if (f[j] == 0) System.out.print(".");
			for (i = 0; i < f[j]; i += 10) {
				System.out.print("*");
			}
			System.out.println();
		}
	}

	@Test
	public void test() {
		main(new String[] {"1000", "1000"});
	}
}
