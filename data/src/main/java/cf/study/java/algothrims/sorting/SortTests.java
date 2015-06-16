package cf.study.java.algothrims.sorting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.junit.Test;

import misc.MiscUtils;



public class SortTests <T extends Comparable<?>> {
	
	public Comparator<T> _cmp = (T c1, T c2)->{
		return ObjectUtils.compare(c1, c2);
	};
	
	public BiFunction<List<T>, Comparator<T>, List<T>> bubbleSort = (List<T> data, Comparator<T> cmp)->{
		if (CollectionUtils.isEmpty(data) || data.size() == 1) {
			return data;
		}
		
		ArrayList<T> _data = new ArrayList(data);
		
		for (int i = 0, _i = data.size(); i < _i; i++) {
			for (int j = 0, _j = _i - i - 1; j < _j; j++) {
				if (cmp.compare(_data.get(j), _data.get(j+1)) > 0) {
					Collections.swap(_data, j, j + 1);
				}
			}
		}
		
		return _data;
	};
	
	@Test
	public void testBubbleSort() {
		List<Long> data = MiscUtils.pi2Longs(10);
		System.out.println(data);
		
		SortTests<Long> st = new SortTests<Long>();
		List<Long> sorted = st.bubbleSort.apply(data, st._cmp);
		
		System.out.println(sorted);
	}
}
