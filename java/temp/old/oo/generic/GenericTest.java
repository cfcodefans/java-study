package cf.study.oo.generic;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;

class Amphibian {
}

class LinkedStack<T> {
	private static class Node<U> {
		U item;
		Node<U> next;

		Node() {
			item = null;
			next = null;
		}

		Node(U item, Node<U> next) {
			this.item = item;
			this.next = next;
		}

		boolean end() {
			return item == null && next == null;
		}
	}

	private Node<T> top = new Node<T>(); // End sentinel

	public void push(T item) {
		top = new Node<T>(item, top);
	}

	public T pop() {
		T result = top.item;
		if (!top.end())
			top = top.next;
		return result;
	}
}

class RandomList<T> {
	private ArrayList<T> storage = new ArrayList<T>();
	private Random rand = new Random(47);

	public void add(T item) {
		storage.add(item);
	}

	public T select() {
		return storage.get(rand.nextInt(storage.size()));
	}

}

class GenericBase<T> {
	private T element;

	public void set(T arg) {
		arg = element;
	}

	public T get() {
		return element;
	}
}

class Derived1<T> extends GenericBase<T> {
}

class Derived2 extends GenericBase {
} // No warning
// class Derived3 extends GenericBase<?> {}
// Strange error:
// unexpected type found : ?
// required: class or interface without bounds

class ErasureAndInheritance {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Derived2 d2 = new Derived2();
		Object obj = d2.get();
		d2.set(obj); // Warning here!
	}
} // /:~

class ArrayMaker<T> {
	private Class<T> kind;

	public ArrayMaker(Class<T> kind) {
		this.kind = kind;
	}

	@SuppressWarnings("unchecked")
	T[] create(int size) {
		return (T[]) Array.newInstance(kind, size);
	}

} // Output:

class Erased<T> {
	private final int SIZE = 100;

	public void f(Object arg) {
		// if (arg instanceof T) {
		// } // Error
		// T var = new T(); // Error
		// T[] array = new T[SIZE]; // Error
		// T[] array = (T) new Object[SIZE]; // Unchecked warning
	}
} // /:~

public class GenericTest {

	@Test
	public void t4() {
		ArrayMaker<String> stringMaker = new ArrayMaker<String>(String.class);
		String[] stringArray = stringMaker.create(9);
		System.out.println(Arrays.toString(stringArray));
	}

	@Test
	public void t3() {
		RandomList<String> rs = new RandomList<String>();
		for (String s : ("The quick brown fox jumped over " + "the lazy brown dog").split(" "))
			rs.add(s);
		for (int i = 0; i < 11; i++)
			System.out.print(rs.select() + " ");
	}

	static TwoTuple<String, Integer> f() {
		// Autoboxing converts the int to Integer:
		return new TwoTuple<String, Integer>("hi", 47);
	}

	static ThreeTuple<Amphibian, String, Integer> g() {
		return new ThreeTuple<Amphibian, String, Integer>(new Amphibian(), "hi", 47);
	}

	static FourTuple<Vehicle, Amphibian, String, Integer> h() {
		return new FourTuple<Vehicle, Amphibian, String, Integer>(new Vehicle(), new Amphibian(), "hi", 47);
	}

	static FiveTuple<Vehicle, Amphibian, String, Integer, Double> k() {
		return new FiveTuple<Vehicle, Amphibian, String, Integer, Double>(new Vehicle(), new Amphibian(), "hi", 47, 11.1);
	}

	@Test
	public void t1() {
		TwoTuple<String, Integer> ttsi = f();
		System.out.println(ttsi);
		// ttsi.first = "there"; // Compile error: final
		System.out.println(g());
		System.out.println(h());
		System.out.println(k());
	}

	@Test
	public void t2() {
		LinkedStack<String> lss = new LinkedStack<String>();
		for (String s : "Phasers on stun!".split(" "))
			lss.push(s);
		String s;
		while ((s = lss.pop()) != null)
			System.out.println(s);
	}
}

class Automobile {
}

class Holder1 {
	private Automobile a;

	public Holder1(Automobile a) {
		this.a = a;
	}

	Automobile get() {
		return a;
	}
} // /:~

class Holder2 {
	private Object a;

	public Holder2(Object a) {
		this.a = a;
	}

	public void set(Object a) {
		this.a = a;
	}

	public Object get() {
		return a;
	}

	public static void main(String[] args) {
		Holder2 h2 = new Holder2(new Automobile());
		Automobile a = (Automobile) h2.get();
		h2.set("Not an Automobile");
		String s = (String) h2.get();
		h2.set(1); // Autoboxes to Integer
		Integer x = (Integer) h2.get();
	}
} // /:~

class Holder3<T> {
	private T a;

	public Holder3(T a) {
		this.a = a;
	}

	public void set(T a) {
		this.a = a;
	}

	public T get() {
		return a;
	}

	public static void main(String[] args) {
		Holder3<Automobile> h3 = new Holder3<Automobile>(new Automobile());
		Automobile a = h3.get(); // No cast needed
		// h3.set("Not an Automobile"); // Error
		// h3.set(1); // Error
		Holder3<?> h4 = h3;
		// h4.set("what");//compile error

		// Holder3<Object> h5 = h3;

		try {
			System.out.println(h3.getClass());

			Type genericSuperclass = h3.getClass().getGenericSuperclass();
			if (genericSuperclass instanceof ParameterizedType) {
				ParameterizedType paramType = (ParameterizedType) genericSuperclass;
				System.out.println(Arrays.toString(paramType.getActualTypeArguments()));
			}

			Type returnType = h3.getClass().getMethod("get").getGenericReturnType();
			if (returnType instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) returnType;
				System.out.println("return type: " + Arrays.toString(pt.getActualTypeArguments()));
			}

			System.out.println(h4.getClass().getMethod("get").getReturnType());
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

		try {
			List<String> strList = new ArrayList<String>();
			Type genericSuperclass = strList.getClass().getGenericSuperclass();
			if (genericSuperclass instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) genericSuperclass;
				System.out.println("super class parameterized type: " + Arrays.toString(pt.getActualTypeArguments()));
			}

			Type[] genericInterfaces = strList.getClass().getGenericInterfaces();
			for (Type genericInterface : genericInterfaces) {
				if (genericInterface instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType) genericInterface;
					System.out.println("interfaces parameterized type: " + Arrays.toString(pt.getActualTypeArguments()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
} // /:~

class TwoTuple<A, B> {
	public final A first;
	public final B second;

	public TwoTuple(A a, B b) {
		first = a;
		second = b;
	}

	public String toString() {
		return "(" + first + ", " + second + ")";
	}
} // /:~

class ThreeTuple<A, B, C> extends TwoTuple<A, B> {
	public final C third;

	public ThreeTuple(A a, B b, C c) {
		super(a, b);
		third = c;
	}

	public String toString() {
		return "(" + first + ", " + second + ", " + third + ")";
	}
} // /:~
// : net/mindview/util/FourTuple.java

class FourTuple<A, B, C, D> extends ThreeTuple<A, B, C> {
	public final D fourth;

	public FourTuple(A a, B b, C c, D d) {
		super(a, b, c);
		fourth = d;
	}

	public String toString() {
		return "(" + first + ", " + second + ", " + third + ", " + fourth + ")";
	}
} // /:~

class FiveTuple<A, B, C, D, E> extends FourTuple<A, B, C, D> {
	public final E fifth;

	public FiveTuple(A a, B b, C c, D d, E e) {
		super(a, b, c, d);
		fifth = e;
	}

	public String toString() {
		return "(" + first + ", " + second + ", " + third + ", " + fourth + ", " + fifth + ")";
	}
} // /:~

class Vehicle {
}

interface IEvent extends Serializable {
	
}

interface IOper<E extends IEvent> {
	void oper(E e);
}

class AbstractEvent implements IEvent {
	
}

abstract class AbstractOper<EA extends AbstractEvent> implements IOper<AbstractEvent> {
}

class TestEvent extends AbstractEvent {
	
}

class TestOper extends AbstractOper<TestEvent> {
	@Override
	public void oper(AbstractEvent e) {
		
	}
}