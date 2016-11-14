package cf.study.data.analysis;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * Created by fan on 2016/11/14.
 */
public class LinearStudies {
	private static final Logger log = LogManager.getLogger(LinearStudies.class);

	static ArrayRealVector randomVector(int dims, int high, int low) {
		double[] data = new double[dims];
		for (int i = 0, j = data.length, bound = high - low; i < j; i++) {
			data[i] = RandomUtils.nextInt(bound) + low;
		}
		return new ArrayRealVector(data);
	}

	static Array2DRowRealMatrix randomMatrix(int cols, int rows, int high, int low) {
		double[][] data = new double[rows][cols];
		int bound = high - low;
		for (int ri = 0, rc = data.length; ri < rc; ri++) {
			data[ri] = new double[cols];
			for (int ci = 0, cc = data[ri].length; ci < cc; ci++) {
				data[ri][ci] = RandomUtils.nextInt(bound) + low;
			}
		}

		return new Array2DRowRealMatrix(data);
	}

	static String format(RealVector rv) {
		return StringUtils.join(DoubleStream.of(rv.toArray()).iterator(), "\t");
	}

	static String format(RealMatrix rm) {
		StringBuilder sb = new StringBuilder("\n");

		double[][] data = rm.getData();
		for (double[] row : data) {
			sb.append(StringUtils.join(row, '\t')).append('\n');
		}
		return sb.toString();
	}

	@Test
	public void testOuterProduct() {
		RealVector rv1 = new ArrayRealVector(new double[]{1, 2});
		log.info(rv1);

		log.info(rv1.outerProduct(rv1));
	}

	@Test
	public void testMapMultiply() {
		RealVector rv1 = new ArrayRealVector(new double[]{1, 2});
		log.info(rv1);
		log.info(rv1.mapMultiply(3));
	}

	@Test
	public void testMapAdd() {
		RealVector rv1 = new ArrayRealVector(new double[]{1, 2});
		log.info(rv1);
		log.info(rv1.mapAdd(3));
	}

	@Test
	public void testDotMultiply() {
		// greater the dot product, more distant between the two vectors
		RealVector rv2 = new ArrayRealVector(new double[]{4, 4});
		{
			RealVector rv1 = new ArrayRealVector(new double[]{1, 4});
			double v = rv1.dotProduct(rv2);
			log.info(String.format("%s x %s = %s", rv1, rv2, v));
		}

		{
			RealVector rv1 = new ArrayRealVector(new double[]{4, 2});
			double v = rv1.dotProduct(rv2);
			log.info(String.format("%s x %s = %s", rv1, rv2, v));
		}

		{
			RealVector rv1 = new ArrayRealVector(new double[]{-4, 0});
			double v = rv1.dotProduct(rv2);
			log.info(String.format("%s x %s = %s", rv1, rv2, v));
		}

		{
			RealVector rv1 = new ArrayRealVector(new double[]{-4, -4});
			double v = rv1.dotProduct(rv2);
			log.info(String.format("%s x %s = %s", rv1, rv2, v));
		}
	}

	@Test
	public void testMatrixMultiply() {
		Array2DRowRealMatrix movieAndFeatures = new Array2DRowRealMatrix(
			new double[][]{{5, 2, 2},
				{2, 5, 2},
				{2, 2, 5}});

		Array2DRowRealMatrix featureAndUsers = new Array2DRowRealMatrix(
			new double[][]{{5, 0}, {2, 2}, {0, 5}});
		log.info(format(movieAndFeatures));
		log.info(format(featureAndUsers));
		log.info(format(movieAndFeatures.multiply(featureAndUsers)));
	}

	@Test
	public void testALS() { //Alternating least squares matrix factorization
		Array2DRowRealMatrix R = randomMatrix(3, 3, 6, 0);
		log.info(format(R));

		ArrayRealVector users = new ArrayRealVector(IntStream.range(0, 3).asDoubleStream().toArray());
		log.info("users:\n\t" + format(users));

		ArrayRealVector movies = new ArrayRealVector(IntStream.range(0, 3).asDoubleStream().toArray());
		log.info("movies:\n\t" + format(movies));

		for (int i = 0; i < 2; i++) {
			
		}

	}

}
