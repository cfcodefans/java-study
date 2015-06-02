package cf.study.misc.quiz;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

public class Solution {
	public int solution(int[] A) {
		// write your code in Java SE 8
		long sum = 0;
		for (int i = 0; i < A.length; i++) {
			sum += A[i];
		}

		for (int i = 0, loSum = 0; i < A.length; i++) {
			if (sum - A[i] - loSum == loSum) {
				return i;
			}
			loSum += A[i];
		}

		return -1;
	}

	@Test
	public void testSolution() {
		int[] A = new int[8];
		A[0] = -1;
		A[1] = 3;
		A[2] = -4;
		A[3] = 5;
		A[4] = 1;
		A[5] = -6;
		A[6] = 2;
		A[7] = 1;
		
		System.out.println(solution(A));
		
		ArrayUtils.reverse(A);
		
		System.out.println(solution(A));
		
		System.out.println(solution(new int[]{500, 1, -2, -1, 2}));
	}
}