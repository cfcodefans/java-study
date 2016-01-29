package cf.study.java8.utils.zip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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

		Path zp = getSrcZipPath();
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
				try (ZipOutputStream zos = new ZipOutputStream(fos)) {
					zos.setLevel(0);
					// zos.setMethod(method);
					paths.forEach((Path _p) -> {
						File f = _p.toFile();
						String zipPath = FilenameUtils.separatorsToUnix(_p.toString());
						System.out.println(zipPath);
						if (!f.isFile()) {
							return;
						}
						try {
							ZipEntry ze = new ZipEntry(zipPath);
							zos.putNextEntry(ze);

								byte[] fileBytes = FileUtils.readFileToByteArray(f);
								zos.write(fileBytes);
							zos.closeEntry();
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
}
