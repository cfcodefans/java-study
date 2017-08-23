package cf.study.framework.org.apache.zookeeper;

import misc.MiscUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class ZooKeeperExamples {

    public static final Logger log = LogManager.getLogger(ZooKeeperExamples.class);

    static final int[] PORTS = new int[]{2181, 12181, 22181, 32181, 42181};

    static final ExecutorService RUNNERS = Executors.newFixedThreadPool(PORTS.length);

    static final Map<Integer, ZooKeeper> zks = new HashMap<>();

    @BeforeClass
    public static void setupClass() throws IOException {
        FileUtils.deleteDirectory(Paths.get("./data/zookeeper").toFile());
        System.setProperty("zookeeper.admin.enableServer", "false");
        log.info(MiscUtils.invocationInfo());
        IntStream.range(0, PORTS.length)
            .forEach(i -> RUNNERS.submit(() -> {
                startZooKeeperServer(PORTS[i], String.format("./data/zookeeper/zookeeper-%d", i));
            }));
        MiscUtils.easySleep(3000);
        log.info("setup finished");
    }

    static void startZooKeeperServer(int port, String dataPathStr) {
        ServerConfig sc = new ServerConfig();
        sc.parse(new String[]{String.valueOf(port), dataPathStr});
//        QuorumPeerConfig qpc = new QuorumPeerConfig();
//        qpc.parse(Properties);
        try {
            ZooKeeperServerMain zs = new ZooKeeperServerMain();
            zs.runFromConfig(sc);
//            QuorumPeerMain qp = new QuorumPeerMain();
//            qp.runFromConfig();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("[Distribution]  distribution server shutdown....");
    }

    public static Watcher watchEventLogger(final String watcherName) {
        return (WatchedEvent we) -> log.info(String.format("\n\t%s receives event:\n\t%s", watcherName, we));
    }

    @Test
    public void testSimpleWatcher() throws Exception {
        String path = "/apple";

        {
            String name = "jerry";
            ZooKeeper jerryZK = new ZooKeeper("localhost:" + PORTS[0], 1000, watchEventLogger(name));
            Stat _stat = jerryZK.exists(path, (WatchedEvent we) -> {
                try {
                    if (we.getType() == Watcher.Event.EventType.NodeCreated) {
                        Stat stat = new Stat();
                        String data = new String(jerryZK.getData(we.getPath(), watchEventLogger(name), stat));

                        log.info(String.format("\n\t%s see %s apples\n\t%s", name, data, stat));
                        log.info(String.format("\n\t%s will take one", name));
                        String newData = String.valueOf(Integer.parseInt(data) - 1);
                        jerryZK.setData(path, newData.getBytes(), stat.getVersion());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
            log.info(String.format("\n\t%s found %s \n\t%s", name, path, _stat));
        }
        {
            String name = "tom";
            Watcher watchLog = watchEventLogger(name);
            ZooKeeper tom = new ZooKeeper("localhost:" + PORTS[0], 1000, watchLog);
            tom.create(path, "2".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            log.info("\n\ttom put 2 apples");
            MiscUtils.easySleep(1000);

            Stat stat = new Stat();
            String data = new String(tom.getData(path, watchLog, stat));
            log.info(String.format("\n\t%s see %s apples\n\t%s", name, data, stat));

            tom.close();
        }
    }

    @Test
    public void testSetup() {
        log.info("setup");
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        log.info(MiscUtils.invocationInfo());
    }
}
