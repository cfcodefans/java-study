package cf.study.web.jetty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;

public class JettyTest {

	static final int DEFAULT_ACCEPTORS = 2 * Runtime.getRuntime().availableProcessors();
	// PREDEFINED PROPERTIES
	static final Logger LOG = LoggerFactory.getLogger(JettyTest.class);
	private static Server server;

	private static void initServer() {
		server = new Server();

		SelectChannelConnector selectChannelConnector = new SelectChannelConnector();
		selectChannelConnector.setHost("localhost");
		selectChannelConnector.setPort(8081);
		selectChannelConnector.setAcceptors(DEFAULT_ACCEPTORS);

		server.addConnector(selectChannelConnector);
	}

	private static void initServletContainer_() {
		final ContextHandlerCollection ctxHandlerCol = new ContextHandlerCollection();
		String ctxPath = "test";
		
        
        ServletContextHandler sch = new ServletContextHandler(ctxHandlerCol, ctxPath) {
        	
        };
        
		try {
			sch.setBaseResource(Resource.newResource(ctxPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		sch.addServlet(DefaultServlet.class, "/");
		sch.addFilter(GuiceFilter.class, "*", EnumSet.of(DispatcherType.REQUEST));
		
		sch.addEventListener(new GuiceServletContextListener() {
			@Override
			protected Injector getInjector() {
				return Guice.createInjector(getServiceModules());
			}
		});
		
		
		server.setHandler(ctxHandlerCol);
	}

	private static void stopServer() {
		try {
			server.stop();
		} catch (final Exception e) {
			LOG.debug("Failed to start servlet container.", e);
		}
	}

	@AfterClass
	public static void destroy() {
		stopServer();
	}

	@BeforeClass
	public static void init() {
		initServer();// set jetty up
		initServletContainer_();
		startServer();
	}

	private static void initServletContainer() {
		ResourceHandler resource_handler = new ResourceHandler();
//        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });

        resource_handler.setResourceBase(".");
        
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
        server.setHandler(handlers);		
	}

	public static void startServer() {
		try {
			server.start();
			server.join();
		} catch (final Exception e) {
			LOG.debug("Failed to start servlet container.", e);
		}
	}

	
	private static Module[] getServiceModules() {
		List<Module> modules = new ArrayList<Module>();
		
		modules.add(new HttpServletModule() {
			protected void configureServlets() {
				super.configureServlets();
//				bind(TestResource.class);
			}
		});
		
		return modules.toArray(new Module[0]);
	}
	
	@Test
	public void testServer() {

	}
}
