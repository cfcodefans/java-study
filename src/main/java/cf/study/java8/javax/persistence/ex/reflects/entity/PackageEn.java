package cf.study.java8.javax.persistence.ex.reflects.entity;

import javax.persistence.Basic;
import javax.persistence.Entity;

@Entity
public class PackageEn extends BaseEn {

	public PackageEn() {
	}

	public PackageEn(Package pkg) {
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

	public static class Factory {
		public static PackageEn by(Package pkg) {
			if (pkg == null)
				return null;
			return new PackageEn(pkg);
		}
	}
}
