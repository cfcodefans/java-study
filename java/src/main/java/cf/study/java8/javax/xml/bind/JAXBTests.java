package cf.study.java8.javax.xml.bind;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;

public class JAXBTests {

	@XmlType(name="test")
	public static class TestBean {
		@XmlElement(name="id")
		public int id;
		
		@XmlElementWrapper(name="params")
		@XmlElements( value = { @XmlElement(name="param") })
		public List<TestParamBean> params = new ArrayList<TestParamBean>();
	}
	
	@XmlType(name="param")
	public static class TestParamBean {
		@XmlElement(name="id")
		public int id;
	}
	
	@Test
	public void testJaxb() {
		TestBean tb = new TestBean();
		tb.params.add(new TestParamBean());
		tb.params.add(new TestParamBean());
		tb.params.add(new TestParamBean());
		tb.params.add(new TestParamBean());
		
		System.out.println(ToStringBuilder.reflectionToString(tb, ToStringStyle.MULTI_LINE_STYLE));

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		JAXB.marshal(tb, bos);
		
		System.out.println(new String(bos.toByteArray()));
	}
}
