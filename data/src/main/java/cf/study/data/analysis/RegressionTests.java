package cf.study.data.analysis;

import org.apache.commons.math3.stat.regression.RegressionResults;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.stream.IntStream;

public class RegressionTests {

	static Logger log = LogManager.getLogger(RegressionTests.class);

	@Test
	public void testSimpleRegression1() {
		SimpleRegression sr = new SimpleRegression();
		IntStream.range(0, 100).forEachOrdered(i -> sr.addData(i, i * 3));
		RegressionResults re = sr.regress();
		log.info("N:\t{}\tR:\t{}", re.getN(), sr.getR());
		log.info("predict: 303 = {}", sr.predict(303));
	}

	@Test
	public void testSimpleRegression2() {
		SimpleRegression sr = new SimpleRegression();
		sr.addData(1, 3);
		sr.addData(2, 5);
		sr.addData(3, 7);
		sr.addData(4, 9);
		RegressionResults re = sr.regress();
		log.info("N:\t{}\tR:\t{}", re.getN(), sr.getR());
		log.info("predict: 10 = {}", sr.predict(10));
		log.info("y = {} + {} * x", sr.getIntercept(), sr.getSlope());
	}

	@Test
	public void testSimpleRegression3() {
		SimpleRegression sr = new SimpleRegression();
		sr.addData(1, 3);
		sr.addData(2, 5);
		sr.addData(3, 3);
		sr.addData(4, 2);
		RegressionResults re = sr.regress();
		log.info("N:\t{}\tR:\t{}", re.getN(), sr.getR());
		log.info("predict: 10 = {}", sr.predict(10));
		log.info("y = {} + {} * x", sr.getIntercept(), sr.getSlope());
	}


}
