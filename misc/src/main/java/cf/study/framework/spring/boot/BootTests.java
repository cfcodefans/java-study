package cf.study.framework.spring.boot;

import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = { "cf.study.framework.spring.boot" })
public class BootTests {

	private static final Logger log = LogManager.getLogger(BootTests.class);

	public static void main(String[] args) {
		ApplicationContext ac = SpringApplication.run(BootTests.class, args);

		log.info("inspect beans provided by Spring Boot");
		Stream.of(ac.getBeanDefinitionNames()).sorted().forEach(log::info);
	}
}
