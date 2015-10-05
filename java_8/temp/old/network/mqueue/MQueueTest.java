package cf.study.network.mqueue;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.EqualPredicate;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public class MQueueTest {
	private static final Logger log = Logger.getLogger(MQueueTest.class.getSimpleName());

	private static ConnectionFactory factory;

	@BeforeClass
	public static void init() {
		factory = new ConnectionFactory();

		// factory.setUsername("admin");
		// factory.setPassword("admin");
		// factory.setVirtualHost("localhost");
		factory.setHost("localhost");
		// factory.setPort(5672);
	}

	private static Connection conn = null;

	// private Channel channel = null;

	@Before
	public void setup() throws Exception {
		conn = factory.newConnection();
		es = Executors.newScheduledThreadPool(5);
		// channel = conn.createChannel();
	}

	@Test
	public void multipleConns() throws Exception {
		List<Connection> conns = new ArrayList<Connection>();
		for (int i = 0; i < 10; i++) {
			conns.add(factory.newConnection());
		}

		System.out.println("created 10 connections");

		for (final Connection conn : conns) {
			conn.close();
		}
	}

	@After
	public void tearDown() throws Exception {
		es.shutdown();
		while (!es.awaitTermination(10, TimeUnit.SECONDS))
			;
		close(conn);
		conn = null;
	}

	private static void close(Connection conn) {
		log.info("\t\t closing connection");
		if (conn == null) {
			return;
		}
		try {
			if (!conn.isOpen()) {
				log.warning(conn.getCloseReason().getMessage());
				return;
			}
			conn.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	private final static String QUEUE_NAME = "hello";

	@Test
	public void testDirectQueue() throws Exception {
		final String exchangeName = "direct_1";
		Channel consumer = conn.createChannel();

		consumer.exchangeDeclare(exchangeName, "direct", false);
		final String queueName = consumer.queueDeclare(QUEUE_NAME, false, false, false, null).getQueue();

		log.info("queue: " + queueName);

		consumer.queueBind(queueName, exchangeName, StringUtils.EMPTY);

		QueueingConsumer qc = new QueueingConsumer(consumer);
		consumer.basicConsume(queueName, true, qc);

		log.info("consumer is set!");

		final String msg = "Hello Consumer!";

		asychPublish(queueName, 0, msg);

		while (true) {
			log.info("wait for delivery");
			final String delivery = new String(qc.nextDelivery().getBody());
			Assert.assertEquals(msg, delivery);
			log.info("received delivery: " + delivery);
			break;
		}

		consumer.close();
	}

	public void asychPublish(final String queueName, long seconds, String... msgs) throws Exception {
		es.schedule(new Publisher(queueName, msgs, StringUtils.EMPTY, queueName), seconds, TimeUnit.SECONDS);
	}

	public void asychPublish(final String queueName, String exchange, long seconds, String... msgs) throws Exception {
		es.schedule(new Publisher(queueName, msgs, exchange, queueName), seconds, TimeUnit.SECONDS);
	}

	public void asychPublish(Channel _ch, String exchange, String routeKey, long seconds, String... msgs) throws Exception {
		es.schedule(new Publisher(_ch, msgs, exchange, routeKey), seconds, TimeUnit.SECONDS);
	}

	ScheduledExecutorService es = Executors.newScheduledThreadPool(5);

	public static class Publisher implements Callable<Void> {
		protected Channel ch = null;
		protected String[] msgs = null;
		protected String exchange;
		protected String routeKey;
		protected String queueName = StringUtils.EMPTY;

		public Publisher(Channel _ch, String[] msgs, String exchange, String routeKey) {
			super();
			this.ch = _ch;
			this.msgs = msgs;
			this.exchange = exchange;
			this.routeKey = routeKey;
		}

		public Publisher(String _queueName, String[] msgs, String exchange, String routeKey) {
			super();
			this.queueName = _queueName;
			this.msgs = msgs;
			this.exchange = exchange;
			this.routeKey = routeKey;
		}

		@Override
		public Void call() throws Exception {
			if (StringUtils.isNotBlank(queueName)) {
				ch = conn.createChannel();
				ch.queueDeclare(queueName, true, false, false, null);
			}
			for (String msg : msgs) {
				log.info(Thread.currentThread().getName() + " publishes message: \t" + msg);
				ch.basicPublish(exchange, routeKey, null, msg.getBytes());
			}
			log.info("\n\n\n");
			if (StringUtils.isNotBlank(queueName)) {
				ch.close();
			}
			return null;
		}
	}

	public Future<List<Serializable>> asychConsume(final Predicate pred, final String queueName, boolean autoAck, boolean requeue) throws Exception {
		return es.schedule(new Consumer(pred, queueName, autoAck, requeue), 2, TimeUnit.SECONDS);
	}

	public static class Consumer implements Callable<List<Serializable>> {
		protected Predicate pred = null;
		protected Channel ch = null;
		protected String queueName;
		protected boolean autoAck;
		protected boolean requeue;

		private Consumer(Predicate pred, Channel ch, String queueName, boolean autoAck, boolean requeue) {
			super();
			this.pred = pred;
			this.ch = ch;
			this.queueName = queueName;
			this.autoAck = autoAck;
			this.requeue = requeue;
		}

		private Consumer(Predicate pred, String queueName, boolean autoAck, boolean requeue) {
			super();
			this.pred = pred;
			this.queueName = queueName;
			this.autoAck = autoAck;
			this.requeue = requeue;
		}

		@Override
		public List<Serializable> call() throws Exception {
			boolean isLocalChannel = ch == null;
			if (isLocalChannel) {
				ch = conn.createChannel();
				ch.queueDeclare(queueName, true, false, false, null);
			}

			QueueingConsumer qc = new QueueingConsumer(ch);
			ch.basicConsume(queueName, autoAck, qc);

			List<Serializable> msgs = new ArrayList<Serializable>();
			try {
				for (Delivery de = qc.nextDelivery(1000); true; de = qc.nextDelivery(1000)) {
					if (de == null) {
						log.info(Thread.currentThread().getName() + " time out ");
						continue;
					}

					String msg = new String(de.getBody());

					long deliveryTag = de.getEnvelope().getDeliveryTag();
					if (pred.evaluate(msg)) {
						log.info(Thread.currentThread().getName() + " received message: \t" + msg);
						if (!autoAck)
							ch.basicAck(deliveryTag, true);
						msgs.add(0, msg);
						return msgs;
					} else {
						msgs.add(msg);
						ch.basicReject(deliveryTag, requeue);
						log.info(Thread.currentThread().getName() + " rejected message: \t" + msg);
					}
				}
			} finally {
				if (isLocalChannel && ch != null && ch.isOpen()) {
					log.info(Thread.currentThread().getName() + " closed channel");
					ch.close();
				}
			}
		}
	}

	@Test
	public void testMultiplePublishersAndConsumers() throws Exception {
		final String queueName = "test";// MiscUtils.invocationInfo();

		log.info("queue: " + queueName + " is on test");

		String msg1 = "Hello";
		String msg2 = "Good Moring";
		String msg3 = "Good day";
		String msg4 = "Good afternoon";
		String msg5 = "Good evening";
		String msg6 = "Good night";
		String msg7 = "howdy";

		Future<List<Serializable>> result1 = asychConsume(EqualPredicate.getInstance(msg1), queueName, false, true);
		Future<List<Serializable>> result2 = asychConsume(EqualPredicate.getInstance(msg2), queueName, false, true);
		Future<List<Serializable>> result3 = asychConsume(EqualPredicate.getInstance(msg3), queueName, false, true);
		log.info("\n\n\n");
		asychPublish(queueName, 1, msg3, msg2, msg1);
		asychPublish(queueName, 1, msg4, msg5, msg6);
		asychPublish(queueName, 1, msg7, msg5, msg6);
		log.info("\n\n\n");

		Assert.assertEquals(result1.get().get(0), msg1);
		Assert.assertEquals(result2.get().get(0), msg2);
		Assert.assertEquals(result3.get().get(0), msg3);
	}

	@Test
	public void testFanout() throws Exception {
		final String exchangeName = "ex_fanout";
		final String fanout = "fanout";
		final String queueName1 = "test_q1";
		final String queueName2 = "test_q2";

		String msg1 = "Hello";
		String msg2 = "Good Moring";
		String msg3 = "Good day";
		String msg4 = "Good afternoon";
		String msg5 = "Good evening";
		String msg6 = "Good night";
		String msg7 = "howdy";

		Future<List<Serializable>> result1 = null;
		{
			Channel ch = conn.createChannel();
			ch.exchangeDeclare(exchangeName, fanout);
			final String qn = ch.queueDeclare(queueName1, true, false, true, null).getQueue();
			ch.queueBind(qn, exchangeName, StringUtils.EMPTY);
			result1 = es.schedule(new Consumer(EqualPredicate.getInstance(msg1), ch, queueName1, false, true), 2, TimeUnit.SECONDS);
		}

		Future<List<Serializable>> result2 = null;
		{
			Channel ch = conn.createChannel();
			ch.exchangeDeclare(exchangeName, fanout);
			final String qn = ch.queueDeclare(queueName2, true, false, true, null).getQueue();
			ch.queueBind(qn, exchangeName, StringUtils.EMPTY);
			result2 = es.schedule(new Consumer(EqualPredicate.getInstance(msg2), ch, queueName2, false, true), 2, TimeUnit.SECONDS);
		}

		{
			Channel ch = conn.createChannel();
			asychPublish(ch, exchangeName, StringUtils.EMPTY, 1, msg7, msg6, msg5, msg4, msg3, msg2, msg1);
		}

		Assert.assertEquals(result1.get().get(0), msg1);
		Assert.assertEquals(result2.get().get(0), msg2);
	}

	@Test
	public void testRouting() throws Exception {
		final String exchangeName = "ex_direct";
		final String direct = "direct";
		final String queueName1 = "test_q1";
		final String queueName2 = "test_q2";

		final String routeKey1 = "routeKey1";
		final String routeKey2 = "routeKey2";

		String msg1 = "Hello";
		String msg2 = "Good Moring";
		String msg3 = "Good day";
		String msg4 = "Good afternoon";
		String msg5 = "Good evening";
		String msg6 = "Good night";
		String msg7 = "howdy";

		Future<List<Serializable>> result1 = null;
		{
			Channel ch = conn.createChannel();
			ch.exchangeDeclare(exchangeName, direct);
			final String qn = ch.queueDeclare(queueName1, true, false, true, null).getQueue();
			ch.queueBind(qn, exchangeName, routeKey1);
			result1 = es.schedule(new Consumer(EqualPredicate.getInstance(msg1), ch, queueName1, false, true), 2, TimeUnit.SECONDS);
		}

		Future<List<Serializable>> result2 = null;
		{
			Channel ch = conn.createChannel();
			ch.exchangeDeclare(exchangeName, direct);
			final String qn = ch.queueDeclare(queueName2, true, false, true, null).getQueue();
			ch.queueBind(qn, exchangeName, routeKey2);
			result2 = es.schedule(new Consumer(EqualPredicate.getInstance(msg2), ch, queueName2, false, true), 2, TimeUnit.SECONDS);
		}

		{
			Channel ch = conn.createChannel();
			asychPublish(ch, exchangeName, routeKey1, 1, msg7, msg5, msg3, msg1);
			asychPublish(ch, exchangeName, routeKey2, 1, msg6, msg4, msg2);
		}

		Assert.assertEquals(result1.get().get(0), msg1);
		Assert.assertEquals(result2.get().get(0), msg2);
	}

	// routing_key - it must be a list of words, delimited by dots
	// * (star) can substitute for exactly one word.
	// # (hash) can substitute for zero or more words.
	@Test
	public void testTopic() throws Exception {
		final String exchangeName = "ex_topic";
		final String type = "topic";

		final String computer = "computer";
		final String database = computer + ".database";
		final String languages = computer + ".langs";

		final String java = languages.concat(".java");
		final String javascript = languages.concat(".javascript");
		final String sql = database + ".sql";

		{
			Channel ch = conn.createChannel();
			ch.exchangeDeclare(exchangeName, type);
			ch.close();
		}

		final String table = "table";
		final String select = "select";
		final String between = "between";

		final String var = "var";
		final String in = "in";

		final String _try = "try";
		final String _catch = "catch";
		final String _for = "for";
		final String _while = "while";
		final String _do = "do";

		final String assign = "=";
		final String minus = "-";
		final String plus = "+";
		final String div = "/";
		final String equal = "==";

		final String _class = "class";
		final String _interface = "interface";
		final String _implements = "implements";

		Future<List<Serializable>> langs = null;
		String langsQueueName = computer + ".langs.#";
		{
			Channel ch = conn.createChannel();
			final String qn = ch.queueDeclare(langsQueueName, true, false, true, null).getQueue();
			ch.queueBind(qn, exchangeName, langsQueueName);
			langs = es.schedule(new Consumer(EqualPredicate.getInstance(_class), ch, langsQueueName, false, true), 5, TimeUnit.SECONDS);
		}

		Future<List<Serializable>> javas = null;
		{
			Channel ch = conn.createChannel();
			final String qn = ch.queueDeclare(java, true, false, true, null).getQueue();
			ch.queueBind(qn, exchangeName, java);
			javas = es.schedule(new Consumer(EqualPredicate.getInstance(_class), ch, java, false, true), 5, TimeUnit.SECONDS);
		}

		Future<List<Serializable>> javascripts = null;
		{
			Channel ch = conn.createChannel();
			final String qn = ch.queueDeclare(javascript, true, false, true, null).getQueue();
			ch.queueBind(qn, exchangeName, javascript);
			javascripts = es.schedule(new Consumer(EqualPredicate.getInstance(var), ch, javascript, false, true), 5, TimeUnit.SECONDS);
		}

		Future<List<Serializable>> sqls = null;
		{
			Channel ch = conn.createChannel();
			final String qn = ch.queueDeclare(sql, true, false, true, null).getQueue();
			ch.queueBind(qn, exchangeName, sql);
			sqls = es.schedule(new Consumer(EqualPredicate.getInstance(table), ch, sql, false, true), 5, TimeUnit.SECONDS);
		}

		{
			Channel ch = conn.createChannel();
			asychPublish(ch, exchangeName, computer + ".#.#", 1, assign, minus, plus, div, equal);
			asychPublish(ch, exchangeName, langsQueueName, 2, _try, _catch, _for, _do, _while);
			asychPublish(ch, exchangeName, java, 3, _interface, _implements, _class);
			asychPublish(ch, exchangeName, javascript, 3, in, var);
			asychPublish(ch, exchangeName, sql, 4, select, between, table);
		}

		System.out.println("langs: " + langs.get());
		System.out.println("javas: " + javas.get());
		System.out.println("javascripts: " + javascripts.get());
		System.out.println("sqls: " + sqls.get());
	}

	@Test
	public void testExchangesAndQueues() throws Exception {
		Channel ch = conn.createChannel();

		final String direct = "direct", fanout = "fanout";
		ch.exchangeDeclare("ex", direct);
		ch.exchangeDeclare("ex", direct);
		ch.exchangeDeclare("ex", fanout);// exchange can't be redeclare on same
											// server with different type

		ch.queueDeclare("q", true, false, true, null);
		ch.queueDeclare("q", true, false, true, null);
		ch.queueDeclare("q", false, true, true, null);

		ch.queueBind("q", "ex", "");
		ch.queueBind("q", "ex", "");
		ch.queueBind("q", "ex", "");
	}

	@Test
	public void testBinding() throws Exception {
		Channel ch = conn.createChannel();

		final String direct = "direct";
		ch.exchangeDeclare("ex1", direct);
		ch.exchangeDeclare("ex2", direct);
		ch.exchangeDeclare("ex3", direct);

		ch.queueDeclare("q1", true, false, true, null);
		ch.queueDeclare("q2", true, false, true, null);
		ch.queueDeclare("q3", true, false, true, null);

		ch.queueBind("q1", "ex1", "ex1");
		ch.queueBind("q1", "ex2", "ex2");
		ch.queueBind("q2", "ex2", "ex2");
		ch.queueBind("q2", "ex3", "ex3");
		ch.queueBind("q3", "ex3", "ex3");
		ch.queueBind("q3", "ex1", "ex1");

		// ch.queueBind("q1", "ex1", null);
		// ch.queueBind("q1", "ex2", null);
		// ch.queueBind("q2", "ex2", null);
		// ch.queueBind("q2", "ex3", null);
		// ch.queueBind("q3", "ex3", null);
		// ch.queueBind("q3", "ex1", null);

		ch.basicPublish("ex1", "ex1", null, "to ex1".getBytes());

		String re = new String(ch.basicGet("q1", true).getBody());
		System.out.println(re);
		re = new String(ch.basicGet("q3", true).getBody());
		System.out.println(re);

	}

	@Test
	public void testRouteKey() throws Exception {
		Channel ch = conn.createChannel();

		final String direct = "direct";
		final String ex1 = "ex1";
		ch.exchangeDelete(ex1);

		ch.exchangeDeclare(ex1, direct);
		ch.queueDeclare("q1", true, false, true, null);
		ch.queueDeclare("q2", true, false, true, null);

		ch.queueBind("q1", ex1, "ex1->q1");
		ch.queueBind("q2", ex1, "ex1->q2");
		ch.queueBind("q3", ex1, "");

		ch.basicPublish(ex1, "ex1->q1", null, "from ex1 to q1".getBytes());
		ch.basicPublish(ex1, "ex1->q2", null, "from ex1 to q2".getBytes());
		ch.basicPublish(ex1, "", null, "from ex1 to q3".getBytes());

		final GetResponse resp1 = ch.basicGet("q1", true);
		String re = resp1 == null ? "null" : new String(resp1.getBody());
		System.out.println("q1: " + re);
		final GetResponse resp2 = ch.basicGet("q2", true);
		re = resp2 == null ? "null" : new String(resp2.getBody());
		System.out.println("q2: " + re);
		final GetResponse resp3 = ch.basicGet("q3", true);
		re = resp3 == null ? "null" : new String(resp3.getBody());
		System.out.println("q3: " + re);

		ch.close();
	}

	@Test
	public void testReject() throws Exception {
		final Channel sender = conn.createChannel();
		final Channel receiver = conn.createChannel();
		final Channel rejecter = conn.createChannel();

		final String ex = "ex";
		final String qu = "qu";
		final String direct = "direct";

		sender.exchangeDeclare(ex, direct);
		sender.queueDeclare(qu, true, false, false, null);
		final String routingKey = "ex_qu";
		sender.queueBind(qu, ex, routingKey);

		sender.basicPublish(ex, routingKey, null, "rejectee".getBytes());
		sender.close();

		QueueingConsumer c = new QueueingConsumer(receiver);
		receiver.basicConsume(qu, c);

		Delivery d = c.nextDelivery();
		String respMsgStr = new String(d.getBody());
		log.info(String.format("haver received %s", respMsgStr));
		Thread.sleep(1000);

		final long deliveryTag = d.getEnvelope().getDeliveryTag();
		rejecter.basicReject(deliveryTag, true);
		log.info(respMsgStr + " is rejected");
		Thread.sleep(1000);

		d = c.nextDelivery();
		respMsgStr = new String(d.getBody());
		log.info(String.format("haver received %s again", respMsgStr));
		Thread.sleep(1000);

		receiver.basicAck(deliveryTag, false);
		Thread.sleep(1000);

		d = c.nextDelivery();
		log.info("not more message: " + String.valueOf(d));
		Thread.sleep(1000);

		if (receiver.isOpen())
			receiver.close();
		if (rejecter.isOpen())
			rejecter.close();
	}

	@Test
	public void testMultiConsumersToOneChannel() throws Exception {
		final Channel sender = conn.createChannel();

		final String ex = "ex";
		final String qu = "qu";
		final String direct = "direct";

		sender.exchangeDeclare(ex, direct);
		sender.queueDeclare(qu, true, false, false, null);
		final String routingKey = "ex_qu";
		sender.queueBind(qu, ex, routingKey);

		
		final Channel receiver = conn.createChannel();

		QueueingConsumer c1 = new TestConsumer(receiver);
		receiver.basicConsume(qu, true, "c1", c1);
		log.info("consumer tag: " + c1.getConsumerTag());

		QueueingConsumer c2 = new TestConsumer(receiver);
		receiver.basicConsume(qu, true, "c2", c2);
		log.info("consumer tag: " + c2.getConsumerTag());

		for (int i = 0; i < 2; i++) {
			sender.basicPublish(ex, routingKey, null, ("message_" + i).getBytes());
		}
		
		if (sender.isOpen()) {
			sender.close();
		}
		
		{
			Delivery d = c1.nextDelivery();
			String respMsgStr = new String(d.getBody());
			log.info("c1: " + respMsgStr);
		}

		{
			Delivery d = c2.nextDelivery();
			String respMsgStr = new String(d.getBody());
			log.info("c2: " + respMsgStr);
		}

		if (sender.isOpen()) {
			sender.close();
		}
	}

	static class TestConsumer extends QueueingConsumer {
		public TestConsumer(Channel ch) {
			super(ch);
		}

		public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
			super.handleDelivery(consumerTag, envelope, properties, body);
			log.info("consume tag: " + consumerTag + " message: " + new String(body));
		}
	}

	@Test
	public void testOneConsumerToMultipleChannels() throws Exception {
		final Channel sender = conn.createChannel();

		final String ex = "ex";
		final String qu = "qu";
		final String direct = "direct";

		sender.exchangeDeclare(ex, direct);
		sender.queueDeclare(qu, true, false, false, null);
		final String routingKey = "ex_qu";
		sender.queueBind(qu, ex, routingKey);

		for (int i = 0; i < 10; i++) {
			sender.basicPublish(ex, routingKey, null, ("message_" + i).getBytes());
		}

		if (sender.isOpen()) {
			sender.close();
		}
		
		final Channel receiver1 = conn.createChannel();
		final Channel receiver2 = conn.createChannel();
		final Channel receiver3 = conn.createChannel();
		
		//Can't work out
	}
}
