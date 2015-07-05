package cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class SourceEnIdClz implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final AtomicLong ID = new AtomicLong(0);
	
	public final long id;
	
	public SourceEnIdClz() {
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
		if (!(obj instanceof SourceEnIdClz))
			return false;
		SourceEnIdClz other = (SourceEnIdClz) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
