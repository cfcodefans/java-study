package cf.study.java8.utils;

import java.util.Spliterator;
import java.util.Spliterator.OfInt;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.junit.Test;

class TaggedArray<T> {
	private final Object[] elements; // immutable after construction
	public TaggedArray(T[] data, Object[] tags) {
		int size = data.length;
		if (tags.length != size)
			throw new IllegalArgumentException();
		
		elements = new Object[2 * size];
		IntStream.range(0, size).forEach(i -> {
			elements[i * 2] = data[i];
			elements[i * 2 + 1] = tags[i];
		});
	}
	
	public Spliterator<T> spliterator() {
		return new TaggedArraySpliterator<>(elements, 0, elements.length);
	}
	
	static class TaggedArraySpliterator<T> implements Spliterator<T> {
		private final Object[] array;
		private int origin; // current index, advanced on split or traversal
		private final int fence; // one past the greatest index
		
		TaggedArraySpliterator(Object[] _array, int _origin, int _fence) {
			array = _array;
			origin = _origin;
			fence = _fence;
		}

		public void forEachRemaining(Consumer<? super T> action) {
			for (; origin < fence; origin += 2) {
				action.accept((T) array[origin]);
			}
		};
		
		@Override
		public boolean tryAdvance(Consumer<? super T> action) {
			if (origin < fence) {
				action.accept((T) array[origin]);
				origin += 2;
				return true;
			}
			return false;
		}

		@Override
		public Spliterator<T> trySplit() {
			int lo = origin; // divide range in half
			int mid = ((lo + fence) >>> 1) & ~1; // force midpoint to be even
			if (lo < mid) { //split out left half
				origin = mid; // reset this Spliterator's origin
				return new TaggedArraySpliterator(array, lo, mid);
			}
			return null;
		}

		@Override
		public long estimateSize() {
			return (long)((fence - origin) / 2);
		}

		@Override
		public int characteristics() {
			return ORDERED | SIZED | IMMUTABLE | SUBSIZED;
		}
	}
}

public class SpliteratorTests {

	@Test
	public void testTryAdvance() {
		IntStream range = IntStream.range(0, 10);
		OfInt si = range.spliterator();
		while(si.tryAdvance((Integer i)->System.out.println(i)));
	}
	
	@Test
	public void testTrySplit() {
		IntStream range = IntStream.range(0, 10);
		OfInt si = range.spliterator();
		si.trySplit().forEachRemaining((Integer i)->System.out.println(i));
		System.out.println();
		si.trySplit().forEachRemaining((Integer i)->System.out.println(i));
	}
	
	@Test
	public void testForEachRemaining() {
		IntStream range = IntStream.range(0, 10);
		OfInt si = range.spliterator();
		si.forEachRemaining((Integer i)->System.out.println(i));
	}
	
}
