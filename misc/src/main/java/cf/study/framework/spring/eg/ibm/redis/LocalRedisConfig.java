package cf.study.framework.spring.eg.ibm.redis;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import misc.MiscUtils;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ComponentScan(basePackages="cf.study.framework.spring.eg.ibm.redis")
public class LocalRedisConfig {
	
	static final Logger log = Logger.getLogger(LocalRedisConfig.class);

	@PostConstruct
	public void init() {
		log.info(MiscUtils.invocationInfo());
	}
	
	@PreDestroy
	public void cleanUp() {
		log.info(MiscUtils.invocationInfo());
	}
	
	@Bean
	public RedisConnectionFactory jedisConnFactory() {
		JedisPoolConfig poolCfg = new JedisPoolConfig();
		poolCfg.setMaxTotal(5);
		poolCfg.setMaxIdle(5);
		poolCfg.setMinIdle(0);
		poolCfg.setTestOnBorrow(true);
		poolCfg.setTestOnReturn(true);
		poolCfg.setTestWhileIdle(true);
		
		JedisConnectionFactory jcf = new JedisConnectionFactory(poolCfg);
		
		jcf.setHostName("192.168.138.131");
		jcf.setPort(6379);
		jcf.setDatabase(0);
		
		return jcf;
	}
	
	@Bean
	public StringRedisTemplate strRedisTempl() {
		return new StringRedisTemplate(jedisConnFactory());
	}
}
