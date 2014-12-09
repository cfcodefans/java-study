package cf.study.java8.javax.script.javascript.nashorn;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;

public class NashornTests {
	static ScriptEngineManager sem = new ScriptEngineManager();
	static final String NASHORN = "nashorn";

	public static void prepare(ScriptEngine se) throws Exception {
		se.eval("function inspect(obj) { "
				+ "print(typeof(obj) + ': ' + obj); "
				+ "print('prototype: ' + obj.prototype);"
				+ "print('json: ' + JSON.stringify(obj)); "
				+ "print('length: ' + obj.length); "
				+ "for (var p in obj) {print(p + ': ' + obj[p]);} "
				+ "print();"
				+ "}");
		
	}
	
	@Test
	public void helloWorld() throws Exception {
		ScriptEngine se = sem.getEngineByName(NASHORN);
		
		se.eval("print('Hello, World!');");
	}
	
	@Test
	public void whatIsPackages() throws Exception {
		ScriptEngine se = sem.getEngineByName(NASHORN);
		final Object _packages = se.eval("Packages");
		
		/*
		 * jdk.nashorn.api.scripting.ScriptObjectMirror@4d49af10[
				  sobj=[JavaPackage ]
				  global=jdk.nashorn.internal.objects.Global@5e25a92e
				  strict=false
				]
		 */
		System.out.println(ToStringBuilder.reflectionToString(_packages, ToStringStyle.MULTI_LINE_STYLE));

		
		se.eval("print(typeof(Packages));");
		
		//{"toJSON":{"toJSON":{"toJSON":{"toJSON":{"toJSON":{"toJSON":{"toJSON":{"toJSON":{}}}}}}}}}
		se.eval("print(JSON.stringify(Packages));");
		
		//toJSON: [object JavaPackage]
		se.eval("for (var p in Packages) {print(p + ': ' + Packages[p]);}");
	}
	
	@Test
	public void whatAreInPackages() throws Exception {
		ScriptEngine se = sem.getEngineByName(NASHORN);
		System.out.println(ToStringBuilder.reflectionToString(se.eval("Packages.java.lang"), ToStringStyle.MULTI_LINE_STYLE));
		se.eval("inspect(Packages.java.lang);");
	}
	
	@Test
	public void whatIsJava() throws Exception {
		ScriptEngine se = sem.getEngineByName(NASHORN);
		prepare(se);
		System.out.println(ToStringBuilder.reflectionToString(se.eval("Packages.java.lang"), ToStringStyle.MULTI_LINE_STYLE));
		se.eval("inspect(Java)");
	}
	
	@Test
	public void whatIsJavaClassLike() throws Exception {
		ScriptEngine se = sem.getEngineByName(NASHORN);
		prepare(se);
		
		se.eval("inspect(java.lang.System);");
		se.eval("inspect(java.lang.String);");
		se.eval("inspect(java.lang.Integer);");
	}
	
	@Test
	public void whatIsJavaObjectLike() throws Exception {
		ScriptEngine se = sem.getEngineByName(NASHORN);
		prepare(se);
		
		se.eval("inspect(java.lang.System);");
		se.eval("inspect(java.lang.String);");
		se.eval("inspect(java.lang.Integer);");
	}
	
	@Test
	public void createJavaObject() throws Exception {
		ScriptEngine se = sem.getEngineByName(NASHORN);
		prepare(se);
		
		se.eval("var Integer = Java.type('java.lang.Integer');inspect(new Integer(15));");
		se.eval("var Long = Java.type('java.lang.Long');inspect(new Long(15));");
		se.eval("var Short = Java.type('java.lang.Short');inspect(new Short(15));");
		se.eval("var Float = Java.type('java.lang.Float');inspect(new Float(15.51));");
		se.eval("var Double = Java.type('java.lang.Double');inspect(new Double(15.51));");
		
		//String is converted into javascript string type
		se.eval("var _String = Java.type('java.lang.String');inspect(new _String(\"Hello, String!\"));");
	}
	
	@Test
	public void accessClassMembers() throws Exception {
		ScriptEngine se = sem.getEngineByName(NASHORN);
		prepare(se);
		
		System.out.println("class member and methods\n");
		
		se.eval("inspect(Java.type(\"java.lang.Math\").PI);");
		se.eval("inspect(Java.type(\"java.lang.System\").currentTimeMillis());");
		
		System.out.println("inner class\n");
		se.eval("inspect(Java.type(\"java.util.Map\").Entry);");
		se.eval("inspect(Java.type(\"java.util.Map.Entry\"));");
	}
	
	public static class TestBean {
		public int publicMember;
		private int privateMember;
		
		public TestBean(int publicMember, int privateMember) {
			super();
			this.publicMember = publicMember;
			this.privateMember = privateMember;
			System.out.println(this);
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("{class:\"TestBean\", publicMember:'").append(publicMember).append("', privateMember:'").append(privateMember).append("'}");
			return builder.toString();
		}

		public TestBean() {
			System.out.println(this);
		}

		public int getPrivateMember() {
			return privateMember;
		}

		public void setPrivateMember(int _privateMember) {
			System.out.println("setPrivateMember(" + _privateMember + ")");
			this.privateMember = _privateMember;
		}
		
		public void setPrivateMember(double value) {
			privateMember = (int) Math.ceil(value);
		}
	}
	
	@Test
	public void accessInstanceMembers() throws Exception {
		ScriptEngine se = sem.getEngineByName(NASHORN);
		prepare(se);
		
		se.eval("Java.type(\"java.lang.System\").out.println('java.lang.System.out.println');");
		se.eval("Java.type(\"java.lang.System\").out.println(100);");
		se.eval("Java.type(\"java.lang.System\").out.println(100.001);");
		
		se.eval("var TestBean = Java.type(\"cf.study.java8.javax.script.javascript.nashorn.NashornTests.TestBean\"); "
				+ "var _bean = new TestBean(), _bean1 = new TestBean(15, 20); "
				+ "inspect(_bean1);"
				+ "inspect(_bean1.publicMember);"
				+ "inspect(_bean1.privateMember);"
				+ "_bean1.publicMember = 16;"
				+ "print(_bean1);"
				+ "_bean1.publicMember++;"
				+ "print(_bean1); "
				+ "_bean1.privateMember = 24; "
				+ "print(_bean1); "
				+ "_bean1.setPrivateMember(25); "
				+ "print(_bean1); "
				+ "_bean1.privateMember--; "
				+ "print(_bean1); "
				+ "_bean1[\"setPrivateMember(double)\"](21.5); "
				+ "print(_bean1)");
		
	}
	
	@Test public void workWithJavaArrays() throws Exception {
		ScriptEngine se = sem.getEngineByName(NASHORN);
		prepare(se);
		
		se.eval("inspect(Java.type(\"int[]\"));");
		se.eval("inspect(Java.type(\"long[]\"));");
		se.eval("inspect(Java.type(\"float[]\"));");
		se.eval("inspect(Java.type(\"java.lang.String[]\"));");
	}
	
}
