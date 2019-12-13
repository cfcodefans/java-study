package study.java.lang.base;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static study.commons.MiscUtils.easySleep;

public class ThreadTests {
    static final Logger log = LoggerFactory.getLogger(ArithmeticTests.class);

    public static class DaemonThreadTest {
        static {
            log.info("first in static {}", Thread.currentThread());
            Thread.currentThread().setDaemon(true);
        }

        public static void main(String[] args) {
            log.info("first in main");
            try {
                new ThreadTests().testDaemon();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testDaemon() throws Exception {
        log.info("starting Daemon thread");

        Thread thread = new Thread(() -> {
            log.info("thread started");
            try {
                Thread.sleep(1000);
                log.info("sleep 1");
                Thread.sleep(1000);
                log.info("sleep 2");
                Thread.sleep(1000);
                log.info("sleep 3");
            } catch (Exception e) {
                log.error("?", e);
            }
            log.info("thread stopped");
        });

        thread.setDaemon(false);
        thread.start();
        Thread.sleep(10);
//        thread.join();
        log.info("exit");
    }

    @Test
    public void testDaemonInfiniteLoop() throws Exception {
        log.info("starting Daemon thread");

        Thread thread = new Thread(() -> {
            log.info("thread started");
            try {
                while (true) {
                    Thread.sleep(1000);
                    log.info("sleep 1");
                    Thread.sleep(1000);
                    log.info("sleep 2");
                    Thread.sleep(1000);
                    log.info("sleep 3");
                }
            } catch (Exception e) {
                log.error("?", e);
            }
            log.info("thread stopped");
        });

        Thread waitForThread = new Thread(() -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                log.error("?", e);
            }
        });

//        thread.setDaemon(true);
        thread.start();
        waitForThread.start();
        Thread.sleep(1010);
    }

    @Test
    public void testThreadHoldingMain() throws Exception {
        log.info("starting test");
        Thread thread = new Thread(() -> {
            jam(2000);
        });
        thread.start();
        Thread.sleep(1000);
        thread.join();
        log.info("I go first");
    }

    private static void jam(int millisec) {
        log.info("start jamming");
        long now = System.currentTimeMillis();
        while (System.currentTimeMillis() - now < millisec) {
            double d = Math.pow(Math.PI, Math.E);
        }
        log.info("stop jamming");
    }

    @Test
    public void testInterrupt() {
        Thread th = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
//                    Thread.sleep(1000);
                    jam(1000);
                    log.info("am I interrupted? {}", Thread.currentThread().isInterrupted());
                }
            } catch (Exception e) {
                log.error("I am interrupted by {}", e);
            }
        });
        th.start();
        try {
            Thread.sleep(3500);
            th.interrupt();
            th.join();
        } catch (InterruptedException e) {
            log.error("I am interrupted by {}", e);
        }
    }

    @Test
    public void testDeadLock() {
        String a = "a";
        String b = "b";

        Thread th1 = new Thread(() -> {
            log.info("going to get a");
            try {
                synchronized (a) {
                    easySleep(1000);
                    log.info("I have a, going to get b");
                    synchronized (b) {
                        log.info("I have both a and b");
                    }
                    log.info("what happened?");
                }
            } catch (Exception e) {
                log.error("what happened? \n{}", e);
            }
        });

        Thread th2 = new Thread(() -> {
            log.info("going to get b");
            try {
                synchronized (b) {
                    easySleep(1000);
                    log.info("I have b, going to get a");
                    synchronized (a) {
                        log.info("I have both a and b");
                    }
                }
            } catch (Exception e) {
                log.error("what happened? \n{}", e);
            }
        });

        th1.start();
        th2.start();
        try {
            th1.join(3000);
            log.info("state of th1 {}", th1.getState());
            th2.join(3000);
            log.info("state of th2 {}", th1.getState());
            th1.interrupt();
            easySleep(1000);
            log.info("state of th1 {} after interruption", th1.getState());
        } catch (InterruptedException e) {
            log.error("I am interrupted by {}", e);
        }
        log.info("I go first");
    }

    @Test
    public void testAvertDeadLock() {
        String a = "a";
        String b = "b";

        Thread th1 = new Thread(() -> {
            log.info("going to get a");
            try {
                synchronized (a) {
                    easySleep(1000);
                    log.info("I had a, give up a now, going to get b");
                    a.wait();
                    synchronized (b) {
                        log.info("I have both a and b");
                    }
                    log.info("what happened?");
                }
            } catch (Exception e) {
                log.error("what happened? \n{}", e, e);
            }
        });

        Thread th2 = new Thread(() -> {
            log.info("going to get b");
            try {
                synchronized (b) {
                    easySleep(1000);
                    log.info("I had b, going to get a");
                    synchronized (a) {
                        log.info("I have both a and b");
                        a.notifyAll();
                    }
                    log.info("now you can take b");
                    b.wait();
                }
            } catch (Exception e) {
                log.error("what happened? \n{}", e);
            }
        });

        th1.start();
        th2.start();
        try {
            th1.join(3000);
            log.info("state of th1 {}", th1.getState());
            th2.join(3000);
            log.info("state of th2 {}", th1.getState());
            th1.interrupt();
            easySleep(1000);
            log.info("state of th1 {} after interruption", th1.getState());
        } catch (InterruptedException e) {
            log.error("I am interrupted by {}", e);
        }
        log.info("I go first");
    }
}
