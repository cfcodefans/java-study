package cf.study.java8.javax.persistence.jpa.general;

import cf.study.java8.javax.persistence.cdi.TransactionalInterceptor;
import misc.Jsons;
import org.junit.*;

import javax.persistence.*;

/**
 * Created by fan on 2017/1/23.
 */
public class JpaOperTests {
    private static EntityManagerFactory emf = null;

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("hsqldb");
    }

    @AfterClass
    public static void tearDownClass() {
        emf.close();
    }

    public static EntityManager getEm() {
        EntityManager em = emf.createEntityManager();
        em.setFlushMode(FlushModeType.COMMIT);
        return em;
    }

    private EntityManager em = null;

    @Before
    public void setUp() {
        em = getEm();
        final EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
    }

    @After
    public void tearDown() {
        final EntityTransaction transaction = em.getTransaction();
        transaction.commit();
        em.close();
    }

    @Test
    public void testPersist() {
        SimpleTestEntity ste = new SimpleTestEntity("test");
        System.out.println(ste);

        Assert.assertFalse(em.contains(ste));
        em.persist(ste);
        Assert.assertTrue(em.contains(ste));

        System.out.println(ste);

        ste.setName("test again");
        em.persist(ste);
        Assert.assertTrue(em.contains(ste));
        System.out.println(ste);

        em.detach(ste);
        Assert.assertFalse(em.contains(ste));
        em.persist(ste);
    }

    @Test
    public void testPersistBind() {

            MasterEntity me = new MasterEntity("Master");
            WorkerEntity we = new WorkerEntity("Worker");
        {
            me.getWorkers().add(we);
            we.setMaster(me);

            me = em.merge(me);
            Assert.assertTrue(em.contains(me.getWorkers().iterator().next()));
            em.flush();
            em.refresh(me);
            System.out.println(me);
        }
        {
            String str = me.toString();
            System.out.println(str);
            MasterEntity _me = Jsons.read(str, MasterEntity.class);
            Assert.assertFalse(em.contains(_me));

            _me.setName("new Master");
            _me = em.merge(_me);
            System.out.println(_me);
            em.flush();
            Assert.assertTrue(em.contains(_me.getWorkers().iterator().next()));

            em.refresh(me);
            System.out.println(me);
        }
    }

    @Test
    public void testMerge() {
        SimpleTestEntity ste = new SimpleTestEntity("test");
        System.out.println(ste);

        Assert.assertFalse(em.contains(ste));
        ste = em.merge(ste);
        Assert.assertTrue(em.contains(ste));
        System.out.println(ste);

        ste.setName("test again");
        ste = em.merge(ste);
        Assert.assertTrue(em.contains(ste));
        System.out.println(ste);

        em.detach(ste);

        ste.setName("detached");
        ste = ste.clone();
        Assert.assertFalse(em.contains(ste));
        ste = em.merge(ste);
        Assert.assertTrue(em.contains(ste));
        System.out.println(ste);
    }

}
