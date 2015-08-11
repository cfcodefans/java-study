package cf.study.data.misc;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import junit.framework.Assert;

import org.junit.Test;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;

public class DiffTests {
	@Test
	public void testDiff() {
		List<String> original = Stream.of(Month.values()).map(Month::name).collect(Collectors.toList());
		
		List<String> revised = new ArrayList<String>(original);
		revised.remove(Month.JULY.name());
		revised.remove(Month.JUNE.name());
		revised.remove(Month.JANUARY.name());
		Stream.of(DayOfWeek.values()).map(DayOfWeek::name).filter(d->d.startsWith("T")).forEach(revised::add);
		
		Patch<String> patch = DiffUtils.diff(original, revised);
		patch.getDeltas().stream().forEach(System.out::println);
		
		try {
			List<String> applyTo = patch.applyTo(original);
			Assert.assertEquals(applyTo, revised);
			List<String> restored = patch.restore(revised);
			Assert.assertEquals(restored, original);
		} catch (PatchFailedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testChanges() {
		List<String> original = Stream.of(Month.values()).map(Month::name).collect(Collectors.toList());
		List<String> revised = new ArrayList<String>(original);
		
		revised.set(0, "Jan");
		
		Patch<String> patch = DiffUtils.diff(original, revised);
		patch.getDeltas().stream().forEach(System.out::println);
		
		revised.remove(0);
		patch = DiffUtils.diff(original, revised);
		patch.getDeltas().stream().forEach(System.out::println);
	}
	
	@Test
	public void testSerialization() {
		List<String> original = Stream.of(Month.values()).map(Month::name).collect(Collectors.toList());
		List<String> revised = new ArrayList<String>(original);
		
		revised.set(0, "Jan");
		
		Patch<String> patch = DiffUtils.diff(original, revised);
		Delta<String> delta = patch.getDeltas().get(0);
	}
	
	
}
