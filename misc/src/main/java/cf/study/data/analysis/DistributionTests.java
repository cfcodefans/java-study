package cf.study.data.analysis;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import misc.MiscUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;
import org.junit.Test;

public class DistributionTests {
	
	static List<Integer> rds = MiscUtils.pi2Longs(101).stream().map(Long::intValue).collect(Collectors.toList());
	
	static RandomGenerator rg = new RandomGenerator() {
		Iterator<Integer> it = rds.listIterator();
		
		@Override
		public void setSeed(int seed) {
		}

		@Override
		public void setSeed(int[] seed) {
		}

		@Override
		public void setSeed(long seed) {
		}

		@Override
		public void nextBytes(byte[] bytes) {
		}

		@Override
		public int nextInt() {
			return it.next().intValue();
		}

		@Override
		public int nextInt(int n) {
			return nextInt();
		}

		@Override
		public long nextLong() {
			return it.next();
		}

		@Override
		public boolean nextBoolean() {
			return it.next() % 2 == 0;
		}

		@Override
		public float nextFloat() {
			return it.next().floatValue();
		}

		@Override
		public double nextDouble() {
			return it.next().doubleValue();
		}

		@Override
		public double nextGaussian() {
			return 0;
		}
		
	};

	private static final Logger	log	= Logger.getLogger(DistributionTests.class);
	
	@Test
	public void testRandoms() {
		log.info(String.format("%d is less than 5", rds.stream().filter(r -> r < 5).count()));
		log.info(String.format("%d is great than 5", rds.stream().filter(r -> r > 5).count()));
		log.info(String.format("%d is equal to 5", rds.stream().filter(r -> r == 5).count()));
		
		log.info(String.format("%d is even", rds.stream().filter(r -> r % 2 == 0).count()));
		log.info(String.format("%d is odd", rds.stream().filter(r -> r % 2 != 0).count()));
		
		rds.stream().distinct().sorted().forEach(i -> log.info(String.format("%d appears %d times", i, CollectionUtils.countMatches(rds, (v)->v==i))));
	}

	@Test
	public void testBinomialDist() {
		BinomialDistribution bd = new BinomialDistribution(10, 0.5);
		
		List<Integer> samples = Stream.of(ArrayUtils.toObject(bd.sample(100))).collect(Collectors.toList());
		log.info(samples);
		
		List<Integer> ds = samples.stream().distinct().sorted().collect(Collectors.toList());
		
		ds.forEach(i -> log.info(String.format("%d\t%d\t%f", i, CollectionUtils.countMatches(samples, (v)->i.equals(v)), bd.probability(i))));
		
		log.info("");
	}
}
