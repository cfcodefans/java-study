package cf.study.jdk7.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cf.study.utils.MiscUtils;

public class MapTests {

	static class SameHashCode {
		int i;
		
		@Override
		public int hashCode() {
			return SameHashCode.class.hashCode();
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("SameHashCode [i=");
			builder.append(i);
			builder.append("]");
			return builder.toString();
		}

		@Override
		public boolean equals(Object obj) {
			System.out.println(MiscUtils.invocationInfo() + "(" + this + ", " + obj + ")");
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof SameHashCode))
				return false;
			SameHashCode other = (SameHashCode) obj;
			if (i != other.i)
				return false;
			return true;
		}
		
	}
	
	@Test
	public void testSameHashCode() {
		Map<SameHashCode, Integer> map = new HashMap<SameHashCode, Integer>();
		for (int i = 0; i < 10; i++) {
			SameHashCode sh = new SameHashCode();
			sh.i = i;
			map.put(sh, i);
		}
		
		System.out.println(map.size());
		{
			SameHashCode sh = new SameHashCode();
			sh.i = 0;
			map.get(sh);
		}
	}
}
