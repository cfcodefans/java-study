package study.env;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * This class is meant to test logging settings
 * as well as some new feature of JUnit5
 */
public class EnvTests {
    static final Logger log = LoggerFactory.getLogger(EnvTests.class);

    int testCount = 0;

    @TestFactory
    Collection<DynamicTest> dynamicTestsFromCollection() {
        return Arrays.asList(
            dynamicTest("1st dynamic test", () -> assertTrue(true)),
            dynamicTest("2nd dynamic test", () -> assertEquals(4, 2 * 2))
        );
    }

    /**
     * <pre>
     * Denotes that the annotated method should be executed before all
     * @Test, @RepeatedTest, @ParameterizedTest, and @TestFactory methods in the current class;
     * analogous to JUnit 4’s @BeforeClass. Such methods are inherited (unless they are hidden or overridden)
     * and must be static (unless the "per-class" test instance lifecycle is used).
     * </pre>
     */
    @BeforeAll
    public static void beforeAll() {
        log.info("this is run before all test cases");
    }

    @BeforeEach
    public void beforeEach() {
        log.info("this is run before each test case: {}", testCount++);
    }

    @AfterEach
    public void afterEach() {
        log.info("this is run after each test case: {}", testCount);
    }

    /**
     * <pre>
     * Denotes that a method is a test method. Unlike JUnit 4’s @Test annotation,
     * this annotation does not declare any attributes,
     * since test extensions in JUnit Jupiter operate based on their own dedicated annotations.
     * Such methods are inherited unless they are overridden.
     * </pre>
     */
    @Test
    public void testCase1() {
        log.info("this is a test run for junit @Test");
    }

    @Test
    public void testCase2() {
        log.info("this is a test run for junit @Test");
    }

    @Test
    @Disabled
    public void testAbort() {
        log.info("this test aborts the junit");
        System.exit(1);
    }

    @RepeatedTest(3)
    public void testRepeated() {
        log.info("I am not sure how many times I am repeated");
    }

    @RepeatedTest(3)
    public void testRepeated1(RepetitionInfo rpi) {
        log.info("I am being repeated the {}-th time out of {} times", rpi.getCurrentRepetition(), rpi.getTotalRepetitions());
    }

    @RepeatedTest(value = 3)
    public void testRepeated2(RepetitionInfo rpi) {
        log.info("I am being repeated the {}-th time out of {} times", rpi.getCurrentRepetition(), rpi.getTotalRepetitions());
        if (rpi.getCurrentRepetition() == 2) {
            throw new RuntimeException("enough! stop!");
        }
    }

    @Test
    public void testWhatInfo(TestInfo ti) {
        log.info("what is in the TestInfo:\n\t{}", ti);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4})
    public void testParamsFromValueSrc(int arg) {
        log.info("what parameter is it? \n\t{}", arg);
    }

//    @ParameterizedTest
//    @ValueSource(ints = {1, 2, 3, 4})
//    public void testParamsFromValueSrcExt(int arg, ParameterContext aa) {
//        log.info("what parameter is it? \n\t{}\n\t among {}", arg, aa);
//    }

    @AfterAll
    public static void afterAll() {
        log.info("this is run after all test cases");
    }
}
