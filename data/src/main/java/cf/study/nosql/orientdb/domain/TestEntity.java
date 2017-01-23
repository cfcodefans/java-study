package cf.study.nosql.orientdb.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.Id;
import javax.persistence.Version;

public class TestEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public TestEntity() {
		
	}
	
	@Id
	public Object oid;
	
	private static AtomicLong ID = new AtomicLong(0);
	
	private long id = ID.getAndIncrement();
	
	private String name = null;
	
	private Date created = new Date();
	
	@Version
	private Long version;

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public TestEntity(String name) {
		super();
		this.name = name;
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
		if (getClass() != obj.getClass())
			return false;
		TestEntity other = (TestEntity) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public String toString() {
		return String.format("SimpleTestEntity [oid=%s, id=%s, name=%s, created=%s, version=%s]", oid, id, name, created, version);
	}
}
