package cf.study.network.distributed.grizzly.sample;

import java.io.IOException;

import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;


/**
 * client filter is responsible for redirecting server response to the standard output
 * @author fan
 *
 */
public class ClientFilter extends BaseFilter {
	@Override
	public NextAction handleRead(final FilterChainContext ctx) throws IOException {
		// We get String message from the context, because we rely prev. Filter in chain is StringFilter
		final String serverResponse = ctx.getMessage();
		
		System.out.println("Server echo: " + serverResponse);
		
		return ctx.getStopAction();
	}
}
