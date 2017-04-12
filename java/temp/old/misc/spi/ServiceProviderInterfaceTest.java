package cf.study.misc.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.junit.Test;

public class ServiceProviderInterfaceTest {
	@Test
	public void testServiceLoader() {
		ServiceLoader<IService> sl = ServiceLoader.load(IService.class);
		for (Iterator<IService> sIt = sl.iterator(); sIt.hasNext();) {
			IService s = sIt.next();
			System.out.println(s.getClass().getName());
		}
	}
}
