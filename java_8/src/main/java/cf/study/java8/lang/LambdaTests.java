package cf.study.java8.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

public class LambdaTests {

	public static final Logger log = Logger.getLogger(LambdaTests.class.getSimpleName());
	
	@Test
	public void lambda() {
		{
			Runnable r = new Runnable() {
				@Override
				public void run() {
					log.log(Level.INFO, "My Runnable");
				}
			};
			new Thread(r).start();
		}
		
		{
			Runnable r = () -> log.info("My lambda Runnable");
			new Thread(r).start();
		}
	}
	
	public static interface VarArgConsumer<T> {
		void accept(T...args);
	}
	
	@Test
	public void testVarArg() {
//		VarArgConsumer<Integer> vac1 = (i1, i2, i3) ->  Math.max(Math.max(i1, i2), i3);
		//doesn't work
		VarArgConsumer<Integer> vac1 = (Integer...is) -> System.out.println(Arrays.toString(is));
	}
	
	/*
	interface IObj {boolean equals(Object obj);}
	//Not functional because equals is already an implicit member (Object class)
	
	interface Comparator<T> {
		boolean equals(Object obj);
		int compare(T o1, T o2);
	}
	//Functional because comparator has only one abstract non-Object method
	
	interface Clonable {
		int m();
		Object clone();
	}
	//Not functional because method Object.clone is not public
	
	interface X { Iterable m(Iterable<String> arg); }
	interface Y { Iterable<String> m(Iterable arg); }
	interface Z extends X, Y {}
	//Functional: Y.m is a subsignature & return-type-substituable
	
	interface X1 { int m(Iterable<String> arg); }
	interface Y1 { int m(Iterable<Integer> arg); }
	interface Z1 extends X1, Y1{} //names clash
	//Not functional: No method has subsignature of all abstract methods
	
	interface X2 { int m(Iterable<String> arg, Class c);}
	interface Y2 { int m(Iterable arg, Class<?> c); }
	interface Z2 extends X2, Y2 {} //names clash
	//Not functional: No method has a subsignature of all abstract methods
	
	interface X3 { long m(); }
	interface Y3 { int m(); }
	interface Z3 extends X3, Y3 {} 
	//Compiler error: no method is return type substitutable
	
	interface Foo<T> { void m(T arg); }
	interface Bar<T> { void m(T arg); }
	interface FooBar<X, Y> extends Foo<X>, Bar<Y> {}
	// Compiler error: different signatures, same erasure
	 */
	
	private static boolean isPrime(int number) {
		if (number < 2) return false;
		for (int i = 2, j = Double.valueOf(Math.ceil(Math.sqrt(number))).intValue(); i < j; i++) {
			if (number % i == 0) return false;
		}
		return true;
	}
	
	private static boolean isPrime_(int number) {
		return number > 1 
				&& IntStream.range(2, Double.valueOf(Math.ceil(Math.sqrt(number))).intValue()).noneMatch(i -> (number % i == 0));
	}
	
	@Test
	public void testPrime() {
		for (int i = 0; i < 100; i++) {
			Assert.assertEquals(isPrime(i), isPrime_(i));
		}
		
		IntStream.range(1, 101).forEach((int i) -> {Assert.assertEquals(isPrime(i), isPrime_(i));});
	}
	
	@Test
	public void testFactor() {
		List<Integer> i3s = new ArrayList<Integer>();
		List<Integer> i5s = new ArrayList<Integer>();
		List<Integer> i7s = new ArrayList<Integer>();
		
		IntStream.range(1, 101).forEach((int i) -> {
			if (i % 3 == 0) {
				i3s.add(i);
			} else if (i % 5 == 0) {
				i5s.add(i);
			} else if (i % 7 == 0) {
				i7s.add(i);
			}
		});
		
		System.out.println("i % 3 == 0 " + i3s);
		System.out.println("i % 5 == 0 " + i5s);
		System.out.println("i % 7 == 0 " + i7s);
		
		List<Integer> i3_5s = new ArrayList<Integer>();
		
		IntPredicate p3 = (int i) -> {
			return i % 3 == 0;
		};
		IntPredicate p3_5 = p3.and((int i) -> {
			return i % 5 == 0;
		});
		IntStream.range(1, 101).filter(p3_5).forEach((int i) -> i3_5s.add(i));
		
		System.out.println("i % (5|3) == 0 " + i3_5s);
	}
	
	public static int sumWithCond(List<Integer> numbers, Predicate<Integer> p) {
		return numbers.parallelStream().filter(p).mapToInt(i -> i).sum();
	}
	
	@Test
	public void testSumWithComd() {
		List<Integer> i100 = new ArrayList<Integer>(); 
		IntStream.range(1, 101).forEach(i -> i100.add(i));
		
		System.out.println(sumWithCond(i100, n -> true));
		System.out.println(sumWithCond(i100, n -> n % 2 == 0));
		System.out.println(sumWithCond(i100, n -> n > 5));
	}
	
	static class Opers {
		public static boolean isOdd(int i) {
			return i % 2 != 0;
		}
		public static boolean isGreaterThan3(int i) {
			return i > 3;
		}
		public static boolean isLessThan11(int i) {
			return i < 11;
		}
	}
	
	@Test
	public void testStaticMethods() {
		int sum = IntStream.range(1, 101)
				  			.filter(Opers::isOdd)
				  			.filter(Opers::isGreaterThan3)
				  			.filter(Opers::isLessThan11)
				  			.max()
				  			.getAsInt();
		System.out.println(sum);
	}
}
