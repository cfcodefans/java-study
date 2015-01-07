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
	public void testCompilable() throws Exception {
		ScriptEngineManager sem = new ScriptEngineManager();

		ScriptEngine se = sem.getEngineByExtension("js");

		Assert.assertTrue(se instanceof Compilable);

		Compilable cpl = (Compilable) se;

		String loadResAsString = MiscUtils.loadResAsString(ScriptTest.class, "invocable.js");
		CompiledScript compiled = cpl.compile(loadResAsString);

		StopWatch sw = new StopWatch();

		for (int t = 0; t < 5; t++) {
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

}
