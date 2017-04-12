package cf.study.misc.spi;


public class MathService implements IService {
	public Number service(Object...params) {
		Number n = 0;
		for (Object param : params) {
			Number pn = (Number)param;
			n = n.floatValue() + pn.floatValue();
		}
		return n.floatValue() / params.length;
	}
}
