package cf.study.java8.lang;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import java.util.Arrays;

public class SystemTests {

    @Test
    public void testArrayCopy() {
        int[] ia = {0xff00ff};

        int[] _ia = {0xff00ff};
        System.arraycopy(ia, 0, _ia, 0, 1);
        System.out.println(Arrays.toString(_ia));


        byte[] ba = new byte[4];

        System.arraycopy(ia, 0, ba, 0, 1);
        System.out.println(Arrays.toString(ba));
    }

    @Test
    public void testArrayCopyPref() {
        for (int r = 0; r < 10; r++) {
            int[] ai = new int[1000];
            for (int i = 0, j = ai.length; i < j; i++) {
                ai[i] = i;
            }
            int[] bi = new int[1000];

            StopWatch sw = new StopWatch();
            sw.start();
            for (int i = 0, j = ai.length; i < j; i++) {
                bi[i] = ai[i];
            }
            sw.stop();
            System.out.printf("loop took %d ns\n", sw.getNanoTime());

            sw.reset();
            sw.start();
            System.arraycopy(ai, 0, bi, 0, ai.length);
            sw.stop();
            System.out.printf("copy took %d ns\n\n", sw.getNanoTime());
        }
    }

    @Test
    public void testEnv() {
        System.getenv().forEach((k, v) -> System.out.println(k + " = \t" + v));
    }

    @Test
    public void testProperties() {
        System.getProperties().forEach((k, v) -> System.out.println(k + " = \t" + v));
    }

}
