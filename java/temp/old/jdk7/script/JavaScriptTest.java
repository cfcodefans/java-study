package cf.study.jdk7.script;

import java.io.FileReader;
import java.util.Date;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;

import org.junit.Test;

public class JavaScriptTest {
	ScriptEngineManager factory = null;// new ScriptEngineManager();
	ScriptEngine engine = null; // factory.getEngineByName("JavaScript");

	public JavaScriptTest() {
		factory = new ScriptEngineManager();
		engine = factory.getEngineByName("JavaScript");
	}

	@Test
	public void helloWorld() throws Exception {
		engine.eval("print('Hello World');");
	}

	@Test
	public void executeFile() throws Exception {
		engine.eval(new FileReader("test.data/test.js"));
	}

	@Test
	public void var() throws Exception {
		engine.put("_date", new Date());
		engine.eval("print(_date);");
	}

	@Test
	public void callJsFunction() throws Exception {
		engine.eval("var foo = function(p) {return 'foo(' + p + ')';}");

		// javax.script.Invocable is an optional interface.
		// Check whether your script engine implements or not!
		// Note that the JavaScript engine implements Invocable interface.
		Invocable inv = (Invocable) engine;

		// invoke the global function named "hello"
		System.out.println(inv.invokeFunction("foo", "Scripting!!"));
		// invoke the global function named "hello"
		// inv.invokeFunction("bar", "Scripting!!" );
	}

	@Test
	public void callJsMethod() throws Exception {
		// JavaScript code in a String. This code defines a script object 'obj'
		// with one method called 'hello'.
		String script = "var obj = new Object(); obj.hello = function(name) { print('Hello, ' + name); }";
		// evaluate script
		engine.eval(script);

		// javax.script.Invocable is an optional interface.
		// Check whether your script engine implements or not!
		// Note that the JavaScript engine implements Invocable interface.
		Invocable inv = (Invocable) engine;

		// get script object on which we want to call the method
		Object obj = engine.get("obj");

		// invoke the method named "hello" on the script object "obj"
		inv.invokeMethod(obj, "hello", "Script Method !!");
	}

	@Test
	public void jsInterface() throws Exception {
		// JavaScript code in a String
		String script = "function run() { println('run called'); }";

		// evaluate script
		engine.eval(script);

		Invocable inv = (Invocable) engine;

		// get Runnable interface object from engine. This interface methods
		// are implemented by script functions with the matching name.
		Runnable r = inv.getInterface(Runnable.class);

		// start a new thread that runs the script implemented
		// runnable interface
		Thread th = new Thread(r);
		th.start();
		th.join();
	}

	@Test
	public void jsObjInterface() throws Exception {
		// JavaScript code in a String
		String script = "var obj = new Object(); obj.run = function() { println('run method called'); }";

		// evaluate script
		engine.eval(script);

		// get script object on which we want to implement the interface with
		Object obj = engine.get("obj");

		Invocable inv = (Invocable) engine;

		// get Runnable interface object from engine. This interface methods
		// are implemented by script methods of object 'obj'
		Runnable r = inv.getInterface(obj, Runnable.class);

		// start a new thread that runs the script implemented
		// runnable interface
		Thread th = new Thread(r);
		th.start();
		th.join();
	}
	
	@Test
	public void multiScopes() throws Exception {
		engine.put("x", "hello");
        // print global variable "x"
        engine.eval("println(x);");
        // the above line prints "hello"

        // Now, pass a different script context
        ScriptContext newContext = new SimpleScriptContext();
        Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);

        // add new variable "x" to the new engineScope
        engineScope.put("x", "world");

        // execute the same script - but this time pass a different script context
        engine.eval("println(x);", newContext);
        // the above line prints "world"
	}
	
	public static class Foo {
		public Object foo() {
			return new Foo();
		}
	}
	
	@Test
	public void callJavaFromJs() throws Exception {
		String script = "importPackage(cf.study.jdk7.script);" 
						+ "var foo = new JavaScriptTest.Foo();"
						+ "print(foo.foo());";
		
		engine.eval(script);
	}
}
