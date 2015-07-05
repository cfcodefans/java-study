package cf.study.java8.utils;

import java.util.Spliterator.OfInt;
import java.util.stream.IntStream;

import org.junit.Test;

public class SpliteratorTests {

	@Test
	public void study() {
		IntStream range = IntStream.range(0, 10);
		OfInt si = range.spliterator();
		while(si.tryAdvance((Integer i)->System.out.println(i)));
		
		range = IntStream.range(0, 10);
		si = range.spliterator();
		si.forEachRemaining((Integer i)->System.out.println(i));
	}
}
