package cf.study.network.socket;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPEchoClientTimeout {
	private static final int TIME_OUT = 3000;	//Resend timeout (milliseconds)
	private static final int MAXTRIES = 5;		//Maximum retransmissions

	public static void main(String[] args) throws IOException {
		//Test for correct # of args
		if ((args.length < 2) || (args.length > 3)) {
			throw new IllegalArgumentException("Parameters(s): <Server> <Word> [<Port>]");
		}
		
		InetAddress serverAddress = InetAddress.getByName(args[0]);	//Server Address
		// Convert the argument string to bytes using the default encoding
		byte[] bytesToSend = args[1].getBytes();
		
		int servPort = (args.length == 3) ? Integer.parseInt(args[2]) : 7;
		
		DatagramSocket socket = new DatagramSocket();
		
		socket.setSoTimeout(TIME_OUT);	//Maximum receive blocking time (milliseconds)
		
		//Sending packet
		DatagramPacket sendPacket = new DatagramPacket(bytesToSend, bytesToSend.length, serverAddress, servPort);
		
		//receiving packet
		DatagramPacket receivePacket = new DatagramPacket(new byte[bytesToSend.length], bytesToSend.length);
		
		int tries = 0;	//Packets may be lost, so we have to keep trying
		boolean receivedResponse = false;
		do {
			socket.send(sendPacket);	//Send the echo String
			try {
				socket.receive(receivePacket);	//Attempt echo reply reception
				
				//Check source
				if (!receivePacket.getAddress().equals(serverAddress)) { 
					throw new IOException("Received packet from an unknown source");
				}
				
				receivedResponse = true;
			} catch(InterruptedIOException e) {	// We did not get anything
				tries += 1;
				System.out.println(String.format("Time out, %d more tries", MAXTRIES - tries));
			}
		} while ((!receivedResponse) && (tries < MAXTRIES));
		
		if (receivedResponse) {
			System.out.println("Received: " + new String(receivePacket.getData()));
		} else {
			System.out.println("No response -- giving up.");
		}
		
		socket.close();
	}

}
