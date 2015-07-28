package cf.study.jdk7.jaxb;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import cf.study.jdk7.jaxb.entity.Person;

public class JaxbTest {

	@Test
	public void testConvertPojoToXml() throws Exception {
		Person person = new Person();
		
		person.setBirthday(DateUtils.parseDate("1981-10-17", new String[] {"yyyy-MM-dd"}));
		
		person.setName(new Person.PersonName("Fan", "Chen", "cf"));
		
		person.setGender(Person.Gender.MALE);
		
		person.setMail("chenfanspost@hotmail.com");
		
		person.setTags((List<String>)Arrays.asList(new String[] {"stupid", "loser", "jerk"}));
		
		JAXBContext jc = JAXBContext.newInstance(Person.class);

		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(person, System.out);
	}
	
	@Test
	public void testConvertXmlToPojo() throws Exception {
		Person person = new Person();
		
		person.setBirthday(DateUtils.parseDate("1981-10-17", new String[] {"yyyy-MM-dd"}));
		
		person.setName(new Person.PersonName("Fan", "Chen", "cf"));
		
		person.setGender(Person.Gender.MALE);
		
		person.setMail("chenfanspost@hotmail.com");
		
		person.setTags((List<String>)Arrays.asList(new String[] {"stupid", "loser", "jerk"}));
		
		JAXBContext jc = JAXBContext.newInstance(Person.class);

		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		StringWriter sw = new StringWriter();
		marshaller.marshal(person, sw);
		
		String xmlStr = sw.toString();
		
		System.out.println(xmlStr);
		
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		
		Person clone = unmarshaller.unmarshal(new StreamSource(new StringReader(xmlStr)), Person.class).getValue();
		
		System.out.println(clone);
	}
}
