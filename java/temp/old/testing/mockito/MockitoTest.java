package cf.study.testing.mockito;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

public class MockitoTest {
	@Test
	public void testHello() {
		System.out.println("test");
		
		List<Object> mockList = mockList();
		
		assertEquals(1, mockList.get(0));
		System.out.println("what are you? \n" + mockList);
		System.out.println(mockList.size());
		System.out.println(mockList.get(1));
		System.out.println(mockList.get(0));
	}

	private List<Object> mockList() {
		List<Object> mockList = Mockito.mock(List.class);
		Mockito.when(mockList.get(0)).thenReturn(1);
		return mockList;
	}
	
	@Test
	public void testIFoo() {
		System.out.println("test");
		
		IFoo foo = Mockito.mock(IFoo.class);
		Mockito.when(foo.getFoo()).thenReturn("mock.foo");
		
		assertEquals("mock.foo", foo.getFoo());
		System.out.println(foo);
	}
	
	@Test
	public void testFooImpl() {
		System.out.println("test");
		
		IFoo foo = Mockito.mock(FooImpl.class);
		Mockito.when(foo.getFoo()).thenReturn("mock.foo");
		
		assertEquals("mock.foo", foo.getFoo());
		System.out.println(foo);
	}
}
