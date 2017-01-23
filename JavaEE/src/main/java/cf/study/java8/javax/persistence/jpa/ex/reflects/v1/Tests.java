package cf.study.java8.javax.persistence.jpa.ex.reflects.v1;

import cf.study.java8.javax.cdi.weld.WeldTest;
import cf.study.java8.lang.reflect.Reflects;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.*;

import javax.enterprise.inject.spi.Bean;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class Tests {

    static ReflectDao dao;

    @BeforeClass
    public static void setUp() {
        WeldTest.setUp();
        dao = WeldTest.getBean(ReflectDao.class);
    }

    @Before
    public void before() {
//		if (dao == null) {
//			dao = new ReflectDao();
//		}
//		
        dao.beginTransaction();
    }

    @After
    public void after() {
        dao.endTransaction();
    }

    @Test
    public void testCDI() {
//		ReflectDao rd = (ReflectDao) WeldTest.cdi.select(ReflectDao.class, UnboundLiteral.INSTANCE).get();
        Set<Bean<?>> beans = WeldTest.bm.getBeans(ReflectDao.class);
        Assert.assertTrue(CollectionUtils.isNotEmpty(beans));
        Assert.assertNotNull(WeldTest.getBean(ReflectDao.class));
//		
//		Bean<?> bean = beans.iterator().next();
//		Object ref = WeldTest.bm.getReference(bean, ReflectDao.class, WeldTest.bm.createCreationalContext(bean));
//		System.out.println(ref);
    }

    @Test
    public void testWithObject() {
        try {
            Class<?> clz = Object.class;
            dao.createClazz(clz);

            System.out.println("result: " + dao.queryCount("select count(be.id) from BaseEn be"));
            System.out.println(StringUtils.join(dao.queryEntity("select be from BaseEn be"), '\n'));
        } catch (Exception e) {
            e.printStackTrace();
            dao.setRollback();
        }
    }

    @Test
    public void testWithJUnit() throws Exception {
        String[] classPaths = StringUtils.split(SystemUtils.JAVA_CLASS_PATH, ';');

        Optional<String> opt = Stream.of(classPaths).filter((String str) -> {
            return str.contains("junit");
        }).findFirst();
        Assert.assertTrue(opt.isPresent());

        List<Class<?>> re = Reflects.loadClzzFromJar(new File(opt.get()), ClassLoader.getSystemClassLoader());
        System.out.println(StringUtils.join(re, '\n'));
        System.out.println(re.size());

        re.stream().forEach((cls) -> {
            dao.createClazz(cls);
        });
        System.out.println("result: " + dao.queryCount("select count(be.id) from BaseEn be"));
    }

    @Test
    public void testWithJUnitParallel() throws Exception {
        String[] classPaths = StringUtils.split(SystemUtils.JAVA_CLASS_PATH, ';');

        Optional<String> opt = Stream.of(classPaths).filter((String str) -> {
            return str.contains("junit");
        }).findFirst();
        Assert.assertTrue(opt.isPresent());

        List<Class<?>> re = Reflects.loadClzzFromJar(new File(opt.get()), ClassLoader.getSystemClassLoader());
        System.out.println(StringUtils.join(re, '\n'));
        System.out.println(re.size());

        re.stream().parallel().forEach((cls) -> {
            new Worker(cls).run();
        });
        System.out.println("result: " + dao.queryCount("select count(be.id) from BaseEn be"));
    }

    @Test
    public void testWithRuntime() throws Exception {
        File _f = new File(String.format("%s/lib/rt.jar", SystemUtils.JAVA_HOME));
        List<Class<?>> re = Reflects.loadClzzFromJar(_f, ClassLoader.getSystemClassLoader());
        System.out.println(StringUtils.join(re, '\n'));
        System.out.println(re.size());

        re.stream().forEach((cls) -> {
            dao.createClazz(cls);
        });
        System.out.println("result: " + dao.queryCount("select count(be.id) from BaseEn be"));
    }

    @Test
    public void testWithRuntimeParallel() throws Exception {
        File _f = new File(String.format("%s/lib/rt.jar", SystemUtils.JAVA_HOME));
        List<Class<?>> re = Reflects.loadClzzFromJar(_f, ClassLoader.getSystemClassLoader());
        System.out.println(StringUtils.join(re, '\n'));
        System.out.println(re.size());

        re.stream().parallel().forEach((cls) -> {
            dao.createClazz(cls);
        });
        System.out.println("result: " + dao.queryCount("select count(be.id) from BaseEn be"));
    }

    @AfterClass
    public static void tearDown() {

    }

    static class Worker implements Runnable {
        ReflectDao dao = WeldTest.getBean(ReflectDao.class);
        final Class<?> clz;
        public Worker(Class<?> _clz) {
            this.clz = _clz;
        }
        @Override
        public void run() {
            dao.createClazz(clz);
        }
    }
}
