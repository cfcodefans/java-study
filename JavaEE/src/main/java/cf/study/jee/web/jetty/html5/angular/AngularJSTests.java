package cf.study.jee.web.jetty.html5.angular;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cf.study.jee.web.jetty.res.WebResources;
import cf.study.jee.web.ws.restful.jersey.JerseyTests;

public class AngularJSTests {
	private static final Logger log = LoggerFactory.getLogger(AngularJSTests.class);
	
	@Test
	public void runAngularJS() throws Exception {
		Server server = new Server(8080);
		
		HandlerList hls = new HandlerList();
		hls.setHandlers(new Handler[] {
			WebResources.defaultRes(),
			WebResources.res("/angular", AngularJSTests.class, "."),
			JerseyTests.setUpApp("/jersey", "/rest/*", JerseyTests.TestApp.class),	
		});
		
		server.setHandler(hls);
		
		server.start();
		
		log.info("started");
		
		server.join();
	}
}
