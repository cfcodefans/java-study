package cf.study.jee.web.jetty.html5.websocket;

import java.net.URI;
import java.util.concurrent.Future;

import misc.MiscUtils;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.junit.Test;

public class Examples {
	public static class WebSocketCreatorExample implements WebSocketCreator {
		 
//	    private MyBinaryEchoSocket binaryEcho;
//	 
//	    private MyEchoSocket textEcho;
	 
	    public WebSocketCreatorExample() {
//	        this.binaryEcho = new MyBinaryEchoSocket();
//	        this.textEcho = new MyEchoSocket();
	    }
	    
	    EventSocket es = new EventSocket();
	 
	    @Override
	    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
	    	System.out.println(req);
	        for (String subprotocol : req.getSubProtocols()) {
	            if ("binary".equals(subprotocol)) {
	                resp.setAcceptedSubProtocol(subprotocol);
//	                return binaryEcho;
	                return es;
	            }
	            if ("text".equals(subprotocol)) {
	                resp.setAcceptedSubProtocol(subprotocol);
//	                return textEcho;
	                return es;
	            }
	        }
	        return es;
	    }
	}
	
	public static class EventSocket extends WebSocketAdapter {
		@Override
		public void onWebSocketConnect(Session sess) {
			super.onWebSocketConnect(sess);
			System.out.println("Socket Connected: " + sess);
		}

		@Override
		public void onWebSocketText(String message) {
			super.onWebSocketText(message);
			System.out.println("Received TEXT message: " + message);
		}

		@Override
		public void onWebSocketClose(int statusCode, String reason) {
			super.onWebSocketClose(statusCode, reason);
			System.out.println("Socket Closed: [" + statusCode + "] " + reason);
		}

		@Override
		public void onWebSocketError(Throwable cause) {
			super.onWebSocketError(cause);
			cause.printStackTrace(System.err);
		}
		
	    public void onWebSocketBinary(byte[] payload, int offset, int len) {
	    	System.out.println("Received Binary message: " + new String(payload, offset, len));
	    }
	}

	public static class SampleWebSocketServlet extends WebSocketServlet {
		private static final long serialVersionUID = 1L;
		public SampleWebSocketServlet() {
			super();
			System.out.println(MiscUtils.invocationInfo());
		}
		@Override
		public void configure(WebSocketServletFactory factory) {
			factory.register(EventSocket.class);
			factory.setCreator(new WebSocketCreatorExample());
		}
	}

	
	public static void main(String ...args) {
		Server server = new Server();
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(8080);
		server.addConnector(connector);
		
		// Setup the basic application "context" for this application at "/"
		// This is also known as the handler tree (in jetty speak)
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
//		ClassLoader cl = Thread.currentThread().getContextClassLoader();
//		context.setClassLoader(cl);
		server.setHandler(context);

		// Add a websocket to a specific path spec
		ServletHolder holderEvents = new ServletHolder("ws-events", SampleWebSocketServlet.class);
		context.addServlet(holderEvents, "/websocket/server/*");

		try {
			server.start();
			server.dump(System.out);
			server.join();
		} catch (Throwable t) {
			t.printStackTrace(System.out);
		}
	}

	@Test
	public void testClient() {
//		main();
		
		URI uri = URI.create("ws://localhost:8080/events/");

		WebSocketClient client = new WebSocketClient();
		try {
			try {
				client.start();
				// The socket that receives events
				EventSocket socket = new EventSocket();
				// Attempt Connect
				Future<Session> fut = client.connect(socket, uri);
				// Wait for Connect
				Session session = fut.get();
				// Send a message
				session.getRemote().sendString("Hello");
				// Close session
				session.close();
			} finally {
				client.stop();
			}
		} catch (Throwable t) {
			t.printStackTrace(System.err);
		}
	}
}
