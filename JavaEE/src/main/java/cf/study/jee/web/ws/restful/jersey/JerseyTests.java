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
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import misc.Jsons;
import misc.MiscUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.message.DeflateEncoder;
import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.EncodingFilter;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JerseyTests {
	
	public static class TestApp extends ResourceConfig {
		public TestApp() {
			log.info(MiscUtils.invocationInfo());
			
			register(EncodingFilter.class);
			register(GZipEncoder.class);
			register(DeflateEncoder.class);
			registerResources(Resource.from(TestRes.class));
		}
	}
	
	@Path("test") 
	public static class TestRes {
		public TestRes() {
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

	private static final Logger log = LoggerFactory.getLogger(JerseyTests.class);
	
	@Test
	public void helloWorld() throws Exception {
		Server srv = new Server(8080);
		
//		ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
//		ctx.setContextPath();//NOTED, don't forget "/"
//		
//		ServletHolder handler = ctx.addServlet(ServletContainer.class, "/rest/*");
//		handler.setInitParameters(MiscUtils.map(
//				"javax.ws.rs.Application", TestApp.class.getName(),
//				"jersey.config.server.application.name", "JerseyTests"));
//		handler.setInitOrder(1);
		
		srv.setHandler(setUpApp("/jersey", "/rest/*", TestApp.class));
		
		
		srv.start();
		srv.dump(System.out);
		srv.join();
	}
	
	public static ServletContextHandler setUpApp(final ServletContextHandler ctx, String servletPathStr, Class<? extends Application> appCls) {
		if (ctx == null || !StringUtils.startsWith(servletPathStr, "/") || appCls == null) 
			return ctx;
		
		ServletHolder handler = ctx.addServlet(ServletContainer.class, servletPathStr);
		handler.setInitParameters(MiscUtils.map(
				"javax.ws.rs.Application", appCls.getName()));
		
		return ctx;
	}
	
	public static ServletContextHandler setUpApp(String ctxPathStr, String servletPathStr, Class<? extends Application> appCls) {
		ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
		ctx.setContextPath(ctxPathStr);
		return setUpApp(ctx, servletPathStr, appCls);
	}
	
}
