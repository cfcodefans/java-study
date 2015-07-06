package cf.study.java8.lang.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

//import cf.study.java8.lang.annotation.test.Dummy;


public class AnnotationTests {
	
	@Test
	public void inheiredAnnotation() {
		Shape s = new Shape();
		{
			final TypeAnnotation at = s.getClass().getAnnotation(TypeAnnotation.class);
			System.out.println(at);
			
			final TypeAnnotation[] ats = s.getClass().getAnnotationsByType(TypeAnnotation.class);
			System.out.println(Arrays.toString(ats));
			System.out.println();
		}
		
		
		Shape r = new Rectangle();
		{
			final TypeAnnotation at = r.getClass().getAnnotation(TypeAnnotation.class);
			System.out.println(at);
			
			final TypeAnnotation[] ats = r.getClass().getAnnotationsByType(TypeAnnotation.class);
			System.out.println(Arrays.toString(ats));
			System.out.println();
		}
		
		Shape p = new Point();
		{
			final TypeAnnotation at = p.getClass().getAnnotation(TypeAnnotation.class);
			System.out.println(at);
			
			final TypeAnnotation[] ats = p.getClass().getAnnotationsByType(TypeAnnotation.class);
			System.out.println(Arrays.toString(ats));
			System.out.println();
		}
	}
	
	/**
	 * Can't get LOCAL_VARIABLE annotation by reflection in runtime
	 */
	@Test
	public void testLocalVariable() {
		@Creator(id=5, who=TYPE_NAME)
		Shape r = new Rectangle();
		
		final Creator at = r.getClass().getAnnotation(Creator.class);
		System.out.println(at);
	}
	
	@Test
	public void testMethodAnnotation() throws Exception {
		final Method md = Rectangle.class.getMethod("draw");
		Assert.assertNotNull(md);
		final MethodAt mdAt = md.getAnnotation(MethodAt.class);
		System.out.println(mdAt);
	}
	
	@Test
	public void testFieldAnnotation() throws Exception {
		final Field fd = Rectangle.class.getField("start");
		Assert.assertNotNull(fd);
		final FieldAt fieldAt = fd.getAnnotation(FieldAt.class);
		System.out.println(fieldAt);
	}
	
	@Test
	public void testParamAnnotation() throws Exception {
		final Method md = Rectangle.class.getMethod("draw", IDrawable.class);
		Assert.assertNotNull(md);
		Stream.of(md.getParameters()).forEach(param->{System.out.println(param.getAnnotation(ParamAt.class));});
	}
	
	@Test
	public void testPackageAnnotation() {
//		final PackageAt at = Dummy.class.getAnnotation(PackageAt.class);
//		System.out.println(at);
	}

	public static final String TYPE_NAME = "cf.study.java8.lang.annotation.AnnotationTests";
}
