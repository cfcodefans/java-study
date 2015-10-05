package cf.study.web.service.egs;

/*
 * For 0.8 and later the "com.sun.ws.rest" namespace has been renamed to
 * "com.sun.jersey". For 0.7 or early use the commented out code instead
 */
// import com.sun.ws.rest.api.client.Client;
// import com.sun.ws.rest.api.client.WebResource;
// import com.sun.ws.rest.spi.container.servlet.ServletContainer;
import java.util.Scanner;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class Main {

	@Path("/")
	public static class TestResource {
		@GET
		public String get() {
			return "HelloWorld";
		}
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) throws Exception {
		ServletHolder sh = new ServletHolder(ServletContainer.class);

		/*
		 * For 0.8 and later the "com.sun.ws.rest" namespace has been renamed to
		 * "com.sun.jersey". For 0.7 or early use the commented out code instead
		 */
		// sh.setInitParameter("com.sun.ws.rest.config.property.resourceConfigClass",
		// "com.sun.ws.rest.api.core.PackagesResourceConfig");
		// sh.setInitParameter("com.sun.ws.rest.config.property.packages",
		// "jetty");
		sh.setInitParameter(
				"com.sun.jersey.config.property.resourceConfigClass",
				"com.sun.jersey.api.core.PackagesResourceConfig");
		sh.setInitParameter("com.sun.jersey.config.property.packages", "jetty");

		Server server = new Server(9999);
		/*
		 * Context context = new Context(server, "/", Context.SESSIONS);
		 * context.addServlet(sh, "/*");
		 */

		ServletContextHandler apiHandler = new ServletContextHandler();
		apiHandler.setContextPath("/");
		apiHandler.addServlet(sh, "/*");
		server.setHandler(apiHandler);

		server.start();

		Scanner scanner = new Scanner(System.in);
		int x = scanner.nextInt();
		System.out.println(x);

		Client c = Client.create();
		WebResource r = c.resource("http://localhost:9999/");
		System.out.println(r.get(String.class));

		server.stop();
	}
}