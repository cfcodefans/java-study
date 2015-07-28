package cf.study.network.distributed.grizzly.sample;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.utils.StringFilter;

public class EchoServer {
	private static final Logger logger = Logger.getLogger(EchoServer.class.getSimpleName());

	public static final String HOST = "localhost";
	public static final int PORT = 7777;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		// create a FilterChain using FilterChainBuilder
		FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();

		// Add TransportFilter, which is responsible
		// for reading and writing data to the connection
		filterChainBuilder.add(new TransportFilter());

		// String filter is responsible for buffer <-> String conversion
		filterChainBuilder.add(new StringFilter(Charset.forName("UTF-8")));

		// EchoFilter is responsible for echoing received messages
		filterChainBuilder.add(new EchoFilter());

		// Create TCP transport
		final TCPNIOTransport transport = TCPNIOTransportBuilder.newInstance().build();

		transport.setProcessor(filterChainBuilder.build());

		try {
			// binding transport to start listen on certain host and port
			transport.bind(HOST, PORT);

			// start the transport
			transport.start();

			logger.info("Press any key to stop the server....");
			System.in.read();
		} finally {
			logger.info("Stopping transport...");
			transport.shutdownNow();
			logger.info("Stopped transport...");
		}
	}

}
