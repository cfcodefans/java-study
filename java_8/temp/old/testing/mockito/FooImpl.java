package cf.study.testing.mockito;

public class FooImpl implements IFoo {

	public FooImpl() {
		System.out.println("FooImpl created");
	}
	
	@Override
	public String getFoo() {
		System.out.println("FooImpl.getFoo");
		return this.toString();
	}

	public String toString() {
		return "I am real FooImpl: " + super.toString();
	}
}
