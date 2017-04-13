package cf.study.java.javax.persistence.jpa.ex.reflects.v1.entity;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class BaseEnIdClz implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final AtomicLong ID = new AtomicLong(0);
	
	public final Long id;
	
	public BaseEnIdClz() {
		id = ID.incrementAndGet();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BaseEnIdClz))
			return false;
		BaseEnIdClz other = (BaseEnIdClz) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
