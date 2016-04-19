package cf.study.data.binding.jackson.annotation;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;

import misc.Jsons;

public class JacksonAnnotationTests {

	static Random rand = new Random();
	
	public static class DummyClass {
		public int _int = rand.nextInt();
		public long _long = rand.nextLong();
		public short _short = (short)rand.nextInt();
		public byte _byte = (byte)rand.nextInt();
		public char _char = (char)rand.nextInt();
		public String _string = RandomStringUtils.randomAlphabetic(15);
		public boolean _boolean = rand.nextBoolean();
		
		public int[] ints = rand.ints(7).toArray();
		public long[] longs = rand.longs(7).toArray();
//		public short[] shorts = rand.ints(7).map(i->(short)i).c;
//		public byte[] bytes;
//		public char[] chars;
		public String[] strings = RandomStringUtils.random(15, "abcdefghijklmnopqrstuvwxyz ").split(" ");
//		public boolean[] booleans;
		
		@JsonProperty("mapped_integer")
		public Integer mappedInteger = rand.nextInt();
		
		@JsonProperty("mapped_string")
		public String mappedString;
		
		@JsonProperty("string_list")
		public List<String> stringList = Arrays.asList("abc", "def");
		
		@JsonProperty("_parent")
		public DummyClass parent;
		
		@JsonProperty("_children")
		public List<DummyClass> children; 
	}
	
	@Test
	public void test() {
		DummyClass dummy = new DummyClass();
		
		dummy.parent = new DummyClass();
		dummy.children = Arrays.asList(new DummyClass(), new DummyClass());
		
		String jsonStr = Jsons.toString(dummy);
		System.out.println(jsonStr);
		
		DummyClass _dummy = Jsons.read(jsonStr, DummyClass.class);
		Assert.assertEquals(dummy._int, _dummy._int);
	}
}
