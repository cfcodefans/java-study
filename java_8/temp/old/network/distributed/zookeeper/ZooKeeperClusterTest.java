package cf.study.old.network.distributed.zookeeper;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class ZooKeeperClusterTest {

	
	
	private static final String PATH = "/cf/study/network/distributed/zookeeper";
	private static final String ROOT_PATH = "/cf";

	public static void main(String[] args) {
		ZooKeeperClusterTest test = new ZooKeeperClusterTest();
		test.testStartZooKeeperServer();
		
	}

	@Test
	public void testStartZooKeeperServer() {
		try {
			final ServerConfig sc = new ServerConfig();
			sc.parse("cfg/dist.cfg");
			FileUtils.deleteDirectory(new File(sc.getDataDir()));

			Thread distributionThread = new Thread() {
				@Override
				public void run() {
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
			};

			distributionThread.setDaemon(true);
			distributionThread.start();
			Thread.sleep(1000);
			
			initZooKeeper();
			testChildEvents();

			distributionThread.join();
		} catch (ConfigException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	ZooKeeper zk;
	private static Logger log = Logger.getLogger(ZooKeeperClusterTest.class);

	@Before
	public void initZooKeeper() {
		try {
			Watcher watcher = new Watcher() {
				@Override
				public void process(WatchedEvent ev) {
					log.info("get an event: ");
					log.info(ev);
				}
			};
			zk = new ZooKeeper("localhost:2181", 3000, watcher);
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
		for (int i = 0; (i = path.indexOf('/', i)) != -1;) {
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
			log.info(zk.create(subPath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));
		}
		if (zk.exists(path, false) == null) {
			log.info(zk.create(path, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));
		}
	}

	Watcher w = new Watcher() {
		@Override
		public void process(WatchedEvent ev) {
			log.info("child watcher");
			log.info(ev);
			try {
				log.info(zk.getChildren(ev.getPath(), this));
			} catch (KeeperException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	};
	
	@Test
	public void testChildWatcher() {
		try {
			createNode(zk, ROOT_PATH, "init.root".getBytes());
			log.info(zk.getChildren(ROOT_PATH, w, null));
			createNode(zk, PATH, "data".getBytes());
			zk.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testChildEvents() {
		try {
			zk.create("/a", null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk.create("/a/b", null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			log.info(zk.getChildren("/a/b", w));
			zk.create("/a/b/c", null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk.create("/a/b/c/d", null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
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
