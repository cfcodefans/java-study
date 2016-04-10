package cf.study.java8.utils.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

public class ApacheCompressTests {

	public Path getSrcZipPath() {
		return Paths.get(SystemUtils.JAVA_HOME).getParent().resolve("src.zip");
	}

	@Test
	public void loadZip() throws Exception {
		System.out.println(SystemUtils.JAVA_HOME);

		Path zp = Paths.get("./test-output/src.zip");// getSrcZipPath();
		System.out.println(zp);

		try (ZipFile zf = new ZipFile(zp.toFile())) {
			zf.stream().forEach(ze -> System.out.println(ze.getName()));
		}
		try (ZipFile zf = new ZipFile(zp.toFile())) {
			Optional<? extends ZipEntry> zeOpt = zf.stream().filter(ze -> !ze.isDirectory()).filter(ze -> ze.getName().endsWith("java")).findFirst();
			if (zeOpt.isPresent()) {
				InputStream is = zf.getInputStream(zeOpt.get());
				System.out.println(IOUtils.toString(is));
			}
		}
	}

	@Test
	public void makeZip() throws Exception {
		Path p = Paths.get("src");
		p.toFile().delete();

		try (Stream<Path> paths = Files.walk(p, FileVisitOption.FOLLOW_LINKS)) {
//			try (FileOutputStream fos = new FileOutputStream("./test-output/src.zip")) {
				try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(new File("./test-output/src.zip"))) {
					zos.setMethod(ZipOutputStream.DEFLATED);
					zos.setLevel(9);
					// zos.setMethod(method);
					paths.forEach((Path _p) -> {
						File f = _p.toFile();
						String zipPath = FilenameUtils.separatorsToUnix(_p.toString());
						
						
						ZipEntry ze = new ZipEntry(zipPath);
						try {
							if (!f.isFile()) {
//								zos.putNextEntry(ze);
//								zos.closeEntry();
								return;
							}

							byte[] fileBytes = FileUtils.readFileToByteArray(f);
							ze.setSize(fileBytes.length);
							ze.setMethod(ZipEntry.DEFLATED);
							ZipArchiveEntry ae = new ZipArchiveEntry(ze);
							ae.setSize(fileBytes.length);
							zos.putArchiveEntry(ae);
							System.out.println(zipPath + "\t" + ae + "\t" + ae.getSize());
							zos.write(fileBytes);
							zos.closeArchiveEntry();
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
					zos.flush();
				}
//				fos.flush();
//			}
		}
	}
	
	@Test
	public void testZipInputStream() throws Exception {
		try (FileInputStream fis = new FileInputStream("./test-output/src.zip")) {
			try (ZipArchiveInputStream zis = new ZipArchiveInputStream(fis)) {
				for (ZipArchiveEntry ze = zis.getNextZipEntry(); ze != null; ze = zis.getNextZipEntry()) {
//					System.out.println(ToStringBuilder.reflectionToString(ze));
//					zis.closeEntry();
					System.out.println(ze.getName() + "\t" + ze.getSize() + "\t" + StringUtils.join(ze.getExtraFields(), '\t'));
				}
			}
		}
	}
	
	@Test 
	public void testUnzip() throws Exception {
		Path base = Paths.get("./test-output").toAbsolutePath();
		
		try (FileInputStream fis = new FileInputStream("./test-output/src.zip")) {
			try (ZipInputStream zis = new ZipInputStream(fis)) {
				for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()) {
					Path target = base.resolve(ze.getName());
					System.out.println(target + " " + ze.getSize());
					
//					byte[] data = new byte[(int)ze.getSize()];
//					zis.read(data);
//					zis.closeEntry();
//					
//					File file = target.toFile();
//					
//					FileUtils.writeByteArrayToFile(file, data);
					
					System.out.println(IOUtils.toString(zis));
				}
			}
		}
	}
}
