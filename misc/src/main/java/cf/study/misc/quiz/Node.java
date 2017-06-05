package cf.study.misc.quiz;

/**
 * Created by fan on 2016/10/18.
 */

class Node {
    int data = 0, ht = 0;
    Node left, right;

    Node(int d) {
        this.data = d;
    }

    public Node() {
    }

    public String toString() {
        return String.valueOf(data);
    }
}
