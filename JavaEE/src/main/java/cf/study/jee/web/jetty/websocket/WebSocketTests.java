package cf.study.jee.web.jetty.websocket;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;
import misc.MiscUtils;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.junit.Test;

public class WebSocketTests {

	private static final Logger log = Logger.getLogger(WebSocketTests.class);

	private Server setUpServlet(Class<? extends HttpServlet> servletClass) {
		if (servletClass == null) return null;
		
		Server server = new Server(8080);
		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(servletClass, "/events/*");
		server.setHandler(handler);
		
		return server;
	}
	
	
	@WebSocket
	public static class AnnotatedTextEchoSocket {
		@OnWebSocketConnect
		public void onConnect(Session session) {
			System.out.println(MiscUtils.stackInfo());
			System.out.println(String.format("session: \t%s", session));
		}
		
		@OnWebSocketClose
		public void onClose(Session session, int statusCode, String reason) {
			System.out.println(MiscUtils.stackInfo());
			System.out.println(String.format("session: \t%s\nstatusCode: \t%d\nreason: \t%s\n", session, statusCode, reason));
		}
		
		@OnWebSocketMessage
		public void onMessage(Session session, String text) {
			System.out.println(MiscUtils.stackInfo());
			System.out.println(String.format("session: \t%s\ntext: \t%s\n", session, text));
		}
		
		@OnWebSocketError
		public void onError(Session session, Throwable throwable) {
			System.out.println(MiscUtils.stackInfo());
			System.out.println(String.format("session: \t%s\nerror: \t%s\n", session, throwable));
		}
	}
	
	public static class TextEchoServlet extends WebSocketServlet {
		@Override
		public void configure(WebSocketServletFactory factory) {
			factory.getPolicy().setIdleTimeout(100000);
			factory.register(AnnotatedTextEchoSocket.class);
		}
		
		@Override
		protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			log.info(MiscUtils.invocationInfo());
			log.info(request);
			super.service(request, response);
		}
	}
	
	@Test
	public void startServer() throws Exception {
		Server server = setUpServlet(TextEchoServlet.class);
		Assert.assertNotNull(server);
		Assert.assertFalse(server.isStarted());
		
		server.start();
		server.join();
	}
	
	@Test
	public void testWebSocketHandler() throws Exception {
		Server server = new Server(8080);
		
		
		
		
		server.start();
		server.join();
	}
}