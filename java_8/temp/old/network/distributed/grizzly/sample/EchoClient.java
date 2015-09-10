package cf.study.network.distributed.grizzly.sample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.utils.StringFilter;

public class EchoClient {
	 private static final Logger logger = Grizzly.logger(EchoClient.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Connection connection = null;
		
		//Create a filter chain using a FilterChainBuilder
		FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
		
		//Add TransportFilter, which is responsible for 
		//reading and writing data to the connection
		filterChainBuilder.add(new TransportFilter());
		
		//StringFilter is responsible for buffer<->String conversion
		filterChainBuilder.add(new StringFilter(Charset.forName("UTF-8")));
		
		//clientFilter is responsible for redirecting server response to the standard output
		filterChainBuilder.add(new ClientFilter());
		
		//create TCP Transport
		final TCPNIOTransport transport = TCPNIOTransportBuilder.newInstance().build();
		
		transport.setProcessor(filterChainBuilder.build());
		
		try {
			//Start the transport
			transport.start();
			
			//perform asynch. connect to the Server
			Future<Connection> future = transport.connect(EchoServer.HOST, EchoServer.PORT);
			
			//wait for connection operation to finish 
			connection = future.get(10, TimeUnit.SECONDS);
			
			assert connection != null;
			
			System.out.println("Ready... (\"q\" to exit)");
			final BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
			
			do {
				final String userInput = inReader.readLine();
				if (userInput == null || "q".equals(userInput)) {
					break;
				}
				connection.write(userInput);
			} while (true);
			
		} finally {
			if (connection != null) {
				connection.close();
			}
			//stop the transport
			transport.shutdownNow();
		}
	}

}
