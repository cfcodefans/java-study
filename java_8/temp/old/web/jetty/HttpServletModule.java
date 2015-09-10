package cf.study.web.jetty;

import java.util.Arrays;
import java.util.HashSet;

import cf.study.web.service.TestResource;

import com.sun.jersey.guice.JerseyServletModule;

public class HttpServletModule extends JerseyServletModule {
	// PREDEFINED PROPERTIES
	  public static final String APPLICATION_PATH_REST="rest";

	protected void configureServlets() {
		super.configureServlets();
		
		bind(JaxbContextResolver.class).toInstance(
				new JaxbContextResolver(new HashSet<Class<?>>(Arrays
						.asList(TestResource.class))));
		
		serve("/" + APPLICATION_PATH_REST + "/*").with(CustomGuiceContainer.class);
	}
}
