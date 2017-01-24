package cf.study.java.algothrims.graph.pregel.simple;


import cf.study.java.algothrims.graph.pregel.api.Message;
import cf.study.java.algothrims.graph.pregel.partition.IGraphPartitioner;
import cf.study.java.algothrims.graph.pregel.worker.IMaster;
import cf.study.java.algothrims.graph.pregel.worker.IWorker;
import cf.study.java.algothrims.graph.pregel.worker.SuperStep;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by fan on 2017/1/23.
 */
public class SimpleMaster implements IMaster {
    @Override
    public ConcurrentLinkedQueue<Message> msgQueue() {
        return null;
    }

    @Override
    public SuperStep currentSuperStep() {
        return null;
    }

    @Override
    public SuperStep previousSuperStep() {
        return null;
    }

    private final SimpleGraphParatitioner partitioner = new SimpleGraphParatitioner();

    @Override
    public IGraphPartitioner getPartitioner() {
        return partitioner;
    }

    @Override
    public Collection<IWorker> getWorkers() {
        return workers;
    }

    private Set<IWorker> workers = new HashSet<>();

    public SimpleMaster() {
        partitioner.partitions().stream().map(part -> new SimpleWorker(part, this)).forEach(workers::add);
    }
}
