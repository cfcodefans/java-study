package study.java.util;

import org.apache.commons.collections4.IteratorUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;

public class LinkedHashMapTests {
    static final Logger log = LoggerFactory.getLogger(LinkedHashMapTests.class);

    @Test
    public void testOrder1() {
        LinkedHashMap<String, String> lhm = new LinkedHashMap<>(16,0.75f,true);
        lhm.put("banana", "yellow");
        lhm.put("apple", "red");
        lhm.put("watermelon", "green");
        lhm.put("plum", "purple");

        log.info("Order: \n\tkeys:\t{}\n\tvalues:\t{}\n\tentries:\t{}",
            lhm.keySet(),
            lhm.values(),
            IteratorUtils.toString(lhm.entrySet().iterator()));

        lhm.get("banana");
        lhm.get("watermelon");

        log.info("Order: \n\tkeys:\t{}\n\tvalues:\t{}\n\tentries:\t{}",
            lhm.keySet(),
            lhm.values(),
            IteratorUtils.toString(lhm.entrySet().iterator()));
    }
}
