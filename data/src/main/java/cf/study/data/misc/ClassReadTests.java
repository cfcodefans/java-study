package cf.study.data.misc;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * Created by fan on 2016/6/27.
 */
public class ClassReadTests {
	private static final Logger log = LogManager.getLogger(ClassReadTests.class);

	@Test
	public void testClassPath() {
		String[] javaClassPaths = SystemUtils.JAVA_CLASS_PATH.split(";");
		log.info(javaClassPaths.length);

		AtomicLong idx = new AtomicLong(0);
		Stream.of(javaClassPaths).map(p -> String.format("%d\t%s", idx.getAndIncrement(), p)).forEach(System.out::println);
	}
}
