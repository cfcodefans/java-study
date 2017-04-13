package cf.study.java.net.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPEchoServer {
	private static final int ECHO_MAX = 255;	//maximum size of echo datagram
	
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {	//test for correct argument list
			throw new IllegalArgumentException("Parameter(s): <Port>");
		}
		
		int servPort = Integer.parseInt(args[0]);
		
		DatagramSocket socket = new DatagramSocket(servPort);
		DatagramPacket packet = new DatagramPacket(new byte[ECHO_MAX], ECHO_MAX);
		
		try {
			for (;;) {	//run forever, receiving and echoing datagrams
				socket.receive(packet);
				System.out.println("Handling client at " + packet.getAddress().getHostAddress() + " on port " + packet.getPort());
				socket.send(packet);	//send the same packet back to client
				packet.setLength(ECHO_MAX);	//Reset length to avoid shrinking buffer
			}
		} catch (Exception e) {
			e.printStackTrace();
			socket.close();
		}
	}

}
