package cf.study.oo.cdi;

import javax.inject.Named;

@Named("en")
public class EnglishHelloWorld implements IHelloWorld {

	public void helloWorld() {
		System.out.println("Hello World!");
	}
}