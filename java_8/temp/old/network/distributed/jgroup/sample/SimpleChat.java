package cf.study.network.distributed.jgroup.sample;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;



public class SimpleChat extends ReceiverAdapter {
	final List<String> state=new LinkedList<String>();
	JChannel channel;
	String userName = System.getProperty("user.name", "n/a");
	
	private void start() throws Exception {
		channel = new JChannel();
		channel.setReceiver(this);
		channel.connect("ChatCluster");
		channel.getState(null, 10000);
		eventLoop();
		channel.close();
	}
	
	public void viewAccepted(View view) {
		System.out.println("View:\t" + view);//when members change in the group, this is called
	}
	
	public void receive(Message msg) {
		String line = msg.getSrc() + ":\t" + msg.getObject();
		System.out.println(line);
		synchronized (state) {
			state.add(line);
		}
	}
	
	//called as new member joins the group and tries to get the replicated state
	public void getState(OutputStream output) throws Exception {
		synchronized (state) {
			Util.objectToStream(state, new DataOutputStream(output));
		}
	}
	
	public void setState(InputStream input) throws Exception {
		List<String> list;
		list = (List<String>)Util.objectFromStream(new DataInputStream(input));
		synchronized (state) {
			state.clear();
			state.addAll(list);
		}
		System.out.println(list.size() + " messages in chat history): ");
		System.out.println(StringUtils.join(list.listIterator(), '\n'));
	}
	
	private void eventLoop() throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.print(">"); 
			System.out.flush();
			
			String line = in.readLine().toLowerCase();
			if (line.startsWith("quit") || line.startsWith("exit")) {
				break;
			}
			
			line = String.format("[ %s ]: \t%s", userName, line);
			Message msg = new Message(null, null, line);
			channel.send(msg);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new SimpleChat().start();
	}


}
