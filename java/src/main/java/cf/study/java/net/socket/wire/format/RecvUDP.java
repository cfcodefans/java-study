package cf.study.java.net.socket.wire.format;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class RecvUDP implements ItemQuoteTextConst {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 1 && args.length != 2) {
			throw new IllegalArgumentException("Parameter(s): <port> [<encoding>]");
		}
		
		int port = Integer.parseInt(args[0]);
		
		DatagramSocket sock = new DatagramSocket(port);
		ItemQuoteDecoder decoder = (args.length == 2 ? new ItemQuoteDecoderText(args[1]) : new ItemQuoteDecoderText());
		
		DatagramPacket packet = new DatagramPacket(new byte[MAX_WIRE_LENGTH], MAX_WIRE_LENGTH);
		sock.receive(packet);
		
		ItemQuote quote = decoder.decode(packet);
		System.out.println(quote);
		
		sock.close();
	}

}
