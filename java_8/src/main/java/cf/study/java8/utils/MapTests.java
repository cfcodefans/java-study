package cf.study.java8.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import misc.MiscUtils;

public class MapTests {

	private static final Logger log = LogManager.getLogger(MapTests.class);

	public static class KeyClzz {
		public int hash;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + hash;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof KeyClzz))
				return false;
			KeyClzz other = (KeyClzz) obj;
			if (hash != other.hash)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return KeyClzz.class.getSimpleName() + "@" + super.hashCode() + ": [hash=" + hash + "]";
		}
	}

	@Test
	public void testRehash() {
		final KeyClzz key_1 = new KeyClzz();
		key_1.hash = 1;
		final Map<KeyClzz, String> map = new HashMap<KeyClzz, String>();
		map.put(key_1, key_1.toString());

		Assert.assertTrue(map.containsKey(key_1));
		log.info(map.get(key_1));

		key_1.hash = 2;

		Assert.assertFalse(map.containsKey(key_1));
		log.info(map.get(key_1));

		// final HashMap<KeyClzz, String> hashMap = (HashMap<KeyClzz, String>)
		// map;
		map.put(key_1, key_1.toString());
		log.info(map);
		log.info(map.get(key_1));
	}

	@Test
	public void testCompute() {
		ConcurrentHashMap<Integer, String> chm = new ConcurrentHashMap<>();

		String v = chm.compute(1, (Integer i, String s) -> {
			log.info("key: {}, value: {}", i, s);
			if (s == null)
				s = i.toString();
			return s;
		});
		log.info(v);

		Assert.assertTrue(chm.containsKey(1));

		v = chm.compute(1, (Integer i, String s) -> {
			log.info("key: {}, value: {}", i, s);
			if (s == null)
				s = i.toString();
			return s;
		});
		log.info(v);
	}

	@Test
	public void testNavMap() {
		ConcurrentSkipListMap<Long, Integer> cm = new ConcurrentSkipListMap<Long, Integer>();

		IntStream.range(0, 10).forEach(i -> {
			MiscUtils.easySleep(50);
			cm.put(System.currentTimeMillis(), i);
		});

		cm.entrySet().forEach(log::info);

		long t = System.currentTimeMillis() - 50 * 5;
		
		log.info(cm.ceilingKey(t));
		log.info(cm.floorKey(t));

		log.info(cm.tailMap(cm.floorKey(t)));
		log.info(cm.tailMap(cm.ceilingKey(t)));

		log.info(cm.headMap(cm.ceilingKey(t)));
	}
}
