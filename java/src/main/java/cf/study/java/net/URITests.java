package cf.study.java.net;

import org.junit.Test;

import java.net.URI;

/**
 * Created by fan on 2017/6/12.
 */
public class URITests {
    @Test
    public void testHDFS() throws Exception {
        URI uri = new URI("hdfs://study-cluster/");
        System.out.println(uri);
//        System.out.println(uri.toExternalForm());
//        System.out.println(uri.toURI());
        System.out.println(uri.getHost());
        System.out.println(uri.getPort());
        System.out.println(uri.getScheme());
        System.out.println(uri.getPath());
        System.out.println(uri.getQuery());
    }
}
