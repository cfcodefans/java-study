package study.java.lang.base;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;
import java.util.function.Supplier;

public class VarScopeTests {
    static final Logger log = LoggerFactory.getLogger(VarScopeTests.class);

    static int i = 0;

    //    int i = 1; //conflict with static i
    {
        log.info("what is i before initial block? \n\t{}", i);
        int i = 2;
        log.info("what is i after initial block? \n\t{}\n\t or VarScopeTests.i: {}", i, VarScopeTests.i);
    }

    @Test
    public void testScope() {
        int i = 3;
        log.info("i inside testScope() is \n\t{}", i);
        {
//            int i = 4;//i is already defined in outer scope
            int j = 4;
            log.info("j inside { } inside testScope() is \n\t{}", j);
        }
        int j = 5;
        log.info("j inside testScope() is \n\t{}", j);

        Runnable r = () -> {
            log.info("check j from inside Runnable inside testScope() is \n\t{}", j);
//            int j = 6; //conflict with j in outer scope
        };
        r.run();

        stub(() -> {
            int k = 8;
            log.info("check k from inside Runnable inside testScope() is \n\t{}", k);
        });

//        Function<Runnable, Void> f = (Runnable r)->{ /* r is already defined at line 338*/};
        Function<Runnable, Void> f = (Runnable _r) -> {
            int k = 9;
            _r.run();
            log.info("check k from inside lambda f() is \n\t{}", k);
            return null;
        };

        f.apply(() -> {
            int k = 10;
            log.info("check k from inside Runnable passed to f inside testScope() is \n\t{}", k);
        });

        Supplier<Supplier<Integer>> s = () -> {
            Integer l = Integer.valueOf(11);
            return () -> l;
        };
        int l = 12;

        for (int a = 0; a < 1; a++) {
            log.info("check a from for loop: {}", a);
        }
//        log.info("check a after for loop: {}", a);
        int a = -1;
        log.info("check new a after for loop: {}", a);
    }

    void stub(Runnable r) {
        int k = 7;
        r.run();
        log.info("check k from inside stub() is \n\t{}", k);
    }
}
