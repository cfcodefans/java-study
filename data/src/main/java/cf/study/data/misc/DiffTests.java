package cf.study.data.misc;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Assert;
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
	
	@Test
	public void testSplitDiff() {
		List<String> original = new ArrayList<String>();
		IntStream.range(0, 9).forEach(i->original.add(String.valueOf(i)));
		
		
		System.out.println(original);
		
		List<String> revised = new ArrayList<String>(original);
		
		revised.set(2, "a");
		revised.remove(4);
		revised.add(6, "b");
		revised.add(8, "c");
		
		System.out.println(revised);
		
		Patch<String> patch = DiffUtils.diff(original, revised);
		patch.getDeltas().stream().forEach(System.out::println);
		
		System.out.println();
		
		
		List<String> fragment1 = original.subList(0, 5);
		List<String> fragment2 = original.subList(5, 9);
		System.out.println(fragment1);
		System.out.println(fragment2);
		
		List<String> _fragment1 = new ArrayList<String>(fragment1);
		List<String> _fragment2 = new ArrayList<String>(fragment2);
		
		_fragment1.set(2, "a");
		_fragment1.remove(4);
		_fragment2.add(6 - 5, "b");
		_fragment2.add(8 - 5, "c");
		
		System.out.println(_fragment1);
		System.out.println(_fragment2);
		
		Patch<String> _patch1 = DiffUtils.diff(fragment1, _fragment1);
		_patch1.getDeltas().stream().forEach(System.out::println);
		Patch<String> _patch2 = DiffUtils.diff(fragment2, _fragment2);
		_patch2.getDeltas().stream().forEach(System.out::println);
	}
}
