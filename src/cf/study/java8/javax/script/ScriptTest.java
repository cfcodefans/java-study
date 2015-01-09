package cf.study.java8.javax.script;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import junit.framework.Assert;
import misc.MiscUtils;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

public class ScriptTest {
	@Test
	public void testInvocable() throws Exception {
		ScriptEngineManager sem = new ScriptEngineManager();

		ScriptEngine se = sem.getEngineByExtension("js");

		Object evaluated = se.eval(MiscUtils.loadResAsString(ScriptTest.class, "invocable.js"));

		System.out.println(ToStringBuilder.reflectionToString(evaluated, ToStringStyle.MULTI_LINE_STYLE));

		Invocable inv = (Invocable) se;

		Runnable runnable = inv.getInterface(Runnable.class);

		System.out.println(runnable);
	}
	
	@Test
	public void testRunnable() throws Exception {
		ScriptEngineManager sem = new ScriptEngineManager();

		ScriptEngine se = sem.getEngineByExtension("js");

		Object evaluated = se.eval(MiscUtils.loadResAsString(ScriptTest.class, "runnable.js"));

		System.out.println(ToStringBuilder.reflectionToString(evaluated, ToStringStyle.MULTI_LINE_STYLE));

		Invocable inv = (Invocable) se;

		Runnable runnable = inv.getInterface(Runnable.class);

		System.out.println(runnable);
	}

	@Test
	public void testCompilable() throws Exception {
		ScriptEngineManager sem = new ScriptEngineManager();

		ScriptEngine se = sem.getEngineByExtension("js");

		Assert.assertTrue(se instanceof Compilable);

		Compilable cpl = (Compilable) se;

		String loadResAsString = MiscUtils.loadResAsString(ScriptTest.class, "pref.js");
		CompiledScript compiled = cpl.compile(loadResAsString);

		StopWatch sw = new StopWatch();

		for (int t = 0; t < 5; t++) {
			System.out.println();
			
			sw.reset();
			sw.start();
			for (int i = 0; i < 1000; i++) {
				compiled.eval();
			}
			sw.stop();
			System.out.println("compiled: " + sw.getTime());

			sw.reset();
			sw.start();
			for (int i = 0; i < 1000; i++) {
				se.eval(loadResAsString);
			}
			sw.stop();
			System.out.println("eval: " + sw.getTime());
			
			sw.reset();
			sw.start();
			for (int i = 0; i < 1000; i++) {
				for (int _i = 0; _i < 1000; _i++) {
					double re = Math.sqrt(_i);
				}
			}
			sw.stop();
			System.out.println("native: " + sw.getTime());
		}
	}

	@Test
	public void testCompiledInDifferentScriptEngine() throws Exception {
		ScriptEngineManager sem = new ScriptEngineManager();

		CompiledScript compiled = null;
		{
			ScriptEngine se = sem.getEngineByExtension("js");
			Compilable cpl = (Compilable) se;
			String loadResAsString = MiscUtils.loadResAsString(ScriptTest.class, "compiled.js");
			compiled = cpl.compile(loadResAsString);
		}
		
		Assert.assertNotNull(compiled);
		
		{
			ScriptEngine se = sem.getEngineByExtension("js");
			String loadResAsString = MiscUtils.loadResAsString(ScriptTest.class, "context.js");
			se.eval(loadResAsString);
			compiled.eval(se.getContext());
		}
		
		{
			ScriptEngine se = sem.getEngineByExtension("js");
			String loadResAsString = MiscUtils.loadResAsString(ScriptTest.class, "context_another.js");
			se.eval(loadResAsString);
			compiled.eval(se.getContext());
		}
		
//		ScriptEngine se1 = sem.getEngineByExtension("js");
//		String loadResAsString1 = MiscUtils.loadResAsString(ScriptTest.class, "compiled.js");
//		CompiledScript compiled1 = cpl.compile(loadResAsString1);
//		
//		compiled1.eval(se.getContext());
	}
}
