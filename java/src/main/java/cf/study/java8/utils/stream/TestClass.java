package cf.study.java8.utils.stream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class TestClass implements Serializable {
	private static final long serialVersionUID = 1L;
	String					msg	= "HEY!";
	SerializableRunnable	runnable;

	public TestClass() {
		TestClass self = this;
		runnable = () -> self.say(); // uses a local copy of 'this'
//		 runnable = () -> this.say(); // uses 'this' directly
	}

	public void say() {
		System.out.println(msg);
	}

	public static void main(String[] args) throws Exception {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try (ObjectOutputStream out = new ObjectOutputStream(buffer)) {
			out.writeObject(new TestClass());
		}
		try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()))) {
			TestClass s = (TestClass) in.readObject();
			s.say();
		}
	}
}

interface SerializableRunnable extends Runnable, Serializable {
}