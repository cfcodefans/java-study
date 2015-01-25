package cf.study.java8.javax.persistence.ex.reflects.entity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "package_en")
public class PackageEn extends BaseEn {

	@Transient transient public Package _package;
	
	public PackageEn() {
		category = CategoryEn.PACKAGE;
	}

	public PackageEn(Package pkg, PackageEn enclosing) {
		super(pkg.getName(), enclosing, CategoryEn.PACKAGE);
		
		this._package = pkg;
		
//		System.out.println(category + ": " + name);

		specTitle = pkg.getSpecificationTitle();
		specVendor = pkg.getSpecificationVendor();
		specVersion = pkg.getSpecificationVersion();

		implTitle = pkg.getImplementationTitle();
		implVersion = pkg.getImplementationVersion();
		implVendor = pkg.getImplementationVendor();
	}

	@Basic
	public String specTitle;
	@Basic
	public String specVersion;
	@Basic
	public String specVendor;
	@Basic
	public String implTitle;
	@Basic
	public String implVersion;
	@Basic
	public String implVendor;

}
