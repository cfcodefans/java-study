package cf.study.java.algothrims;

public class JoinCollectionExample {

    int[] preNodes = new int[1000];

    int find(int x) { //look for root
        int r = x;
        while (preNodes[r] != r) {
            r = preNodes[r];
        }
        int i = x, j;
        while (i != r) {
            j = preNodes[i];
            preNodes[i] = r;
            i = j;
        }
        return r;
    }

    void join(int x, int y) {
        int fx = find(x), fy = find(y);
        if (fx != fy) {
            preNodes[fx] = fy;
        }
    }
}
