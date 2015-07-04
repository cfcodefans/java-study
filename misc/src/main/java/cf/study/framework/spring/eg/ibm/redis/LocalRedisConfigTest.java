package cf.study.framework.spring.eg.ibm.redis;

import javax.inject.Inject;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration(classes=LocalRedisConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class LocalRedisConfigTest {

	@Inject
	private JedisConnectionFactory jcf;
	
	@Inject
	private StringRedisTemplate strRedisTempl;
	
	@Test
	public void testJedisConnectionFactory() {
		Assert.assertNotNull(jcf);
	}
	
	@Test
	public void testRedisTemplate() {
		Assert.assertNotNull(strRedisTempl);
	}
	
}
