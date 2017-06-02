package cf.study.misc.quiz;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import java.util.*;
import java.util.function.Consumer;

public class TreeTests {

    static int indexOf(int[] array, int target, int start, int end) {
        for (int i = start, j = Math.min(end, array.length - 1); i <= j; i++) {
            if (array[i] == target) return i;
        }
        return 0;
    }

    static int indexOf(int[] array, int target) {
        return indexOf(array, target, 0, array.length - 1);
    }

    static Node makeTree(int[] inOrder, int[] preOrder) {
        if (ArrayUtils.isEmpty(inOrder) || ArrayUtils.isEmpty(preOrder) || inOrder.length != preOrder.length) {
            return null;
        }

        Node root = new Node(preOrder[0]);
        Stack<int[]> s = new Stack<>();
        s.push(new int[]{0, preOrder.length, 0, inOrder.length});

        int count = 1;
        Node _root = root;

        while (!s.empty()) {
            int[] bound = s.pop();
            int preLeft = bound[0], preRight = bound[1], inLeft = bound[2], inRight = bound[3];
            int rootIndex = indexOf(inOrder, _root.data, inLeft, inRight);

            while (inRight - inLeft > 1) {
                _root.left = new Node(preOrder[rootIndex + 1]);
                _root = _root.left;
                rootIndex = indexOf(inOrder, _root.data, inLeft, inRight);
                int toLeft = rootIndex - inLeft;
                s.push(new int[]{preLeft + toLeft, preRight, rootIndex + 1, inRight});
            }
        }

        return root;
    }

    Node makeTree(Node root, int... datas) {
        if (root == null) root = new Node(datas[0]);

        Node n = root;
        for (int d : datas) {
            while (true) {
                if (d == n.data) break;
                if (d > n.data) {
                    if (n.right == null) {
                        n.right = new Node(d);
                        break;
                    } else n = n.right;
                } else {
                    if (n.left == null) {
                        n.left = new Node(d);
                        break;
                    } else n = n.left;
                }
            }
            n = root;
        }

        return root;
    }

    static void printPreOrder(Node node) {
        if (node == null) return;
        System.out.print(node.data + " ");
        printPreOrder(node.left);
        printPreOrder(node.right);
    }

    static void forPreOrder(Node node, Consumer<Node> foo) {
        if (node == null) return;
        foo.accept(node);
        forPreOrder(node.left, foo);
        forPreOrder(node.right, foo);
    }

    static void printInOrder(Node node) {
        if (node == null) return;
        printInOrder(node.left);
        System.out.print(node.data + " ");
        printInOrder(node.right);
    }

    static void forInOrder(Node node, Consumer<Node> foo) {
        if (node == null) return;
        forInOrder(node.left, foo);
        foo.accept(node);
        forInOrder(node.right, foo);
    }

    static void printPostOrder(Node node) {
        if (node == null) return;
        printPostOrder(node.left);
        printPostOrder(node.right);
        System.out.print(node.data + " ");
    }

    static void forPostOrder(Node node, Consumer<Node> foo) {
        if (node == null) return;
        forPostOrder(node.left, foo);
        forPostOrder(node.right, foo);
        foo.accept(node);
    }

    void printPreOrderS(Node root) {
        Stack<Node> s = new Stack<>();

        Node node = root;
        if (node.left == null && node.right == null) {
            System.out.print(node.data + " ");
            return;
        }
//		s.push(node);
        do {
            System.out.print(node.data + " ");
            if (node.left == null && node.right == null) {
                Node n = s.pop();
                while ((node == n.right || n.right == null) && !s.empty()) {
                    node = n;
                    n = s.pop();
                }

                if (n == root) {
                    if (n.right == null || node == n.right) {
                        break;
                    }
                }

                s.push(n);
                node = n.right;
            } else {
                s.push(node);
                if (node.left != null) {
                    node = node.left;
                    continue;
                }

                if (node.right != null) {
                    node = node.right;
                    continue;
                }
            }
        } while (!s.empty());
    }

    @Test
    public void testPrintPreOrders() {
        Node root = makeTree(null, 7, 3, 5, 2, 1, 4, 6, 7);

        printPreOrder(root);
        System.out.println();

        printPreOrderS(root);
        System.out.println();

        preOrder(root);
        System.out.println();
    }

    public static void preOrder(Node biTree) {
        Stack<Node> stack = new Stack<Node>();
        while (biTree != null || !stack.isEmpty()) {
            while (biTree != null) {
                System.out.print(biTree.data + ",");
                stack.push(biTree);
                biTree = biTree.left;
            }
            if (!stack.isEmpty()) {
                biTree = stack.pop();
                biTree = biTree.right;
            }
        }
    }

    public static Node insert(Node root, int data) {
        if (root == null) {
            return new Node(data);
        }

        Node cur;
        if (data <= root.data) {
            cur = insert(root.left, data);
            root.left = cur;
        } else {
            cur = insert(root.right, data);
            root.right = cur;
        }
        return root;
    }

    public static Node insertNodes(Node root, int... data) {
        int[] v = data;
        for (int i : data) root = insert(root, i);
        return root;
    }

    static void levelOrder(Node root) {
        //Write your code here
        Map<Integer, List<Node>> map = new LinkedHashMap<Integer, List<Node>>();
        Map<Node, Integer> _map = new LinkedHashMap<Node, Integer>();

        Stack<Node> s = new Stack<Node>();
        Node n = root;
        //s.push(n);
        int level = 0;
        while (n != null || !s.empty()) {
            while (n != null) {
                System.out.println(level + "\t" + n.data);
                final Node _n = n;
                map.compute(level, (Integer k, List<Node> nodeList) -> {
                    if (nodeList == null) nodeList = new LinkedList<Node>();
                    nodeList.add(_n);
                    return nodeList;
                });
                _map.put(n, level);
                level++;
                s.push(n);
                n = n.left;
            }

            if (!s.empty()) {
                n = s.pop();
                level = _map.get(n) + 1;
                n = n.right;
            }

        }

        final StringBuilder sb = new StringBuilder();
        map.keySet().stream().sorted().forEach((Integer k) -> {
            List<Node> nodeList = map.get(k);
            if (nodeList != null) {
                nodeList.stream().forEach((Node node) -> sb.append(" ").append(node.data));
            }
        });

        System.out.println(sb.toString().trim());
    }

    @Test
    public void testLevelPrint() {
        Node tree = insertNodes(null, 9, 20, 50, 35, 44, 9, 15, 62, 11, 13);
        printPreOrder(tree);
        System.out.println();
        levelOrder(tree);
    }

    public static Node search(Node root, int v) {
        if (root == null) return null;

        for (Node n = root; n != null; ) {
            if (v > n.data) n = n.right;
            else if (v < n.data) n = n.left;
            else if (v == n.data) return n;
        }

        return null;
    }


    public static StringBuilder prettyPrint(Node root, int currentHeight, int totalHeight) {
        StringBuilder sb = new StringBuilder();
        int spaces = getSpaceCount(totalHeight - currentHeight + 1);
        if (root == null) {
            //create a 'spatial' block and return it
            String row = String.format("%" + (2 * spaces + 1) + "s%n", "");
            //now repeat this row space+1 times
            String block = new String(new char[spaces + 1]).replace("\0", row);
            return new StringBuilder(block);
        }
        if (currentHeight == totalHeight) return new StringBuilder(root.data + "");
        int slashes = getSlashCount(totalHeight - currentHeight + 1);
        sb.append(String.format("%" + (spaces + 1) + "s%" + spaces + "s", root.data + "", ""));
        sb.append("\n");
        //now print / and \
        // but make sure that left and right exists
        char leftSlash = root.left == null ? ' ' : '/';
        char rightSlash = root.right == null ? ' ' : '\\';
        int spaceInBetween = 1;
        for (int i = 0, space = spaces - 1; i < slashes; i++, space--, spaceInBetween += 2) {
            for (int j = 0; j < space; j++) sb.append(" ");
            sb.append(leftSlash);
            for (int j = 0; j < spaceInBetween; j++) sb.append(" ");
            sb.append(rightSlash + "");
            for (int j = 0; j < space; j++) sb.append(" ");
            sb.append("\n");
        }
        //sb.append("\n");

        //now get string representations of left and right subtrees
        StringBuilder leftTree = prettyPrint(root.left, currentHeight + 1, totalHeight);
        StringBuilder rightTree = prettyPrint(root.right, currentHeight + 1, totalHeight);
        // now line by line print the trees side by side
        Scanner leftScanner = new Scanner(leftTree.toString());
        Scanner rightScanner = new Scanner(rightTree.toString());
//      spaceInBetween+=1;
        while (leftScanner.hasNextLine()) {
            if (currentHeight == totalHeight - 1) {
                sb.append(String.format("%-2s %2s", leftScanner.nextLine(), rightScanner.nextLine()));
                sb.append("\n");
                spaceInBetween -= 2;
            } else {
                sb.append(leftScanner.nextLine());
                sb.append(" ");
                sb.append(rightScanner.nextLine() + "\n");
            }
        }

        return sb;

    }

    public static int getSpaceCount(int height) {
        return (int) (3 * Math.pow(2, height - 2) - 1);
    }

    public static int getSlashCount(int height) {
        if (height <= 3) return height - 1;
        return (int) (3 * Math.pow(2, height - 3) - 1);
    }

    public static int getHeight(Node n) {
        if (n == null) return 0;

        return ((n.left != null || n.right != null) ? 1 : 0) + Math.max(getHeight(n.left), getHeight(n.right));
    }
}
