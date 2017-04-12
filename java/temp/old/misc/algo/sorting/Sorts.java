package cf.study.misc.algo.sorting;

import java.util.Comparator;
import java.util.Random;

import org.apache.commons.collections.ComparatorUtils;
import org.junit.Test;

import cern.colt.Arrays;

public class Sorts {
	
	static class QuickSort {

		public static final Random RND = new Random();

		private static void swap(Object[] array, int i, int j) {
			Object tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}

		private static <E> int partition(E[] array, int begin, int end, Comparator<? super E> cmp) {
			int index = begin + RND.nextInt(end - begin + 1);
			E pivot = array[index];
			swap(array, index, end);
			for (int i = index = begin; i < end; ++i) {
				if (cmp.compare(array[i], pivot) <= 0) {
					swap(array, index++, i);
				}
			}
			swap(array, index, end);
			return (index);
		}

		private static <E> void qsort(E[] array, int begin, int end, Comparator<? super E> cmp) {
			if (end > begin) {
				int index = partition(array, begin, end, cmp);
				qsort(array, begin, index - 1, cmp);
				qsort(array, index + 1, end, cmp);
			}
		}

		public static <E> void sort(E[] array, Comparator<? super E> cmp) {
			qsort(array, 0, array.length - 1, cmp);
		}
	}
	
	@Test
	public void testQuickSort() {
		Integer[] is = new Integer[] {3,1,4,1,5,9,2,6,5,3,5,8,9,7,9,3,2};
		QuickSort.sort(is, ComparatorUtils.naturalComparator());
		System.out.println(Arrays.toString(is));
	}
	
	static class MergeSort {
		
		
		static void merge(Comparable[] cs, Comparable[] tmp, int left, int right, int end) {
			int leftEnd = right - 1;
			int tempPosition = left;
			int leftTmp = left;
			
			while (left <= leftEnd && right <= end) {
				if (cs[left].compareTo(cs[right]) <= 0) {
					tmp[tempPosition] = cs[left];
					left++;
				} else {
					tmp[tempPosition] = cs[right];
					right++;
				}
				 
				tempPosition++;
			}
			
			while (left <= leftEnd) {
				tmp[tempPosition] = cs[left];
				left++;
				tempPosition++;
			}
			
			while (right <= end) {
				tmp[tempPosition] = cs[right];
				right++;
				tempPosition++;
			}
			
			for (int i = leftTmp; i <= end; i++) {
				cs[i] = tmp[i];
			}
		}
		
		static void mergeSort(Comparable[] cs, Comparable[] tmp, int left, int right) {
			if (left >= right) return;
			int center = (left + right) / 2;
			mergeSort(cs, tmp, left, center);
			mergeSort(cs, tmp, center + 1, right);
			merge(cs, tmp, left, center + 1, right);
		}
		
		static void sort(Comparable[] cs) {
			mergeSort(cs, new Comparable[cs.length], 0, cs.length - 1);
		}
	}
	
	@Test
	public void testMergeSort() {
		Integer[] is = new Integer[] {3,1,4,1,5,9,2,6,5,3,5,8,9,7,9,3,2};		
		MergeSort.sort(is);
		System.out.println(Arrays.toString(is));
	}
	
	static class ExternalMergeSort {
		void multiMerge(Comparable[] dest, Comparable[][] multiSources) {
			
		}
	}
}
