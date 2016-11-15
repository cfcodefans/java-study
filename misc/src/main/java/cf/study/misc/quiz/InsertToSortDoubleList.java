package cf.study.misc.quiz;

import org.junit.Test;

/**
 * Created by Administrator on 2016/9/29.
 */

class Node {
	int data;
	Node next;
	Node prev;
}

public class InsertToSortDoubleList {
	Node sortedInsert(Node head, int data) {
		Node n = new Node();
		n.data = data;

		if (head == null) {
			return n;
		}

		if (data <= head.data) {
			head.prev = n;
			n.next = head;
			return n;
		}

		Node _n = head;
		for (; _n.next != null; _n = _n.next) {
			if (n.data <= _n.data) {
				n.prev = _n.prev;
				_n.prev.next = n;
				_n.prev = n;
				n.next = _n;
				return head;
			}
		}

		if (n.data <= _n.data) {
			n.prev = _n.prev;
			_n.prev.next = n;
			_n.prev = n;
			n.next = _n;
		} else {
			_n.next = n;
			n.prev = _n;
		}

		return head;
	}

	String toString(Node head) {
		if (head == null) return String.valueOf(null);
		StringBuilder sb = new StringBuilder();

		for (Node n = head; n != null; n = n.next) sb.append(n.data).append(" ");

		return sb.toString();
	}

	@Test
	public void test() {
		Node head = sortedInsert(null, 2);
		head = sortedInsert(head, 1);
		head = sortedInsert(head, 4);
		head = sortedInsert(head, 3);

		System.out.println(toString(head));
	}
}
