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
	@JoinTable(name = "exceptions", 
						joinColumns= {@JoinColumn(name="method_en_id", referencedColumnName="id")},
	                    inverseJoinColumns = {@JoinColumn(name="exception_en_id", referencedColumnName="id")})
	public List<ClassEn> exceptionClzz = new LinkedList<ClassEn>();

	public MethodEn() {
		category = CategoryEn.METHOD;
	}

	public MethodEn(Executable m, BaseEn enclosed) {
		super(m, enclosed, CategoryEn.METHOD);
		this.method = m;
		paramsHash = Objects.hashCode((Object[])m.getParameters());
	}
	
	public boolean isMatch(Executable method) {
		return (method != null) && (name.equals(method.getName()) && paramsHash == Objects.hashCode((Object[])method.getParameters()));
	}
	
	@Transient transient public Executable method;
	
	@Basic
	public int paramsHash;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + paramsHash;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof MethodEn))
			return false;
		MethodEn other = (MethodEn) obj;
		if (paramsHash != other.paramsHash)
			return false;
		return true;
	}
}
