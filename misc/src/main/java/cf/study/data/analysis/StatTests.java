package cf.study.data.analysis;

import static org.apache.commons.math3.stat.StatUtils.geometricMean;
import static org.apache.commons.math3.stat.StatUtils.mean;
import static org.apache.commons.math3.stat.StatUtils.normalize;
import static org.apache.commons.math3.stat.StatUtils.populationVariance;
import static org.apache.commons.math3.stat.StatUtils.sum;
import static org.apache.commons.math3.stat.StatUtils.variance;

import java.util.Arrays;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.log4j.Logger;
import org.junit.Test;

import misc.MiscUtils;

public class StatTests {
	
	private static final Logger	log	= Logger.getLogger(StatTests.class);

	@Test
	public void testRandoms() {
		MiscUtils.pi2Longs(10).forEach(i -> System.out.print(i + ", "));
	}
	
	private static double[] randoms(int n) {
		return MiscUtils.pi2Longs(n).stream().mapToDouble(_long -> (double)_long).toArray();
	}
	
	@Test
	public void testArithmeticMean() {
		double[] rs = randoms(10);
		log.info(Arrays.toString(rs));
		double mean = mean(rs);
		log.info(String.format("mean is %f", mean));
		
		double sum = DoubleStream.of(rs).sum();
		log.info(String.format("sum(...) = %f / %d = %f", sum, rs.length, sum / rs.length));
		
		rs = DoubleStream.iterate(0.0, d1 -> d1 + 3).limit(10).toArray();
		log.info(Arrays.toString(rs));
		mean = mean(rs);
		log.info(String.format("mean is %f for d + 3", mean));
		
		rs = DoubleStream.iterate(2.0, d1 -> d1 + d1).limit(10).toArray();
		log.info(Arrays.toString(rs));
		mean = mean(rs);
		log.info(String.format("mean is %f for 2 * d", mean));
		
		rs = DoubleStream.iterate(2.0, d1 -> d1 * d1).limit(10).toArray();
		log.info(Arrays.toString(rs));
		mean = mean(rs);
		log.info(String.format("mean is %f for d * d", mean));
	}
	
	@Test
	public void testGeometricMean() {
		double[] rs = randoms(10);
		log.info(Arrays.toString(rs));
		double mean = geometricMean(rs);
		log.info(String.format("mean is %f", mean));
		
		double control = Math.pow(DoubleStream.of(rs).reduce(1.0, (d1, d2) -> (d1 * d2)), 1.0 / rs.length);
		log.info(control);
		
		rs = DoubleStream.iterate(0.0, d1 -> d1 + 6).limit(10).toArray();
		log.info(Arrays.toString(rs));
		mean = geometricMean(rs);
		log.info(String.format("geometric mean is %f for d + 3", mean));
		
		rs = DoubleStream.iterate(2.0, d1 -> d1 * 2).limit(10).toArray();
		log.info(Arrays.toString(rs));
		mean = geometricMean(rs);
		log.info(String.format("geometric mean is %f for 2 * d", mean));
		
		rs = DoubleStream.iterate(2.0, d1 -> d1 * 3).limit(10).toArray();
		log.info(Arrays.toString(rs));
		mean = geometricMean(rs);
		log.info(String.format("geometric mean is %f for 3 * d", mean));
	}
	
	@Test
	public void testNormalization() {
		double[] rs = randoms(10);
		log.info(Arrays.toString(rs));
		double[] normalizeds = normalize(rs);
		log.info(mean(rs));
		IntStream.range(0, rs.length).forEach(i -> log.info(String.format("%f \t %f", rs[i], normalizeds[i])));
	}
	
	
	@Test
	public void testVariance() {
		double[] rs = randoms(7);
		log.info(Arrays.toString(rs));
		double variance = variance(rs);
		log.info(variance);
		
		final double mean = mean(rs);
		double control = DoubleStream.of(rs)
			.map(d -> d - mean)
			.map(_d -> (_d * _d))
			.sum() / rs.length - 1;
		
		log.info(control);
	}
	
	@Test
	public void testPopulationVariance() {
		double[] rs = randoms(7);
		log.info(Arrays.toString(rs));
		double variance = populationVariance(rs);
		log.info(variance);
		
		final double mean = mean(rs);
		double control = DoubleStream.of(rs)
			.map(d -> d - mean)
			.map(_d -> (_d * _d))
			.sum() / rs.length;
		
		log.info(control);
	}
	
	@Test
	public void testSum() {
		double[] rs = randoms(7);
		log.info(Arrays.toString(rs));
		double sum = sum(rs);
		log.info(sum);
	}
	
	@Test
	public void testMedian() {
		double[] rs = randoms(RandomUtils.nextInt() % 20);
		Median m = new Median();
		double evaluate = m.evaluate(rs);
		Arrays.sort(rs);
		log.info(String.format("median of %s \n is %f", Arrays.toString(rs), evaluate));
	}
}
