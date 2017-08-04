package cf.study.java.lang;

import org.junit.Test;

import java.net.URL;

public class ClassLoaderTests {
    @Test
    public void testLoadRes() {
        String packagePath = ClassLoaderTests.class.getPackage().getName().replace('.', '/');
        String classFilePath = packagePath + "/" + ClassLoaderTests.class.getSimpleName() + ".class";
        System.out.println(classFilePath);
        ClassLoader cl = ClassLoaderTests.class.getClassLoader();
        URL resUrl = cl.getResource(classFilePath);
        System.out.println(resUrl);
    }
}
