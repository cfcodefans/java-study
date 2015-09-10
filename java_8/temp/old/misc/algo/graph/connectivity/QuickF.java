package cf.study.misc.algo.graph.connectivity;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class QuickF {

	static int[][] testPairs = {{3,4},
		{4,9},
		{8,0},
		{2,3},
		{5,6},
		{2,9},
		{5,9},
		{7,3},
		{4,8},
		{5,6},
		{0,2},
		{6,1}};
	
	@Test
	public void test() {
		main(new String[]{"10"});
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int N = Integer.parseInt(args[0]);
		int[] id = new int[N];
		for (int i = 0; i < N; i++) {
			id[i] = i;
		}

		for (int[] pair : testPairs) {
			int p = pair[0];
			int q = pair[1];

			int t = id[p];
			if (t == id[q])
				continue;
			for (int i = 0; i < N; i++) {
				if (id[i] == t)
					id[i] = id[q];
			}
			System.out.println(String.format(" %s %s\t%s", p, q, StringUtils.join(ArrayUtils.toObject(id), ' ')));
		}
	}

}
