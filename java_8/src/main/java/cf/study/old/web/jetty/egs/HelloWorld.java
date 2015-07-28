package cf.study.web.jetty.egs;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

public class HelloWorld extends AbstractHandler {

	static void cfgServerWithManyConnectors(Server srv) {
		SelectChannelConnector conn0 = new SelectChannelConnector();
		conn0.setPort(8080);
		conn0.setMaxIdleTime(30000);
		conn0.setRequestBufferSize(8192);
		
		SelectChannelConnector conn1 = new SelectChannelConnector();
		conn1.setHost("127.0.0.1");
		conn1.setPort(8081);
		conn1.setThreadPool(new QueuedThreadPool(20));
		conn1.setName("admin");
		
		SslSelectChannelConnector sslConn = new SslSelectChannelConnector();
		sslConn.setPort(8443);
//		SslContextFactory cf = sslConn.getSslContextFactory();
//		cf.setKeyStore("./jetty/keystore");
//		cf.setKeyStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
//        cf.setKeyManagerPassword("OBF:1u2u1wml1z7s1z7a1wnl1u2g");
        
        srv.setConnectors(new Connector[] {conn0, conn1});
	}
	
	public static void main(String[] args) throws Exception {
		Server srv = new Server();
		
		cfgServerWithManyConnectors(srv);
		
		cfgServerWithCtx(srv);
		
		//srv.setHandler(new HelloWorld());
		
		srv.start();
		srv.join();
	}

	private static void cfgServerWithCtx(Server srv) {
		final ContextHandler ctx = new ContextHandler();
		ctx.setContextPath("/ctx1");
		ctx.setResourceBase(".");
		ctx.setClassLoader(Thread.currentThread().getContextClassLoader());
		
		ctx.setHandler(new AbstractHandler() {
			@Override
			public void handle(String s, Request req, HttpServletRequest hReq, HttpServletResponse hResp) throws IOException, ServletException {
				PrintWriter w = hResp.getWriter();
				w.println("<h1>Context</h1>	");
				w.printf("target: %s <br/>", s);
				
				w.printf("<pre>");
				w.println(ToStringBuilder.reflectionToString(ctx, ToStringStyle.MULTI_LINE_STYLE));
				w.printf("</pre>");
				
				w.printf("<pre>");
				w.println(ToStringBuilder.reflectionToString(req, ToStringStyle.MULTI_LINE_STYLE));
				w.printf("</pre>");
				
				w.printf("<pre>");
				w.println(ToStringBuilder.reflectionToString(hReq, ToStringStyle.MULTI_LINE_STYLE));
				w.printf("</pre>");
				
				w.flush();
			}
		});
		
		srv.setHandler(ctx);
	}

	@Override
	public void handle(String target, Request req, HttpServletRequest hReq, HttpServletResponse hResp) throws IOException, ServletException {
		hResp.setContentType("text/html;charset=utf-8");
		hResp.setStatus(HttpServletResponse.SC_OK);
		
		req.setHandled(true);
		
		PrintWriter w = hResp.getWriter();
		w.println("<h1>Hello World</h1>	");
		w.printf("target: %s <br/>", target);
		
		w.printf("<pre>");
		w.println(ToStringBuilder.reflectionToString(req, ToStringStyle.MULTI_LINE_STYLE));
		w.printf("</pre>");
		
		w.printf("<pre>");
		w.println(ToStringBuilder.reflectionToString(hReq, ToStringStyle.MULTI_LINE_STYLE));
		w.printf("</pre>");
	}

}
