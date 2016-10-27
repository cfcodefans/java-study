package cf.study.misc.quiz;
import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Solution1 {

	public static void main(String[] args) {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */
		try (Scanner sc = new Scanner(System.in)) {
			int n = sc.nextInt();
			for (int ti = 0; ti < n; ti ++) {
				sc.nextLine();
				int sn = sc.nextInt();
				int en = sc.nextInt();

				int[] a = new int[sn];
				for (int i = 0; i < sn; i++) a[i] = sc.nextInt();

				int cn = 0;
				for (int t : a) if (t > 0) cn++;

				System.out.println(sn - cn > en ? "YES": "NO");
			}
		}
	}
}