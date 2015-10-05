package cf.study.jdk7.util;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.junit.Test;

public class CollectionTest {
	@Test
	public void testCircalurBuffer() {
		CircularFifoBuffer cfq = new CircularFifoBuffer(10);
		System.out.println(cfq.maxSize());
		for (int i = 0; i < 11; i++) {
			cfq.add(new Integer(i));
		}
		
		Iterator<Integer> it = cfq.iterator();
//		for (; it.hasNext();) {
//			System.out.println(it.next());
//		}
		
		for (int i = 0; i < 20; i++) {
			System.out.println(it.next());
		}
	}
	
	@Test
	public void testArrayBlockingQueue() {
		ArrayBlockingQueue<Integer> abq = new ArrayBlockingQueue<>(10);
		for (int i = 0; i < 10; i++) {
			abq.add(i);
		}
		
		Iterator<Integer> it = abq.iterator();
		for (int i = 0; i < 20; i++) {
			System.out.println(it.next());
		}
	}
}
