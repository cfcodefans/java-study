package cf.study.framework.org.apache.shiro;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.Ini;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Test;


public class ShiroTests {

	static final Logger log = Logger.getLogger(ShiroTests.class);

	@Test
	public void quickStart() {
		log.info("My First Apache Shiro Application");
		
		Ini config = new Ini();
		config.load(ShiroTests.class.getResourceAsStream("shiro.ini"));
		Factory<SecurityManager> factory = new IniSecurityManagerFactory(config);
		
		SecurityUtils.setSecurityManager(factory.getInstance());
		
		Subject currentUser = SecurityUtils.getSubject();
		
		Session session = currentUser.getSession();
		
		session.setAttribute("someKey", "aValue");
		
		if (!currentUser.isAuthenticated()) {
			//collect user principals and credentials in a gui specific manner
			//such as username/password html form, x509 certificate, OpenID, etc.
			//We'll use the username/password example here since it is the most common.
			UsernamePasswordToken token = new UsernamePasswordToken("lonestarr", "vespa");
			//this is all you have to do to support 'remember me' (no config - built in!):
			token.setRememberMe(true);
			currentUser.login(token);
		}
		
		//print their identifying principals (in this case, a username):
		log.info(String.format("User [%s] logged in successfully.", currentUser.getPrincipal()));
		
		if (currentUser.hasRole("schwartz")) {
			log.info("May the Schwartz be with you!");
		} else {
			log.info("Hello, mere mortal.");
		}
		
		if (currentUser.isPermitted("lightsaber:weild")) {
			log.info("You may use a lightsaber ring. Use it wisely.");
		} else {
			log.info("Sorry, lightsaber rings are for schwartz masters only.");
		}
		
		if (currentUser.isPermitted("winnebago:drive:eagle5")) {
			log.info("You are permitted to 'drive' the 'winnebago' with license plate (id) 'eagle5'. "
					+ "Here are the keys - have fun!");
		} else {
			log.info("Sorry, you aren't allowed to drive the 'eagle5' winnebago!");
		}
		
		currentUser.logout();
	}
}
