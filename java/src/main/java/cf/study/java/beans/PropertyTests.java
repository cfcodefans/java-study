package cf.study.java.beans;

import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import misc.MiscUtils;

public class PropertyTests {
	
	public static class BizProc extends Thread {
		public final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
		
		private static void monitor() {
			log.info("monitor started");
			while (!Thread.interrupted()) {
				pools.forEach(bp -> bp.getState());
			}
			log.info("monitor stopped");
		}
		private static final ExecutorService es = Executors.newSingleThreadExecutor();
		static {
			es.submit(BizProc::monitor);
		}
		public static final Set<BizProc> pools = Collections.synchronizedSet(new HashSet<BizProc>());

		private State _state = null;
		
		public BizProc(Runnable r) {
			super(r);
			_state = super.getState();
			pools.add(this);
		}
		
		public State getState() {
			State s = super.getState();
			if (s != _state) {
				setState(s);
			}
			return s;
		}
		
		private void setState(State s) {
			pcs.firePropertyChange("state", _state, s);
			_state = s;
		}
	}

	private static final Logger log = LoggerFactory.getLogger(PropertyTests.class);
	
	@Test
	public void testPropertyChangeListener() throws InterruptedException {
		Runnable r = ()->{
			log.info(MiscUtils.invocationInfo());
			MiscUtils.easySleep(200);
			log.info(MiscUtils.invocationInfo());
		};
		BizProc bp = new BizProc(r);
		bp.pcs.addPropertyChangeListener("state", (pe)->{
			log.info(ToStringBuilder.reflectionToString(pe, ToStringStyle.MULTI_LINE_STYLE));
		});
		bp.start();
		bp.join();
	}
}
