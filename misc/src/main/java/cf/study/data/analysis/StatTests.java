package cf.study.data.analysis;

import java.util.Arrays;

import misc.MiscUtils;

import org.apache.commons.math3.stat.StatUtils;
import org.junit.Test;

public class StatTests {
	
	@Test
	public void testRandoms() {
		MiscUtils.pi2Longs(10).forEach(i -> System.out.print(i + ", "));
	}
	
	private static double[] randoms(int n) {
		return MiscUtils.pi2Longs(n).stream().mapToDouble(_long -> (double)_long).toArray();
	}
	
	@Test
	public void testNormalization() {
		double[] rs = randoms(3);
		System.out.println(Arrays.toString(rs));
		double[] normalizeds = StatUtils.normalize(rs);
		System.out.println(Arrays.toString(normalizeds));
	}
	
	@Test
	public void testVariance() {
		double[] rs = randoms(7);
		System.out.println(Arrays.toString(rs));
		double variance = StatUtils.variance(rs);
		System.out.println(variance);
	}
	
	@Test
	public void testSum() {
		double[] rs = randoms(7);
		System.out.println(Arrays.toString(rs));
		double sum = StatUtils.sum(rs);
		System.out.println(sum);
		
		
	}
	
	
	
}
