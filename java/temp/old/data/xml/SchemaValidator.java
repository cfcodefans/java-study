package cf.study.data.xml;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.junit.Test;

public class SchemaValidator {

	@Test
	public void test() {
		main(new String[] {"cfg/job.xml"});
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 1. Lookup a factory for the W3C XML Schema language
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

		try {
			// 2. Compile the schema.
			// Here the schema is loaded from a java.io.File, but you could use
			// a java.net.URL or a javax.xml.transform.Source instead.
			File schemaLocation = new File("cfg/job.xsd");
			Schema schema = factory.newSchema(schemaLocation);

			// 3. Get a validator from the schema.
			Validator validator = schema.newValidator();

			// 4. Parse the document you want to check.
			Source source = new StreamSource(args[0]);

			// 5. Check the document
			validator.validate(source);
			System.out.println(args[0] + " is valid.");
			
			//add tnc, the name space for xquery
			Map<String, String> map = new HashMap<String, String>();
	        map.put("tnc","tnc");
	        SAXReader saxReader = new SAXReader();
//	        File file = new File(args[0]);
	        saxReader.getDocumentFactory().setXPathNamespaceURIs(map);
//	        Document document = saxReader.read(file);
//	        List<?> tmp = document.selectNodes("//tnc:jobs/tnc:conf");
		} catch (Exception ex) {
			System.out.println(args[0] + " is not valid because ");
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

}
