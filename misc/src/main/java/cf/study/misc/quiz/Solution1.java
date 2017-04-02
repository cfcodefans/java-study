package cf.study.misc.quiz;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solution1 {

    public static void main(String[] args) {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */
        try (Scanner sc = new Scanner(System.in)) {
            int n = sc.nextInt();
            for (int ti = 0; ti < n; ti++) {
                sc.nextLine();
                int sn = sc.nextInt();
                int en = sc.nextInt();

                int[] a = new int[sn];
                for (int i = 0; i < sn; i++) a[i] = sc.nextInt();

                int cn = 0;
                for (int t : a) if (t > 0) cn++;

                System.out.println(sn - cn > en ? "YES" : "NO");
            }
        }
    }

    public static void _main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            final List<Integer> collect = IntStream.range(0, sc.nextInt()).mapToObj(i -> Integer.valueOf(sc.nextInt())).collect(Collectors.toList());
            System.out.printf("%.1f\n", collect.stream().reduce((a, b) -> a + b).get() / (float)collect.size());

            int mid = collect.size() / 2;
            Collections.sort(collect);
            float median = collect.get(mid);
            if (collect.size() % 2 == 0) {
                median = (median + collect.get(mid - 1)) / 2f;
            }
            System.out.printf("%.1f\n", median);

            Map<Integer, Integer> elementAndCounts = new HashMap<>();
            collect.forEach(i -> elementAndCounts.compute(i, (Integer k, Integer v) -> (v != null ? v + 1 : 1)));
            Map<Integer, List<Integer>> countAndElements = new HashMap<>();
            elementAndCounts.forEach((k, v) -> countAndElements.compute(v, (_k, _vs) -> _vs == null ? new ArrayList<>() : _vs).add(k));
            final List<Integer> modes = Collections.max(countAndElements.entrySet(), (e1, e2) -> e1.getKey().compareTo(e2.getKey())).getValue();
            System.out.println(Collections.min(modes));
        }
    }
}