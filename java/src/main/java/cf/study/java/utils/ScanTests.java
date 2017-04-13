package cf.study.java.utils;

import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by fan on 2016/9/22.
 */
public class ScanTests {



	@Test
	public void testScan() {
		try (Scanner scan = new Scanner(System.in)) {
			String countStr = scan.nextLine().trim();
			int count = Integer.parseInt(countStr);

			if (count <= 0) {
				return;
			}

			String numberStr = scan.nextLine().trim();
			List<Integer> list = Stream.of(numberStr.split(" ", count))
				.map(String::trim)
				.map(str -> Integer.valueOf(str))
				.collect(Collectors.toList());
			Collections.reverse(list);
			System.out.println(String.join(" ", list.stream().map(i -> i.toString()).collect(Collectors.toList())));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		new ScanTests().testScan();
	}
}
