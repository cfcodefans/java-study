package cf.study.framework.org.apache.zookeeper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.junit.Before;
import org.junit.Test;

public class ZooKeeperClusterTest {
	public static final Logger log = LogManager.getLogger(ZooKeeperClusterTest.class);

	private static final String PATH = "/cf/study/network/distributed/zookeeper";
	private static final String ROOT_PATH = "/cf";

	public static void main(String[] args) {
		ZooKeeperClusterTest test = new ZooKeeperClusterTest();
		test.testStartZooKeeperServer();
	}

	public static ServerConfig getServerCfg() {
		ServerConfig sc = new ServerConfig();
		sc.parse(new String[]{"2181", "./data/zookeeper"});
		return sc;
	}

	public void startZooKeeperServer() {
		ServerConfig sc = getServerCfg();
		try {
			ZooKeeperServerMain zs = new ZooKeeperServerMain();
			zs.runFromConfig(sc);
			log.info("[Distribution]  distribution server started up.... host:" + sc.getClientPortAddress().getHostName() + ";address:"
				+ sc.getClientPortAddress().getAddress().getHostAddress() + ";port:" + sc.getClientPortAddress().getPort());
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("[Distribution]  distribution server shutdown....");
	}

	@Test
	public void testStartZooKeeperServer() {
		try {
			Thread distributionThread = new Thread(this::startZooKeeperServer);
			distributionThread.setDaemon(true);
			distributionThread.start();
			Thread.sleep(1000);

			initZooKeeper();
			testChildEvents();
			distributionThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	ZooKeeper zk;

	public void watch(WatchedEvent ev) {
		log.info("get an event:\n\t" + ev);
	}

	@Before
	public void initZooKeeper() {
		try {
			zk = new ZooKeeper("localhost:2181", 3000, this::watch);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testStartZooKeeperClient() {
		try {
			createNode(zk, PATH, "init".getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createNode(ZooKeeper zk, String path, byte[] data) throws Exception {
		for (int i = 0; (i = path.indexOf('/', i)) != -1; ) {
			String subPath = path.substring(0, i);
			if (StringUtils.isBlank(subPath)) {
				i++;
				continue;
			}
			Stat stat = zk.exists(subPath, false);
			if (stat != null) {
				i++;
				continue;
			}
			log.info(zk.create(subPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));
		}
		if (zk.exists(path, false) == null) {
			log.info(zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));
		}
	}

	public void watchChildren(WatchedEvent ev) {
		log.info("child watcher");
		log.info(ev);
		try {
			log.info(zk.getChildren(ev.getPath(), this::watchChildren));
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testChildWatcher() {
		try {
			createNode(zk, ROOT_PATH, "init.root".getBytes());
			log.info(zk.getChildren(ROOT_PATH, this::watchChildren, null));
			createNode(zk, PATH, "data".getBytes());
			zk.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testChildEvents() {
		try {
			zk.create("/a", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk.create("/a/b", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			log.info(zk.getChildren("/a/b", this::watchChildren));
			zk.create("/a/b/c", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk.create("/a/b/c/d", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//			zk.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testWatcher() {
		try {
			log.info(zk.getChildren(ROOT_PATH, false));
			log.info(zk.getChildren(PATH, false));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
