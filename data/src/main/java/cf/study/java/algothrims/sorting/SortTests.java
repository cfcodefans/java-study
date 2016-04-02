package cf.study.java.algothrims.sorting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.BiFunction;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Range;
import org.junit.Test;

import misc.MiscUtils;



public class SortTests <T extends Comparable<? super T>> {
	
	public Comparator<T> _cmp = (T c1, T c2)->{
		return ObjectUtils.compare(c1, c2);
	};
	
	public BiFunction<List<T>, Comparator<T>, List<T>> bubbleSort = (List<T> data, Comparator<T> cmp)->{
		System.out.println(data);
		if (CollectionUtils.isEmpty(data) || data.size() == 1) {
			return data;
		}
		
		ArrayList<T> _data = new ArrayList<T>(data);
		
		for (int i = 0, _i = data.size(); i < _i; i++) {
			for (int j = 0, _j = _i - i - 1; j < _j; j++) {
				if (cmp.compare(_data.get(j), _data.get(j+1)) > 0) {
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
			for (ListIterator<T> it = linkedList.listIterator(); it.hasNext();) {
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
	};
	
	public BiFunction<List<T>, Comparator<T>, List<T>> quickSort = (List<T> data, final Comparator<T> cmp)->{
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
}
