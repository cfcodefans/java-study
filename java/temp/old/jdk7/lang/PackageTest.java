package cf.study.jdk7.lang;

import java.io.IOException;

import javax.swing.JFrame;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class PackageTest {
	@Test
	public void testPackage() {
		System.out.println(JFrame.class.getName()); 
		System.out.println(Package.getPackage("javax.swing"));
	}
	
	@Test
	public void testLoadRes() throws Exception {
		System.out.println(IOUtils.toString(this.getClass().getResourceAsStream("/log4j2.xml")));
		System.out.println(IOUtils.toString(this.getClass().getResourceAsStream("/META-INF/persistence.xml")));
	}
}
