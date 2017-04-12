package cf.study.jdk7.lang;

import javax.annotation.PostConstruct;

import org.junit.Test;

public class AnnotationTest {

	@PostConstruct
	public void postConstruct() {
		System.out.println("AnnotationTest.postConstruct()");
	}
	
	@Test
	public void testPostConstruct() {
		new AnnotationTest();
	}
}
