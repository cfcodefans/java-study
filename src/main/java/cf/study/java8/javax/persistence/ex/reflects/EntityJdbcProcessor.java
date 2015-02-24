package cf.study.java8.javax.persistence.ex.reflects;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections4.MapUtils;

import cf.study.java8.javax.persistence.ex.reflects.entity.ClassEn;

public class EntityJdbcProcessor {
	static final AtomicLong ID = new AtomicLong(0l);
	public final Map<String, ClassEn> classEnPool = MapUtils.synchronizedMap(new LinkedHashMap<String, ClassEn>(21000));
}
