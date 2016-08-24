package cf.study.data.binding.avro;

import org.apache.avro.Schema;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

/**
 * Created by fan on 2016/8/9.
 * Avro schemas are defined using JSON. Schemas are composed of primitive types (null, boolean, int, long, float, double, bytes, and string) and complex types (record, enum, array, map, union, and fixed). You can learn more about Avro schemas and types from the specification, but for now let's start with a simple schema example, user.avsc
 */
public class AvroTests {
	private static final Logger log = Logger.getLogger(AvroTests.class);

	static String USER_SCHEMA = "";

	@BeforeClass
	public static void setupClass() throws Exception {
		USER_SCHEMA = IOUtils.toString(AvroTests.class.getResourceAsStream("./user.avsc"));
	}

	@Test
	public void testParseSchema() throws Exception {
		Assert.assertFalse(USER_SCHEMA.isEmpty());
		Schema.Parser p = new Schema.Parser();
		Schema schema = p.parse(USER_SCHEMA);
		log.info(schema);
	}

	@Test
	public void testSchema() throws Exception {
		Schema.Parser p = new Schema.Parser();
		Schema schema = p.parse(USER_SCHEMA);
		log.info(schema);

		Schema.Type type = schema.getType();
		log.info(type);


	}
}
