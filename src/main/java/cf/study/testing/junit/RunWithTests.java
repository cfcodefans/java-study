package cf.study.testing.junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import cf.study.java8.lang.MathTests;

@RunWith(Suite.class)
@SuiteClasses(MathTests.class)
public class RunWithTests {

}
