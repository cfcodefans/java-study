package cf.study.misc.logging;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class SLF4JTest {
	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(SLF4JTest.class);
		logger.info("Hello World");
	}
	
	@Test
	public void testJavaUtilLogger() {
		Logger logger = LoggerFactory.getLogger("testJavaUtilLogger");
		logger.info(logger.getName());
	}
	
	@Test
	public void testMarker() {
		Logger logger = LoggerFactory.getLogger("testMarker");
		Marker m = MarkerFactory.getMarker("testedMarker");
		
		logger.info(m, "how does marker work?");
	}
	
	@Test
	public void testLogback() {
		Logger log = LoggerFactory.getLogger("com.thenetcircle.services.livefeed");
		log.debug("debug");
	}
}
