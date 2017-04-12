package cf.study.jdk7.util;

import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Test;

public class ScannerTest {

	@Test
	public void testScanner() {
		Scanner sc = new Scanner(System.in);
		
		while (!sc.hasNextLine()) {
			System.out.println("waitting");
		}
		
		System.out.println(sc.nextLine());
		sc.close();
	}
	
	@Test
	public void testInputStream() throws IOException {
		System.out.println("going to read from System.in");
		try {
			for (LineIterator it = IOUtils.lineIterator(System.in, null); it.hasNext(); ) {
				final String nextLine = it.nextLine();
				System.out.println(nextLine);
				System.out.println("waiting");
				if (nextLine.startsWith("q")) break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("finish");
		
		LineIterator it = IOUtils.lineIterator(System.in, null);
		System.out.println("going to read from System.in");
			for (; it.hasNext(); ) {
				final String nextLine = it.nextLine();
				System.out.println(nextLine);
				System.out.println("waiting");
				if (nextLine.startsWith("q")) break;
			}
		System.out.println("finish");
		
		System.out.println("going to read from System.in");
		for (; it.hasNext(); ) {
			final String nextLine = it.nextLine();
			System.out.println(nextLine);
			System.out.println("waiting");
			if (nextLine.startsWith("q")) break;
		}
		System.out.println("finish");
	}
}
