package cf.study.jmx.egs.helloworld;

public interface HelloWorldMBean {
	void setGreeting(String greeting);
	String getGreeting();
	void printGreeting();
}
