package cf.study.java8.javax.script;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import misc.MiscUtils;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;

public class InvocableTest {
	@Test
	public void testInvocable() throws Exception {
		ScriptEngineManager sem = new ScriptEngineManager();
		
		ScriptEngine se = sem.getEngineByExtension("js");
		
		Object evaluated = se.eval(MiscUtils.loadResAsString(InvocableTest.class, "invocable.js"));
		
		System.out.println(ToStringBuilder.reflectionToString(evaluated, ToStringStyle.MULTI_LINE_STYLE));
		
		Invocable inv = (Invocable)se;
		
		Runnable runnable = inv.getInterface(Runnable.class);
		
		System.out.println(runnable);
	}
}
