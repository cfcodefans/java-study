package cf.study.java8.lang;

import java.util.LinkedList;
import java.util.Queue;

import misc.MiscUtils;

import org.junit.Test;

public class InterfaceTests {

	static interface IFoo {
		void foo();
		default String _toString() {
			return "IFoo";
		}
	}
	
	static interface IBar {
		void bar();

		default String _toString() {
			return "IBar";
		}
	}
	
	// Duplicate default methods named _toString with the parameters () and () are inherited from 
	// the types InterfaceTests.IBar and InterfaceTests.IFoo
	static class Impl implements IFoo {//, IBar {
//		@Override
//		public void bar() {
//			
//		}

		public String _toString() {
			//can't be referred as super._toString()
			System.out.println(((IFoo)this)._toString());
			return _toString() + "Impl";
		}
		
		@Override
		public void foo() {
			
		}
	}
	
	@Test
	public void testDefaultMethods() {
		IFoo foo = ()->System.out.println(MiscUtils.invocationInfo());
		foo.foo();
		System.out.println(foo._toString());
	}
	
	static interface IntQueue {
		default int get() {
			return getQueue().poll();
		}
		void put(int i);
		Queue<Integer> getQueue();
	}
	
	static interface Doubling extends IntQueue {
		default void put(int i) {
//			super.put(i * 2); //can't call super, while scala's trait can
			getQueue().add(i*2);
		}
	}
	
	static interface Incrementing extends IntQueue {
		default void put(int i) {
//			super.put(i * 2); //can't call super, while scala's trait can
			getQueue().add(i + 1);
		}
	}
	
	//methods with same name from two different interfaces cause conflicts
	static class DoubleQueue implements Doubling {//, Incrementing {
		Queue<Integer> queue = new LinkedList<Integer>();
		public Queue<Integer> getQueue() {
			return queue;
		}
	}
	
	//Duplicate default methods named put with the parameters (int) and (int) are inherited from the types 
	 //InterfaceTests.Doubling and InterfaceTests.Incrementing
//	static class DoubleAndIncrementQueue extends DoubleQueue implements Incrementing {
//		
//	}
	
}
