package cf.study.java8.utils.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

public class ZipTests {

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

			try (FileOutputStream fos = new FileOutputStream("./test-output/src.zip")) {
				CheckedOutputStream cos = new CheckedOutputStream(fos, new Adler32());
				try (ZipOutputStream zos = new ZipOutputStream(cos)) {
					zos.setMethod(ZipOutputStream.DEFLATED);
					zos.setLevel(9);
					// zos.setMethod(method);
					paths.forEach((Path _p) -> {
						File f = _p.toFile();
						String zipPath = FilenameUtils.separatorsToUnix(_p.toString());
						System.out.println(zipPath);
						
						
						ZipEntry ze = new ZipEntry(zipPath);
						try {
							if (!f.isFile()) {
//								zos.putNextEntry(ze);
//								zos.closeEntry();
								return;
							}

							byte[] fileBytes = FileUtils.readFileToByteArray(f);
							ze.setSize(fileBytes.length);
							String sizeInfo = String.format("size: %d", fileBytes.length);
							ze.setComment(sizeInfo);
							ze.setExtra(sizeInfo.getBytes());
							ze.setMethod(ZipEntry.DEFLATED);
							zos.putNextEntry(ze);
							zos.write(fileBytes);
//							zos.closeEntry();
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
					zos.flush();
				}
				fos.flush();
			}
		}
	}
	
	@Test
	public void testZipInputStream() throws Exception {
		try (FileInputStream fis = new FileInputStream("./test-output/src.zip")) {
			CheckedInputStream cis = new CheckedInputStream(fis, new Adler32());
			try (ZipInputStream zis = new ZipInputStream(cis)) {
				for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()) {
//					System.out.println(ToStringBuilder.reflectionToString(ze));
//					zis.closeEntry();
					System.out.println(String.format("%s %s", ze.getName(), new String(ze.getExtra())));
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
