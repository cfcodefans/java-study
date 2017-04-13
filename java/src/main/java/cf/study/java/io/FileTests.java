package cf.study.java.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Stack;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

public class FileTests {
	
	@Test
	public void testFileOperations() throws Exception {
		File f = Paths.get("test/f").toAbsolutePath().toFile();
		System.out.println(f.getAbsolutePath());
		Assert.assertFalse(f.exists());
		
//		can't create a file before its parent folder exists
//		f.createNewFile();
		
//		unsupported on windows
//		Files.createFile(f.toPath(), PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-rw-rw-")));
		
//		can't create the whole path
//		try (OutputStream newOutputStream = Files.newOutputStream(f.toPath(), CREATE, APPEND)) { // read is not allowed?, StandardOpenOption.READ)) {
//			IOUtils.write("new file", newOutputStream);
//			newOutputStream.flush();
//		}
		
		FileTests.createFile(f);
		Assert.assertTrue(f.exists());
		Assert.assertTrue(f.canRead());
		Assert.assertTrue(f.canWrite());
		Assert.assertTrue(f.isFile());
		
		String what = "what";
		try (FileOutputStream fos = new FileOutputStream(f)) {
			fos.write(what.getBytes());
		}
		
		Assert.assertEquals(f.length(), what.length());
		
		try (FileInputStream fis = new FileInputStream(f)) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[16];
			
			for (int read = fis.read(buf); read != -1; read = fis.read(buf)) {
				baos.write(buf, 0, read);
			}
			
			String x = new String(baos.toByteArray());
			System.out.println(x);
			Assert.assertEquals(x, what);
		}
		
		f.delete();
		Assert.assertFalse(f.exists());
	}
	
	public static boolean deleteFile(File f) throws Exception {
		if (f == null || !f.exists()) {
			return true;
		}
		
		Path ap = f.toPath().toAbsolutePath();
		
		if (!f.delete()) {
			return false;
		}
		
		for (Path _p = ap.getParent(); _p != null; _p = _p.getParent()) {
			File _f = _p.toFile();
			if (ArrayUtils.isNotEmpty(f.list())) {
				return true;
			}
			if (!_f.delete()) {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean createFile(File f) throws Exception {
		if (f == null || f.exists()) {
			return false;
		}
		
		Path ap = f.toPath().toAbsolutePath();
		Stack<Path> s = new Stack<Path>();
		for (Path _p = ap.getParent(); _p != null; _p = _p.getParent()) {
			s.push(_p);
		}
		
		while (!s.isEmpty()) {
			Path _p = s.pop();
			File _f = _p.toFile();
			if (_f.exists()) continue;
			
			if (!_f.mkdir()) {
				return false;
			}
		}
		
		return f.createNewFile();
	}
	
	@Test
	public void testCreateFile() throws Exception {
		File f = Paths.get("test/a/b/c/d/e/f").toAbsolutePath().toFile();
		createFile(f);
		deleteFile(f);
	}
}
