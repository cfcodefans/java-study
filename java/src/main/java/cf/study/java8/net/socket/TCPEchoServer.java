package cf.study.java8.net.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPEchoServer {

	public static final int BUF_SIZE = 32;
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			throw new IllegalArgumentException("Parameters(s): <Port>");
		}
		
		int servPort = Integer.parseInt(args[0]);
		
		//Create a server socket to accept client connection requests
		ServerSocket servSock = new ServerSocket(servPort);
		
		int recvMsgSize; //Size of received message
		byte[] byteBuffer = new byte[BUF_SIZE];//Receive Buffer
		
		try {
			for (;;) { //Run forever, accepting and servicing connections
				Socket clntSock = servSock.accept(); //Get Client connection
				
				System.out.println("Handling client at " + clntSock.getInetAddress().getHostAddress() + " on port " + clntSock.getPort());
				
				InputStream in = clntSock.getInputStream();
				OutputStream out = clntSock.getOutputStream();
				
				//Receive until client closes connection, indicated by -1 return
				while ((recvMsgSize = in.read(byteBuffer)) != -1) {
					out.write(byteBuffer, 0, recvMsgSize);
				}
				
				clntSock.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			servSock.close();
		}
	}

}
