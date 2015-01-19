package cf.study.java8.javax.cdi.weld.beans;

import javax.inject.Named;

@Named
public interface IBizProcessor<P, R> {
	R proc(final P param);
}
