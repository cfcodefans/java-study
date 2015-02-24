package cf.study.java8.utils.zip;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;


public class ZipTest {

	public Path getSrcZipPath() {
		return Paths.get(SystemUtils.JAVA_HOME).getParent().resolve("src.zip");
	}
	
	@Test
	public void loadZip() throws Exception {
		System.out.println(SystemUtils.JAVA_HOME);
		
		Path zp = getSrcZipPath();
		System.out.println(zp);
		
		try (ZipFile zf = new ZipFile(zp.toFile())) {
			zf.stream().forEach(ze->System.out.println(ze.getName()));
		} 
		try (ZipFile zf = new ZipFile(zp.toFile())) {
			Optional<? extends ZipEntry> zeOpt = zf.stream().filter(ze->!ze.isDirectory()).filter(ze->ze.getName().endsWith("java")).findFirst();
			if (zeOpt.isPresent()) {
				InputStream is = zf.getInputStream(zeOpt.get());
				System.out.println(IOUtils.toString(is));
			}
		}
	}
}
