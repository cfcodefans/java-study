package cf.study.data.analysis;

import misc.ProcTrace;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CombinationTests {
    static int line = 1;

    static ConcurrentLinkedQueue<Collection<Integer>> buf = new ConcurrentLinkedQueue<>();

    private static void collect(Collection<Integer> combination) {
        buf.offer(combination);
    }

    static void iterateForCombination(List<Integer> src, Collection<Integer> combination, int c) {
        if (c == 1) {
            for (Integer e : src) {
                final ArrayList<Integer> _combination = copyOnAppend(combination, e);
                collect(_combination);
            }
            return;
        }

        for (int i = 0, j = src.size(); i < j; i++) {
            if (j - i <= c) {
                combination.addAll(src.subList(i, src.size()));
                collect(combination);
                break;
            }
            final Integer e = src.get(i);
            final ArrayList<Integer> _combination = copyOnAppend(combination, e);
            iterateForCombination(src.subList(i + 1, j), _combination, c - 1);
        }
    }

    private static ArrayList<Integer> copyOnAppend(Collection<Integer> combination, Integer e) {
        final ArrayList<Integer> _combination = new ArrayList<>(combination.size() + 1);
        _combination.addAll(combination);
        _combination.add(e);
        return _combination;
    }

    static class StackFrame {
        List<Integer> src;
        Collection<Integer> combination;
        int c;

        public StackFrame(List<Integer> list, Collection<Integer> combination, int c) {
            this.src = list;
            this.combination = combination;
            this.c = c;
        }
    }

    static void iterateForCombinationWithStack(List<Integer> _src, int _c) {
        ArrayDeque<StackFrame> stack = new ArrayDeque<>(1000000);
        stack.push(new StackFrame(_src, new ArrayList<>(_c), _c));
        while (!stack.isEmpty()) {
            StackFrame sf = stack.pop();

            List<Integer> src = sf.src;
            int c = sf.c;
            Collection<Integer> combination = sf.combination;

            if (c == 1) {
                for (Integer e : src) {
                    final ArrayList<Integer> _combination = copyOnAppend(combination, e);
                    collect(_combination);
                }
                continue;
            }

            for (int _i = 0, j = src.size(); _i < j; _i++) {
                if (j - _i <= c) {
                    combination.addAll(src.subList(_i, src.size()));
                    collect(combination);
                    break;
                }
                final Integer e = src.get(_i);
                final ArrayList<Integer> _combination = copyOnAppend(combination, e);
                stack.push(new StackFrame(src.subList(_i + 1, j), _combination, c - 1));
            }
        }
    }

    public static void main(String[] args) {
        List<Integer> list = IntStream.range(1, 20).mapToObj(Integer::valueOf).collect(Collectors.toList());
        System.out.println(list);
        int r = 8;

        for (int i = 0; i < 10; i++) {
            ProcTrace.start();
            ProcTrace.ongoing("start");

            iterateForCombinationsWithForkJoin(list, r);
            ProcTrace.ongoing("start forkjoin");

            buf = new ConcurrentLinkedQueue<>();
            iterateForCombination(list, new ArrayList<>(), r);
            ProcTrace.ongoing("start recursive");

            buf = new ConcurrentLinkedQueue<>();
            iterateForCombinationWithStack(list, r);
            ProcTrace.ongoing("start stack");
            ProcTrace.end();
            System.out.println(buf.size());
//            buf.forEach(combination -> System.out.printf("%d\t%s\n", line++, combination));
            System.out.println(ProcTrace.flush());
        }
    }

    private static void iterateForCombinationsWithForkJoin(List<Integer> list, int r) {
        final CombinationTask ct = new CombinationTask(list, new ArrayList<>(r), r);
        ct.invoke();
        try { ct.get(); } catch (Exception e) { e.printStackTrace(); }
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
}
