package cf.study.java.algothrims.graph.pregel.worker;

import cf.study.java.algothrims.graph.pregel.api.Message;
import cf.study.java.algothrims.graph.pregel.partition.IGraphPartitioner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by fan on 2017/1/22.
 */
public interface IMaster<OUTPUT extends Serializable & Comparable<OUTPUT>> extends Callable<OUTPUT> {
    Logger log = LogManager.getLogger(IMaster.class);

    ConcurrentLinkedQueue<Message> msgQueue();

    SuperStep currentSuperStep();

    SuperStep previousSuperStep();

    IGraphPartitioner getPartitioner();

    <R extends Serializable & Comparable<R>> Collection<IWorker<R>> getWorkers();

    default OUTPUT call() {
        try {
            while (!Thread.interrupted()) {

            }
        } catch (Exception e) {
            log.error(String.format("error happened, worker exits"), e);
        }
        return null;
    }
}
