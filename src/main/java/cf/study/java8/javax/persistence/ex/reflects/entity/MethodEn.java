package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.lang.reflect.Executable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "method_en")
public class MethodEn extends MemberEn {

	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name="return_clz_id", nullable=true)
	public ClassEn returnClass;

	@ManyToMany(cascade = { CascadeType.REFRESH })
	@JoinTable(name = "exceptions")
	@JoinColumn(nullable = true)
	public List<ClassEn> exceptionClzz = new LinkedList<ClassEn>();

	public MethodEn() {
		category = CategoryEn.METHOD;
	}

	public MethodEn(Executable m, BaseEn enclosed) {
		super(m, enclosed, CategoryEn.METHOD);
		this.method = m;
		paramsHash = Objects.hash((Object[])m.getParameters());
	}
	
	@Transient transient public Executable method;
	
	@Basic
	public int paramsHash;
}
