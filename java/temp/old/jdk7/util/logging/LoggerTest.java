package cf.study.jdk7.util.logging;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.util.logging.XMLFormatter;

import org.apache.commons.lang.time.DateFormatUtils;
import org.junit.Test;

public class LoggerTest {
	static final Logger _log = Logger.getLogger("console");

	@Test
	public void testSimpleFormatter() {
		Logger log = Logger.getLogger("testFormatter");
		log.setLevel(Level.INFO);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		StreamHandler sh = new StreamHandler(os, new SimpleFormatter());

		log.addHandler(sh);
		log.info("Hello, I want to test SimpleFormatter");
		_log.info(os.toString());
	}

	@Test
	public void testXMLFormatter() {
		Logger log = Logger.getLogger("testFormatter");
		log.setLevel(Level.INFO);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		StreamHandler sh = new StreamHandler(os, new XMLFormatter());

		log.addHandler(sh);
		log.info("Hello, I want to test XMLFormatter");
		_log.info(os.toString());
	}

	@Test
	public void testMyFormatter() {
		Logger log = Logger.getLogger("testFormatter");
		log.setLevel(Level.INFO);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		StreamHandler sh = new ConsoleHandler();
		sh.setFormatter(new Formatter() {
			@Override
			public String format(LogRecord record) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(record.getMillis());
				return String.format("%s %s: %s.%s()\t%s\t%s\n", DateFormatUtils.format(cal.getTimeInMillis(), "yyyy-MM-dd hh:mm:ss"), record.getLoggerName(),
								record.getSourceClassName(), record.getSourceMethodName(), record.getThreadID(), record.getMessage());
			}
		});

		log.addHandler(sh);
		log.info("Hello, I want to test MyFormatter");
		_log.info(os.toString());
	}
}
