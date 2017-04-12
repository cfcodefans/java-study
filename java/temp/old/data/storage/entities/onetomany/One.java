package cf.study.data.storage.entities.onetomany;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

@Entity
public class One {

	@Id
	@SequenceGenerator(name = "seq_one", sequenceName = "PUBLIC.seq_one")
	@GeneratedValue(generator = "seq_one", strategy = GenerationType.SEQUENCE)	
	private int id;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="the_one")
	private List<Many> many = new ArrayList<Many>();
	public List<Many> getMany() {
		return many;
	}

	public void setMany(List<Many> many) {
		this.many = many;
	}
	
	@OneToMany(cascade=CascadeType.PERSIST)
	@JoinColumn(name="the_one")
	private List<Many> cascadePersistMany = new ArrayList<Many>();
	public List<Many> getCascadePersistMany() {
		return cascadePersistMany;
	}

	public void setCascadePersistMany(List<Many> cascadePersistMany) {
		this.cascadePersistMany = cascadePersistMany;
	}
	
	@OneToMany(cascade=CascadeType.MERGE)
	@JoinColumn(name="the_one")
	private List<Many> cascadeMergeMany = new ArrayList<Many>();
	public List<Many> getCascadeMergeMany() {
		return cascadeMergeMany;
	}

	public void setCascadeMergeMany(List<Many> cascadeMergeMany) {
		this.cascadeMergeMany = cascadeMergeMany;
	}
	
	@OneToMany(cascade=CascadeType.REMOVE)
	@JoinColumn(name="the_one")
	private List<Many> cascadeRemoveMany = new ArrayList<Many>();
	public List<Many> getCascadeRemoveMany() {
		return cascadeRemoveMany;
	}

	public void setCascadeRemoveMany(List<Many> cascadeRemoveMany) {
		this.cascadeRemoveMany = cascadeRemoveMany;
	}
	
	@OneToMany(cascade=CascadeType.REFRESH)
	@JoinColumn(name="the_one")
	private List<Many> cascadeRefreshMany = new ArrayList<Many>();
	public List<Many> getCascadeRefreshMany() {
		return cascadeRefreshMany;
	}

	public void setCascadeRefreshMany(List<Many> cascadeRefreshMany) {
		this.cascadeRefreshMany = cascadeRefreshMany;
	}
	
	@OneToMany(cascade=CascadeType.DETACH)
	@JoinColumn(name="the_one")
	private List<Many> cascadeDetachMany = new ArrayList<Many>();
	public List<Many> getCascadeDetachMany() {
		return cascadeDetachMany;
	}

	public void setCascadeDetachMany(List<Many> cascadeDetachMany) {
		this.cascadeDetachMany = cascadeDetachMany;
	}
	
}
