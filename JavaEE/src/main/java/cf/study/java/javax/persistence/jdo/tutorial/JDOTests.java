package cf.study.java.javax.persistence.jdo.tutorial;

import misc.MiscUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import java.io.IOException;
import java.net.URL;

//TODO
//found no examples for JSON, fuck!
public class JDOTests {
    static Logger log = LogManager.getLogger(JDOTests.class);

    @Test
    public void testEnv() {
        String filePath = getPersistenceXmlPath();
        PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(filePath);

        pmf.close();
    }

    private static String getPersistenceXmlPath() {
        return JDOTests.class.getPackage().getName().replace('.', '/') + "/persistence.xml";
    }

    @Test
    public void testLoadPersistenceXML() {
        String persistenceXmlPath = getPersistenceXmlPath();
        System.out.println(persistenceXmlPath);
        String xml = MiscUtils.loadResAsString(JDOTests.class, persistenceXmlPath);
        log.info(xml);
    }

    @Test
    public void testLoadRes() throws IOException {
        String packagePath = JDOTests.class.getPackage().getName().replace('.', '/');
        String filePath = packagePath + "/persistence.xml";//"/" + JDOTests.class.getSimpleName() + ".class";
        log.info(filePath);
        ClassLoader cl = JDOTests.class.getClassLoader();
        URL resUrl = cl.getResource(filePath);
        log.info(resUrl);
        log.info(IOUtils.toString(cl.getResourceAsStream(filePath)));
    }
}
