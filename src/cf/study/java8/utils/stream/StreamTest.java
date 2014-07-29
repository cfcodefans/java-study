package cf.study.java8.utils.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

public class StreamTest {
	@Test
	public void example() {
		
		{//find out sum of all the integers greater than 5. 
			final List<Integer> intList = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5); 
				//Collections.addAll(new ArrayList<Integer>(), 3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
			
			//Prior to Java 8, it could be: 
			int sum1 = 0;
			for (final Integer iv : intList) {
				if (iv > 5) sum1 += iv;
			}
			
			int sum2 = intList.stream().filter(i -> (i > 5)).mapToInt(i -> i).sum();
			
			Assert.assertEquals(sum1, sum2);
		}
	}

	@Test
	public void createStream() {
		{
			Stream<Integer> intStream = Stream.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
			intStream.forEach(i -> (System.out.print(i + ", ")));
			System.out.println();
		}
		
		{
			Stream<Integer> intStream = Stream.of(new Integer[] { 3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5 });
			intStream.forEach(i -> (System.out.print(i + ", ")));
			System.out.println();
			
			{
				Stream<Integer> parallelIntStream = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5).parallelStream();
				parallelIntStream.forEach(i -> (System.out.print(i + ", ")));
				System.out.println();
			}
		}
		
		if (false) {
			Stream<String> strStream1 = Stream.generate(new Supplier<String>() {
				int i = 0;
				public String get() {
					i++;
					if (i > 10) return null;
					return "from a Supplier";
				}
			});
			
			strStream1.forEach(i -> (System.out.println(i)));
		}
		
		{
			String str = "abcdefg";
			str.chars().forEach(c -> {System.out.println((char)c);});
		}
		
		{
			String str1 = "abcdefg";
			String str2 = "abcdefg";
			
			IntStream stream1 = str1.chars();
			IntStream stream2 = str2.chars();
			
			IntStream concated = IntStream.concat(stream1, stream2);
			
			concated.forEach(c -> {System.out.print((char)c + ", ");});
		}
	}
	
	@Test
	public void toCollectionAndArray() {
		Stream<Integer> intStream = Stream.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
		List<Integer> intList = intStream.collect(Collectors.toList());
		System.out.println(intList);
		
		intStream = Stream.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
		Map intAndChars = intStream.distinct().collect(Collectors.toMap(i -> i, i -> (char)(97 + i)));  
		System.out.println(intAndChars);
		
		intStream = Stream.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
		Integer[] intArray = intStream.toArray(Integer[]::new);
		System.out.println(Arrays.toString(intArray));
	}
	
	@Test
	public void filter() {
		Stream<Integer> intStream = Stream.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
		int sum = intStream.filter(i -> (i % 2 == 0)).collect(Collectors.summingInt(i -> i));
		System.out.println(sum);
		
		sum = 0;
		for (final int i : new int[] {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5}) {
			if (i % 2 == 0) sum += i;
		}
		System.out.println(sum);
	}
	
	@Test
	public void map() {
		Stream<Integer> intStream = Stream.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
		intStream.map(i -> {return Math.abs(10 - i);}).forEach(i -> {System.out.print(i + ", ");});
	}
	
	@Test
	public void sorted() {
		Stream<Integer> intStream = Stream.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
		List<Integer> intList = intStream.sorted().collect(Collectors.toList());
		System.out.println(intList);
	}
	
	@Test
	public void flatMap() {
		Stream<List<String>> listStream = Stream.of(
					Arrays.asList("html", "javascript", "css"),
					Arrays.asList("servlet", "filter", "jsp"),
					Arrays.asList("ejb", "jms", "jpa")
				);
		
		Stream<String> flatStream = listStream.flatMap(strList -> strList.stream());
		flatStream.forEach(System.out::println);
	}
	
	@Test
	public void reduce() {
		Stream<Integer> intStream = Stream.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
		Optional<Integer> sum = intStream.reduce((_sum, i) -> (_sum += i));
		System.out.println(sum.get());
	}
	
	@Test
	public void count() {
		Stream<Integer> intStream = Stream.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
		System.out.println(intStream.count());
	}
	
	@Test
	public void match() {
		Stream<Integer> intStream = Stream.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
		
		System.out.println("even numbers: " + intStream.anyMatch(i -> (i % 2 == 0)));
		
		intStream = Stream.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
		System.out.println("even numbers: " + intStream.allMatch(i -> (i % 2 == 0)));
		
		intStream = Stream.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
		System.out.println("odd numbers: " + intStream.anyMatch(i -> (i % 2 == 1)));
		
		intStream = Stream.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
		System.out.println("odd numbers: " + intStream.noneMatch(i -> (i % 2 == 0)));
		
	}
}
