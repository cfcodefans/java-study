package cf.study.java.algothrims.graph.pregel.simple;

import cf.study.java.algothrims.graph.pregel.api.Message;
import cf.study.java.algothrims.graph.pregel.api.Vertex;
import cf.study.java.algothrims.graph.pregel.partition.IGraphPartition;
import cf.study.java.algothrims.graph.pregel.worker.IMaster;
import cf.study.java.algothrims.graph.pregel.worker.IWorker;

import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by fan on 2017/1/23.
 */
public class SimpleWorker<R extends Serializable & Comparable<R>> implements IWorker<R> {

    public SimpleWorker(IGraphPartition partition, IMaster master) {
        this.partition = partition;
        this.master = master;
    }

    private IGraphPartition partition = null;

    @Override
    public IGraphPartition getGraphPartition() {
        return partition;
    }

    private ConcurrentLinkedQueue<Message> msgQueue = new ConcurrentLinkedQueue<>();

    @Override
    public ConcurrentLinkedQueue<Message> getMsgQueue() {
        return msgQueue;
    }

    private IMaster master = null;

    @Override
    public IMaster getMaster() {
        return master;
    }

    @Override
    public void setMaster(IMaster master) {
        this.master = master;
    }

    @Override
    public <V extends Serializable & Comparable<V>> void createBy(V newValue, Vertex<?> v) {
        if (newValue == null || v == null) return;

        Message<V> creationMsg = new Message<V>();
        creationMsg.setSrcVertexID(v.getId());
        creationMsg.setValue(newValue);
        creationMsg.setSuperStepID(master.currentSuperStep().getId());

        master.msgQueue().offer(creationMsg);
    }

    @Override
    public void delete(Vertex<?> v) {
        if (v == null) return;

    }
}
