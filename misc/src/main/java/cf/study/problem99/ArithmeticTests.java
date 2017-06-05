package cf.study.problem99;

import org.junit.Test;

/**
 * Created by fan on 2017/6/4.
 */
public class ArithmeticTests {
    //38th
    public static int gcd(int a, int b) {
        if (a == 0) return b;
        if (b == 0) return a;

        if (a > b) return gcd(a - b, b);
        return gcd(a, b - a);
    }

    @Test
    public void testGCD() {
        System.out.println(gcd(135, 60));
        System.out.println(gcd(125, 60));
        System.out.println(gcd(125, 75));
    }

    //39
    /**
     * (*) Determine whether two positive integer numbers are coprime.
     */
    public static boolean coprime(int a, int b) {
        return gcd(a, b) == 1;
    }
}
