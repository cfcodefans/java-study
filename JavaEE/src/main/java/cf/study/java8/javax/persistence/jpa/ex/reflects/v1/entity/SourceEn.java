package cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity;

import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import misc.Jsons;

@Entity
@Table(name = "source_en")
//@IdClass(SourceEnIdClz.class)
public class SourceEn {
	public static final AtomicLong ID = new AtomicLong();
	
	public SourceEn() {
//		id = ID.incrementAndGet();
	}
	
	public SourceEn(final String _name) {
		this();
		name = _name;
		clzName = _name.trim().replace('/', '.').replace(".java", "");
	}
	
	
	@Id
	public long id;

	@Basic
	public String name;
	
	@Basic
	public String clzName;

	@ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.PERSIST})
	@JoinColumn(name = "jar_id", nullable = true)
	public JarEn jar;

	@Basic
	@Column(columnDefinition="MEDIUMTEXT")
	public String source;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((jar == null) ? 0 : jar.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SourceEn))
			return false;
		SourceEn other = (SourceEn) obj;
		if (id != other.id)
			return false;
		if (jar == null) {
			if (other.jar != null)
				return false;
		} else if (!jar.equals(other.jar))
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
