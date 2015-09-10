package cf.study.misc.algo.datastructure;

import org.apache.commons.lang.math.RandomUtils;

public class ListSortExample {

	static Node create() {
		Node a = new Node(0, null);
		for (int i = 0; i < 20; i++) {
			a.next = new Node(RandomUtils.nextInt(100), a.next);
		}
		return a;
	}
	
	static Node sort(Node a) {
		Node t, u, x, b = new Node(0, null);
		while (a.next != null) {
			t = a.next;
			u = t.next;
			a.next = u;
			
			for (x = b; x.next != null; x = x.next) {
				if (x.next.val > t.val) break;
			}
			t.next = x.next; x.next = t;
		}
		return b;
	}
	
	static void print(Node h) {
		for (Node t = h.next; t!= null; t = t.next) {
			System.out.print(t.val + " ");
		}
		System.out.println();
	}
	
	public static void main(String[] args) {
		Node head = create();
		print(head);
		print(sort(head));
	}
}
