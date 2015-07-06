package cf.study.data.mining.entity;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name = "package_en")
//@Cacheable(false)
public class PackageEn extends BaseEn {

	@Transient transient public Package _package;
	
	public PackageEn() {
		category = CategoryEn.PACKAGE;
	}

	public PackageEn(Package pkg, PackageEn enclosing) {
		super(pkg.getName(), enclosing, CategoryEn.PACKAGE);
		
		this._package = pkg;

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

	public static Package getParentPkg(Package _package) {
		if (_package == null || !_package.getName().contains("."))
			return null;

		String pkgStr = StringUtils.substringBeforeLast(_package.getName(), ".");
		if (StringUtils.isBlank(pkgStr))
			return null;
		return Package.getPackage(pkgStr);
	}
	
	public PackageEn clone() {
		return clone(null);
	}
	
	public PackageEn clone(PackageEn _pe) {
		if (_pe == null) {
			_pe = new PackageEn();
		}
		
		_pe = (PackageEn)super.clone(_pe);
		
		_pe._package = _package;
		_pe.specTitle = specTitle;
		_pe.specVendor = specVendor;
		_pe.specVersion = specVersion;
		
		_pe.implTitle = implTitle;
		_pe.implVendor = implVendor;
		_pe.implVersion = implVersion;
		
		return _pe;
	}

}
