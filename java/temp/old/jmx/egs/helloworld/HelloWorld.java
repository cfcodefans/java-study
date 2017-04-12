package cf.study.jmx.egs.helloworld;

public class HelloWorld implements HelloWorldMBean {

	private String greeting = null;
	
	public HelloWorld() {
		super();
		this.greeting = "HelloWorld, I am a standard MBean";
	}
	
	public HelloWorld(String greeting) {
		super();
		this.greeting = greeting;
	}

	public void setGreeting(String greeting) {
		this.greeting = greeting;
	}

	public String getGreeting() {
		return greeting;
	}

	public void printGreeting() {
		System.out.println(greeting);
	}

}
