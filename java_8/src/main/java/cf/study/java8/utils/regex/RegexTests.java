package cf.study.java8.utils.regex;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class RegexTests {

    @Test
    public void testMatchGroup() {
//        String regex = "(https{0,1}://.*/|https{0,1}://[^/]*)";
        String regex = "https{0,1}://[^/]*";
        {
            String urlStr = "https://www.google.com.hk/search?q=spark+executor.CoarseGrainedExecutorBackend:+RECEIVED+SIGNAL+15:+SIGTERM&newwindow=1&safe=strict&source=lnt&tbs=qdr:y&sa=X&ved=0ahUKEwjs-q3TsofRAhWI2YMKHZU-AOgQpwUIEw&biw=1920&bih=986";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(urlStr);
            m.find();
            System.out.println(m);
        }

        {
            String urlStr = "http://www.google.com.hk/search?q=spark+executor.CoarseGrainedExecutorBackend:+RECEIVED+SIGNAL+15:+SIGTERM&newwindow=1&safe=strict&source=lnt&tbs=qdr:y&sa=X&ved=0ahUKEwjs-q3TsofRAhWI2YMKHZU-AOgQpwUIEw&biw=1920&bih=986";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(urlStr);
            m.find();
            System.out.println(m);
        }

        {
            String urlStr = "https://www.google.com.hk";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(urlStr);
            m.find();
            System.out.println(m);
        }
    }

    @Test
    public void testUserIdAndOper() {
        Pattern p = Pattern.compile("/[1-9]\\d*/login$");
        Assert.assertTrue(p.matcher("/1234/login").matches());

        Pattern np = Pattern.compile("[1-9]\\d*");
        Matcher m = np.matcher("/1234/login");
        System.out.println(m.matches());
        System.out.println(m.find());
        System.out.println(m.toMatchResult());
        System.out.println(m.toMatchResult().group());
    }

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

    @Test
    public void testSuffix() {
        Assert.assertTrue(Pattern.matches(".*abc$", "123abc"));
        Assert.assertTrue(Pattern.compile("abc$").matcher("123abc12").find());
    }

    @Test
    public void testCtrlChar() throws Exception {
        Character z = new Character((char) 26);
        StringBuilder sb = new StringBuilder();
        sb.append("abc").append(z).append(123);
        System.out.println(sb);

        System.out.println(String.format("%c", z));

        Assert.assertTrue(Pattern.matches(".*a.*", sb));
        Assert.assertTrue(Pattern.matches(".*b.*", sb));
        Assert.assertTrue(Pattern.matches(String.format(".*%c.*", z), sb));
        Assert.assertTrue(Pattern.matches(".*\\p{Cc}.*", sb));
        Assert.assertTrue(Pattern.matches(".*\\cZ.*", sb)); // \c control key
//		Assert.assertTrue(Pattern.matches(".*\\u0026.*", sb));

        LineIterator li = IOUtils.lineIterator(new FileReader(Paths.get("d:\\var\\data1").toFile()));
        String line1 = li.nextLine();
        li.close();
        System.out.println(line1);

        Assert.assertTrue(Pattern.matches("^(.*)\\cZ(.*)$", line1));

        List<String> tmpList = new ArrayList<String>(128);
        IntStream.range(0, 1).forEach(i -> tmpList.add("(.*)"));
        String pattern = "^" + StringUtils.join(tmpList, "\\cZ") + "$";
        System.out.println(pattern);

        Assert.assertTrue(Pattern.matches(pattern, line1));
        String[] values = line1.split("\\cZ");
        System.out.println(values.length);
        System.out.println(StringUtils.join(values, "\n"));

//		String repl = "\\N".replaceAll("\\\\N", "0");
//		System.out.println(repl);

//		
//		Matcher matcher = Pattern.compile(pattern).matcher(line1);
//		
//		System.out.println(matcher.matches());
//		
//		System.out.println(matcher.groupCount());

//		IntStream.range(0, matcher.groupCount()).forEach(i->{
//			System.out.println(i);
//			System.out.println(matcher.group(i));
//		});
    }

    @Test
    public void testSplit() {
        System.out.println(Arrays.toString("1 2 3 4".split(" ", 4)));
    }
}
