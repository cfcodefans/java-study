package cf.study.jee.web.jetty.tutorial;

import com.google.common.net.MediaType;
import misc.MiscUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.rewrite.handler.RedirectPatternRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JettyTests {

	private static final Logger log = LoggerFactory.getLogger(JettyTests.class);

	@Test
	public void creatServer() throws Exception {
		Server server = new Server(8080);
		server.start();
		server.dumpStdErr();
		
		Executors.newScheduledThreadPool(1).schedule((Runnable)this::testServer, 1, TimeUnit.SECONDS);
		MiscUtils.easySleep(4000);
	}
	
	public void testServer() {
		testAtUrl("http://localhost:8080/");
	}
	
	public void testServer(String pathStr) {
		testAtUrl("http://localhost:8080/" + pathStr);
	}

	private void testAtUrl(String url) {
		HttpResponse httpResponse = MiscUtils.easyGet(url);
		if (httpResponse == null) {
			log.info("response is null");
			return;
		}
		log.info(httpResponse.toString());
		try {
			log.info(EntityUtils.toString(httpResponse.getEntity()));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static class HelloHandler extends AbstractHandler {
		final String respStr;
		
		public HelloHandler(String respStr) {
			super();
			this.respStr = respStr;
		}

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
			log.info("target: \t" + target);
			log.info("baseRequest: \t" + baseRequest);
			log.info("request: \t" + request);
			log.info("response: \t" + response);
			
			response.setContentType(MediaType.HTML_UTF_8.toString());
			response.setStatus(HttpServletResponse.SC_OK);
			
			response.getWriter().println(respStr);
			
			baseRequest.setHandled(true);
		}
	}
	
	public static class LogHandler extends AbstractHandler {
		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
			log.info("target: \t" + target);
			log.info("baseRequest: \t" + baseRequest);
			log.info("request: \t" + request);
			log.info("response: \t" + response);
		}
	}
	
	@Test
	public void helloHandler() throws Exception {
		Server server = new Server(8080);
		server.setHandler(new HelloHandler(MiscUtils.invocInfo()));
		server.start();
		
		Executors.newScheduledThreadPool(1).schedule((Runnable)this::testServer, 1, TimeUnit.SECONDS);
		MiscUtils.easySleep(4000);
	}
	
	@Test
	public void testResourceHandler() throws Exception {
		//create a basic Jetty server object that will listen on port 8080.
		//Note that if you set this to port 0 then a randomly available port will be 
		//assigned that you can either look in the logs for the port,
		//or programmatically obtain it for use in test cases.
		Server server = new Server(8080);
		
		//Create the resourceHandler. It is the object that will actually handle the request for a given file.
		//It is a jetty handler object so it is suitable for chaining with other handlers as you will see in other examples.
		ResourceHandler resHandler = new ResourceHandler();
		
		//Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
		//In this example it is the current directory but it can be configured to anything that the jvm has access to.
		resHandler.setDirectoriesListed(true);
		resHandler.setResourceBase(".");
		
		//Add the ResourceHandler to the server.
		HandlerList handlerList = new HandlerList();
		handlerList.setHandlers(new Handler[] {new LogHandler(), resHandler, new DefaultHandler(), new LogHandler()});
		server.setHandler(handlerList);
		
		//Start things up! By using the server.join() the server thread will join with the current thread.
		server.start();
		server.join();
	}
	
	@Test
	public void testConnector() throws Exception {
		//The server
		Server server = new Server();
		
		//HTTP connector
		ServerConnector http = new ServerConnector(server) {
			@Override
			protected void doStart() throws Exception {
				super.doStart();
				log.info(MiscUtils.invocationInfo());
			}
			
			@Override
			protected void doStop() throws Exception {
				super.doStop();
				log.info(MiscUtils.invocationInfo());
			}
		};
		http.setHost("localhost");
		http.setPort(8080);
		http.setIdleTimeout(30000);
		
		//Set the connector
		server.addConnector(http);
		
		server.start();
		Executors.newScheduledThreadPool(1).schedule((Runnable)this::testServer, 1, TimeUnit.SECONDS);
		Executors.newScheduledThreadPool(1).schedule((Runnable)this::testServer, 1, TimeUnit.SECONDS);
		
		MiscUtils.easySleep(4000);
	}
	
	public static class HelloServlet extends HttpServlet {
		private static final long serialVersionUID = 1L;

		public HelloServlet() {
			log.info(MiscUtils.invocationInfo());
		}
		
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//			super.doGet(req, resp);
			resp.setContentType(ContentType.TEXT_HTML.getMimeType());
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().println("<h1>Hello from <br>\n" + MiscUtils.invocationInfo() + "</h1>");
			log.info("context: \t" + this.getServletContext().getContextPath());
			log.info(req.getPathInfo());
			log.info(req.getPathTranslated());
			log.info(this.getServletContext().getRealPath("."));
			log.info(req.getRequestURI());
			log.info(req.getRequestURL().toString());
			log.info(this.getServletContext().getResource("/").toString());
		}
	}
	
	@Test
	public void minimalServlet() throws Exception {
		//create a basic jetty server object that will listen on port 8080
		//Note that if you set this to port 0 then a randomly available port
		//will be assigned that you can either look in the logs for the port,
		//or programmatically obtain it for use in test cases.
		Server server = new Server(8080);
		
		//The servletHandler is a dead simple way to create a context handler
		//That is backed by an instance of Servelt
		//This handler then needs to be registered with the Server Object.
		ServletHandler handler = new ServletHandler();
		server.setHandler(handler);
		
		//Passing in the class for the servlet allows jetty to instantiate an
		//instance of that Servlet and mount it on a given context path.
		handler.addServletWithMapping(HelloServlet.class, "/*");
		
		//start things up
		server.start();
		Executors.newScheduledThreadPool(1).schedule((Runnable)this::testServer, 1, TimeUnit.SECONDS);
		MiscUtils.easySleep(4000);
	}
	
	@Test
	public void oneContext() throws Exception {
		Server server = new Server(8080);
		//Add a single handler on context "/hello"
		ContextHandler context = new ContextHandler();
		context.setContextPath("/hello");
		context.setHandler(new HelloHandler("context"));
		
		server.setHandler(context);
		
		//Can be accessed using http://localhost:8080/hello
		server.start();
		Executors.newScheduledThreadPool(1).schedule((Runnable)this::testHelloContext, 1, TimeUnit.SECONDS);
		MiscUtils.easySleep(4000);
	}
	
	public void testHelloContext() {
		testAtUrl("http://localhost:8080/hello");
	}
	
	@Test
	public void multipleContexts() throws Exception {
		Server server = new Server(8080);
		
		ContextHandler root = new ContextHandler("/");
		root.setContextPath("/");
		root.setHandler(new HelloHandler("Root Hello"));
		
		ContextHandler ctxFr = new ContextHandler("/fr");
		ctxFr.setHandler(new HelloHandler("Bonjour"));
		
		ContextHandler ctxIT = new ContextHandler("it");
		ctxIT.setHandler(new HelloHandler("Bongiorno"));
		
		ContextHandler ctxV = new ContextHandler("/");
		ctxV.setVirtualHosts(new String[] {"127.0.0.2"});
		ctxV.setHandler(new HelloHandler("Virtual Hello"));
		
		ContextHandlerCollection ctxs = new ContextHandlerCollection();
		ctxs.setHandlers(new Handler[] {root, ctxFr, ctxIT, ctxV});
		
		server.setHandler(ctxs);
		
		server.start();
		ScheduledExecutorService threads = Executors.newScheduledThreadPool(4);
		
		threads.schedule((Runnable)()->testAtUrl("http://localhost:8080/"), 1, TimeUnit.SECONDS);
		threads.schedule((Runnable)()->testAtUrl("http://localhost:8080/fr"), 1, TimeUnit.SECONDS);
		threads.schedule((Runnable)()->testAtUrl("http://localhost:8080/it"), 1, TimeUnit.SECONDS);
		threads.schedule((Runnable)()->testAtUrl("http://127.0.0.2:8080/"), 1, TimeUnit.SECONDS);
		MiscUtils.easySleep(4000);
	}
	
	@Test
	public void oneServletContext() throws Exception {
		Server server = new Server(8080);
		
		ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
		ctx.setContextPath("/ctx");
		ctx.setResourceBase(SystemUtils.JAVA_IO_TMPDIR);
		
		server.setHandler(ctx);
		
		ctx.addServlet(HelloServlet.class, "/*");
		ctx.addServlet(HelloServlet.class, "/dump/");
		
		server.start();
		
		ScheduledExecutorService threads = Executors.newScheduledThreadPool(4);
		
		threads.schedule((Runnable)()->testAtUrl("http://localhost:8080/ctx"), 1, TimeUnit.SECONDS);
		threads.schedule((Runnable)()->testAtUrl("http://localhost:8080/ctx/dump/fr"), 1, TimeUnit.SECONDS);
		MiscUtils.easySleep(4000);
	}
	
	@Test
	public void testServletHolder() throws Exception {
		Server server = new Server(8080);

		ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
		ctx.setContextPath("/");
		ctx.setResourceBase(SystemUtils.JAVA_IO_TMPDIR);

		server.setHandler(ctx);

		ServletHolder sh = ctx.addServlet(HelloServlet.class, "/*");
		
		Servlet si0 = sh.getServletInstance();
		log.info(si0.toString());

		server.start();

		Servlet si1 = sh.getServletInstance();
		log.info(si1.toString());
		
		ScheduledExecutorService threads = Executors.newScheduledThreadPool(4);

		threads.schedule((Runnable) () -> testAtUrl("http://localhost:8080/"), 1, TimeUnit.SECONDS);
		
		Servlet si3 = sh.getServletInstance();
		log.info(si3.toString());
		
		Servlet si4 = sh.getServletInstance();
		log.info(si4.toString());
		
		MiscUtils.easySleep(4000);
	}
	
	
	
	public void war() throws Exception {
        // Create a basic jetty server object that will listen on port 8080.
        // Note that if you set this to port 0 then a randomly available port
        // will be assigned that you can either look in the logs for the port,
        // or programmatically obtain it for use in test cases.
        Server server = new Server(8080);
 
        // Setup JMX
        MBeanContainer mbContainer = new MBeanContainer(
                ManagementFactory.getPlatformMBeanServer());
        server.addBean(mbContainer);
 
        // The WebAppContext is the entity that controls the environment in
        // which a web application lives and breathes. In this example the
        // context path is being set to "/" so it is suitable for serving root
        // context requests and then we see it setting the location of the war.
        // A whole host of other configurations are available, ranging from
        // configuring to support annotation scanning in the webapp (through
        // PlusConfiguration) to choosing where the webapp will unpack itself.
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        File warFile = new File(
                "../../jetty-distribution/target/distribution/demo-base/webapps/test.war");
        webapp.setWar(warFile.getAbsolutePath());
 
        // A WebAppContext is a ContextHandler as well so it needs to be set to
        // the server so it is aware of where to send the appropriate requests.
        server.setHandler(webapp);
 
        // Configure a LoginService
        // Since this example is for our test webapp, we need to setup a
        // LoginService so this shows how to create a very simple hashmap based
        // one. The name of the LoginService needs to correspond to what is
        // configured in the webapp's web.xml and since it has a lifecycle of
        // its own we register it as a bean with the Jetty server object so it
        // can be started and stopped according to the lifecycle of the server
        // itself.
        HashLoginService loginService = new HashLoginService();
        loginService.setName("Test Realm");
        loginService.setConfig("src/test/resources/realm.properties");
        server.addBean(loginService);
 
        // Start things up!
        server.start();
 
        // The use of server.join() the will make the current thread join and
        // wait until the server is done executing.
        // See http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#join()
        server.join();
    }
	
	@Test
	public void testResHandler() throws Exception {
		Server server = new Server(8080);
		ResourceHandler resHandler = new ResourceHandler();
		resHandler.setDirectoriesListed(true);
		Path resBasePath = Paths.get(JettyTests.class.getResource(".").toURI()).resolve("../res") .toAbsolutePath().normalize();
		
		log.info(resBasePath.toString());
		
		resHandler.setResourceBase(resBasePath.toString());
		
		ContextHandler ctxHandler = new ContextHandler("/res");
		ctxHandler.setHandler(resHandler);
		
		HandlerList hls = new HandlerList();
		hls.setHandlers(new Handler[] {ctxHandler});
		server.setHandler(hls);
		
		server.start();
		server.join();
	}
	
	@Test
	public void testRedirectToDifferentPath() throws Exception {
		Server server = new Server(8080);
		
		RewriteHandler rewrite = new RewriteHandler();
		rewrite.setRewritePathInfo(false);
		rewrite.setRewriteRequestURI(false);
		rewrite.setOriginalPathAttribute("requestedPath");
		
		RedirectPatternRule redirect = new RedirectPatternRule();
		redirect.setPattern("/redirect/*");
		redirect.setLocation("/true");
		rewrite.addRule(redirect);
		
		ContextHandler ctxHandler = new ContextHandler("/true");
		ctxHandler.setHandler(new HelloHandler(Boolean.TRUE.toString()));
		
		HandlerList hls = new HandlerList();
		hls.addHandler(ctxHandler);
		hls.addHandler(rewrite);
		
		server.setHandler(hls);
		
		server.start();
		server.join();
//		Executors.newScheduledThreadPool(1).schedule(()->this.testServer("redirect/what"), 1, TimeUnit.SECONDS);
//		MiscUtils.easySleep(4000);
	}
	
	@Test
	public void testRedirectToDifferentServer() throws Exception {
		Server rewriteSrv = new Server(8080);
		
		RewriteHandler rewrite = new RewriteHandler();
		rewrite.setRewritePathInfo(true);
		rewrite.setRewriteRequestURI(true);
		rewrite.setOriginalPathAttribute("requestedPath");
		
		RedirectPatternRule redirect = new RedirectPatternRule();
		redirect.setPattern("/redirect/*");
		redirect.setLocation("http://localhost:8081/true/");
		rewrite.addRule(redirect);
		
		
//		HandlerList hls = new HandlerList();
//		hls.addHandler(ctxHandler);
//		hls.addHandler(rewrite);
		
		rewriteSrv.setHandler(rewrite);
		rewriteSrv.start();
		
		Server server = new Server(8081);
		ContextHandler ctxHandler = new ContextHandler("/true");
		ctxHandler.setHandler(new HelloHandler(Boolean.TRUE.toString()));
		server.setHandler(ctxHandler);
		server.start();
		
//		server.join();
		Executors.newScheduledThreadPool(1).schedule((Runnable)()->this.testServer("redirect/what"), 1, TimeUnit.SECONDS);
		MiscUtils.easySleep(4000);
	}
	
	@Test
	public void testProxySerlvet() {
		Server proxySrv = new Server(8080);
		
		ServletContextHandler sch = new ServletContextHandler();
		sch.setContextPath("/proxy");
		sch.addServlet(ProxyServlet.class, "/*");
		
		proxySrv.setHandler(proxySrv);
	}


}
