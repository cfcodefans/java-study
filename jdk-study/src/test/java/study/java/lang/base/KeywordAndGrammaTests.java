package study.java.lang.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.Callable;

public class KeywordAndGrammaTests {
    static final Logger log = LoggerFactory.getLogger(KeywordAndGrammaTests.class);

    public static final String[] KEY_WORDS = {"abstract", // checked
        "assert", // checked
        "boolean", "break", // checked
        "byte", "case", // checked
        "catch", "char", "class", "const", // checked
        "continue", "default", // checked
        "do", "double", "else", // checked
        "enum", "extends", "final", "finally", "float", "for", "goto", "if", // checked
        "implements", "import", // checked
        "instanceof", "int", "interface", "long", "native", "new", "package", // checked
        "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", // checked
        "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"};

    static <C extends Comparable> int cmp(C cmp1, C cmp2, String mark) {
        log.info("comparing {} to {} with mark:\t{}", cmp1, cmp2, mark);
        return Comparator.<C>nullsFirst(Comparator.<C>naturalOrder()).compare(cmp1, cmp2);
    }

    @Test
    public void testIfAndElse() {
        if (true) log.info("if (true)");
        if (false) log.info("if (false)");

        if (false) ;
        else log.info("no true");

        int i = 5;

        if (cmp(i, 0, "i == 0") == 0) {
            log.info("i:{} == 0", i);
        } else if (cmp(i, 0, "i > 0") > 0) {
            log.info("i:{} > 0", i);
        } else {
            log.info("i:{} < 0", i);
        }

        int j = 0;
        Random rand = new Random();
        if ((j = rand.nextInt()) > 10) {
            log.info("j: {} > 10", j);
        }
        Assertions.assertNotEquals(0, j);
        log.info("j: {} changed", j);

        //invalid
//        if ((int k = rand.nextInt()) >10){
//            log.info("k: {} > 10", k);
//        }
//        log.info("k: {} changed", k);
    }

    @Test
    public void testAssert() throws Exception {
        assert true;
        log.info("assert true");
//        assert false; // run java with "-ea", this throws AssertError
//        log.info("assert false");

        Assertions.assertThrows(AssertionError.class, () -> {
            assert cmp(1, 2, "false") > 0 : "1 is not greater than 2";
        }, "got you, AssertionError");

        //invalid
//        assert null;

        Assertions.assertThrows(NullPointerException.class, () -> {
            Boolean nullBoolean = null;
            assert nullBoolean;
        }, "got you, NullPointerException");

        Assertions.assertThrows(RuntimeException.class, () -> {
            Callable foo = () -> {
                throw new RuntimeException("Boom");
            };
            assert false : foo.call();
        }, "got you, RuntimeException");
    }

    @Test
    public void testBreak() {
        // break; break can't be used outside of loop or switch
        for (int i = 0; i < 1; i++) {
            break;
        }
        do {
            break;
        } while (true);

        while (true) {
            break;
        }

        switch (1) {
            case 1:
                break;
            default:
                break;
        }

        _outer:
        for (int i = 0; i < 3; i++) {
            log.info("outer begins: " + i);
            _inner: for (int j = 0; j < i; j++) {
                log.info("\t" + j);
                break _inner; // jump out of outer loop too
            }
            log.info("outer ends: " + i);
        }
    }


}
