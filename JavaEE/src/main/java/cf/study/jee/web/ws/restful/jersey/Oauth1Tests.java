package cf.study.jee.web.ws.restful.jersey;

import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import misc.Jsons;
import misc.MiscUtils;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.glassfish.jersey.message.DeflateEncoder;
import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.EncodingFilter;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.oauth1.OAuth1ServerFeature;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Oauth1Tests {

	private static final Logger log = LoggerFactory.getLogger(Oauth1Tests.class);
	public static class Oauth1App extends ResourceConfig {
		
		public Oauth1App() {
			log.info(MiscUtils.invocationInfo());
			
			register(EncodingFilter.class);
			register(GZipEncoder.class);
			register(DeflateEncoder.class);
			registerResources(Resource.from(TestOauth1Res.class));
			
			
			register(OAuth1ServerFeature.class);
		}
	}

	@Path("test") 
	public static class TestOauth1Res {
		public TestOauth1Res() {
//			log.info(MiscUtils.invocationInfo());
		}
		
		@Path("hello_world.txt")
		@Produces(MediaType.TEXT_PLAIN)
		@GET
		public String helloWorldTxt() {
			return MiscUtils.invocationInfo();
		}
		
		@Path("now")
		@Produces(MediaType.TEXT_PLAIN)
		@GET
		public String now() {
			return String.valueOf(System.currentTimeMillis());
		}
		
		@Path("now")
		@Produces(MediaType.APPLICATION_JSON)
		@POST
		public String nowByUTCOffset(@FormParam("offset") int offset) {
			log.info("offset: \t" + offset);
			Map<String, Calendar> res = Stream.of(TimeZone.getAvailableIDs(offset * 3600 * 1000))
				.map(tzId->new ImmutablePair<>(tzId, Calendar.getInstance(TimeZone.getTimeZone(tzId))))
				.collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
			log.info(res.toString());
			return Jsons.toString(res);
		}
		
		@Path("/")
		@Produces(MediaType.TEXT_PLAIN)
		@GET
		public String info() {
			return "info";
		}
	}
	
	@Test
	public void twitterExample() throws Exception {
		//...
	}
}
