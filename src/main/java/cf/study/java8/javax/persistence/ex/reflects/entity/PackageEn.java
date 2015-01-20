package cf.study.java8.javax.persistence.ex.reflects.entity;

import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.persistence.Basic;
import javax.persistence.Entity;

@Entity
public class PackageEn extends ElementEn implements PackageElement {

	@Basic
	public String qualifiedName;
	
	@Basic
	public boolean unnamed = false;
	
	public Name getQualifiedName() {
		return new NameImpl(qualifiedName);
	}

	public boolean isUnnamed() {
		return unnamed;
	}
}
