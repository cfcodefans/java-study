package cf.study.misc.algo.sorting;

import org.junit.Test;

import cern.colt.Arrays;

public class ArraySortBasic {

	public static int cnt = 0;

	public static boolean less(double v, double w) {
		cnt++;
		return v < w;
	}

	public static void exch(double[] a, int i, int j) {
		double t = a[i];
		a[i] = a[j];
		a[j] = t;
	}

	public static void compExch(double[] a, int i, int j) {
		if (less(a[j], a[i])) {
			exch(a, i, j);
		}
	}

	public static void sort(double[] a, int l, int r) {
		for (int i = l + 1; i <= r; i++) {
			for (int j = i; j > l; j--) {
				compExch(a, j - 1, j);
			}
		}
	}
	
	public static void sort(double[] a) {
		sort(a, 0, a.length-1);
	}
	
	@Test
	public void testSort() {
		final double[] da = new double[] {3,1,4,1,5,9,2,6,5,3,5,8,9,7,9,3,2};
		sort(da);
		System.out.println(Arrays.toString(da));
	}
}
