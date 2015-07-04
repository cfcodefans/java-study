package cf.study.java8.utils;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class MapTests {

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
			return KeyClzz.class.getSimpleName() + "@" + super.hashCode() +": [hash=" + hash + "]";
		}
	}
	
	@Test
	public void testRehash() {
		final KeyClzz key_1 = new KeyClzz();
		key_1.hash = 1;
		final Map<KeyClzz, String> map = new HashMap<MapTests.KeyClzz, String>();
		map.put(key_1, key_1.toString());
		
		Assert.assertTrue(map.containsKey(key_1));
		System.out.println(map.get(key_1));
		
		key_1.hash = 2;
		
		Assert.assertFalse(map.containsKey(key_1));
		System.out.println(map.get(key_1));
		
//		final HashMap<KeyClzz, String> hashMap = (HashMap<KeyClzz, String>) map;
		map.put(key_1, key_1.toString());
		System.out.println(map);
		System.out.println(map.get(key_1));
	}
}
