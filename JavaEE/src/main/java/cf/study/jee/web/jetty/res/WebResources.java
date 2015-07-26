package cf.study.jee.web.jetty.res;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class WebResources {

	private static final Logger log = Logger.getLogger(WebResources.class);
	
	public static ContextHandler defaultRes() throws Exception {
		Path resBasePath = Paths.get(WebResources.class.getResource(".").toURI()).toAbsolutePath().normalize();
		return res("/res", resBasePath.toString());
	}
	
	public static ContextHandler res(String ctxName, Class<?> cls, String path)  throws Exception {
		Path resBasePath = Paths.get(cls.getResource(".").toURI()).resolve(path).toAbsolutePath().normalize();
		return res(ctxName, resBasePath.toString());
	}
	
	public static ContextHandler res(String ctxName, String basePathStr) {
		basePathStr = basePathStr.replace("target\\test-classes", "src\\main\\java");
		System.out.println(basePathStr);
		
		return serveByDefaultServlet(ctxName, basePathStr);
	}

	private static ContextHandler _res(String ctxName, String basePathStr) {
		ResourceHandler resHandler = new ResourceHandler();
		resHandler.setDirectoriesListed(true);
		resHandler.setResourceBase(basePathStr);
		
		ContextHandler ctxHandler = new ContextHandler(ctxName);
		ctxHandler.setHandler(resHandler);
		return ctxHandler;
	}
	
	private static ContextHandler serveByDefaultServlet(String ctxName, String basePathStr) {
		ServletContextHandler sch = new ServletContextHandler();
		sch.setContextPath(ctxName);
		
		ServletHolder sh = new ServletHolder(DefaultServlet.class);
		sh.setInitParameter("useFileMappedBuffer", Boolean.FALSE.toString());
		sh.setInitParameter("resourceBase", basePathStr);
		sch.addServlet(sh, "/*");
		
		log.info(String.format("map %s \tto %s", ctxName, basePathStr));
		
		return sch;
	}
}
