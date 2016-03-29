package cf.study.framework.spring.script.groovy;

import java.util.Collection;

import org.springframework.stereotype.Service;

@Service
public interface IDepartmentService {
	Collection<Department> getAllDepts();
	Department getDepartment(long id);
}
