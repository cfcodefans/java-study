package cf.study.misc.quiz;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import java.awt.*;
import java.util.Scanner;

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

	public static void main(String[] args) {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */

		try (Scanner sc = new Scanner(System.in)) {
			int n = sc.nextInt();
			String[] grid = new String[n];

			Point robotPos = new Point();
			Point princessPos = new Point();

			for (int i = 0; i < n; i++) {
				grid[i] = sc.nextLine();
				int x = grid[i].indexOf("m");
				if (x >= 0) {
					robotPos.x = x;
					robotPos.y = i;
				}

				x = grid[i].indexOf("p");
				if (x >= 0) {
					princessPos.x = x;
					princessPos.y = i;
				}
			}

			if (robotPos.x < princessPos.x)
				System.out.println("RIGHT");
			else if (robotPos.x > princessPos.x)
				System.out.println("LEFT");

			if (robotPos.y < princessPos.y)
				System.out.println("UP");
			else if (robotPos.y > princessPos.y)
				System.out.println("DOWN");

		}
	}
}