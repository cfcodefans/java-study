package cf.study.jee.web.jetty.html5;

import javax.servlet.http.HttpServlet;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.junit.Test;

import cf.study.jee.web.jetty.html5.websocket.WebSocketTests.TextEchoServlet;
import cf.study.jee.web.jetty.res.WebResources;

public class Html5Tests {

	private static final Logger log = Logger.getLogger(Html5Tests.class);
	private Server setUpServlet(Class<? extends HttpServlet> servletClass) throws Exception {
		if (servletClass == null) return null;
		
		Server server = new Server(8080);
		
		HandlerList hls = new HandlerList();
		hls.setHandlers(new Handler[] {
			WebResources.res("/html5", Html5Tests.class, "."),
			WebResources.defaultRes(),
		});
		
		server.setHandler(hls);
		
		return server;
	}

	@Test
	public void startServer() throws Exception {
		Server server = setUpServlet(TextEchoServlet.class);
		Assert.assertNotNull(server);
		Assert.assertFalse(server.isStarted());
		
		server.start();
		server.join();
	}
}
