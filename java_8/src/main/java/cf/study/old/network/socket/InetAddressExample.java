package cf.study.network.socket;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

public class InetAddressExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			InetAddress address = InetAddress.getLocalHost();
			System.out.println("Local Host: ");
			System.out.println("\t" + address.getHostName());
			System.out.println("\t" + address.getHostAddress());
		} catch (UnknownHostException e) {
			System.out.println("Unable to determine this host's address");
		}
		
		for (String arg : args) {
			try {
				InetAddress[] addrs = InetAddress.getAllByName(arg);
				System.out.println(arg + ": ");
				System.out.println("\t" + addrs[0].getHostName());
				for (InetAddress addr : addrs) {
					System.out.println("\t" + addr.getHostAddress());
				}
			} catch (UnknownHostException e) {
				System.out.println("Unable to find address for " + arg);
			}
		}
	}
	
	@Test
	public void testMain() {
		main(new String[] {"www.mkp.com"});
		
		main(new String[] {"169.1.1.1"});
	}

}
