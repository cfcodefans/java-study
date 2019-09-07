package study.javax.cdi.weld;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

public class WeldTests {
    static final Logger log = LoggerFactory.getLogger(WeldTests.class);

    public static Weld weld;
    public static WeldContainer container;
    public static BeanManager bm;
    @SuppressWarnings("rawtypes")
    public static CDI _CDI;

    @BeforeAll
    public static void setUp() {
        try {
            weld = new Weld();
            container = weld.initialize();
            bm = container.getBeanManager();
            _CDI = CDI.current();
        } catch (final Exception e) {
            log.error("failed to initiate...", e);
            System.exit(-1);
        }
    }

}
