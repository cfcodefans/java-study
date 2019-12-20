package study.java.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ListTests {
    static final Logger log = LoggerFactory.getLogger(ListTests.class);

    @Test
    public void testRemoveInForeach() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");

        for (String str : list) {
            if ("2".equals(str)) {
                list.remove(str);
            }
        }
        log.info("list is {}", list);
    }

}
