package cf.study.java.javax.cdi.weld.beans;

import javax.inject.Named;

@Named
public interface IBizProcessor<P, R> {
	R proc(final P param);
}
