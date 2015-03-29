package cf.study.misc.guava;

import java.util.concurrent.Executors;

import misc.MiscUtils;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class EventBusTests {

	@FunctionalInterface
	public static interface IEventListener<T> {
		@Subscribe
		T onEvent(T t);
	}

	private static final Logger log = Logger.getLogger(EventBusTests.class);
	
	@Test
	public void testMultipleListeners() {
		String id = MiscUtils.invocationInfo();
		EventBus eb = new EventBus(id);
		
		IEventListener<Integer> intListener = (Integer i)->{
			log.info(String.format("%s(event=%s)", MiscUtils.invocationInfo(), i));
			return i;
		};
		IEventListener<String> strListener = (String str)->{
			log.info(String.format("%s(event=%s)", MiscUtils.invocationInfo(), str));
			return str;
		};
		eb.register(intListener);
		eb.register(strListener);
		
		eb.post(new Integer(123));
		eb.post("I am a string event");
	}
	
	public static class TestEventListener {
		@Subscribe
		public String onEvnent(String str) {
			log.info(String.format("%s(event=%s)", MiscUtils.invocationInfo(), str));
			return str;
		}
		
		@Subscribe
		public Integer onEvent(Integer i) {
			log.info(String.format("%s(event=%s)", MiscUtils.invocationInfo(), i));
			return i;
		}
	}
	
	@Test
	public void testMultipleListenMethods() {
		String id = MiscUtils.invocationInfo();
		EventBus eb = new EventBus(id);
		eb.register(new TestEventListener());
		eb.post(new Integer(123));
		eb.post("I am a string event");
	}
	
	@Test
	public void testAsyncEventBus() {
		String id = MiscUtils.invocationInfo();
		EventBus eb = new AsyncEventBus(id, Executors.newSingleThreadExecutor());
		eb.register(new TestEventListener());
		eb.post(new Integer(123));
		MiscUtils.easySleep(10000);
		eb.post("I am a string event");
	}
}
