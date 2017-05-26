package cf.study.problem99;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by fan on 2017/5/24.
 */
public class ListProblems {
    public static boolean isEmpty(Collection col) {
        return col == null || col.size() == 0;
    }

    //find the last element of a list
    public static <T> T last(List<T> list) {
        return isEmpty(list) ? null : list.get(list.size() - 1);
    }

    public static <T> T last(LinkedList<T> linkedList) {
        return linkedList.getLast();
    }

    public static <T> T lastRecursive(List<T> list) {
        if (isEmpty(list)) return null;
        if (list.size() == 1) return list.iterator().next();
        return lastRecursive(list.subList(1, list.size()));
    }

    //find the second last of a list
    public static <T> T secondLast(List<T> list) {
        if (list.size() < 2) throw new NoSuchElementException("less than 2 elements");
        return list.get(list.size() - 2);
    }

    public static <T> T secondLastRecursive(List<T> list) {
        if (list.size() < 2) throw new NoSuchElementException("less than 2 elements");
        if (list.size() == 2) return list.get(list.size() - 1);
        return secondLastRecursive(list.subList(1, list.size()));
    }

    //find the kth element of a list
    public static <T> T kth(final List<T> list, final int k) {
        return list.get(k);
    }

    public static <T> T kthByRecursive(final List<T> list, final int k) {
        if (k == 0) return list.listIterator().next();
        return kthByRecursive(list.subList(1, list.size()), k - 1);
    }

    public static <T> T kthByStream(List<T> list, int k) {
        return list.stream().limit(k).collect(Collectors.toCollection(LinkedList::new)).getLast();
    }

    //find the number of elements in a list
    public static <T> int len(List<T> list) {
        return list.size();
    }

    public static <T> long lenByStream(List<T> list) {
        return list.stream().count();
    }

    public static <T> int lenByStreamMap(List<T> list) {
        return list.stream().mapToInt(x -> 1).sum();
    }

    public static <T> int lenRecursive(List<T> list) {
        return _lenRecursive(list.listIterator(), 0);
    }

    public static <T> int _lenRecursive(ListIterator<T> listIterator, int s) {
        if (!listIterator.hasNext()) return s;
        listIterator.next();
        return _lenRecursive(listIterator, ++s);
    }

    //reverse a list
    public static <T> List<T> reverse(List<T> list) {
        if (list == null) return null;
        Collections.reverse(list);
        return list;
    }

    public static <T> List<T> reverseForEach(List<T> list) {
        if (list == null) return null;
        List<T> reversed = new ArrayList<>(list.size());
        for (int i = list.size() - 1; i >= 0; i++) {
            reversed.add(list.get(i));
        }
        return reversed;
    }

    public static <T> List<T> reverseByStream(List<T> list) {
        if (list == null) return null;
        return IntStream.iterate(list.size() - 1, i -> i - 1).limit(list.size()).mapToObj(list::get).collect(Collectors.toList());
    }
}
