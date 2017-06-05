package cf.study.hackerrank;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Collections;
import java.util.Scanner;
import java.util.Stack;
import java.util.TreeSet;
import java.util.stream.IntStream;

/**
 * Created by fan on 2016/11/15.
 * https://www.hackerrank.com/challenges/largest-rectangle
 */
public class LargestRectangle {
	public long solution(int[] hs) {
		if (hs.length == 0) return 0;
		if (hs.length == 1) return hs[0];

		long max = hs[0];
		Stack<Integer> stack = new Stack<>();
		stack.push(hs[0]);

		TreeSet<Long> rects = new TreeSet<>();

		for (int i = 1, size = hs.length; i < size; i++) {
			final int h = hs[i];
			int peek = stack.peek();
			if (peek > h) {
				Stack<Integer> tmpStack = new Stack<>();
				while (!stack.empty() && stack.peek() > h) tmpStack.push(stack.pop());

				int removedSize = tmpStack.size();
				for (int a = 0; a <= removedSize; a++) stack.push(h);

				do {
					long t = (long) tmpStack.peek();
					long area = tmpStack.size() * t;
					if (area > max) {
						System.out.printf("i:\t%d, a[x] = %d \tarea = %d\n", i, t, area);
						max = area;
					}
					while (!tmpStack.empty() && tmpStack.peek() == t) {
						tmpStack.pop();
					}
				} while (!tmpStack.empty());
			} else {
				stack.push(h);
			}
		}

		if (!stack.empty()) {
			Collections.reverse(stack);
			do {
				long t = stack.peek();
				long area = stack.size() * t;
				if (area > max) {
					System.out.printf("a[x] = %d \tarea = %d\n", t, area);
					max = area;
				}
				while (!stack.empty() && stack.peek() == t) {
					stack.pop();
				}
			} while (!stack.empty());
		}

		return max;
	}

	@Test
	public void test() {
		System.out.println(solution(new int[]{1, 2, 3, 4, 5}));
		System.out.println(solution(new int[]{5, 4, 3, 2, 1}));
		System.out.println(solution(new int[]{1, 2, 3, 4, 5, 4, 3, 2, 1}));
		System.out.println(solution(new int[]{5, 5, 5, 5, 5}));
		System.out.println(solution(new int[]{1, 5, 5, 5, 5}));
		System.out.println(solution(new int[]{5, 5, 5, 5, 1}));
		System.out.println(solution(new int[]{5, 4, 3, 4, 5}));
		System.out.println(solution(new int[]{6320, 6020, 6098, 1332, 7263, 672, 9472, 2838, 3401, 9494}));
	}

	@Test
	public void test11() {
		try (Scanner sc = new Scanner(LargestRectangle.class.getResourceAsStream("./test"))) {
			int n = sc.nextInt();
			int[] hs = new int[n];
			for (int i = 0; i < n; i++) {
				hs[i] = sc.nextInt();
			}
			System.out.println(solution(hs));
		}
	}

	@Test
	public void test1() {
		try (Scanner sc = new Scanner(LargestRectangle.class.getResourceAsStream("./test"))) {
			int n = sc.nextInt();
			int[] hs = new int[n];
			for (int i = 0; i < n; i++) {
				hs[i] = sc.nextInt();
			}
			System.out.println(area(hs));
		}
	}

	@Test
	public void debug1() {
		try (Scanner sc = new Scanner(LargestRectangle.class.getResourceAsStream("./test"))) {
			int n = sc.nextInt();
			int[] hs = new int[n];
			for (int i = 0; i < n; i++) {
				hs[i] = sc.nextInt();
			}
			int[] subarray = ArrayUtils.subarray(hs, 97104, 97303);
			System.out.println("min: " + IntStream.of(subarray).min().getAsInt());

//			System.out.println(StringUtils.join(IntStream.of(subarray).iterator(), "\n"));
		}
	}

	@Test
	public void test2() {
		try (Scanner sc = new Scanner(LargestRectangle.class.getResourceAsStream("./test"))) {
			int n = sc.nextInt();
			int[] hs = new int[n];
			for (int i = 0; i < n; i++) {
				hs[i] = sc.nextInt();
			}
			System.out.println(area(ArrayUtils.subarray(hs, 97104, 97303)));
		}
	}

	@Test
	public void test21() {
		try (Scanner sc = new Scanner(LargestRectangle.class.getResourceAsStream("./test"))) {
			int n = sc.nextInt();
			int[] hs = new int[n];
			for (int i = 0; i < n; i++) {
				hs[i] = sc.nextInt();
			}
			System.out.println(solution(ArrayUtils.subarray(hs, 97104, 97303)));
		}
	}

	static int area(int[] a) {
		int i, area = 0;
		int maxArea = 0;
		Stack<Integer> st = new Stack<>();
		for (i = 0; i < a.length; ) {
			if (st.isEmpty() || a[st.peek()] <= a[i]) {
				st.push(i++);
			} else {
				int x = st.pop();
				int start = 0;
				if (st.isEmpty()) {
					area = a[x] * i;
					start = 0;
				} else {
					start = st.peek() - 1;
					area = a[x] * (i - st.peek() - 1);
				}
				if (area > maxArea) {
					System.out.printf("start:\t%d, i:\t%d, x = %d a[x] = %d \tarea = %d\n", start, i, x, a[x], area);
					maxArea = area;
				}
			}
		}

		while (!st.isEmpty()) {
			int x = st.pop();
			int start = 0;
			if (st.isEmpty()) {
				area = a[x] * i;
				start = 0;
			} else {
				start = st.peek() - 1;
				area = a[x] * (i - st.peek() - 1);
			}
			if (area > maxArea) {
				System.out.printf("start:\t%d, i:\t%d, x = %d a[x] = %d \tarea = %d\n", start, i, x, a[x], area);
				maxArea = area;
			}
		}

		return maxArea;
	}
}

