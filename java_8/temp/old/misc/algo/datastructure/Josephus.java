package cf.study.misc.algo.datastructure;

import org.junit.Test;

public class Josephus {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int N = Integer.parseInt(args[0]);
		int M = Integer.parseInt(args[1]);

		Node t = new Node(1);
		Node x = t;
		for (int i = 2; i <= N; i++) {
			x = (x.next = new Node(i));
		}

		x.next = t;

		while (x != x.next) {
			for (int i = 1; i < M; i++) {
				x = x.next;
			}
			x.next = x.next.next;
		}
		System.out.println("Survivor is " + x.val);
	}
	
	@Test
	public void test() {
		main(new String[] {"9", "5"});
	}
}
