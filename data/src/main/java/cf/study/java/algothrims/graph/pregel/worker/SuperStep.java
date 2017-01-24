package cf.study.java.algothrims.graph.pregel.worker;


import cf.study.java.algothrims.graph.pregel.api.BaseObj;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by fan on 2017/1/20.
 */
public class SuperStep extends BaseObj {
    protected final AtomicLong ID = new AtomicLong(0);

    public SuperStep() {
        setId(ID.getAndIncrement());
    }

    private Map<String, Serializable> context = new ConcurrentHashMap<>();

    public Map<String, Serializable> getContext() {
        return context;
    }
}
