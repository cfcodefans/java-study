package cf.study.java8.javax.script.javascript.rhino;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;

public class JSTests {
	
	public static Logger log = Logger.getLogger(JSTests.class.getSimpleName());
	
	private static final ScriptEngineManager sem = new ScriptEngineManager();
	
	public static String getScript(final String fileName) {
		try {
			return IOUtils.toString(JSTests.class.getResourceAsStream(fileName));
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		return StringUtils.EMPTY;
	}
	
	public static Object runScript(final String scriptStr) throws Exception {
		final ScriptEngine se = sem.getEngineByName("Nashorn");
		return se.eval(scriptStr);
	}
	
	@Test
	public void control() throws Exception {
		Context cx = Context.enter();
		
		//Set version to Javascript1.2 so that we get object-literal style
		// printing instead of "[object Object]"
		cx.setLanguageVersion(Context.VERSION_1_2);
		
		//Initialize the standard objects (Object, Function, etc.)
		//This must be done before scripts can be executed.
		Scriptable scope = cx.initStandardObjects();
		
		//Now we can evaluate a script. Let's create a new Object 
		//using the object literal notation
		Object result = cx.evaluateString(scope, "obj = {a:1, b:['x','y']}", "MySource", 1, null);
		
		Scriptable obj = (Scriptable) scope.get("obj", scope);
		 
		// Should print "obj == result" (Since the result of an assignment
		// expression is the value that was assigned
		System.out.println(String.format("obj %s result", obj == result ? "==" : "!="));
		
		// Should print "obj.a == 1"
		System.out.println("obj.a == " + obj.get("a", obj));
		
		Scriptable b = (Scriptable) obj.get("b", obj);
		
		//Should print "obj.b[0] == x"
		System.out.println("obj.b[0] == " + b.get(0, b));
		
		//Should print "obj.b[1] == y"
		System.out.println("obj.b[1] == " + b.get(1, b));
		
		// Should print {a:1, b:["x", "y"]}
		Function fn = (Function) ScriptableObject.getProperty(obj, "toString");
		System.out.println(fn.call(cx, scope, obj, new Object[0]));
		
		Context.exit();
	}
	
	public static class Counter extends ScriptableObject {
		private static final long serialVersionUID = 1L;

		@Override
		public String getClassName() {
			return Counter.class.getSimpleName();
		}
		
		// The zero-argument constructor used by Rhino runtimes to create instances
		public Counter() {}
		
		//@JSConstructor annotation defines the JavaScript constructor
		@JSConstructor
		public Counter(int a) { count = a;}
		
		// The method getCount defines the count property
		@JSGetter
		public int getCount() {
			return count++;
		}
		
		// Methods can be defined the @JSFunction annotation.
		// Here we define resetCount for JavaScript
		@JSFunction
		public void resetCount() { count = 0; }
		
		private int count;
	}
	
	/**
	 * An example illustrating how to create a JavaScript object and retrieve
	 * properties and call methods.
	 * <p>
	 * Output should be:
	 * <pre>
	 * count = 0
	 * count = 1
	 * resetCount
	 * count = 0
	 * </pre>
	 */
	@Test 
	public void counter() throws Exception {
		Context cx = Context.enter();
		try {
			Scriptable scope = cx.initStandardObjects();
			ScriptableObject.defineClass(scope, Counter.class);
			
			Scriptable testCounter = cx.newObject(scope, "Counter");
			
			Object count = ScriptableObject.getProperty(testCounter, "count");
			System.out.println("count = " + count);
			
			count = ScriptableObject.getProperty(testCounter, "count");
			System.out.println("count = " + count);
			
			ScriptableObject.callMethod(testCounter, "resetCount", new Object[0]);
			System.out.println("resetCount");
			
			count = ScriptableObject.getProperty(testCounter, "count");
			System.out.println("count = " + count);
		} finally {
			Context.exit();
		}
	}
	
	public static class MyFactory extends ContextFactory {
		public static boolean useDynamicScope;
		protected boolean hasFeature(Context cx, int featureIndex) {
			if (featureIndex == Context.FEATURE_DYNAMIC_SCOPE) {
				return useDynamicScope;
			}
			return super.hasFeature(cx, featureIndex);
		}
		
		static {
			ContextFactory.initGlobal(new MyFactory());
		}
		
		public static void runScripts(Context cx, Script script) {
			//Initialize the standard objects (Object, Function, etc.)
			//This must be done before scripts can be executed. The call
			//returns a new scope that we will share.
			ScriptableObject sharedScope = cx.initStandardObjects(null, true);
			
			//Now we can execute the precompiled script against the scope
			//to define x variable and f function in the shared scope
			script.exec(cx, sharedScope);
			
	        // Now we spawn some threads that execute a script that calls the
	        // function 'f'. The scope chain looks like this:
	        // <pre>
	        //            ------------------                ------------------
	        //           | per-thread scope | -prototype-> |   shared scope   |
	        //            ------------------                ------------------
	        //                    ^
	        //                    |
	        //               parentScope
	        //                    |
	        //            ------------------
	        //           | f's activation   |
	        //            ------------------
	        // </pre>
	        // Both the shared scope and the per-thread scope have variables 'x'
	        // defined in them. If 'f' is compiled with dynamic scope enabled,
	        // the 'x' from the per-thread scope will be used. Otherwise, the 'x'
	        // from the shared scope will be used. The 'x' defined in 'g' (which
	        // calls 'f') should not be seen by 'f'.
			final int threadCount = 3;
			Thread[] t = new Thread[threadCount];
			String scriptStr = getScript("thread_scope.js");
			for (int i = 0; i < threadCount; i++) {
				t[i] = new Thread(new PerThread(sharedScope, scriptStr, "thread" + i));
			}
			
			for (Thread _t : t) {
				_t.start();
			}
			
			//Don't return in this thread until all the spawned threads have 
			//completed.
			for (Thread _t : t) {
				try {
					_t.join();
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
	/**
    * Main entry point.
    *
    * Set up the shared scope and then spawn new threads that execute
    * relative to that shared scope. Try to run functions with and
    * without dynamic scope to see the effect.
    *
    * The expected output is
    * <pre>
    * sharedScope
    * nested:sharedScope
    * sharedScope
    * nested:sharedScope
    * sharedScope
    * nested:sharedScope
    * thread0
    * nested:thread0
    * thread1
    * nested:thread1
    * thread2
    * nested:thread2
    * </pre>
    * The final three lines may be permuted in any order depending on
    *  thread scheduling.
    */
	@Test
	public void testDynamicScope() throws Exception {
		Context cx = Context.enter();
		try {
			//Precompile souce only once
			String source = getScript("dynamic.js");
			Script script = cx.compileString(source, "sharedScript", 1, null);
			
			MyFactory.useDynamicScope = false;
			MyFactory.runScripts(cx, script);
			MyFactory.useDynamicScope = true;
			MyFactory.runScripts(cx, script);
		} finally {
			Context.exit();
		}
	}
	
	private static class PerThread implements Runnable {

		@Override
		public void run() {
			//We need a new Context for this thread.
			Context cx = Context.enter();
			try {
				//We can share the scope.
				Scriptable threadScope = cx.newObject(sharedScope);
				threadScope.setPrototype(sharedScope);
				
				// We want "threadScope" to be a new top-level
				// scope, so set its parent scope to null. This means
				// that any variables created by assignments
				// will be properties of "threadScope"
				threadScope.setParentScope(null);
				
				//Create a Javascript property of the thread scope named
				//'x' and save a value for it.
				threadScope.put("x", threadScope, x);
				cx.evaluateString(threadScope, source, "threadScript", 1, null);
				
			} finally {
				Context.exit();
			}
		}
		
		private Scriptable sharedScope;
		private String source;
		private String x;
		public PerThread(Scriptable sharedScope, String source, String x) {
			super();
			this.sharedScope = sharedScope;
			this.source = source;
			this.x = x;
		}
	}
	
	@Test
	public void fooTest() throws Exception {
		JSTests.runScript(getScript("foo.js"));
	}
	
	@Test
	public void wrapFactoryTest() throws Exception {
//		RhinoTests.runScript(getScript("wrap_factory.js"));
		String scriptStr = getScript("wrap_factory.js");
		
		final ScriptEngine se = sem.getEngineByName("Nashorn");
		se.eval(scriptStr);
	}
}
