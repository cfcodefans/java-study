package cf.study.framework.spring.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import misc.MiscUtils;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import cf.study.framework.spring.script.groovy.Employee;
import cf.study.framework.spring.script.groovy.IEmployeeCacheService;

@Service
public class EmployeeCacheService implements IEmployeeCacheService {
	private Map<Long, Employee>	cache	= new ConcurrentHashMap<>();
	static final Logger			log		= Logger.getLogger(EmployeeCacheService.class);

	public void addEmployeeToCache(Employee employee) {
		getCache().put(employee.getId(), employee);
		log.info(MiscUtils.invocationInfo() + "\n" + employee);
	}

	public void removeEmployeeFromCache(Employee employee) {
		getCache().remove(employee.getId());
		log.info(MiscUtils.invocationInfo() + "\n" + employee);
	}

	public Employee getEmployee(long id) {
		Employee employee = getCache().get(id);
		log.info(MiscUtils.invocationInfo() + "\n" + employee);
		return employee;
	}

	public Map<Long, Employee> getCache() {
		return cache;
	}

	public void setCache(Map<Long, Employee> cache) {
		this.cache = cache;
	}

}
