package cf.study.cache.redis;

import java.util.Arrays;
import java.util.stream.IntStream;

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

	private Jedis jedis = null;
	
	private static JedisPool jedisPool;
	
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

			jedisPool = new JedisPool(poolCfg, "192.168.138.131", 6379);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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
	
	@AfterClass
	public static void tearDownAll() {
		if (jedisPool != null && !jedisPool.isClosed())
			jedisPool.close();
	}
	
	@Test
	public void testMisc() {
		System.out.println(jedis.asking());
	}
	
	@Test
	public void testAuth() {
		jedis.configGet("*").forEach(System.out::println);
//		System.out.println(jedis.auth(""));
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
	public void testSet() {
		byte[] key = "byte_key".getBytes();
		jedis.set(key, bytes(1));
		System.out.println(Arrays.toString(bytes(1)));
		System.out.println(Arrays.toString(jedis.get(key)));
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
		System.out.println(jedis.decrBy(sk, 3));
	}
	
	public static byte[] bytes(int...is) {
		byte[] bs = new byte[is.length];
		for (int i = 0; i < is.length; i++) {
			bs[i] = (byte)is[i];
		}
		return bs;
	}
}
