package cf.study.java8.lang.annotation;

import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;

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
		@Creator(id = 5, who = TYPE_NAME)
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
		Stream.of(md.getParameters()).forEach(param -> {
			System.out.println(param.getAnnotation(ParamAt.class));
		});
	}

	@Test
	public void testPackageAnnotation() {
//		final PackageAt at = Dummy.class.getAnnotation(PackageAt.class);
//		System.out.println(at);
	}

	public static final String TYPE_NAME = "cf.study.java8.lang.annotation.AnnotationTests";

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface FamilyBudget {
		String userRole() default "GUEST";

		//~~Complete the interface~~
		int budget() default 0;
	}

	static class FamilyMember {
		//~~Complete this line~~
		@FamilyBudget(userRole = "SENIOR", budget = 100)
		public void seniorMember(int budget, int moneySpend) {
			System.out.println("Senior Member");
			System.out.println("Spend: " + moneySpend);
			System.out.println("Budget Left: " + (budget - moneySpend));
		}

		//~~Complete this line~~
		@FamilyBudget(userRole = "SENIOR", budget = 50)
		public void juniorUser(int budget, int moneySpend) {
			System.out.println("Junior Member");
			System.out.println("Spend: " + moneySpend);
			System.out.println("Budget Left: " + (budget - moneySpend));
		}
	}

	@Test
	public void testHackerrank() {
		Scanner in = new Scanner("3\n" +
			"SENIOR 75\n" +
			"JUNIOR 45\n" +
			"SENIOR 40");
		int testCases = Integer.parseInt(in.nextLine());
		while (testCases > 0) {
			String role = in.next();
			int spend = in.nextInt();
			try {
				Class annotatedClass = FamilyMember.class;
				Method[] methods = annotatedClass.getMethods();
				for (Method method : methods) {
					if (method.isAnnotationPresent(FamilyBudget.class)) {
						FamilyBudget family = method.getAnnotation(FamilyBudget.class);
						String userRole = family.userRole();
						int budgetLimit = family.budget();//~~Complete this line~~;
						if (userRole.equals(role)) {
							if(budgetLimit > spend){
								method.invoke(FamilyMember.class.newInstance(), budgetLimit, spend);
							}else{
								System.out.println("Budget Limit Over");
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			testCases--;
		}
	}

	public static void main(String[] args) {
		new AnnotationTests().testHackerrank();
	}
}
