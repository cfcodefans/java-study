package cf.study.java8.utils;

import misc.MiscUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.BitSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by Administrator on 2016/6/5.
 */
public class SetTests {
    private static final Logger log = LogManager.getLogger(SetTests.class);

    @Test public void testNavigable() {
        ConcurrentSkipListSet<MutablePair<Integer, String>> s = new ConcurrentSkipListSet<>((p1, p2) -> {
            if (p1.getValue().equals(p2.getValue())) {
                log.info("{} equals {}", p1.getValue(), p2.getValue());
                p2.setLeft(p1.getLeft());
                return 0;
            }

            if (p1.getKey().equals(p2.getKey())) {
                log.info("{} equals {}", p1.getKey(), p2.getKey());
                return p2.getValue().compareTo(p1.getValue());
            }
            return p2.getKey().compareTo(p1.getKey());
        });

        s.add(new MutablePair<Integer, String>(10, "abc"));
        s.add(new MutablePair<Integer, String>(15, "abc"));
        s.add(new MutablePair<Integer, String>(10, "abc1"));
        s.add(new MutablePair<Integer, String>(11, "abc1"));
        s.add(new MutablePair<Integer, String>(14, "abc2"));
        s.add(new MutablePair<Integer, String>(14, "abc3"));
        s.add(new MutablePair<Integer, String>(18, "abc3"));

        log.info(s);
    }

    @Test
    public void testBitSet() {
        BitSet bs = new BitSet();
        List<Long> longs = MiscUtils.pi2Longs(100);
        longs.stream().mapToInt(Long::intValue).forEach(bs::set);
        System.out.println(ArrayUtils.toString(bs.stream().toArray(), ", "));
    }

    @Test
    public void testLonelyInteger() {
        try (Scanner sc = new Scanner(System.in)) {
//            int n = sc.nextInt();
//            sc.nextLine();
//            int[] ar = new int[n];
//            for (int i = 0; i < n; i++) {
//                ar[i] = sc.nextInt();
//            }

            int[] ar = {1,1,2   };

            BitSet bs = new BitSet();
            for (int i : ar) {
                bs.set(i, !bs.get(i));
            }
            System.out.println(ArrayUtils.toString(bs.stream().toArray(), ", "));
        }
    }
}
