package cf.study.java8.utils.properties;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Properties;

/**
 * Created by fan on 2016/8/11.
 */
public class PropertiesTests {
	@Test
	public void testPropertiesVar() throws Exception {
		Properties p = new Properties();
		p.load(PropertiesTests.class.getResourceAsStream("./test.properties"));
		System.out.println(StringUtils.join(p.values()));
	}
}
