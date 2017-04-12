package cf.study.pref.cache;

import org.junit.Test;

import cf.study.utils.TimeCounter;

public class CpuCacheTest {
	int[] arr = new int[64 * 1024 * 1024];
	
	private void step(int step) {
		TimeCounter.start();
		System.out.println("step: " + step);
		// Loop 1
		for (int i = 0; i < arr.length; i+=step) arr[i] *= 3;
		TimeCounter.end();
	}
	
	@Test
	public void steps() {
		step(1);
		step(8);
		
		step(1);
		step(8);
	}
}
