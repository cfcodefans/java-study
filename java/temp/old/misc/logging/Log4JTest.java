package cf.study.misc.logging;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.junit.Test;

public class Log4JTest {
	static final Map<String, Logger> queueNameAndLoggers = new HashMap<String, Logger>();

	@Test 
	public void testLog() {
		Logger log = getLoggerByQueueConf("test.log");
		String testStr = StringUtils.repeat("-", 2800);
		System.out.println(testStr);
		log.info(testStr);
		log.error(testStr);
//		log.info(testStr);
//		log.info(testStr);
	}
	
	static synchronized Logger getLoggerByQueueConf(String logFileName ) {
		if (StringUtils.isBlank(logFileName)) {
			logFileName = "a";
		}
	
		Logger logger = queueNameAndLoggers.get(logFileName);
		if (logger != null) {
			return logger;
		}
	
		String maxLogSize = "1KB";
	
		RollingFileAppender originalLogAppender = (RollingFileAppender) Logger.getRootLogger().getAppender("consumerDispatcherLog");
	
		PatternLayout srcLayout = (PatternLayout) originalLogAppender.getLayout();
	
		RollingFileAppender queueLogAppender = new RollingFileAppender();
	
		queueLogAppender.setEncoding(originalLogAppender.getEncoding());
	
		try {
			queueLogAppender.setFile(logFileName, originalLogAppender.getAppend(), originalLogAppender.getBufferedIO(),
					originalLogAppender.getBufferSize());
	
			queueLogAppender.setErrorHandler(originalLogAppender.getErrorHandler());
			queueLogAppender.setImmediateFlush(originalLogAppender.getImmediateFlush());
	
			PatternLayout pl = new PatternLayout(srcLayout.getConversionPattern());
			queueLogAppender.setLayout(pl);
			queueLogAppender.setMaxBackupIndex(originalLogAppender.getMaxBackupIndex());
			queueLogAppender.setMaxFileSize(maxLogSize);
	
			queueLogAppender.setName(logFileName);
			queueLogAppender.setThreshold(originalLogAppender.getThreshold());
			//Logger.getRootLogger().addAppender(queueLogAppender);
	
			logger = Logger.getLogger(logFileName);
			
			logger.addAppender(queueLogAppender);
	
			queueNameAndLoggers.put(logFileName, logger);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return logger;
	}

	public static void main(String[] args) {
		new Log4JTest().testLog();
	}
}
