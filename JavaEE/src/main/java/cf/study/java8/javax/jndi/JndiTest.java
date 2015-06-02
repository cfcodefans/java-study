package cf.study.java8.javax.jndi;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;

import org.junit.Test;

public class JndiTest {

	public static void traverseContext(final Context ctx) throws Exception {
		if (ctx == null) {
			return;
		}
		
//		final NamingEnumeration<NameClassPair> list = ctx.list("/");
		final NamingEnumeration<Binding> list = ctx.listBindings("");
		while (list != null && list.hasMore()) {
			final Binding next = list.next();
			System.out.println(next);
			
			final Object _lookup = next.getObject();
			if (_lookup instanceof Context) {
				Context _ctx = (Context) _lookup;
				traverseContext(_ctx);
			}
		}
	}
	
	@Test
	public void example_1() throws Exception {
		
		//create an initial context
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
		Context ctx = new InitialContext(env);
		
		traverseContext(ctx);
		
		ctx.close();
	}
}
