package cf.study.java.net;

import org.junit.Test;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by fan on 2017/1/30.
 */
public class URLDecoderTests {
    @Test public void testDecode() {
        String encoded = URLEncoder.encode("/");
        System.out.println(encoded);
        System.out.println(URLDecoder.decode(encoded));

        String uri = "amqp://poppen:poppen@snowball:5672/poppen";
        encoded = URLEncoder.encode(uri);
        System.out.println(encoded);
        System.out.println(URLDecoder.decode(encoded));
    }
}
