package cf.study.jmx.egs.ibm;


public class ServerMonitor implements ServerMonitorMBean {
	private final ServerImpl target;

	public ServerMonitor(ServerImpl target) {
		this.target = target;
	}

	public long getUpTime() {
		return System.currentTimeMillis() - target.startTime;
	}
}