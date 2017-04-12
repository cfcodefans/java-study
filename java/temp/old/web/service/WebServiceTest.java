package cf.study.web.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Assert;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.BeforeClass;
import org.junit.Test;

import cf.study.utils.MiscUtils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class WebServiceTest {

	static Logger log = Logger.getLogger(WebServiceTest.class.getName());

	@Test
	public void testPathParams() {
		WebResource service = getRes();
		// Fluent interfaces
		String strParam = "strabc";
		WebResource path = service.path("getMethodWithPathParams").path(strParam).path("version123").path("entity314");
		System.out.println(path.getURI());
		String result = path.get(String.class);
		System.out.println(result);
	}
	
	@Test
	public void testQueryParams() {
		WebResource service = getRes();
		// Fluent interfaces
		WebResource path = service.path("getMethodWithQueryParams").queryParam("strParam1", "test").queryParam("numParam1", "123").queryParam("id", "314");
		System.out.println(path.getURI());
		String result = path.get(String.class);
		System.out.println(result);
	}
	
	@Test
	public void testHeaderParams() {
		WebResource service = getRes();
		// Fluent interfaces
		WebResource path = service.path("getMethodWithHeaderParams");
		System.out.println(path.getURI());
		String result = path.get(String.class);
		System.out.println(result);
	}

	private WebResource getRes() {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(getBaseURI());
		return service;
	}
	
	@Test
	public void testMatrixParams() {
		WebResource service = getRes();
		// Fluent interfaces
		WebResource path = service.path("getMethodWithMatrixParams").path(";strParam1=str1;numParam1=123;id=314");
		System.out.println(path.getURI());
		String result = path.get(String.class);
		System.out.println(result);
	}
	
	@Test
	public void testUriInfoQueryParameters() {
		String strInGet = getRes().path("getQueryParameterInGet").queryParam("test", "abc").get(String.class);
		String strInPost = getRes().path("getQueryParameterInPost").queryParam("test", "abc").post(String.class);
		String strInEntity = getRes().path("getFormParameterInPost").entity("test=abc", MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(String.class);
		Assert.assertEquals(strInPost, strInGet);
		Assert.assertEquals(strInEntity, strInGet);
	}
	
	@Path("webService")
	@Produces({ MediaType.APPLICATION_JSON })
	public static class WebService {
		
		@GET
		@Path("getQueryParameterInGet")
		public String getQueryParameterInGet(@Context UriInfo uri) {
			log.info(uri.toString());
			return String.valueOf(uri.getQueryParameters().get("test"));
		}
		
		@POST
		@Path("getQueryParameterInPost")
		public String getQueryParameterInPost(@Context UriInfo uri) {
			log.info(uri.toString());
			return String.valueOf(uri.getQueryParameters().get("test"));
		}
		
		@POST
		@Path("getFormParameterInPost")
		public String getFormParameterInPost(@Context HttpRequestContext req) {
			log.info(req.toString());
			return req.getFormParameters().getFirst("test");
		}
		
		public WebService() {
			log.info(MiscUtils.invocationInfo());
		}
		
		@GET
		@Path("getMethodWithMatrixParams")
		@Produces(MediaType.TEXT_PLAIN)
		public Response getMethodWithMatrixParams(
				@MatrixParam("strParam1") String strParam1, 
				@MatrixParam("numParam1") Integer numParam1,
				@MatrixParam("id") PathParamEntity pEn) {
			
			log.info(MiscUtils.invocationInfo() + ":\t" + Arrays.toString(new Object[] {strParam1, numParam1, pEn}));
			return Response.ok(strParam1).build();
		}
		
		@GET
		@Path("getMethodWithHeaderParams")
		@Produces(MediaType.TEXT_PLAIN)
		public Response getMethodWithHeaderParams(@HeaderParam("Accept") String acceptHeader, @HeaderParam("Content-Type") MediaType contentType) {
			log.info(MiscUtils.invocationInfo() + ":\t" + Arrays.toString(new Object[] {acceptHeader, contentType}));
			return Response.ok(acceptHeader).build();
		}
		
		@GET
		@Path("getMethodWithQueryParams")
		@Produces(MediaType.TEXT_PLAIN)
		public Response getMethodWithQueryParams(
				@QueryParam("strParam1") String strParam1, 
				@QueryParam("numParam1") Integer numParam1,
				@QueryParam("id") PathParamEntity pEn) {
			
			log.info(MiscUtils.invocationInfo() + ":\t" + Arrays.toString(new Object[] {strParam1, numParam1, pEn}));
			return Response.ok(strParam1).build();
		}

		
		@GET
		@Path("getMethodWithPathParams/str{strParam1}/version{numParam1}/entity{id}")
		@Produces(MediaType.TEXT_PLAIN)
		public Response getMethodWithPathParams(
				@PathParam("strParam1") String strParam1, 
				@PathParam("numParam1") Integer numParam1,
				@PathParam("id") PathParamEntity pEn) {
			
			log.info(MiscUtils.invocationInfo() + ":\t" + Arrays.toString(new Object[] {strParam1, numParam1, pEn}));
			return Response.ok(strParam1).build();
		}

		

		@GET
		@Path("getMd")
		// "webService/getMd"
		public Response getMethod() {
			log.info(MiscUtils.invocationInfo());
			return Response.ok("getMd").build();
		}
		

		// if there is not path cfg
		// it responses at class's path
		@GET
		@Produces(MediaType.TEXT_PLAIN)
		public Response getPlainTestRespWithoutPath() {
			return Response.ok("plain text", MediaType.TEXT_PLAIN).build();
		}

		// if there is two methods without path, their paths conflict leads to
		// exception
		// @GET
		// @Produces(MediaType.TEXT_PLAIN)
		// public Response getPlainTestRespWithoutPath2() {
		// return Response.ok("plain text2", MediaType.TEXT_PLAIN).build();
		// }

		@GET
		@Path("samePath")
		@Produces(MediaType.TEXT_PLAIN)
		public Response getPlainTestRespWithSamePath() {
			return Response.ok("plain text", MediaType.TEXT_PLAIN).build();
		}

		// if there is two methods with same path, their paths conflict leads to
		// exception, however, if MediaTypes are different, it still works fine according 
		// to request's accept type
		// @GET
		// @Path("samePath")
		// @Produces(MediaType.TEXT_PLAIN)
		// public Response getPlainTestRespWithSamePath1() {
		// return Response.ok("plain text1", MediaType.TEXT_PLAIN).build();
		// }

		@GET
		@Path("hello")
		@Produces(MediaType.TEXT_PLAIN)
		public String sayPlainTextHello() {
			return "Hello Jersey";
		}

		@GET
		@Path("hello")
		@Produces(MediaType.TEXT_XML)
		public String sayXMLHello() {
			return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey" + "</hello>";
		}

		// This method is called if HTML is request
		@GET
		@Path("hello")
		@Produces(MediaType.TEXT_HTML)
		public String sayHtmlHello() {
			return "<html> " + "<title>" + "Hello Jersey" + "</title>" + "<body><h1>" + "Hello Jersey" + "</body></h1>" + "</html> ";
		}

		
	}

	HttpClient hc = new HttpClient();

	private HttpMethod get(String uri, Map<String, String> params) throws Exception {
		if (StringUtils.isBlank(uri)) {
			return null;
		}
		HttpMethod hm = new GetMethod(uri);
		if (!MapUtils.isEmpty(params)) {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>(params.size());
			for (Map.Entry<String, String> en : params.entrySet()) {
				nvps.add(new NameValuePair(en.getKey(), en.getValue()));
			}
			hm.setQueryString(nvps.toArray(new NameValuePair[0]));
		}
		hc.executeMethod(hm);
		return hm;
	}

	private HttpMethod post(String uri, Map<String, String> params) throws Exception {
		if (StringUtils.isBlank(uri)) {
			return null;
		}
		HttpMethod hm = new PostMethod(uri);
		if (!MapUtils.isEmpty(params)) {
			HttpMethodParams hmps = new HttpMethodParams();
			for (Map.Entry<String, String> en : params.entrySet()) {
				hmps.setParameter(en.getKey(), en.getValue());
			}
			hm.setParams(hmps);
		}
		hc.executeMethod(hm);
		return hm;
	}

	@BeforeClass
	public static void setUp() throws Exception {
		try {
			final Server server = new Server(8080);

			ServletContextHandler root = new ServletContextHandler(server, "/cf");

			ResourceConfig resCfg = new DefaultResourceConfig(WebService.class);
			ServletContainer servletContainer = new ServletContainer(resCfg);

			ServletHolder servletHolder = new ServletHolder(servletContainer);
			root.addServlet(servletHolder, "/study/*");
			root.addServlet(new ServletHolder(new HttpServlet() {
				private static final long serialVersionUID = 1L;

				protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
					resp.getWriter().write("Hello World");
					resp.flushBuffer();
				}
			}), "/helloworld/*");
			server.start();
			new Thread(
					new Runnable() {
						public void run() {
							try {
								server.join();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testServer() throws Exception {
		HttpMethod hm = get("http://localhost:8080/cf/helloworld/", null);
		Assert.assertNotNull(hm);
		Assert.assertEquals(hm.getStatusCode(), HttpStatus.SC_OK);
		Assert.assertEquals("Hello World", hm.getResponseBodyAsString());
	}

	@Test
	public void testClient() throws Exception {
		WebResource service = getRes();
		// Fluent interfaces
		System.out.println(service.path("hello").accept(MediaType.TEXT_PLAIN).get(ClientResponse.class).toString());
		// Get plain text
		System.out.println(service.path("hello").accept(MediaType.TEXT_PLAIN).get(String.class));
		// Get XML
		System.out.println(service.path("hello").accept(MediaType.TEXT_XML).get(String.class));
		// The HTML
		System.out.println(service.path("hello").accept(MediaType.TEXT_HTML).get(String.class));
	}

	private String getBaseURI() {
		return "http://localhost:8080/cf/study/webService";
	}
}
