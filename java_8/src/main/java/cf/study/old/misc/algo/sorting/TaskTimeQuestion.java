package cf.study.misc.algo.sorting;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

/**
 * The question is:
 * Given a list of tasks, each task has a time window defined by start time and end time.
 * to get sum of all Tasks's perform time.
 * 
 * 
 * @author Fan
 *
 */
public class TaskTimeQuestion {

	private static final int TASK_NUM_RANGE = 30;

	static class Task {
		int start;
		int end;
		public Task() { this(0,0);}
		public Task(int start, int end) {
			super();
			this.start = start;
			this.end = end;
		}
		public String toString() {
			return String.format("(%d,%d)", start, end);
		}
	}
	
	final int TIME_RANGE = 1000;

	
	public List<Task> randomTasks() {
		LinkedList<Task> tasks = new LinkedList<Task>();
		for (int i = 0, j = RandomUtils.nextInt(20) + 1; i < j; i++) {
			Task t = new Task();
			t.start = RandomUtils.nextInt(TIME_RANGE);
			t.end = t.start + RandomUtils.nextInt((TIME_RANGE - t.start));
			tasks.add(t);
		}
		System.out.println("Test Data: " + tasks);
		return tasks;
	}
	
	public List<Task> nonOverlayRandomTasks() {
		LinkedList<Task> tasks = new LinkedList<Task>();
		tasks.add(new Task(0, RandomUtils.nextInt(TIME_RANGE)));
		for (int i = 0, j = RandomUtils.nextInt(TASK_NUM_RANGE) + 1; i < j; i++) {
			Task t = new Task();
			t.start = tasks.peekLast().end + RandomUtils.nextInt(TIME_RANGE - tasks.peekLast().end);
			t.end = t.start + RandomUtils.nextInt((TIME_RANGE - t.start));
			tasks.add(t);
		}
		System.out.println("Test Data: " + tasks);
		return tasks;
	}
	
	public List<Task> randomTasksWithSameStartTime() {
		LinkedList<Task> tasks = new LinkedList<Task>();
		int startTime = RandomUtils.nextInt(TIME_RANGE) + 1;
		
		for (int i = 0, j = RandomUtils.nextInt(TASK_NUM_RANGE) + 1; i < j; i++) {
			Task t = new Task(startTime, startTime + RandomUtils.nextInt((TIME_RANGE - startTime)));
			tasks.add(t);
		}
		
		System.out.println("Test Data: " + tasks);
		return tasks;
	}
	
	public List<Task> randomTasksWithSameEndTime() {
		LinkedList<Task> tasks = new LinkedList<Task>();
		int endTime = RandomUtils.nextInt(TIME_RANGE) + 1;
		
		for (int i = 0, j = RandomUtils.nextInt(TASK_NUM_RANGE) + 1; i < j; i++) {
			Task t = new Task(RandomUtils.nextInt(endTime), endTime);
			tasks.add(t);
		}
		
		System.out.println("Test Data: " + tasks);
		return tasks;
	}
	
	/**
	 * calculate the used time
	 * by marking every time unit, it isn't optimal for memory usage and time
	 * it can provide the result to verify another implementation
	 * @return return the sum of used time of all tasks
	 */
	private int sum1(List<Task> tasks) {
		byte[] buf = new byte[TIME_RANGE];
		
		for (Task task : tasks) {
			for (int i = task.start; i < task.end; i++) {
				buf[i] = 1;
			}
		}
		
		int sum = 0;
		for (int i = 0; i < buf.length; i++) {
			if (buf[i] == 1) {
				sum++;
			}
		}
		
		return sum;
	}
	
	/**
	 * 1. get the time window of all tasks.
	 * 2. sort the tasks according to start time.
	 * 3. iterate through the sorted tasks list, and take out each task's time window from the overall time window.
	 * 3.1 if the task's time window doesn't over with last task, 
	 * 	   then there is some unused time between these two tasks.
	 * 4. sum up the unused time.
	 * 5. overall time window minus unused time is the overall used time.
	 * 
	 * @return
	 */
	private int sum2(List<Task> tasks) {
		final List<Task> tempTaskLst = new LinkedList<Task>(tasks);
		
		final Comparator<Task> startTimeCmp = new Comparator<Task>() {
			@Override
			public int compare(Task t1, Task t2) {
				return t1.start - t2.start;
			}
		};
		final int startTimeForAll = Collections.min(tempTaskLst, startTimeCmp).start;
		
		final Comparator<Task> endTimeCmp = new Comparator<Task>() {
			@Override
			public int compare(Task t1, Task t2) {
				return t1.end - t2.end;
			}
		};
		final int endTimeForAll = Collections.max(tempTaskLst, endTimeCmp).end;
		
		Task taskForAll = new Task(startTimeForAll, endTimeForAll);
		Collections.sort(tempTaskLst, startTimeCmp);// sort all tasks according to start time
		
		int unusedTime = 0;
		
		for (Task task : tempTaskLst) {
			if (task.start > taskForAll.start) {
				unusedTime += task.start - taskForAll.start;
			}
			if (taskForAll.start < task.end) {
				taskForAll.start = task.end;
			}
		}
		
		return endTimeForAll - startTimeForAll - unusedTime;
	}
	
	@Test
	public void testSum() {
		for (int i = 0; i < 100; i++) {
			List<Task> randomTasks = randomTasks();
			Assert.assertEquals(sum1(randomTasks), sum2(randomTasks));
		}
		
		for (int i = 0; i < 100; i++) {
			List<Task> nonOverlayRandomTasks = nonOverlayRandomTasks();
			Assert.assertEquals(sum1(nonOverlayRandomTasks), sum2(nonOverlayRandomTasks));
		}
		
		for (int i = 0; i < 100; i++) {
			List<Task> randomTasksWithSameStartTime = randomTasksWithSameStartTime();
			Assert.assertEquals(sum1(randomTasksWithSameStartTime), sum2(randomTasksWithSameStartTime));
		}
		
		for (int i = 0; i < 100; i++) {
			List<Task> randomTasksWithSameEndTime = randomTasksWithSameEndTime();
			Assert.assertEquals(sum1(randomTasksWithSameEndTime), sum2(randomTasksWithSameEndTime));
		}
	}
	
	@Test
	public void testPerf() {
		List<Task> randomTasks = randomTasks();
		
		long t = System.currentTimeMillis();
		int cnt = 100000;
		for (int i = 0; i < cnt; i++) {
			sum1(randomTasks);
		}
		System.out.println(String.format("sum 1 consumes %d for %d times", System.currentTimeMillis()- t, cnt));
		
		t = System.currentTimeMillis();
		for (int i = 0; i < cnt; i++) {
			sum2(randomTasks);
		}
		System.out.println(String.format("sum 2 consumes %d for %d times", System.currentTimeMillis()- t, cnt));
	}
}
