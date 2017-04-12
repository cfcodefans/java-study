package cf.study.data.analysis;

import misc.ProcTrace;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CombinationTests {
    static int line = 1;

    static ConcurrentLinkedQueue<Set<Integer>> buf = new ConcurrentLinkedQueue<>();

    private static void collect(Set<Integer> combination) {
        buf.offer(combination);
    }

    static void iterateForCombination(List<Integer> src, Set<Integer> combination, int c) {
        for (int i = 0, j = src.size(); i < j; i++) {
            if (j - i <= c) {
                combination.addAll(src.subList(i, j));
                collect(combination);
                return;
            }
            final Integer e = src.get(i);
            final HashSet<Integer> _combination = new HashSet<>(combination);
            _combination.add(e);
            if (c == 1) {
                collect(_combination);
                continue;
            }
            iterateForCombination(src.subList(i + 1, j), _combination, c - 1);
        }
    }

    static class StackFrame {
        List<Integer> src;
        Set<Integer> combination;
        int c;

        public StackFrame(List<Integer> list, Set<Integer> combination, int c) {
            this.src = list;
            this.combination = combination;
            this.c = c;
        }
    }

    static void iterateForCombinationWithStack(List<Integer> _src, Set<Integer> _combination, int _c) {
        ArrayDeque<StackFrame> stack = new ArrayDeque<>(100);
        stack.push(new StackFrame(_src, _combination, _c));
        while (!stack.isEmpty()) {
            StackFrame sf = stack.pop();
            List<Integer> src = sf.src; int c = sf.c; Set<Integer> combination = sf.combination;

            for (int _i = 0, j = src.size(); _i < j; _i++) {
                if (j - _i <= c) {
                    combination.addAll(src.subList(_i, src.size()));
                    collect(combination);
                    break;
                }
                final Integer e = src.get(_i);
                combination.add(e);
                if (c == 1) {
                    collect(combination);
                    combination.remove(e);
                    continue;
                }
                stack.push(new StackFrame(src.subList(_i + 1, j), new HashSet<>(combination), c - 1));
                combination.remove(e);
            }
        }
    }

    public static void main(String[] args) {
        List<Integer> list = IntStream.range(1, 10).mapToObj(Integer::valueOf).collect(Collectors.toList());
        System.out.println(list);
        int r = 5;
        ProcTrace.start("start ");



        ProcTrace.ongoing("start forkjoin");
        final CombinationTask ct = new CombinationTask(list, new HashSet<>(), r);
        ct.invoke();
        try {
            ct.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ProcTrace.ongoing("start recursive");
        iterateForCombination(list, new HashSet<>(), r);

        ProcTrace.ongoing("start stack");
        iterateForCombinationWithStack(list, new HashSet<>(), r);

        ProcTrace.ongoing("done");
        ProcTrace.end();
        System.out.println(buf.size());
//        buf.forEach(combination -> System.out.printf("%d\t%s\n", line++, combination));
        System.out.println(ProcTrace.flush());
    }

    static class CombinationTask extends RecursiveAction {
        List<Integer> src;
        Set<Integer> combination;
        int c;

        public CombinationTask(List<Integer> list, Set<Integer> combination, int c) {
            this.src = list;
            this.combination = combination;
            this.c = c;
        }

        @Override
        protected void compute() {
            List<CombinationTask> taskList = new ArrayList<>();
            for (int i = 0, j = src.size(); i < j; i++) {
                if (j - i <= c) {
                    combination.addAll(src.subList(i, j));
                    collect(combination);
                    break;
                }

                final Integer e = src.get(i);
                combination.add(e);
                if (c == 1) {
                    collect(combination);
                    combination.remove(e);
                    continue;
                }
                taskList.add(new CombinationTask(src.subList(i + 1, j), new HashSet<>(combination), c - 1));
                combination.remove(e);
            }
            invokeAll(taskList);
        }
    }
}
