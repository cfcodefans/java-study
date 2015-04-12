package cf.study.jee.web.jetty.res;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class WebResources {

	public static ContextHandler defaultRes() throws Exception {
		Path resBasePath = Paths.get(WebResources.class.getResource(".").toURI()).toAbsolutePath().normalize();
		return res("/res", resBasePath.toString());
	}
	
	public static ContextHandler res(String ctxName, String basePathStr) {
		ResourceHandler resHandler = new ResourceHandler();
		resHandler.setDirectoriesListed(true);
		resHandler.setResourceBase(basePathStr);
		
		ContextHandler ctxHandler = new ContextHandler(ctxName);
		ctxHandler.setHandler(resHandler);
		return ctxHandler;
	}
}
