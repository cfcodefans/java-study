package cf.study.data.storage.redis;

import java.util.UUID;

import junit.framework.Assert;

import org.junit.Test;

import redis.clients.jedis.Jedis;

public class RedisTest {
	@Test
	public void testRedis() {
		Jedis jedis = new Jedis("akira");
		String keyStr = "foo";
		jedis.set(keyStr, "bar");
		String value = jedis.get(keyStr);
		System.out.println(value);
		long re = jedis.del(keyStr);
		System.out.println(re);
		value = jedis.get(keyStr);
		System.out.println(value);
		jedis.disconnect();
	}
	
	@Test
	public void testRedisCmds() {
		Jedis jedis = new Jedis("akira");
		
		String keyStr = "foo";
		jedis.setnx(keyStr, String.valueOf(0));
		jedis.incr(keyStr);
		String value = jedis.get(keyStr);
		System.out.println(value);
		jedis.del(keyStr);
		
		jedis.disconnect();
	}
	
	@Test
	public void testRedisQuery() {
		Jedis jedis = new Jedis("akira");
		
		UUID uuid = UUID.randomUUID();
		String keyStr = uuid.toString();
		
		System.out.println(keyStr);
		
		String valStr = jedis.get(keyStr);
		
		System.out.println(valStr);
		Assert.assertNull(valStr);
		
		jedis.set(keyStr, "1");
		jedis.incrBy(keyStr, 1);
		
		valStr = jedis.get(keyStr);
		System.out.println(valStr);
		Assert.assertEquals(valStr, "2");
		
		jedis.disconnect();
	}
	
	@Test
	public void testRedisIncrement() {
		Jedis jedis = new Jedis("akira");
		
		UUID uuid = UUID.randomUUID();
		String keyStr = uuid.toString();
		System.out.println(keyStr);
		
		jedis.incrBy(keyStr, 1);
		String valStr = jedis.get(keyStr);
		
		System.out.println(valStr);
		Assert.assertEquals(valStr, "1");
		
//		jedis.set(keyStr, "1");
		jedis.incrBy(keyStr, 1);
		
		valStr = jedis.get(keyStr);
		System.out.println(valStr);
		Assert.assertEquals(valStr, "2");
		
		jedis.disconnect();
	}
	
	@Test
	public void testRedisArray() {
		Jedis jedis = new Jedis("akira");
		
		UUID uuid = UUID.randomUUID();
		String keyStr = uuid.toString();
		System.out.println(keyStr);
		
		String[] testArray = {"a", "b", "c", "d"};
		
		jedis.set(keyStr, "test");
		for (String element : testArray) {
			jedis.append(keyStr, element);
		}
		
		System.out.println(jedis.get(keyStr));
		
		jedis.del(keyStr);
	}
	
	@Test
	public void testQuery() {
		Jedis jedis = new Jedis("moleman-poppen", 6379);
		for (String key : jedis.keys("job*")) {
			System.out.println(key + ":\t" + jedis.get(key));
			jedis.del(key);
		}
	}
}
