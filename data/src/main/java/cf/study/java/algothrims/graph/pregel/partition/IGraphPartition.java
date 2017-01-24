package cf.study.java.algothrims.graph.pregel.partition;


import cf.study.java.algothrims.graph.pregel.api.Vertex;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by fan on 2017/1/20.
 */
public interface IGraphPartition extends Serializable {
    Collection<Vertex<?>> vertice();
}
