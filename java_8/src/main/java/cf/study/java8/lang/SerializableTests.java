package cf.study.java8.lang;

import java.io.Serializable;

import misc.MiscUtils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;

public class SerializableTests {
	public static class SerializableBaseCls extends CloneTests.CloneableBaseCls implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public Object publicAttr;
		private Object privateAttr;
		protected Object protectedAttr;
		

		public SerializableBaseCls() {
			super();
			publicAttr = RandomStringUtils.randomNumeric(3);
			privateAttr = RandomStringUtils.randomNumeric(3);
			protectedAttr = RandomStringUtils.randomNumeric(3);
			System.out.println("SerializableBaseCls() -> " + this.toString());
		}

		public String toString() {
			return String.format("super.toString()=%s \n\tSerializableBaseCls [publicAttr=%s, privateAttr=%s, protectedAttr=%s]\n",super.toString(), publicAttr, privateAttr, protectedAttr);
		}

		public String foo() {
			return SerializableBaseCls.class.getSimpleName() + ".foo()";
		}

		private void writeObject(java.io.ObjectOutputStream stream) throws java.io.IOException {
			System.out.println("writeObject: " + this.toString());
			stream.defaultWriteObject();
		}

		private void readObject(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException {
			System.out.println("before defaultReadObject: " + this.toString());
			stream.defaultReadObject();
			System.out.println("after defaultReadObject: " +this.toString());
		}
	}

	public static class SerializableSubCls extends SerializableBaseCls {
		private static final long serialVersionUID = 1L;
		public static final String DEFAULT_STR_VAL = StringUtils.EMPTY;
		private String strAttr = DEFAULT_STR_VAL;

		public String getStrAttr() {
			return strAttr;
		}

		public void setStrAttr(String strAttr) {
			this.strAttr = strAttr;
		}

		public String foo() {
			return SerializableSubCls.class.getSimpleName() + ".foo()";
		}

		@Override
		public String toString() {
			return String.format("toString()=%s\n\tSerializableSubCls [strAttr=%s, ]", super.toString(), strAttr);
		}
	}

	@Test
	public void testSerialization() {
		SerializableBaseCls src = new SerializableBaseCls();
		byte[] bytes = SerializationUtils.serialize(src);
		System.out.println("bytes: " + bytes.length);
		SerializableBaseCls one = (SerializableBaseCls) SerializationUtils.deserialize(bytes);
		System.out.println(one.toString());
		
		System.out.println("\n \t conclusion: the super class's content is not serialized and deserialized unless they implement serializable!");
	}


	@Test
	public void testExtraAttrs() {
		SerializableSubCls subSrc = new SerializableSubCls();
		SerializableBaseCls baseSrc = new SerializableBaseCls();

//		subSrc.setStrAttr("extra.attribute");
//
//		byte[] subBytes = SerializationUtils.serialize(subSrc);
//		byte[] baseBytes = SerializationUtils.serialize(subSrc);
//
//		System.out.println(String.format("subByte: %d baseByte: %d\n\n", subBytes.length, baseBytes.length));
		
		subSrc.setStrAttr(RandomStringUtils.randomNumeric(3));
		byte[] subBytes = SerializationUtils.serialize(baseSrc);
		SerializableSubCls subDst = (SerializableSubCls) SerializationUtils.deserialize(subBytes);
		
		System.out.println("after deserialization: \n" + subDst);
	}
	
	public static class A  {
		public A() {System.out.println(MiscUtils.invocationInfo());};
		public Object a;
	}
	
	public static class B extends A implements Serializable {
		public Object b;
		public B() {System.out.println(MiscUtils.invocationInfo());};
	}
	
	public static class C extends B {
		public Object c;
		public C() {System.out.println(MiscUtils.invocationInfo());};
	}
	
	@Test
	public void testInheritance() {
		C c = new C();
		c.a = "a";
		c.b = "b";
		c.c = "c";
		
		C c1 = (C)SerializationUtils.deserialize(SerializationUtils.serialize(c));
		
		String str = ToStringBuilder.reflectionToString(c1);
		
		System.out.println(str);
		System.out.println("when deserialization, Serializable classes and super classes don't call their constructors!");
	}
	
	static class SerializableSingleton implements Serializable {
		static SerializableSingleton instance = new SerializableSingleton();
		private Object readResolve() {
			System.out.println(this);
			System.out.println(instance);
			return instance;
		}
	}
	
	@Test
	public void testSingleton() {
		byte[] buf = SerializationUtils.serialize(SerializableSingleton.instance);
		SerializableSingleton ss = (SerializableSingleton) SerializationUtils.deserialize(buf);
		System.out.println(ss == SerializableSingleton.instance);
	}
}
