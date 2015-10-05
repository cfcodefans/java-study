package cf.study.jmx.egs.mxbean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class TestMXBean implements ITestMXBean {

	private void printStack() {
		System.out.println();
		for (StackTraceElement stf : Thread.currentThread().getStackTrace()) {
			System.out.println(String.format("%s,\t%s: \t%s.%s", stf.getFileName(), stf.getLineNumber(), stf.getClassName(), stf.getMethodName()));
		}
	}
	
	public String getString() {
		printStack();
		return "getString";
	}

	public Integer getInteger() {
		printStack();
		return 5;
	}

	public Double getDouble() {
		printStack();
		return 5d;
	}

	public Serializable getSerializable() {
		printStack();
		return new Date();
	}

	public Long getLong() {
		printStack();
		return 5l;
	}

	public void foo() {
		printStack();
	}

	public Set<String> getStrings() {
		return new HashSet<String>(Arrays.asList("Hello", "World"));
	}

	public Set<Long> getLongs() {
		return new HashSet<Long>(Arrays.asList(3L, 1L, 4L));
	}

	public void setStrings(Set<String> strs) {
		printStack();
		System.out.println(strs);
	}

}
