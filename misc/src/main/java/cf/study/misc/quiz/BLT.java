package cf.study.misc.quiz;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static cf.study.misc.quiz.TreeTests.getHeight;
import static cf.study.misc.quiz.TreeTests.prettyPrint;

public class BLT {

	/**
	 * Created by fan on 2016/10/27.
	 */

	@Test
	public void testInsert() {
		Node_ root = new Node_(3);
		insert(root, 2);
		insert(root, 4);
		insert(root, 5);
		insert(root, 6);

		TreeTests.printInOrder(root);
		System.out.println();
		TreeTests.printPreOrder(root);
		System.out.println();
	}

	@Test
	public void testInsert2() {
		int[] vs = {25, 21, 10, 23, 7, 26, 12, 30, 16};
//		int[] vs = {17, 119, 101, 97, 71, 76, 6, 142, 81, 34, 173, 122, 169, 129, 181, 39, 16, 35, 24, 74, 70, 120, 176, 75, 2, 186, 104, 21, 14, 124, 47, 95, 105, 99, 94, 87, 165, 139, 36};
		Node_ root = null;//new Node(14);
		for (int i : vs) {
//			System.out.println(i);
			root = insert(root, i);
//			System.out.print(i + "\t\t\t\t");
//			TreeTests.printInOrder(root);
//			System.out.println();
		}

		insert(root, 69);

		Arrays.sort(vs);
		System.out.println(Arrays.toString(vs));


		Node_ erro = TreeTests.search(root, 47);
		System.out.println(prettyPrint(root, 0, getHeight(root)));

//		System.out.println();
		{
			List<Node_> nodeList = new LinkedList<>();
			TreeTests.forInOrder(root, nodeList::add);
			System.out.println(nodeList.size());
			System.out.println(StringUtils.join(nodeList.stream().map(n -> {
				return getBF(n);
			}).collect(Collectors.toList()), " "));
		}
//
		{
			List<Node_> nodeList = new LinkedList<>();
			TreeTests.forPreOrder(root, nodeList::add);
			System.out.println(nodeList.size());
			System.out.println(StringUtils.join(nodeList.stream().map(n -> {
				return getBF(n);
			}).collect(Collectors.toList()), " "));
		}

//		TreeTests.printInOrder(root);
//		System.out.println();
//		TreeTests.printPreOrder(root);
//		System.out.println();
//		//TreeTests.printPostOrder(root);
//		System.out.println();
	}

	static String getBF(Node_ node) {
		return String.format("%d(BF=%d)", node.data, Math.abs(getHeight(node.left) - getHeight(node.right)));
	}

	static Node_ rotateLeftRight(Node_ n, Node_ p, Node_ gp) {
		p.right = n.left;

		n.left = p;
		gp.left = n;

		p.ht = getHeight(p);
		n.ht = getHeight(n);
		gp.ht = getHeight(gp);

		return rotateLeftLeft(p, n, gp);
	}

	static Node_ rotateLeftLeft(Node_ n, Node_ p, Node_ gp) {
		gp.left = p.right;
		p.right = gp;

		p.ht = getHeight(p);
		n.ht = getHeight(n);
		gp.ht = getHeight(gp);

		return p;
	}

	static Node_ rotateRightLeft(Node_ n, Node_ p, Node_ gp) {
		p.left = n.right;
		n.right = p;
		gp.right = n;

		p.ht = getHeight(p);
		n.ht = getHeight(n);
		gp.ht = getHeight(gp);

		return rotateRightRight(p, n, gp);
	}

	static Node_ rotateRightRight(Node_ n, Node_ p, Node_ gp) {
		gp.right = p.left;
		p.left = gp;

		p.ht = getHeight(p);
		n.ht = getHeight(n);
		gp.ht = getHeight(gp);

		return p;
	}

	static Node_ insert(Node_ root, int val) {
		Node_ n = new Node_();
		n.data = val;

		if (root == null) return n;

		java.util.LinkedList<Node_> nodeList = new java.util.LinkedList<Node_>();

		Node_ _n = root, p = root, gp = root, ggp = root;
		while (_n != null) {
			nodeList.push(_n);

			if (_n.data > n.data) {
				if (_n.left != null) {
					_n.ht++;
					_n = _n.left;
					continue;
				} else {
					_n.left = n;
					_n.ht += (_n.right == null ? 1 : 0);
					break;
				}
			} else if (_n.data < n.data) {
				if (_n.right != null) {
					_n.ht++;
					_n = _n.right;
					continue;
				} else {
					_n.right = n;
					_n.ht += (_n.left == null ? 1 : 0);
					break;
				}
			}
		}

		if (nodeList.size() == 1) return root;

		while (nodeList.size() >= 2) {
			p = nodeList.pop();
			gp = nodeList.pop();
			ggp = nodeList.isEmpty() ? null : nodeList.pop();

			int gp_and_ggp = (ggp == null) ? 0 : (gp == ggp.left ? -1 : 1);

			//left case
			if (p == gp.left && (gp.right == null || p.ht - gp.right.ht > 1)) {
				//left left case
				if (n == p.left) {
					gp = rotateLeftLeft(n, p, gp);
				} else { //left right case
					gp = rotateLeftRight(n, p, gp);
				}
			} else if (p == gp.right && (gp.left == null || p.ht - gp.left.ht > 1)) {
				//right left case
				if (n == p.left) {
					gp = rotateRightLeft(n, p, gp);
				} else { // right right case
					gp = rotateRightRight(n, p, gp);
				}
			}

			if (gp_and_ggp < 0) ggp.left = gp;
			else if (gp_and_ggp > 0) ggp.right = gp;

			if (ggp == null) {
				return gp;
			}

			ggp.ht = getHeight(ggp);
			nodeList.push(ggp);
			nodeList.push(gp);
			n = p;
		}

		for (Node_ pn : nodeList) {
			pn.ht = getHeight(pn);
		}

		return root;
	}
}
