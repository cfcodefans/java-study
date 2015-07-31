package cf.study.data.binding;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class JacksonTests {
	static final ObjectMapper mapper = new ObjectMapper();

	// @JsonFilter("WebDavUrl")
	public static class WebDavUrl {
		private String url;
		private String userName;
		private String password;

		public WebDavUrl() {
			// System.out.println("WebDavUrl()");
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		@Override
		public String toString() {
			try {
				return super.toString() + " " + mapper.writeValueAsString(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return super.toString();
		}
	}

	@Test
	public void testJosnToObjWithFilter() throws Exception {
		ObjectMapper OM = new ObjectMapper();
		ObjectWriter OW = null;
		{
			SimpleFilterProvider filter = new SimpleFilterProvider();
			filter.addFilter("WebDavUrl", SimpleBeanPropertyFilter.serializeAllExcept("password"));
			OM.setFilters(filter);
			OW = OM.writer(filter);
		}
		WebDavUrl wdu = new WebDavUrl();
		System.out.println(OW.writeValueAsString(wdu));
	}

	@Test
	public void testJosnToObjWithTransformFilter() throws Exception {
		ObjectMapper OM = new ObjectMapper();
		ObjectWriter OW = null;
		{
			SimpleFilterProvider filter = new SimpleFilterProvider();
			filter.addFilter("WebDavUrl", SimpleBeanPropertyFilter.serializeAllExcept("password"));
			OM.setFilters(filter);
			OW = OM.writer(filter);
		}
		WebDavUrl wdu = new WebDavUrl();
		System.out.println(OW.writeValueAsString(wdu));
	}

	static class SimpleSerializer extends JsonSerializer<WebDavUrl> {
		@Override
		public void serialize(WebDavUrl value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
			jgen.writeStartObject();
			jgen.writeObjectField("address", value.url);
			jgen.writeObjectField("name", value.userName);
			jgen.writeObjectField("pwd", value.password);
			jgen.writeEndObject();
		}

		public Class<WebDavUrl> handledType() {
			return WebDavUrl.class;
		}
	}

	@Test
	public void testJosnToObjWithSerializer() throws Exception {
		ObjectMapper OM = new ObjectMapper();
		ObjectWriter OW = null;
		{
			SimpleModule simpleModule = new SimpleModule("SimpleModule", new Version(1, 0, 0, null));
			simpleModule.addSerializer(new SimpleSerializer());
			OM.registerModule(simpleModule);
		}
		WebDavUrl wdu = new WebDavUrl();
		System.out.println(OM.writeValueAsString(wdu));
	}

	@Test
	public void testJosnToObj() {
		String jsonStr = "{\"url\":\"localhost:8080\", \"userName\":\"cf\", \"password\":\":5:{s:9:\\\"lastLogin\\\";i:1370420246;s:15:\\\"lastOnlineState\\\";i:0;s:6:\\\"userId\\\";i:2791891;s:16:\\\"lastVisibleLogin\\\";i:1370420246;s:4:\\\"type\\\";s:17:\\\"update login info\\\";}\"}";

		ObjectMapper mapper = new ObjectMapper();

		try {
			Object obj = mapper.readValue(jsonStr, WebDavUrl.class);
			System.out.println(obj);

			{
				long t = System.currentTimeMillis();
				for (int i = 0; i < 40000; i++) {
					obj = mapper.readValue(jsonStr, WebDavUrl.class);
				}
				System.out.println(System.currentTimeMillis() - t);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testObjsToJsonStr() throws Exception, JsonMappingException, IOException {
		ObjectMapper om = new ObjectMapper();
		System.out.println(om.writeValueAsString(null));
		System.out.println(om.writeValueAsString(new Date()));
		System.out.println(om.writeValueAsString(new String("String")));
		System.out.println(om.writeValueAsString(1));

		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("date", new Date());
		map.put("array", new float[] { 3, 1, 4, 2, 1, 5, 9 });
		map.put(5, 2);
		System.out.println(om.writeValueAsString(map));
	}

	@Test
	public void testJosnToJsonNode() {
		String jsonStr = "{\"url\":\"localhost:8080\", \"userName\":\"cf\", \"password\":\"start123\"}";

		ObjectMapper mapper = new ObjectMapper();

		try {
			Object obj = mapper.readValue(jsonStr, JsonNode.class);
			System.out.println(obj);

			{
				long t = System.currentTimeMillis();
				for (int i = 0; i < 40000; i++) {
					obj = mapper.readValue(jsonStr, JsonNode.class);
				}
				System.out.println(System.currentTimeMillis() - t);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testJsonNode() {
		String jsonStr = "{\"url\":\"localhost:8080\", \"userName\":\"cf\", \"password\":\"start123\"}";

		ObjectMapper mapper = new ObjectMapper();
		Object obj;
		try {
			obj = mapper.readValue(jsonStr, JsonNode.class);
			System.out.println(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testJsonNodeWithSpecialChars() {
		// String jsonStr =
		// "{\"q\"=\"age:[28 TO 33]\",\"start\"=0,\"rows\"=10,\"d\"=2500,\"sfield\"='store',\"pt\"=\"15.1256,45.4569\",\"fq\"=\"{!geofilt+sfield=store}\",\"fl\"=\"*,position,_dist_:geodist()\" }";
		// String jsonStr =
		// "{q:\"age:[28 TO 33]\",start:0,rows:10,d:2500,sfield:store,pt:\"15.1256,45.4569\",fq:\"{!geofilt+sfield=store}\",fl:\"*,position,_dist_:geodist()\" }";
		String jsonStr = "{\"q\":\"age:[28 TO 33]\",\"start\":0,\"rows\":10,\"d\":2500,\"sfield\":\"store\",\"pt\":\"15.1256,45.4569\",\"fq\":\"{!geofilt+sfield=store}\",\"fl\":\"*,position,_dist_:geodist()\" }";

		ObjectMapper mapper = new ObjectMapper();
		Object obj;
		try {
			obj = mapper.readValue(jsonStr, JsonNode.class);
			System.out.println(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
