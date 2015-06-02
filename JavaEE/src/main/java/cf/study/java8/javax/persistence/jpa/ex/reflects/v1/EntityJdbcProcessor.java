package cf.study.java8.javax.persistence.jpa.ex.reflects.v1;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections4.MapUtils;

import cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity.ClassEn;

public class EntityJdbcProcessor {
	static final AtomicLong ID = new AtomicLong(0l);
	public final Map<String, ClassEn> classEnPool = MapUtils.synchronizedMap(new LinkedHashMap<String, ClassEn>(21000));
}
