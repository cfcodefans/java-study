package cf.study.cache.redis;

import java.time.Month;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import misc.MiscUtils;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import redis.clients.jedis.BitOP;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisTests {

	private static JedisPool jedisPool;
	
	public static byte[] bytes(int...is) {
		byte[] bs = new byte[is.length];
		for (int i = 0; i < is.length; i++) {
			bs[i] = (byte)is[i];
		}
		return bs;
	}
	
	@BeforeClass
	public static void setupAll() {
		try {
			JedisPoolConfig poolCfg = new JedisPoolConfig();
			poolCfg.setMaxTotal(5);
			poolCfg.setMaxIdle(5);
			poolCfg.setMinIdle(0);
			poolCfg.setTestOnBorrow(true);
			poolCfg.setTestOnReturn(true);
			poolCfg.setTestWhileIdle(true);
			
			jedisPool = new JedisPool(poolCfg, "192.168.60.129", 6379);
//			jedisPool = new JedisPool(poolCfg, "192.168.138.131", 6379);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void tearDownAll() {
		if (jedisPool != null && !jedisPool.isClosed())
			jedisPool.close();
	}
	
	private Jedis jedis = null;
	
	@Before
	public void setup() {
		jedis = jedisPool.getResource();
		jedis.connect();
	}
	
	@After
	public void tearDown() {
		if (jedis != null && jedis.isConnected()) {
			jedis.keys("*").forEach(jedis::del);
			jedis.close();
		}
	}
	
	@Test
	public void testAppend() {
		String key = "mykey";
		Assert.assertFalse(jedis.exists(key));
		Assert.assertEquals(jedis.append(key, "Hello").intValue(), "Hello".length());
		Assert.assertEquals(jedis.append(key, " World").intValue(), "Hello world".length());
		Assert.assertEquals(jedis.get(key), "Hello World");
	}
	
	@Test
	public void testAuth() {
		jedis.configGet("*").forEach(System.out::println);
//		System.out.println(jedis.auth(""));
	}
	
	@Test
	public void testBitCount() {
		String key = "BITCOUNT_key";
		System.out.println(jedis.set(key.getBytes(), new byte[] {2}));
		Assert.assertEquals(jedis.bitcount(key.getBytes()).intValue(), 1);
		Assert.assertEquals(jedis.append(key.getBytes(), new byte[] {6}).intValue(), 2);
		Assert.assertEquals(jedis.bitcount(key.getBytes()).intValue(), 3);
	}
	
	@Test
	public void testBitOps() {
		byte[] key1 = "bit_operand_1".getBytes();
		byte[] key2 = "bit_operand_2".getBytes();
		byte[] reKey = "bit_result".getBytes();
		
		jedis.set(key1, bytes(2));
		jedis.set(key2, bytes(1));
		System.out.println(Arrays.toString(jedis.get(key1)));
		System.out.println(Arrays.toString(jedis.get(key2)));
		jedis.bitop(BitOP.AND, reKey, key1, key2);
		byte[] expecteds = jedis.get(reKey);
		System.out.println(Arrays.toString(expecteds));
		Assert.assertArrayEquals(expecteds, bytes(0));
		System.out.println();
		
		jedis.set(key1, bytes(2));
		jedis.set(key2, bytes(1));
		System.out.println(Arrays.toString(jedis.get(key1)));
		System.out.println(Arrays.toString(jedis.get(key2)));
		jedis.bitop(BitOP.OR, reKey, key1, key2);
		expecteds = jedis.get(reKey);
		System.out.println(Arrays.toString(expecteds));
		Assert.assertArrayEquals(expecteds, bytes(3));
		System.out.println();
		
		jedis.set(key2, bytes(1));
		jedis.bitop(BitOP.NOT, reKey, key2);
		System.out.println(Arrays.toString(jedis.get(key2)));
		System.out.println(Arrays.toString(expecteds));
		Assert.assertArrayEquals(jedis.get(reKey), bytes(-2));
		System.out.println();
		
		jedis.set(key1, bytes(3));
		jedis.set(key2, bytes(1));
		System.out.println(Arrays.toString(jedis.get(key1)));
		System.out.println(Arrays.toString(jedis.get(key2)));
		jedis.bitop(BitOP.XOR, reKey, key1, key2);
		expecteds = jedis.get(reKey);
		System.out.println(Arrays.toString(expecteds));
		Assert.assertArrayEquals(expecteds, bytes(2));
		System.out.println();
	}
	
	@Test
	public void testBitPos() {
		//from right to left
		byte[] key = "byte_key".getBytes();
		jedis.set(key, bytes(4));
		System.out.println(jedis.bitpos(key, true));
		jedis.set(key, bytes(2));
		System.out.println(jedis.bitpos(key, true));
		jedis.set(key, bytes(128));
		Assert.assertEquals(jedis.bitpos(key, true).intValue(), 1);
	}
	
	@Test
	public void testClient() {
		System.out.println(jedis.clientList());
		System.out.println(jedis.clientGetname());
	}
	
	@Test
	public void testDecr() {
		byte[] key = "byte_key".getBytes();
		jedis.set(key, bytes(0,0,0,0,0,0,0,5));
		System.out.println(Arrays.toString(jedis.get(key)));
		jedis.decr(key);
		byte[] expecteds = jedis.get(key);
		System.out.println(Arrays.toString(expecteds));
//		Assert.assertArrayEquals(expecteds, bytes(0, 4));
		
		String sk = "key";
		jedis.set(sk, "10");
		System.out.println(jedis.decr(sk));
		
	}
	
	@Test
	public void testDecrBy() {
		String sk = "key";
		jedis.set(sk, "10");
		Assert.assertEquals(7, jedis.decrBy(sk, 3).intValue());
		Assert.assertEquals(10, jedis.decrBy(sk, -3).intValue());
	}
	
	@Test 
	public void testExistsAndDel() {
		String key1 = "key1";
		Assert.assertFalse(jedis.exists(key1));
		jedis.set(key1, "value1");
		Assert.assertTrue(jedis.exists(key1));
		jedis.del(key1);
		Assert.assertFalse(jedis.exists(key1));
	}
	
	@Test
	public void testExpire() {
		String key = "key";
		jedis.set(key, "value");
		Assert.assertTrue(jedis.exists(key));
		jedis.expire(key, 2);
		MiscUtils.easySleep(1000);
		Assert.assertTrue(jedis.exists(key));
		MiscUtils.easySleep(1000);
		Assert.assertFalse(jedis.exists(key));
	}
	
	@Test
	public void testExpireAt() {
		String key = "key";
		jedis.set(key, "value");
		Assert.assertTrue(jedis.exists(key));
		jedis.expireAt(key, Calendar.getInstance().getTimeInMillis() / 1000 + 2);
		MiscUtils.easySleep(1000);
		Assert.assertTrue(jedis.exists(key));
		MiscUtils.easySleep(1001);
		Assert.assertFalse(jedis.exists(key));
	}
	
	
	@Test
	public void testGet() {
		String key = "key";
		Assert.assertNull(jedis.get(key));
		jedis.set(key, "value");
		Assert.assertEquals("value", jedis.get(key));
	}
	
	@Test
	public void testGetBit() {
		String key = "key";
		Assert.assertFalse(jedis.getbit(key, 1));
		Assert.assertNull(jedis.get(key));
		jedis.setbit(key, 1, true);
		Assert.assertTrue(jedis.getbit(key, 1));
		jedis.setbit(key, 7, true);
		Assert.assertTrue(jedis.getbit(key, 7));
		System.out.println((short)jedis.get(key).charAt(0));
		System.out.println((short)'A');
	}
	
	@Test
	public void testGetRange() {
		String key = "key";
		String value = "This is a string";
		jedis.set(key, value);
		Assert.assertEquals(jedis.getrange(key, 0, 3), StringUtils.substring(value, 0, 4));
		Assert.assertEquals(jedis.getrange(key, -3, -2), StringUtils.substring(value, -3, -1));
	}
	
	@Test
	public void testGetSet() {
		String key = "key";
		String value = "value";
		Assert.assertNull(jedis.getSet(key, value));
//		Assert.assertEquals(value, jedis.getSet(key, null));// can't set null to redis
		Assert.assertEquals(value, jedis.getSet(key, key));
		Assert.assertEquals(key, jedis.getSet(key, value));
	}
	
	@Test
	public void testIncr() {
		String key = "key";
		jedis.set(key, "20");
		Assert.assertEquals(21, jedis.incr(key).intValue());
		Assert.assertEquals("21", jedis.get(key));
		jedis.set(key, "A");
		//INCR commands are limited to 64 bit signed integers.
//		Assert.assertEquals('B', jedis.incr(key).shortValue());
//		Assert.assertEquals("B", jedis.get(key));
	}
	
	@Test
	public void testIncrBy() {
		String key = "key";
		jedis.set(key, "20");
		Assert.assertEquals(25, jedis.incrBy(key, 5).intValue());
		Assert.assertEquals(20, jedis.incrBy(key, -5).intValue());
	}
	
	@Test
	public void testIncrByFloat() {
		String key = "key";
		jedis.set(key, String.valueOf(10.5));
		Assert.assertEquals(10.6, jedis.incrByFloat(key, 0.1).floatValue(), 0.01);
		Assert.assertEquals(10.5, jedis.incrByFloat(key, -0.1).floatValue(), 0.01);
	}
	
	@Test
	public void testMGet() {
		String key1 = "key1";
		String key2 = "key2";
		String what = "what";
		jedis.set(key1, "sorry");
		jedis.set(key2, "my fault");
		List<String> mget = jedis.mget(key1, key2, what);
		
		Assert.assertEquals("sorry", mget.get(0));
		Assert.assertEquals("my fault", mget.get(1));
		Assert.assertNull(mget.get(2));
		
	}
	
	@Test
	public void testMisc() {
		System.out.println(jedis.asking());
	}
	
	@Test
	public void testMSet() {
		String key1 = "key1";
		String key2 = "key2";
		String what = "what";

		jedis.mset(key1, "sorry", key2, "my fault");
		List<String> mget = jedis.mget(key1, key2, what);
		
		Assert.assertEquals("sorry", mget.get(0));
		Assert.assertEquals("my fault", mget.get(1));
		Assert.assertNull(mget.get(2));
		
	}
	
	@Test
	public void testSet() {
		byte[] key = "byte_key".getBytes();
		jedis.set(key, bytes(1));
		System.out.println(Arrays.toString(bytes(1)));
		System.out.println(Arrays.toString(jedis.get(key)));
	}
	
	@Test
	public void testMSet_MGet_Keys() {
		jedis.mset("one", "1", "two", "2", "three", "3", "four", "4");
		final List<String> vals = jedis.mget("one", "two", "three");
		Assert.assertEquals(vals, Arrays.asList("1", "2", "3"));
		
		Set<String> keys = jedis.keys("*o*");
		Assert.assertTrue(keys.contains("one"));
		Assert.assertTrue(keys.contains("two"));
		Assert.assertTrue(keys.contains("four"));
		Assert.assertFalse(keys.contains("three"));
		
		keys = jedis.keys("o*");
		Assert.assertTrue(keys.contains("one"));
		Assert.assertFalse(keys.contains("two"));
		Assert.assertFalse(keys.contains("four"));
		Assert.assertFalse(keys.contains("three"));
		
		keys = jedis.keys("[^o]*");
		System.out.println(keys);
		Assert.assertFalse(keys.contains("one"));
		Assert.assertTrue(keys.contains("two"));
		Assert.assertTrue(keys.contains("four"));
		Assert.assertTrue(keys.contains("three"));
	}
	
	@Test
	public void testRename() {
		String key = "k";
		final String value = "v";
		jedis.set(key, value);
		Assert.assertTrue(jedis.exists(key));
		final String newkey = "key";
		jedis.rename(key, newkey);
		Assert.assertFalse(jedis.exists(key));
		Assert.assertTrue(jedis.exists(newkey));
		Assert.assertEquals(value, jedis.get(newkey));
	}
	
	@Test
	public void testList() {
		final String key = "month";
		Stream.of(Month.values()).forEach(m->jedis.lpush(key, m.name()));
		System.out.println(jedis.lrange(key, 0, -1));
		System.out.println(jedis.lrange(key, 0, jedis.llen(key)));
		System.out.println(jedis.lrange(key, 0, 0));
		System.out.println(jedis.lindex(key, 0));
		Assert.assertEquals(jedis.lindex(key, 0), jedis.lpop(key)); 
		System.out.println(jedis.lrange(key, 0, -1));
		jedis.lset(key, 0, Month.DECEMBER.name());
		Assert.assertEquals(Month.DECEMBER.name(), jedis.lpop(key)); 
		Assert.assertEquals(10, jedis.llen(key).intValue());
		jedis.lrem(key, 0, Month.JANUARY.name());
		System.out.println(jedis.lrange(key, 0, -1));
	}
	
	@Test
	public void testSets() {
		final String key = "months";
		Stream.of(Month.values()).forEach(m->jedis.sadd(key, m.name()));
		
		System.out.println(jedis.scard(key) + ":" + jedis.smembers(key));
		
		jedis.sadd(key, Month.MAY.name());
		
		System.out.println(jedis.scard(key) + ":" +jedis.smembers(key));
	}
}
