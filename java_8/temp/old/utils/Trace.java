package cf.study.utils;

import java.util.LinkedList;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;

public class Trace {
	private static final String INDENT = "    ";
	private StringBuilder buf = new StringBuilder("\n");
	private final Stack<TraceEntry> stack = new Stack<TraceEntry>();
	private final Stack<StringBuilder> finishedStack = new Stack<StringBuilder>();
	
	private static enum Status {
		ongoing, finished
	}
	
	public static class TraceStep {
		long perfTime;
		String stepInfo;
		public String toString() {
			return perfTime + " ms:\t" + stepInfo;
		}
	}
	
	public static class TraceSubStep extends TraceStep {
		public String toString() {
			return stepInfo;
		}
	}
	
	public static class TraceEntry {
		final long startTime = System.currentTimeMillis();;
		long currentTime;
		final LinkedList<TraceStep> steps = new LinkedList<TraceStep>();
	}
	
	private static final ThreadLocal<Trace> instancePool = new ThreadLocal<Trace>() {
	    protected Trace initialValue() {
	        return new Trace();
	    }
	};
	
	public static Trace instance() {
		return instancePool.get();
	}
	
	private Trace() {
		
	}

	public static TraceEntry start() {
		return start(MiscUtils.invocationInfo(3));
	}
	
	public static TraceEntry start(String logMsg) {
		Trace pt = Trace.instance();
		
		final TraceEntry te = new TraceEntry();
		te.currentTime = te.startTime;
		
		TraceStep ts = new TraceStep();
		ts.perfTime = 0;
		ts.stepInfo = logMsg;
		te.steps.push(ts);
		
		pt.stack.push(te);
		
		return te;
	}
	
	public static void ongoing(String logMsg) {
		final TraceStep newStep = new TraceStep();
		newStep.stepInfo = logMsg;
		ongoing(newStep);
	}
	
	public static void ongoing(final TraceStep ts) {
		final Trace pt = Trace.instance();
		
		if (pt.stack.isEmpty()) {
			start(ts.stepInfo);
			return;
		}
		
		final TraceEntry te = pt.stack.peek();
		final TraceStep lastStep = te.steps.peekLast();
		final long curTime = System.currentTimeMillis();
		lastStep.perfTime = curTime - te.currentTime;
		te.currentTime = curTime;
		te.steps.add(ts);
	}

	public static void end() {
		Trace pt = Trace.instance();
		
		if (pt.stack.isEmpty()) {
			pt.buf.append("\nend of nothing\n");
			return;
		}
		
		final int layer = pt.stack.size();
		final String prefix = StringUtils.repeat(INDENT, layer);
		
		final TraceEntry te = pt.stack.pop();
		
		final StringBuilder sb = new StringBuilder();
		sb.append("\n").append(prefix).append(System.currentTimeMillis() - te.startTime).append(" ms:\t").append(te.steps.poll().stepInfo);
		for (TraceStep ts : te.steps) {
			sb.append('\n').append(prefix).append(INDENT).append(ts.toString());
		}
		sb.append('\n');
//		pt.buf.append(sb);
		if (pt.stack.isEmpty()) {
			pt.buf.append(sb);
			while (!pt.finishedStack.isEmpty()) {
				pt.buf.append(pt.finishedStack.pop());
			}
		} else {
			TraceStep ts = new TraceSubStep();
			ts.stepInfo = sb.toString();
			ongoing(ts);
		}
	}
	
	public static void end(TraceEntry te, String logMsg) {
		Trace pt = Trace.instance();
		if (te == null || StringUtils.isBlank(logMsg)) {
			return;
		}
		
		final int layer = pt.stack.indexOf(te);
//		pt.finishedStack.push(new StringBuilder("\n" + StringUtils.repeat(INDENT, layer + 1) + "Exception: " + logMsg));
		for (TraceEntry _te = pt.stack.peek(); _te != te; _te = pt.stack.peek()) {
			end();
		}
		Trace.ongoing("Exception: " + logMsg);
	}
	
	public static void end(TraceEntry te, Throwable e) {
		if (te == null || e == null) {
			return;
		}
		end(te, e.getMessage());
	}

	public static String flush() {
		Trace pt = Trace.instance();
		
		while (!pt.stack.isEmpty()) {
			end();
		}
		
		String str = pt.buf.toString();
		pt.buf = new StringBuilder("\n");
		return str;
	}
}
