package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "method_en")
public class MethodEn extends MemberEn {

	@ManyToOne(cascade = { CascadeType.REFRESH })
	public ClassEn returnClass;

	@ManyToMany(cascade = { CascadeType.REFRESH })
	@JoinTable(name = "params")
	@JoinColumn(nullable = true)
	public List<ClassEn> paramsClzz = new LinkedList<ClassEn>();

	@ManyToMany(cascade = { CascadeType.REFRESH })
	@JoinTable(name = "exceptions")
	@JoinColumn(nullable = true)
	public List<ClassEn> exceptionClzz = new LinkedList<ClassEn>();

	public MethodEn() {
		category = CategoryEn.METHOD;
	}

	public MethodEn(Method m, BaseEn enclosed) {
		super(m, enclosed, CategoryEn.METHOD);
	}
	
	@Basic
	public int paramsHash;
}
