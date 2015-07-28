package cf.study.oo.dynamic.cglib;

public interface IDao<T> {
	String getId(T entity);
	String getProperty(String propertyName, T entity);
}
