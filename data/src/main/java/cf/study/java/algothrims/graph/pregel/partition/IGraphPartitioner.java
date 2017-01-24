package cf.study.java.algothrims.graph.pregel.partition;

import java.util.Collection;

/**
 * Created by fan on 2017/1/20.
 */
public interface IGraphPartitioner {
    Collection<IGraphPartition> partitions();
    IGraphPartition getPartition(long vid);
}
