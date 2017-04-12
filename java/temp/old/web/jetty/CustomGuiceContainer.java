package cf.study.web.jetty;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.ws.rs.core.MediaType;

import com.google.inject.Injector;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;

/**
 * CustomGuiceContainer
 * 
 * @author carsten@thenetcircle.com
 */
@Singleton
public class CustomGuiceContainer extends GuiceContainer {
	// PREDEFINED PROPERTIES
	private static final long serialVersionUID = 1L;

	// CONSTRUCTION
	@Inject
	CustomGuiceContainer(final Injector injector) {
		super(injector);
	}

	// OVERRIDES/IMPLEMENTS
	@Override
	protected ResourceConfig getDefaultResourceConfig(
			final Map<String, Object> properties, final WebConfig webConfig)
			throws ServletException {
		final ResourceConfig resourceConfig = super.getDefaultResourceConfig(
				properties, webConfig);

		resourceConfig.getMediaTypeMappings().put(
				MediaType.APPLICATION_JSON_TYPE.getSubtype(),
				MediaType.APPLICATION_JSON_TYPE);

		resourceConfig.getMediaTypeMappings().put(
				MediaType.TEXT_XML_TYPE.getSubtype(), MediaType.TEXT_XML_TYPE);

		return resourceConfig;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		super.doFilter(null, null, chain);
	}

}
