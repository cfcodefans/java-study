package cf.study.utils;

import java.util.Stack;

public class TimeCounter {

	static long t = 0;
	static Stack<String> callers = new Stack<String>();
	
	public static void start(String caller) {
		callers.push(caller);
		t = System.currentTimeMillis();
	}
	
	public static void end() {
		System.out.println(String.format("%s comsumes : %d", callers.pop(), System.currentTimeMillis() - t));
	}
	
	public static void start() {
		start(MiscUtils.invocationInfo(3));
	}
}
