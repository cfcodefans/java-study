package cf.study.misc.algo.datastructure;

import org.junit.Test;

public class CircularList {
	
	//memory allocation
	
	static Node M[];
	static int free, max = 10000;
	
	public CircularList() {
		M = new Node[max + 1];
		for (int i = 0; i < max; i++) {
			M[i] = new Node();
			
		}
	} 
	
	Node next(Node x) {
		return x.next;
	}
	
	int val(Node x) {
		return x.val;
	}
	
	Node insert(Node x, int v) {
		Node t = new Node(v);
		if (x == null) t.next = t;
		else {
			t.next = x.next;
			x.next = t;
		}
		return t;
	}
	
	void remove(Node x) {
		x.next = x.next.next;
	}
	
	@Test
	public void josephusY() {
		int N = (int)(10 * Math.random());
		int M = (int)(10 * Math.random());
		
		CircularList cl = new CircularList();
		Node x = null;
		for (int i = 0; i < N; i++) {
			x = cl.insert(x, i);
		}
		
		while (x != cl.next(x)) {
			for (int i = 0; i < M; i++) {
				x = cl.next(x);
			}
			cl.remove(x);
		}
		
		System.out.println("Survivor is " + cl.val(x));
	}
}
