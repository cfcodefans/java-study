package cf.study.web.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(/*test/rest*/"test")
@Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
public class TestResource {

	// PREDEFINED PROPERTIES
	static final Logger LOG = LoggerFactory.getLogger(TestResource.class);

	public TestResource() {
		LOG.info("TestResource created");
	}
	
	@GET
	@Path(/*user/rest/test*/"echo")
	public Response echo(@QueryParam("arg") String param) {
		LOG.info("TestResource.echo: " + param);
		return Response.ok().entity(param).build();
	}
}
