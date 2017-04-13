package cf.study.java.utils.logging;

import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

public class LogTests {

	public static class LogCfgs {
		public LogCfgs() {
			try {
				try (InputStream in = getClass().getResourceAsStream("logging.properties")) {
					LogManager.getLogManager().readConfiguration(in);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@BeforeClass
	public static void setUpClass() {
		System.setProperty("java.util.logging.config.class", LogCfgs.class.getName());
	}

	@Test
	public void testLog() {
		Logger log = Logger.getLogger("test");
		log.info("info");

		log.info("test");
	}
}
