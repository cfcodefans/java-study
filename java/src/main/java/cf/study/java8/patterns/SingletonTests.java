package cf.study.java8.patterns;

import misc.MiscUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by fan on 2016/8/19.
 */
class Singleton {
	private final static Logger log = LogManager.getLogger(Singleton.class);

	final static AtomicInteger COUNTER = new AtomicInteger(0);

	private Singleton() {
		log.info(MiscUtils.invocInfo());
		COUNTER.incrementAndGet();
	}

	private static Singleton simple;

	public static Singleton simple() {
		if (simple == null) {
			simple = new Singleton();
		}
		return simple;
	}
}

public class SingletonTests {

}
