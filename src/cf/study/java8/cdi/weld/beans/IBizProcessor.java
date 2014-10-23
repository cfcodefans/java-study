package cf.study.java8.cdi.weld.beans;

public interface IBizProcessor<P, R> {
	R proc(final P param);
}
