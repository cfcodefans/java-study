package cf.study.java.algothrims.graph.pregel.worker;

import cf.study.java.algothrims.graph.pregel.api.Message;
import cf.study.java.algothrims.graph.pregel.api.Vertex;
import cf.study.java.algothrims.graph.pregel.partition.IGraphPartition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by fan on 2017/1/20.
 */
public interface IWorker<R extends Serializable & Comparable<R>> extends Callable<R> {
    Logger log = LogManager.getLogger(IWorker.class);

    IGraphPartition getGraphPartition();

    ConcurrentLinkedQueue<Message> getMsgQueue();

    IMaster getMaster();

    void setMaster(IMaster master);

    default R call() {
        try {
            while (!Thread.interrupted()) {

            }
        } catch (Exception e) {
            log.error(String.format("error happened, worker exits"), e);
        }
        return null;
    }

    <V extends Serializable & Comparable<V>> void createBy(V newValue, Vertex<?> v);
    void delete(Vertex<?> v);
}
