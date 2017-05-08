package cf.study.data.analysis;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CombinationTest {
    static ConcurrentLinkedQueue<Collection<Integer>> buf = new ConcurrentLinkedQueue<>();

    private static void collect(Collection<Integer> combination) {
        buf.offer(combination);
    }

    private static void iterateForCombinationsWithForkJoin(List<Integer> list, int r) {
        final CombinationTask ct = new CombinationTask(list, new ArrayList<>(r), r);
        ct.invoke();
        try { ct.join(); } catch (Exception e) { e.printStackTrace(); }
    }

    static class CombinationTask extends RecursiveAction {
        List<Integer> src;
        Collection<Integer> combination;
        int c;

        public CombinationTask(List<Integer> list, Collection<Integer> combination, int c) {
            this.src = list;
            this.combination = combination;
            this.c = c;
        }

        @Override
        protected void compute() {
            if (c == 1) {
                for (Integer e : src) {
                    final ArrayList<Integer> _combination = copyOnAppend(this.combination, e);
                    collect(_combination);
                }
                return;
            }

            List<CombinationTask> taskList = new ArrayList<>(c);
            for (int i = 0, j = src.size(); i < j; i++) {
                if (j - i <= c) {
                    combination.addAll(src.subList(i, j));
                    collect(combination);
                    break;
                }
                final Integer e = src.get(i);
                final ArrayList<Integer> _combination = copyOnAppend(this.combination, e);
                taskList.add(new CombinationTask(src.subList(i + 1, j), _combination, c - 1));
            }
            invokeAll(taskList);
        }
    }

    private static ArrayList<Integer> copyOnAppend(Collection<Integer> combination, Integer e) {
        final ArrayList<Integer> _combination = new ArrayList<>(combination.size() + 1);
        _combination.addAll(combination);
        _combination.add(e);
        return _combination;
    }

    public static void main(String[] args) {
        List<Integer> list = IntStream.range(1, 6).mapToObj(Integer::valueOf).collect(Collectors.toList());
        System.out.println(list);
        int r = 3;
        iterateForCombinationsWithForkJoin(list, r);
        buf.forEach(combination -> System.out.printf("%s\n", combination));
    }
}
