package cf.study.osgi;

import org.apache.commons.lang.StringUtils;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.osgi.util.ManifestElement;
import org.junit.Test;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

public class BundleMiscTest {

	@Test
	public void testManifestItem() throws BundleException {
		String header = " com.thenetcircle.services.lib, com.thenetcircle.services.manager, com.thenetcircle.services.http, com.thenetcircle.services.ldap;bundle-version=\"0.1.0\"";
		ManifestElement[] es = ManifestElement.parseHeader(Constants.REQUIRE_BUNDLE, header);
		
		System.out.println(StringUtils.join(es, "\n"));
		
		for (ManifestElement me : es) {
			String verAttr = me.getAttribute(Constants.BUNDLE_VERSION_ATTRIBUTE);
			if (verAttr == null) continue;
			VersionRange vr = new VersionRange(verAttr);
			
			System.out.println(String.format("value: %s, version: %s", me.getValue(), vr.toString()));
		}
	}

}
