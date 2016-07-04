package cf.study.framework.spring.boot;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloCtrl {
	@RequestMapping(path = { "/" })
	public String index() {
		return "Greetings from Spring Boot!";
	}
}
