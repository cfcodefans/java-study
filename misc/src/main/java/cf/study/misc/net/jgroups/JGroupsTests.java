package cf.study.misc.net.jgroups;

import java.io.InputStream;
import java.io.OutputStream;

import misc.MiscUtils;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public class JGroupsTests {

	//supported protocols
	//UDP/TCP, PING/TCPPING, FD_SOCK/FD/FD_ALL, NAKACK, UNICAST, STABLE, GMS, UFC/MFC, FRAG, STATE_TRANSFER
	
	
	
	static final String	CLUSTER_NAME	= JGroupsTests.class.getSimpleName();

	static class MasterReceiver implements Receiver {
		public void receive(Message msg) {
			log.info(MiscUtils.invocationInfo());
		}

		public void getState(OutputStream output) throws Exception {
			log.info(MiscUtils.invocationInfo());
		}

		public void setState(InputStream input) throws Exception {
			log.info(MiscUtils.invocationInfo());
			log.info(IOUtils.toString(input));
		}

		public void viewAccepted(View new_view) {
			log.info(MiscUtils.invocationInfo());
			log.info(new_view);
		}

		public void suspect(Address suspected_mbr) {
			log.info(MiscUtils.invocationInfo());
			log.info(suspected_mbr);
		}

		public void block() {
			log.info(MiscUtils.invocationInfo());
		}

		public void unblock() {
			log.info(MiscUtils.invocationInfo());
		}
	}

	@BeforeClass
	public static void setup() throws Exception {
		log.info(MiscUtils.invocationInfo());
	}

	@Before
	public void before() {
		log.info("");
	}

	@After
	public void after() {
		log.info("");
	}

	@AfterClass
	public static void tearDown() {
		log.info(MiscUtils.invocationInfo());
	}

	private static final Logger	log	= Logger.getLogger(JGroupsTests.class);
}
