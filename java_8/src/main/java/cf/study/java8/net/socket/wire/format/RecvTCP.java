package cf.study.java8.net.socket.wire.format;

import java.net.ServerSocket;
import java.net.Socket;

public class RecvTCP {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			throw new IllegalArgumentException("Parameter(s): <Port>");
		}
		
		int port = Integer.parseInt(args[0]);
		
		ServerSocket servSock = new ServerSocket(port);
		Socket clntSock = servSock.accept();
		
		//Receive text-encoded quote
		ItemQuoteDecoder decoder = new ItemQuoteDecoderText();
		ItemQuote quote = decoder.decode(clntSock.getInputStream());
		System.out.println("Received Text-Encoder Quote:");
		System.out.println(quote);
		
		//Repeat quote with binary encoding, adding 10 cents to the price
		ItemQuoteEncoder encoder = new ItemQuoteEncoderBin();
		quote.unitPrice += 10;
		System.out.println(); //Add 10 cents to unit price
		System.out.println("Sending (binary)...");
		clntSock.getOutputStream().write(encoder.encode(quote));
		
		clntSock.close();
		servSock.close();
	}

}
