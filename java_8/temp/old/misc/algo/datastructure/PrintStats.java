package cf.study.misc.algo.datastructure;

import org.junit.Test;

public class PrintStats {
	@Test
	public void stats() {
		main(new String[] { "100" });
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int N = Integer.parseInt(args[0]);
		double m = 0.0, s = 0.0;
		for (int i = 0; i < N; i++) {
			int x = (int) (Math.random() * 10000);
			double d = (double) x;
			m += d / N;
			s += (d * d) / N;
		}
		s = Math.sqrt(s - m * m);
		System.out.println("Avg.: " + m);
		System.out.println("Std. dev.: " + s);
	}

}
