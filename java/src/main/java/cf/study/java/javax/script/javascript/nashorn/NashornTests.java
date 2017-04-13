package cf.study.java.javax.script.javascript.nashorn;

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
				+ "print(typeof(obj) + ': \t' + obj); "
				+ "print('class: \t' + obj.class); "
				+ "print('prototype: \t' + obj.prototype); "
				+ "print('constructor: \t' + obj.constructor); "
				+ "print('json: \t' + JSON.stringify(obj)); "
				+ "print('length: \t' + obj.length); "
				+ "for (var p in obj) {print(p + ': \t' + obj[p]);} "
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
		
		se.eval("var TestBean = Java.type(\"cf.study.java.javax.script.javascript.nashorn.NashornTests.TestBean\"); "
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
	
	@Test 
	public void workWithJavaArrays() throws Exception {
		ScriptEngine se = sem.getEngineByName(NASHORN);
		prepare(se);
		
		se.eval("inspect(Java.type(\"int[]\"));");
		se.eval("inspect(Java.type(\"long[]\"));");
		se.eval("inspect(Java.type(\"float[]\"));");
		se.eval("inspect(Java.type(\"java.lang.String[]\"));");
		
		se.eval("var IntArrayType = Java.type(\"int[]\");"
				+ "var iArr = new IntArrayType(10); "
				+ "inspect(iArr); "
				+ "iArr[1] = 123; "
				+ "iArr[2] = 321; "
				+ "print(iArr[1] + iArr[2]); ");
		
		se.eval("var jsArr = ['a', 'b', 'c']; "
				+ "inspect(jsArr); "
				+ "var StrArrType = Java.type('java.lang.String[]'); "
				+ "var javaStrArr =  Java.to(jsArr, StrArrType); "
				+ "inspect(javaStrArr); "
				+ "\n "
//				+ "var _jsArr = Java.to(javaStrArr, Array); " //can't work reversely
//				+ "inspect(_jsArr); "
				+ " ");
		
		se.eval("for (var i = 0, j = iArr.length; i < j; i++) { "
				+ "print(i + ':\t' + iArr[i]); }");
		
		se.eval("for (var i in iArr) { "
				+ "print(iArr[i]); }");
		
	}
	
	@Test
	public void workWithJavaString() throws Exception {
		ScriptEngine se = sem.getEngineByName(NASHORN);
		prepare(se);
		
		se.eval("var a = \"abc\"; "
				+ "inspect(a); "
				+ "var b = a + \"def\"; "
				+ "inspect(b); "
				+ "var c = String(b); "
				+ "inspect(c); ");
	}
	
	@Test
	public void workWithNumbers() throws Exception {
		ScriptEngine se = sem.getEngineByName(NASHORN);
		prepare(se);
		
		se.eval("var intNum = 10; "
				+ "inspect(intNum); "
				+ "var dblNum = Number(intNum); "
				+ "inspect(dblNum); ");
	}
	
	@Test
	public void workWithJavaCollections() throws Exception {
		ScriptEngine se = sem.getEngineByName(NASHORN);
		prepare(se);
		
		se.eval("var ArrayList = Java.type(\"java.util.ArrayList\"); "
				+ "var aList = new ArrayList(); "
				+ "inspect(aList); "
				+ "aList.add(\"a\"); "
				//+ "aList.push(\"b\"); " //though the ArrayList is treated as Array in Javascript, but it doesn't have Javascript Array's methods like push.....
				+ "aList.add(\"c\"); "
				+ "print(aList[1]); "
				+ "for each (var e in aList) { "
				+ "print(e);} print(); ");
		
		se.eval("var HashSet = Java.type(\"java.util.HashSet\"); "
				+ "var _set = new HashSet(); "
				+ "_set.add(\"a\"); "
				+ "_set.add(\"b\"); "
				+ "_set.add(\"c\"); "
				+ "_set.add(\"a\"); "
				+ "print(_set[2]); " //though the Set is treated as Array in Javascript, but it doesn't have index like Array
				+ "inspect(_set); "
				+ "for (var i in _set) {print(i);} "
				+ "print(); "
				+ "for each (var i in _set) {print(i);} "
				+ "print(); "
				+ " ");
		
		se.eval("var HashMap = Java.type(\"java.util.HashMap\"); "
				+ "var _map = new HashMap(); "
				+ "_map.put(\"name\", \"fan\"); "
				+ "_map.put(\"height\", 1.75); "
				+ "_map.put(\"weight\", 80); "
				+ "print(_map[\"name\"]); " //This could work, but it isn't an Object
				+ "inspect(_map); "); //
	}
	
	@Test
	public void forInAndForEachIn() throws Exception {
		ScriptEngine se = sem.getEngineByName(NASHORN);
		prepare(se);
		
		se.eval(" var arr = ['a', 'b', 'c', 'd', 'e']; "
				+ "for (var i in arr) {print(i);} "
				+ "print(); "
				+ "for each (var i in arr) {print(i);} "
				+ "print(); ");
		
		se.eval(" var point = {x: 100, y:200, z: 200}; "
				+ "for (var i in point) {print(i);} "
				+ "print(); "
				+ "for each (var i in point) {print(i);} ");
		
	}
	
	@Test
	public void inheritance() throws Exception {
		ScriptEngine se = sem.getEngineByName(NASHORN);
		prepare(se);
		
		Object obj = se.eval("var TestBean = Java.type(\"cf.study.java.javax.script.javascript.nashorn.NashornTests.TestBean\"); \n "
				+ " var JsTestBean = Java.extend(TestBean, { "
				+ " jsObjMember : {}, "
				+ " jsNumMember : 17, "
				+ " jsArrMember : [], "
				+ " jsMethodCallSuper : function() { "
				+ " var _super_ = Java.super(this); "
				+ " print('super.getPrivateMember: \t' + _super_.getPrivateMember()); "
				+ " }, "
				+ " toString : function() {return JSON.stringify(this);}, "
				+ " jsMethodAccessSuper : function() {print('super.publicMember: \t' + this.publicMember);} "
				+ " }); \n "
				+ " inspect(JsTestBean); "
				+ " var jsTestBean = new JsTestBean(); \n "
				//+ " print(typeof(jsTestBean.jsMethodCallSuper)); \n " //jsMethodCallSuper disappears!
				+ " jsTestBean.jsMethodCallSuper = function() { "
				+ " var _super_ = Java.super(this); "
				+ " print('super.getPrivateMember: \t' + _super_.getPrivateMember()); "
				+ " }; \n"
				+ " print(typeof(jsTestBean.jsMethodCallSuper)); "
				//+ " print(\"super.getPrivateMember: \" + jsTestBean.jsMethodCallSuper()); \n "
				+ " inspect(jsTestBean); "
				+ " jsTestBean;");
		
		System.out.println(obj);
		System.out.println(obj.getClass());
		System.out.println((obj instanceof TestBean));
	}
	
	@Test
	public void implementsInterface() throws Exception {
		ScriptEngine se = sem.getEngineByName(NASHORN);
		prepare(se);
		
		// Create an object that implements the Runnable interface by implementing
		// the run() method as a JavaScript function
		se.eval("var r  = new java.lang.Runnable() { "
		    + "run: function() { print('running...');} "
			+ "};");

		// The r variable can be passed to Java methods that expect an object implementing
		// the java.lang.Runnable interface
		se.eval("var th = new java.lang.Thread(r); "
				+ " th.start(); "
				+ " th.join(); ");
		
		// Define a JavaScript function
		se.eval("function func() {print(\"I am func!\");};");
		// Pass the JavaScript function instead of an object that implements
		// the java.lang.Runnable interface
		se.eval("var th = new java.lang.Thread(func); "
				+ " th.start(); "
				+ " th.join(); ");
	}
	
	@Test
	public void extendsAbstractClass() throws Exception {
		ScriptEngine se = sem.getEngineByName(NASHORN);
		prepare(se);
		
		se.eval("var TimerTask =  Java.type(\"java.util.TimerTask\");");
		se.eval("var task = new TimerTask({ run: function() { print(\"Hello World!\") } });");
		se.eval("var Timer = Java.type(\"java.util.Timer\");");
		se.eval("var timer = new Timer();");
		se.eval("timer.schedule(task, 1);");
		
	}
	
	
}
