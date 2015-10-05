package cf.study.misc.algo.datastructure;

class Node {
	int val;
	Node next;

	Node(int v, Node t) {
		val = v;
		next = t;
	}

	Node(int v) {
		val = v;
	}

	Node() {

	}
}