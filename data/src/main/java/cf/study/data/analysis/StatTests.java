package cf.study.data.analysis;

import misc.MiscUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.apache.commons.math3.stat.StatUtils.*;

public class StatTests {

	private static final Logger log = LogManager.getLogger(StatTests.class);

	@Test
	public void testRandoms() {
		MiscUtils.pi2Longs(10).forEach(i -> log.info(i + ", "));
	}

	private static double[] randoms(int n) {
		return MiscUtils.pi2Longs(n).stream().mapToDouble(_long -> (double) _long).toArray();
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
		log.info(String.format("geometric mean is (x1 * x2 * ... xn) ^ (1 / n) = %f", mean));

		double control = Math.pow(DoubleStream.of(rs).reduce(1.0, (d1, d2) -> (d1 * d2)), 1.0 / rs.length);
		log.info(control);

		rs = DoubleStream.iterate(0.0, d1 -> d1 + 6).limit(10).toArray();
		log.info(Arrays.toString(rs));
		mean = geometricMean(rs);
		log.info(String.format("geometric mean is %f for d + 6", mean));

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
		double[] normalized = normalize(rs);
		log.info(mean(rs));
		IntStream.range(0, rs.length).forEach(i -> log.info(String.format("%f \t %f", rs[i], normalized[i])));
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
		double[] rs = randoms(RandomUtils.nextInt(9, 19));
		Median m = new Median();
		double evaluate = m.evaluate(rs);
		Arrays.sort(rs);
		log.info(String.format("median of %s \n is %f", Arrays.toString(rs), evaluate));
	}

//	def weightedMean(): Unit = {
//        import java.util.Scanner
//		val scan: Scanner = new Scanner(System.in)
//		try {
//			val n: Int = scan.nextInt
//			val X: Array[Int] = scan.nextLine().split(" ").map(_.toInt).toArray
//			val W: Array[Int] = scan.nextLine().split(" ").map(_.toInt).toArray
//			println("%.1f".format(X.zipAll(W, 0, 0).map((x_w)=> x_w._1 * x_w._2).sum / n.toFloat))
//		} finally {
//			scan.close
//		}
//	}

	@Test
	public void weightedMean() {
		try (Scanner scan = new Scanner(System.in)) {
			int n = scan.nextInt();
			scan.nextLine();
			int[] X = Stream.of(scan.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
			int[] W = Stream.of(scan.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
			int s1 = IntStream.range(0, n).map(i -> X[i] * W[i]).sum();
			System.out.printf("%.1f\n", s1 / (float)IntStream.of(W).sum());
		}
	}
}
