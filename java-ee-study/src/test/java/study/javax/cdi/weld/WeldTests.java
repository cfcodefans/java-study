package study.javax.cdi.weld;

import org.jboss.weld.context.RequestContext;
import org.jboss.weld.context.unbound.UnboundLiteral;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import study.javax.cdi.weld.beans.AppBean;

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

    @AfterAll
    public static void tearDown() {
        if (weld != null)
            weld.shutdown();
    }

    @BeforeEach
    public void activateCtx() {
        RequestContext reqCtx = (RequestContext) _CDI.select(RequestContext.class, UnboundLiteral.INSTANCE).get();
        reqCtx.activate();
    }

    public static <T> T getBean(Class<T> cls) {
        return CDI.current().select(cls).get();
    }

    @Test
    public void testCDI() {
        AppBean app = getBean(AppBean.class);
        Assertions.assertNotNull(app);
        log.info("AppBean:\t{}", app);
    }

}
