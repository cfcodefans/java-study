package cf.study.java.algothrims.sorting;

import misc.MiscUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class SortTests<T extends Comparable<? super T>> {

    public Comparator<T> _cmp = (T c1, T c2) -> {
        return ObjectUtils.compare(c1, c2);
    };

    public BiFunction<List<T>, Comparator<T>, List<T>> bubbleSort = (List<T> data, Comparator<T> cmp) -> {
        System.out.println(data);
        if (CollectionUtils.isEmpty(data) || data.size() == 1) {
            return data;
        }

        ArrayList<T> _data = new ArrayList<T>(data);

        for (int i = 0, _i = data.size(); i < _i; i++) {
            for (int j = 0, _j = _i - i - 1; j < _j; j++) {
                if (cmp.compare(_data.get(j), _data.get(j + 1)) > 0) {
                    Collections.swap(_data, j, j + 1);
                }
            }
        }

        return _data;
    };

    public List<T> insertSort(List<T> data, Comparator<T> cmp) {
        System.out.println(data);
        if (CollectionUtils.isEmpty(data) || data.size() == 1) {
            return data;
        }

        final LinkedList<T> linkedList = new LinkedList<T>();

        linkedList.addFirst(data.get(0));

        data.stream().skip(1).forEach(t -> {
            for (ListIterator<T> it = linkedList.listIterator(); it.hasNext(); ) {
                T _t = it.next();
                if (cmp.compare(_t, t) <= 0) {
                    continue;
                }
                it.previous();
                it.add(t);
                return;
            }
            linkedList.addLast(t);
        });

        return linkedList;
    }

    public BiFunction<List<T>, Comparator<T>, List<T>> quickSort = (List<T> data, final Comparator<T> cmp) -> {
        System.out.println(data);
        if (CollectionUtils.isEmpty(data) || data.size() == 1) {
            return data;
        }

        ArrayList<T> _data = new ArrayList<T>(data);

        BiFunction<List<T>, Range<Integer>, List<T>> swap = (List<T> __data, Range<Integer> range) -> {
//			if (range.getMinimum())
            return __data;
        };

        return _data;
    };

    @Test
    public void testBubbleSort() {
        List<Long> data = MiscUtils.pi2Longs(10);
        System.out.println(data);

        SortTests<Long> st = new SortTests<Long>();
        List<Long> sorted = st.bubbleSort.apply(data, st._cmp);

        System.out.println(sorted);
        System.out.println(st.bubbleSort.apply(MiscUtils.pi2Longs(1), st._cmp));
        System.out.println(st.bubbleSort.apply(MiscUtils.pi2Longs(4), st._cmp));
    }

    @Test
    public void testBubbleSort1() {
        SortTests<Long> st = new SortTests<Long>();
        System.out.println(st.bubbleSort.apply(MiscUtils.pi2Longs(400), st._cmp));
    }

    @Test
    public void testInsertSort1() {
        SortTests<Long> st = new SortTests<Long>();
        System.out.println(st.insertSort(MiscUtils.pi2Longs(400), st._cmp));
    }

    @Test
    public void testMaxHeap() {
        LinkedHashSet<Long> set = new LinkedHashSet<>(MiscUtils.pi2Longs(100));
        System.out.println(StringUtils.join(set, ", "));
        Long[] data = set.toArray(new Long[0]);
        MaxHeap.sort(data);
        System.out.println(StringUtils.join(data, ", "));
    }

    public static class MaxHeap {
        public static <T extends Comparable<T>> void sort(T[] data) {
            if (ArrayUtils.isEmpty(data)) return;
            if (data.length == 1) return;
            buildMaxHeap(data);
            for (int i = data.length - 1; i > 0; i--) {
                swap(data, 0, i);
                adjustMaxHeap(data, i, 0);
            }
        }

        public static <T extends Comparable<T>> void buildMaxHeap(T[] data) {
            int start = getParentIdx(data.length - 1);
            for (; start >= 0; start--) {
                adjustMaxHeap(data, data.length, start);
            }
        }

        private static <T extends Comparable<T>> void adjustMaxHeap(T[] data, int length, int index) {
            int left = getLeftChildIdx(index);
            int right = getRightChildIdx(index);
            int max = index;

            if (left < length && data[max].compareTo(data[left]) < 0) {
                max = left;
            }
            if (right < length && data[max].compareTo(data[right]) < 0) {
                max = right;
            }

            if (max != index) {
                swap(data, index, max);
                adjustMaxHeap(data, length, max);
            }
        }

        private static <T extends Comparable<T>> void swap(T[] data, int i, int j) {
            T temp = data[i];
            data[i] = data[j];
            data[j] = temp;
        }

        private static int getLeftChildIdx(int index) {
            return (index << 1) + 1;
        }

        private static int getRightChildIdx(int index) {
            return (index << 1) + 2;
        }

        private static int getParentIdx(int i) {
            return (i - 1) >> 1;
        }
    }

    public static List<Long> bucketSort(List<Long> nums) {
        Long max = nums.stream().max(Comparator.naturalOrder()).get();
        Long min = nums.stream().min(Comparator.naturalOrder()).get();

        int step = 100;
        int bucketNum = (int) (max / step - min / step + 1);
        List<List<Long>> bucketList = new ArrayList<>(bucketNum);
        IntStream.range(0, bucketNum).mapToObj(ArrayList<Long>::new).forEach(bucketList::add);
        for (long num : nums) {
            bucketList.get((int) (num / step)).add(num);
        }
        bucketList.parallelStream().forEach(Collections::sort);
        return bucketList.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    @Test
    public void testBucketSort() {
        List<Long> nums = MiscUtils.pi2Longs(4000, 4);
//        nums.forEach(System.out::println);

        List<Long> sorted = bucketSort(nums);
        sorted.forEach(System.out::println);
    }
}
