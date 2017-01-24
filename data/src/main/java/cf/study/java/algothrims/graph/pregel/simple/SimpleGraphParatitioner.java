package cf.study.java.algothrims.graph.pregel.simple;


import cf.study.java.algothrims.graph.pregel.partition.IGraphPartition;
import cf.study.java.algothrims.graph.pregel.partition.IGraphPartitioner;
import misc.MiscUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by fan on 2017/1/20.
 */
public class SimpleGraphParatitioner implements IGraphPartitioner {
    @Override
    public Collection<IGraphPartition> partitions() {
        return Arrays.asList(parts);
    }

    @Override
    public IGraphPartition getPartition(long vid) {
        return parts[(int) (vid % parts.length)];
    }

    private IGraphPartition[] parts = new IGraphPartition[MiscUtils.AVAILABLE_PROCESSORS];

}
