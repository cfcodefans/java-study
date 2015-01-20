package cf.study.java8.utils.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class RegexTests {

	@Test
	public void test() {
		Pattern pattern = Pattern.compile("(ab|cd)");
		Matcher matcher = pattern.matcher("ababab");
		System.out.println(matcher.find());
		matcher = pattern.matcher("cdcdcd");
		System.out.println(matcher.find());
//		Assert.assertTrue(Pattern.matches("(a)", "aaa"));
//		Assert.assertTrue(Pattern.matches("(b)", "bbb"));
	}
}
