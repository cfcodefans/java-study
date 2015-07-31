package cf.study.java8.lang;

import java.util.ArrayList;
import java.util.List;

import misc.MiscUtils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;

public class CloneTests {
	
	public static class CloneableBaseCls implements Cloneable {
		private String	name;
		private long	id;
		private List<Object> things;
		
		protected Object protectedAttr;
		
		public static long COUNT = 0;
		
		public CloneableBaseCls() {
			id = COUNT++;
			name = RandomStringUtils.randomAlphabetic(5);
			things = new ArrayList<Object>();
			protectedAttr = RandomStringUtils.randomNumeric(3);
			System.out.println("CloneableCls() - >" + toString());
		}
		
		@Override
		public String toString() {
			return String.format("CloneableBaseCls [ id=%s, name=%s, things=%s, protectedAttr=%s]", id, name, things, protectedAttr);
		}
		
		public CloneableBaseCls clone() {
			try {
				return (CloneableBaseCls) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public static class CloneableCls1 extends CloneableBaseCls {
		private String extendStr;

		public CloneableCls1() {
			super();
			extendStr = RandomStringUtils.randomAscii(5);
		}
		
		@Override
		public String toString() {
			return "CloneableCls1 [extendStr=" + extendStr + ", toString()=" + super.toString() + "]";
		}
	}
	
	@Test
	public void testClone() {
		CloneableBaseCls src = new CloneableBaseCls();
		CloneableBaseCls clone1 = src.clone();
		System.out.println(CloneableBaseCls.COUNT);
		System.out.println(clone1.toString());
	}
	
	@Test
	public void testCloneSubClass() {
		CloneableBaseCls src = new CloneableCls1();
		CloneableBaseCls clone1 = src.clone();
		System.out.println(CloneableBaseCls.COUNT);
		System.out.println(clone1.toString());
	}

	
	public static class A  {
		public A() {System.out.println(MiscUtils.invocationInfo());}
		public Object a;
	}
	
	public static class B extends A implements Cloneable {
		public Object b;
		public B() {System.out.println(MiscUtils.invocationInfo());}
	}
	
	public static class C extends B {
		public Object c;
		public C() {System.out.println(MiscUtils.invocationInfo());}
		public C clone() {
			try {
				return (C) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	@Test
	public void testInheritance() {
		C c = new C();
		c.a = "a";
		c.b = "b";
		c.c = "c";
		
		C c1 = c.clone();
		
		String str = ToStringBuilder.reflectionToString(c1);
		
		System.out.println(str);
		System.out.println("when cloning, all classes in inheritance hierarchy don't call their constructors!");
	}
}
