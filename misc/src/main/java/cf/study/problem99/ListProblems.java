package cf.study.problem99;


import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

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
        return IntStream.iterate(list.size() - 1, i -> i - 1).limit(list.size()).mapToObj(list::get).collect(toList());
    }

    public static <T> List<T> reverseByCustomStream(ArrayDeque<T> list) {
        if (isEmpty(list)) return Collections.emptyList();
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(list.descendingIterator(), Spliterator.ORDERED), false).collect(toList());
    }

    //6th find out whether a list is a palindrome
    public static <T> boolean isPalindrome(List<T> list) {
        List<T> _list = new ArrayList<>(list);
        reverse(list);
        return Objects.equals(_list, list);
    }

    public static <T> boolean isPalindromeSubList(List<T> list) {
        if (isEmpty(list)) return true;
        final int size = list.size();
        final List<T> firstHalf = list.subList(0, size / 2);
        final List<T> secondHalf = new ArrayList(list.subList(size / 2 + size % 2, size));
        return Objects.equals(firstHalf, reverse(secondHalf));
    }

    public static <T> boolean isPalindromeStack(List<T> list) {
        if (isEmpty(list)) return true;
        final int size = list.size();
        Deque<T> stack = new LinkedList<T>();
        ListIterator<T> it = list.listIterator();
        for (int i = 0, j = size / 2; i < j; i++) {
            stack.push(it.next());
        }
        if (size % 2 == 1) it.next();
        for (; it.hasNext(); ) {
            if (!Objects.equals(stack.pop(), it.next())) return false;
        }
        return true;
    }

    @Test
    public void testIsPalindromeSubList() {
        Assert.assertTrue(isPalindromeSubList(Arrays.asList(1, 2, 3, 2, 1)));
        Assert.assertTrue(isPalindromeSubList(Arrays.asList(1, 2, 1)));
        Assert.assertTrue(isPalindromeSubList(Arrays.asList(1, 2, 2, 1)));
        Assert.assertTrue(isPalindromeSubList(Arrays.asList(1, 1)));

        Assert.assertTrue(isPalindromeStack(Arrays.asList(1, 2, 3, 2, 1)));
        Assert.assertTrue(isPalindromeStack(Arrays.asList(1, 2, 1)));
        Assert.assertTrue(isPalindromeStack(Arrays.asList(1, 2, 2, 1)));
        Assert.assertTrue(isPalindromeStack(Arrays.asList(1, 1)));
    }

    //7th Flatten a nested list structure
    //Transform a list, possibly holding lists as elements into a 'flat' list by replacing each list with its elements (recursively)
    public static <T> List<T> flatten(List<?> list, Class<T> elementType) {
        List<T> flatten = new ArrayList<>();
        list.forEach(e -> {
            if (e instanceof List) {
                flatten.addAll(flatten((List) e, elementType));
            } else {
                flatten.add(elementType.cast(e));
            }
        });
        return flatten;
    }

    public static <T> List<T> flattenByStream(List<?> list, Class<T> elementType) {
        return list.stream()
            .flatMap(e -> e instanceof List ? flattenByStream((List) e, elementType).stream() : Stream.of(e))
            .map(e -> elementType.cast(e))
            .collect(toList());
    }

    /**
     * 8th
     * <b>(**) Eliminate consecutive duplicates of list elements</b>
     * <p>If a list contains repeated elements they should be replaced with a single copy of the element.
     * The order of the elements should not be changed.</p>
     * <p>
     * <pre>
     *     compress(Arrays.asList(a,a,a,a,b,c,c,a,a,d,e,e,e,e))
     *     [a,b,c,d,e]
     * </pre>
     */
    public static <T> List<T> compress(List<T> list) {
        if (isEmpty(list)) return Collections.emptyList();
        List<T> reList = new ArrayList<>();
        T last = null;
        for (T e : list) {
            if (!Objects.equals(e, last)) {
                reList.add(e);
                last = e;
            }
        }
        return reList;
    }

    /**
     * 9th
     * <b>(**) Pack consecutive duplicates of list elements into sublists</b>
     * <p>If a list contains repeated elements they should be placed in separate sublists.</p>
     * <p>
     * <pre>
     *     pack(Arrays.asList("a", "a", "a", "a", "b", "c", "c", "a", "a", "d", "e", "e", "e", "e"))
     *     [["a","a","a","a"],["b"],["c","c"],["a","a"],["d"],["e","e","e","e"]]
     * </pre>
     */
    public static <T> List<List<T>> pack(List<T> list) {
        if (isEmpty(list)) return Collections.emptyList();
        List<List<T>> reList = new ArrayList<>();
        List<T> elementList = new ArrayList<>();
        T last = null;
        for (T e : list) {
            if (!Objects.equals(e, last)) {
                elementList = new ArrayList<>();
                reList.add(elementList);
                last = e;
            }
            elementList.add(e);
        }
        return reList;
    }

    @Test
    public void testPack() {
        System.out.println(pack(pack(Arrays.asList("a", "a", "a", "a", "b", "c", "c", "a", "a", "d", "e", "e", "e", "e"))));
    }

    /**
     * 10th
     * <b> (*) Run-length encoding of a list.</b>
     */
    public static <T> List<AbstractMap.SimpleEntry<Integer, T>> encode(List<T> list) {
        return pack(list).stream().map(_list -> new AbstractMap.SimpleEntry<>(_list.size(), _list.get(0))).collect(toList());
    }

    public static <T> List<Object> encode_(List<T> list) {
        return pack(list).stream()
            .map(_list -> _list.size() == 1 ? _list.get(0) : new AbstractMap.SimpleEntry<>(_list.size(), _list.get(0)))
            .collect(toList());
    }

    @Test
    public void testEncode_() {
        System.out.println(encode_(Arrays.asList("a", "a", "a", "a", "b", "c", "c", "a", "a", "d", "e", "e", "e", "e")));
    }

    /**
     * <b>(*) Modified run-length encoding</b>
     * <pre>
     *      encode_modified(Arrays.asList("a", "a", "a", "a", "b", "c", "c", "a", "a", "d", "e", "e", "e", "e"))
     * </pre>
     */
    public static <T> List<T> decode(List<Object> encoded) {
        return encoded.stream().flatMap(e -> {
            if (e instanceof AbstractMap.SimpleEntry) {
                AbstractMap.SimpleEntry<Integer, T> se = (AbstractMap.SimpleEntry) e;
                return Collections.nCopies(se.getKey(), se.getValue()).stream();
            }
            return Stream.of((T) e);
        }).collect(Collectors.toList());
    }

    /**
     * 14th
     * (*) Duplicate the elements of a list
     * <pre>
     *          duplicate(Arrays.asList("a", "b", "c", "d"))
     * </pre>
     */
    public static <T> List<T> duplicate(List<T> list) {
        return list.stream().flatMap(e -> Stream.of(e, e)).collect(toList());
    }

    public static <T> List<T> duplicate(List<T> list, int n) {
        return list.stream().flatMap(e -> Collections.nCopies(n, e).stream()).collect(toList());
    }

    /**
     * 16th
     * <b><(**) Drop every N'th element from a list/b>
     */
    public static <T> List<T> dropEveryNth(List<T> list, int n) {
        if (n <= 0 || n >= list.size()) return list;
        List<T> reList = new ArrayList<>(list.size() - list.size() / n + 1);
        int i = 0;
        ListIterator<T> li = list.listIterator();
        while (li.hasNext()) {
            final T e = li.next();
            if (i++ % n == 0) continue;
            reList.add(e);
        }
        return reList;
    }

    /**
     * 17th
     * <b>(*) Split a list into two parts; the length of the first part is given</b>
     */
    public static <T> List<T>[] split(List<T> list, int n) {
        if (n <= 0 || n >= list.size()) return new List[]{list};
        return new List[]{list.subList(0, n), list.subList(n, list.size())};
    }

    /**
     * 18th
     * (**) Extract a slice from a list
     */
    public static <T> List<T> slice(List<T> list, int start, int end) {
        return list.subList(start, end);
    }

    /**
     * 19th
     * (**) Rotate a list N places to the left
     */
    public static <T> List<T> rotate(List<T> list, int n) {
        final int size = list.size();
        n = n % size;
        return n == 0 ? new ArrayList<>(list) : Stream.of(list.subList(size - n, size), list.subList(0, size - n)).flatMap(List::stream).collect(toList());
    }

    @Test
    public void testRotate() {
        List list = IntStream.range(0, 10).mapToObj(Integer::valueOf).collect(toList());
        System.out.println(list);
        System.out.println(rotate(list, 4));
        System.out.println(rotate(list, 6));
        System.out.println(rotate(list, 16));
    }

    /**
     * 20th
     * (*) Remove the K'th element from a list
     */
    public static <T> List<T> removeAt(List<T> list, int k) {
        if (k <= 0 || k >= list.size()) return new ArrayList<>(list);
        list.remove(k);
        return list;
    }

    /**
     * 22th
     * (*) Create a list containing all integers within a given range
     */
    public static List<Integer> range(int start, int end) {
        return IntStream.rangeClosed(start, end).boxed().collect(toList());
    }

    /**
     * 23th
     * (**) Extract a given number of randomly selected elements from a list
     */
    public static <T> List<T> randomSelect(List<T> list, int n) {
        return new Random().ints(n, 0, list.size()).mapToObj(list::get).collect(toList());
    }

    public static <T> List<T> randomSelectRecursive(List<T> list, int n, List<T> reList) {
        if (reList == null) reList = new ArrayList<>(n);
        if (n == 0) return reList;
        reList.add(list.remove(new Random().nextInt(list.size())));
        return randomSelectRecursive(list, n - 1, reList);
    }

    @Test
    public void testRandomSelectRecoursive() {
        System.out.println(randomSelectRecursive(range(0, 9), 3, null));
        System.out.println(randomSelectRecursive(range(0, 9), 4, null));
        System.out.println(randomSelectRecursive(range(0, 9), 5, null));
        System.out.println(randomSelectRecursive(range(0, 9), 6, null));
    }
}
