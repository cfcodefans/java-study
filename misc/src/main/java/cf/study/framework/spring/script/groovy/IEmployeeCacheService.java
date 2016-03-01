package cf.study.framework.spring.script.groovy;

public interface IEmployeeCacheService {
	void addEmployeeToCache(Employee employee);

	void removeEmployeeFromCache(Employee employee);

	Employee getEmployee(long id);
}
