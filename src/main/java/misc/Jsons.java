package misc;

import java.io.InputStream;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public final class Jsons {
	private Jsons() {
	}

	public static final ObjectMapper MAPPER = new ObjectMapper();

	static {
		MAPPER.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
		MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		MAPPER.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		MAPPER.setSerializationInclusion(Include.NON_NULL);
		MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);
		MAPPER.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
		MAPPER.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
		
//		JaxbAnnotationModule module = new JaxbAnnotationModule();
//		// configure as necessary
//		MAPPER.registerModule(module);
//		
//		AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
//		// if ONLY using JAXB annotations:
//		MAPPER.setAnnotationIntrospector(introspector);
	}

	/*This method is unused except the tests*/
	public static JsonNode read(final InputStream input) {
		if (input == null) {
			throw new NullPointerException("reading json however the input stream is empty");
		}
		try {
			return MAPPER.readValue(input, JsonNode.class);
		} catch (Exception e) {
			throw new RuntimeException("reading json stream",e);
		}
	}

	public static JsonNode read(final String raw) {
		if (raw == null) {
			throw new NullPointerException("reading json however the json raw is empty");
		}
		try {
			return MAPPER.readValue(raw, JsonNode.class);
		} catch (Exception e) {
			throw new RuntimeException("reading json raw",e);
		}
	}
	
	public static <T> T read(final String raw, final Class<T> cls) {
		if (raw == null) {
			throw new NullPointerException("reading json however the json raw is empty");
		}
		try {
			return MAPPER.readValue(raw, cls);
		} catch (Exception e) {
			throw new RuntimeException("reading json raw",e);
		}
	}

	public static String toString(final Object obj) {
		try {
			return MAPPER.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException("deserialing json to string",e);
		}
	}

}

