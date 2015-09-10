package cf.study.jdk7.util.regex;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class RegexTest {
//	The rules for selecting a username are:
//		1. The minimum length of the username must be 5 characters and the maximum may be 10.
//		2. It should contain at least one letter from A-Z
//		3. It should contain at least one digit from 0-9
//		4. It should contain at least one character from amongst @#*=
//		5. It should not contain any spaces
	
	@Test
	public void validateUserNameLength() {
		Assert.assertFalse(Pattern.matches(".{5}", "1234"));
		Assert.assertTrue(Pattern.matches(".{5}", "12345"));
		Assert.assertTrue(Pattern.matches(".{5,10}", "123456"));
		Assert.assertTrue(Pattern.matches(".{5,10}", "1234567890"));
		Assert.assertFalse(Pattern.matches(".{5,10}", "12345678901"));
	}
	
	@Test
	public void validateUserNameNonSpaces() {
		String patternStr = "[^\\s]{5,10}";
		Assert.assertFalse(Pattern.matches(patternStr, "1234"));
		Assert.assertFalse(Pattern.matches(patternStr, "1234 "));
		Assert.assertFalse(Pattern.matches(patternStr, " 1234"));
		Assert.assertFalse(Pattern.matches(patternStr, " 1234 "));
		Assert.assertFalse(Pattern.matches(patternStr, "12  34"));
		Assert.assertTrue(Pattern.matches(patternStr, "12345678"));
		Assert.assertFalse(Pattern.matches(patternStr, "1234\t5678"));
		Assert.assertFalse(Pattern.matches(patternStr, "123456789 "));
		Assert.assertTrue(Pattern.matches(patternStr, "1234/t5678"));
	}
	
	@Test
	public void validateUserNameAtLeastOneCapital() {
		
		// at least one upper case letter
		String patternStr = "^(?=.*[A-Z].*)[^\\s]{5,10}$";
		Assert.assertFalse(Pattern.matches(patternStr, "1234"));
		Assert.assertFalse(Pattern.matches(patternStr, "1234 "));
		Assert.assertFalse(Pattern.matches(patternStr, " 1234"));
		Assert.assertFalse(Pattern.matches(patternStr, " 1234 "));
		Assert.assertFalse(Pattern.matches(patternStr, "12  34"));
		Assert.assertFalse(Pattern.matches(patternStr, "12345678"));
		Assert.assertFalse(Pattern.matches(patternStr, "1234\t5678"));
		Assert.assertFalse(Pattern.matches(patternStr, "123456789 "));
		
		Assert.assertTrue(Pattern.matches(patternStr, "1234E5678"));
		Assert.assertTrue(Pattern.matches(patternStr, "1234EF678"));
		Assert.assertTrue(Pattern.matches(patternStr, "a234EF678"));
	}
	
	@Test
	public void validateUserNameAtLeastTwoCapital() {
		
		// at least one upper case letter
		String patternStr = "^(?=.*[A-Z]{2,}+.*)[^\\s]{5,10}$";
		Assert.assertFalse(Pattern.matches(patternStr, "1234"));
		Assert.assertFalse(Pattern.matches(patternStr, "1234 "));
		Assert.assertFalse(Pattern.matches(patternStr, " 1234"));
		Assert.assertFalse(Pattern.matches(patternStr, " 1234 "));
		Assert.assertFalse(Pattern.matches(patternStr, "12  34"));
		Assert.assertFalse(Pattern.matches(patternStr, "12345678"));
		Assert.assertFalse(Pattern.matches(patternStr, "1234\t5678"));
		Assert.assertFalse(Pattern.matches(patternStr, "123456789 "));
		
		Assert.assertFalse(Pattern.matches(patternStr, "1234E5678"));
		Assert.assertTrue(Pattern.matches(patternStr, "1234EF678"));
		Assert.assertTrue(Pattern.matches(patternStr, "a234EaF678"));
	}
	
	@Test
	public void validateUserNameAtLeastOneCapitalAndAtLeastOneNumber() {
		
		// at least one upper case letter
		String patternStr = "^(?=.*[A-Z].*)(?=.*[0-9].*)[^\\s]{5,10}$";
		Assert.assertFalse(Pattern.matches(patternStr, "abcd"));
		Assert.assertFalse(Pattern.matches(patternStr, "Abcd "));
		Assert.assertFalse(Pattern.matches(patternStr, " aBcd"));
		Assert.assertFalse(Pattern.matches(patternStr, " abCd "));
		Assert.assertFalse(Pattern.matches(patternStr, "ab  cD"));
		Assert.assertFalse(Pattern.matches(patternStr, "abcdefgh"));
		Assert.assertFalse(Pattern.matches(patternStr, "abcd\tefgh"));
		Assert.assertFalse(Pattern.matches(patternStr, "abcdefgh "));
		Assert.assertFalse(Pattern.matches(patternStr, "abcdefgh9"));
		Assert.assertFalse(Pattern.matches(patternStr, "12cdefg8"));
		
		Assert.assertTrue(Pattern.matches(patternStr, "Abc4efgh"));
		Assert.assertTrue(Pattern.matches(patternStr, "1bcdefgH"));
		Assert.assertTrue(Pattern.matches(patternStr, "12cdZfg8"));
	}
	
	@Test
	public void validateUserNameAtLeastOneCapitalAndAtLeastOneNumberAndSpecialChars() {
		
		// at least one upper case letter
		String patternStr = "^(?=.*[A-Z].*)(?=.*[0-9].*)(?=.*[@#\\*\\=].*)[^\\s]{5,10}$";
		Assert.assertFalse(Pattern.matches(patternStr, "abcd"));
		Assert.assertFalse(Pattern.matches(patternStr, "Abcd "));
		Assert.assertFalse(Pattern.matches(patternStr, " aBcd"));
		Assert.assertFalse(Pattern.matches(patternStr, " abCd "));
		Assert.assertFalse(Pattern.matches(patternStr, "ab  cD"));
		Assert.assertFalse(Pattern.matches(patternStr, "abcdefgh"));
		Assert.assertFalse(Pattern.matches(patternStr, "abcd\tefgh"));
		Assert.assertFalse(Pattern.matches(patternStr, "abcdefgh "));
		Assert.assertFalse(Pattern.matches(patternStr, "abcdefgh9"));
		Assert.assertFalse(Pattern.matches(patternStr, "12cdefg8"));
		
		Assert.assertFalse(Pattern.matches(patternStr, "Abc4efgh"));
		Assert.assertFalse(Pattern.matches(patternStr, "1bcdefgH"));
		Assert.assertFalse(Pattern.matches(patternStr, "12cdZfg8"));
		
		Assert.assertTrue(Pattern.matches(patternStr, "Abc4e@fgh"));
		Assert.assertTrue(Pattern.matches(patternStr, "1bcde@#fgH"));
		Assert.assertTrue(Pattern.matches(patternStr, "12cd**Zfg8"));
	}
	
	@Test
	public void extract() throws Exception {
		String str = FileUtils.readFileToString(new File("D:/git/payment/log/server.log_2013-12-30T21-40-59"));
		System.out.println(str.length());
		
		String regex = "<eventlist.*?eventlist>";  
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL|Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			System.out.println(matcher.group());
		}
	}
	
	@Test
	public void extractTo() throws Exception {
		final String rootPathStr = "D:/git/payment/log/";
		File xmlFile = new File(rootPathStr + "mms.xml");
		
		FileOutputStream fos = new FileOutputStream(xmlFile, false);
		
		for (File lf : Paths.get(rootPathStr).toFile().listFiles()) {
			if (!lf.getName().startsWith("server.log"))
				continue;
			String log = FileUtils.readFileToString(lf);
			String[] strs = log.split("Received MMS PUSH Message");
			for (int i = 1; i < strs.length; i++) {
				strs[i] = strs[i].split("</eventlist>")[0] + "</eventlist>";
				System.out.println(strs[i]);
				IOUtils.write(strs[i] + '\n', fos);
			}
			fos.flush();
		}
		fos.close();
	}
	
	@Test
	public void matchMultilines() {
		String str =   
                "<table>                \n" +  
                "  <tr>                 \n" +  
                "    <td>               \n" +  
                "       Hello World!    \n" +  
                "    </td>              \n" +  
                "  </tr>                \n" +  
                "</table>";  
		
		System.out.println(str);
		
        String regex = "<td>(.+?)</td>";  
		{
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(str);
			while (matcher.find()) {
				System.out.println(matcher.group(1).trim());
			}
		}
        
		System.out.println("doesn't work");
        //doesn't work 
		{
			Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
			Matcher matcher = pattern.matcher(str);
			while (matcher.find()) {
				System.out.println(matcher.group(1).trim());
			}
		}
	}
	
}
