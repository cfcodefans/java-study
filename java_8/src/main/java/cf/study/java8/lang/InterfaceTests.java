package cf.study.java8.lang;

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
	
}
