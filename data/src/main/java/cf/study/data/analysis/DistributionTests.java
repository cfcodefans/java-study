package cf.study.data.analysis;

import misc.MiscUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DistributionTests {

	static List<Integer> rds = MiscUtils.pi2Longs(101).stream().map(Long::intValue).collect(Collectors.toList());

	private static final Logger log = Logger.getLogger(DistributionTests.class);

	@Test
	public void testRandoms() {
		IntStream is = rds.stream().mapToInt(r -> r);

		log.info(String.format("%d is less than 5", is.filter(r -> r < 5).count()));
		log.info(String.format("%d is great than 5", is.filter(r -> r > 5).count()));
		log.info(String.format("%d is equal to 5", is.filter(r -> r == 5).count()));

		log.info(String.format("%d is even", is.filter(r -> r % 2 == 0).count()));
		log.info(String.format("%d is odd", is.filter(r -> r % 2 != 0).count()));

		is.distinct().sorted().forEach(i -> log.info(String.format("%d appears %d times", i, IterableUtils.countMatches(rds, (v) -> v == i))));
	}

	@Test
	public void testBinomialDist() {
		BinomialDistribution bd = new BinomialDistribution(10, 0.5);

		List<Integer> samples = Stream.of(ArrayUtils.toObject(bd.sample(100))).collect(Collectors.toList());
		log.info(samples);

		List<Integer> ds = samples.stream().distinct().sorted().collect(Collectors.toList());

		ds.forEach(i -> log.info(String.format("%d\t%d\t%f", i, IterableUtils.countMatches(samples, (v) -> i.equals(v)), bd.probability(i))));

		log.info("");
	}
}
