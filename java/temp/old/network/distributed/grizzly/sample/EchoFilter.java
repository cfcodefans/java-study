package cf.study.network.distributed.grizzly.sample;

import java.io.IOException;

import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

public class EchoFilter extends BaseFilter {
	public NextAction handleRead(FilterChainContext fcc) throws IOException {
		final Object peerAddress = fcc.getAddress();
		
		final Object message = fcc.getMessage();
		
		fcc.write(peerAddress, message, null);
		
		return fcc.getStopAction();
	}
}
