package cf.study.data.storage.hazelcast;

import java.util.Map;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;

public class IPSHazelcastTest implements MembershipListener {

	public IPSHazelcastTest() {
		System.out.println("IPSHazelcastTest initializes");
		Cluster cluster = Hazelcast.getCluster();
		String memberId = cluster.getLocalMember().getUuid();
		storage = Hazelcast.getMap(memberId);
		
		System.out.printf("member: %s is up\n", memberId);
	}
	
	
	private IMap storage = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Cluster cluster = Hazelcast.getCluster();
		cluster.addMembershipListener(new IPSHazelcastTest());
	}

	public void memberAdded(MembershipEvent me) {
		String memberId = me.getMember().getUuid();
		System.out.println(String.format("new Member: %s is up", me.getMember().getInetAddress()));
	}

	public void memberRemoved(MembershipEvent me) {
		String memberId = me.getMember().getUuid();
		System.out.println(String.format("Member: %s is down", memberId));
		System.out.println("take its jobs over!");
		
		IMap<Object, Object> mapOfTheOther = Hazelcast.getMap(memberId);
		for (Map.Entry<Object, Object> entry : mapOfTheOther.entrySet()) {
			processJob(Long.valueOf(entry.getKey().toString()), entry);
		}
	}
	
	public void processJob(long id, Object job) {
		storage.put(id, job);
		System.out.println(String.format("IPSHazelCastTest: %s is working on job:{id: %d, job: '%s'}", storage.getName(), id, job.toString()));
		storage.remove(id);
	}

}
