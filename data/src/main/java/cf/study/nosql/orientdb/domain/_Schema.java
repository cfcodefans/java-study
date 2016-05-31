package cf.study.nosql.orientdb.domain;

public class _Schema {
	private Long sid;
	private String name;

	public Long getSid() {
		return sid;
	}

	public void setSid(Long id) {
		this.sid = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "_Schema [id=" + sid + ", name=" + name + "]";
	}

}
