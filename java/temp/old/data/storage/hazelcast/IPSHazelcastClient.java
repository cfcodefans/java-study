package cf.study.data.storage.hazelcast;

import com.hazelcast.client.ClientConfig;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class IPSHazelcastClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClientConfig clientCfg = new ClientConfig();
		clientCfg.addAddress("127.0.0.1:5701");
		HazelcastInstance client = HazelcastClient.newHazelcastClient(clientCfg);
		IMap<Object, Object> map = client.getMap("79e710b4-800d-41c9-9450-8f402c83ffa7");
		
		for (int i = 0; i < 10; i++) {
			map.put(i, "order" + i);
		}
		
	}

}
