package cf.study.java8.javax.persistence.ex.reflects.entity;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class MethodEn extends MemberEn {
	
	@ManyToOne(cascade= {CascadeType.REFRESH})
	public ClassEn returnClass;
	
	@ManyToMany(cascade= {CascadeType.REFRESH})
	@JoinTable(name="params")
	public List<ClassEn> paramsClzz = new LinkedList<ClassEn>();
	
	@ManyToMany(cascade= {CascadeType.REFRESH})
	@JoinTable(name="params")
	public List<ClassEn> exceptionClzz = new LinkedList<ClassEn>();
}
