package cf.study.java.utils;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.junit.Test;

public class OptionalTests {

	@SuppressWarnings("deprecation")
	@Test
	public void testOptional() {
		{
			final Optional<Date> dateOpt = Optional.of(new Date());

			final Predicate<? super Date> springPred = (_d) -> (IntStream.of(2, 3, 4).anyMatch((i) -> (i == _d.getMonth())));
			final Consumer<? super Date> springPrint = (_d) -> System.out.println("spring");
			
			final Predicate<? super Date> winterPred = (_d) -> (IntStream.of(10, 11, 12).anyMatch((i) -> (i == _d.getMonth())));
			final Consumer<? super Date> winterPrint = (_d) -> System.out.println("winter");

			dateOpt.filter(springPred).ifPresent(springPrint);
			dateOpt.filter(winterPred).ifPresent(winterPrint);
		}
		
		{	
			final Date d = new Date(); 
			if (Arrays.asList(1,2,3).contains(d.getMonth())) {
				System.out.println("spring");
			} else if (Arrays.asList(10, 11, 12).contains(d.getMonth())) {
				System.out.println("winter");
			} 
		}
	}
}
