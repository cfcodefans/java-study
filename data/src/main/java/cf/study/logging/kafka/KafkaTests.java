package cf.study.logging.kafka;

import java.util.Arrays;
import java.util.Properties;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.kafka.clients.KafkaClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import kafka.admin.AdminUtils;
import kafka.common.TopicAndPartition;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import misc.Jsons;
import misc.MiscUtils;
import scala.collection.Iterator;
import scala.collection.JavaConverters;

public class KafkaTests {

	private static final Logger log = LogManager.getLogger(KafkaTests.class);
	/*
	 * step 2: start bin\windows\zookeeper-server-start.bat
	 * config\zookeeper.properties step 3: start
	 * bin\windows\kafka-server-start.bat config\server.properties
	 * 
	 */
	static KafkaClient kc = null;
	static ZkClient zc = null;
	static ZkUtils zu = null;

	@BeforeClass
	public static void setUpClass() {
		// port is in kafka/conf/zookeeper.properties
		zc = new ZkClient("localhost:2181", 10000, 10000, ZKStringSerializer$.MODULE$);
		// zc.connect(1000, log::info); //don't start again

		zu = ZkUtils.apply(zc, false);
	}

	@Test
	public void validateSetUp() {
		Assert.assertNotNull(zc);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		zu.close();

		if (kc != null) {
			kc.close();
		}
	}

	@Before
	public void before() {
		System.out.println();
		zu = zu.apply(zc, false);
	}

	@Test
	public void testListTopics() {
		log.info(MiscUtils.invocationInfo());
		for (Iterator<TopicAndPartition> it = zu.getAllPartitions().iterator(); it.hasNext();) {
			TopicAndPartition tp = it.next();
			log.info(tp);
		}
	}
	
	@Test
	public void testCreateTopic() {
		AdminUtils.createTopic(zu, "test", 2, 1, new Properties());
		testListTopics();
	}
	
	@Test
	public void testDeleteAllTopics() {
		log.info(MiscUtils.invocationInfo());
		JavaConverters.asJavaListConverter(zu.getAllTopics()).asJava().forEach(tp-> {
			log.info("delete " + tp);
			AdminUtils.deleteTopic(zu, tp);
		});
	}
	
	@Test
	public void testProducer() {
		LongSerializer ks = new LongSerializer();
		StringSerializer vs = new StringSerializer();
		//port is in kafka/conf/server.properties
		try (KafkaProducer<Long, String> kp = new KafkaProducer<>(MiscUtils.map("bootstrap.servers", "localhost:9092",
				"client.id", KafkaTests.class.getSimpleName()), ks, vs)) {
			
			for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
				String msg = Jsons.toString(ste);
				log.info("sending {}", msg);
				
				kp.send(new ProducerRecord<Long, String>("test", msg), (rm, e)->{
					log.info("callback: {}, {}", ToStringBuilder.reflectionToString(rm), e);
				});
			}
		}
	}
	
	@Test
	public void testConsumer() {
		LongDeserializer ks = new LongDeserializer();
		StringDeserializer vs = new StringDeserializer();

		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", "test");
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.LongDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
			consumer.subscribe(Arrays.asList("test"));
			consumer.seekToBeginning();

			// while (true) {
			ConsumerRecords<String, String> records = consumer.poll(100);
			for (ConsumerRecord<String, String> record : records)
				log.info("offset = {}, key = {}, value = {}", record.offset(), record.key(), record.value());
			// }
		}

	}

	@After
	public void after() {
		System.out.println();
		zu.close();
	}

}
