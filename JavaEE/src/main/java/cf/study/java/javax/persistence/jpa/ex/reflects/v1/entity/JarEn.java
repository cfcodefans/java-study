package cf.study.java.javax.persistence.jpa.ex.reflects.v1.entity;

import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import misc.Jsons;

@Entity
@Table(name = "jar_en")
//@IdClass(JarEnIdClz.class)
public class JarEn {
	public static final AtomicLong ID = new AtomicLong();
	
	public JarEn() {
//		id = ID.incrementAndGet();
	}
	
	@Id
	public long id;
	
	@Basic
	public String name;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof JarEn))
			return false;
		JarEn other = (JarEn) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return Jsons.toString(this);
	}
}
