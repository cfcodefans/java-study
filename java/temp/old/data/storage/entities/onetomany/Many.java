package cf.study.old.data.storage.entities.onetomany;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class Many {

	private int id;

	@Id
	@SequenceGenerator(name = "seq_many", sequenceName = "PUBLIC.seq_many")
	@GeneratedValue(generator = "seq_many", strategy = GenerationType.SEQUENCE)	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Column
	@Basic
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Many))
			return false;
		Many other = (Many) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("Many [id=%s, name=%s]", id, name);
	}
	
	
	
}
