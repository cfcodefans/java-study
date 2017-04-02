package cf.study.misc.asm;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * Created by fan on 2016/6/27.
 */
public class ClassReadTests {
    private static final Logger log = LogManager.getLogger(ClassReadTests.class);

    @Test
    public void testClassPath() {
        String[] javaClassPaths = SystemUtils.JAVA_CLASS_PATH.split(";");
        log.info(javaClassPaths.length);

        AtomicLong idx = new AtomicLong(0);
        Stream.of(javaClassPaths).map(p -> String.format("%d\t%s", idx.getAndIncrement(), p)).forEach(System.out::println);
    }

    public static class ClassPrinter extends ClassVisitor {
        public ClassPrinter() {
            super(Opcodes.ASM6);
        }

        public void visit(int version, int access, String name, String signature,
                          String superName, String[] interfaces) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("version:\t%d\n", version))
                .append(String.format("access:\t%d\n", access))
                .append(String.format("isInterface:\t%s\n", Boolean.valueOf((access & Opcodes.ACC_INTERFACE) != 0)))
                .append(String.format("name:\t%s\n", name))
                .append(String.format("signature:\t%s\n", signature))
                .append(String.format("superName:\t%s\n", superName))
                .append(String.format("interfaces:\t%s\n", StringUtils.join(interfaces, ",")));

            log.info(sb);
        }
    }

    @Test
    public void testClassPrinter() throws IOException {
        ClassReader cr = new ClassReader(Runnable.class.getName());
        cr.accept(new ClassPrinter(), ClassReader.SKIP_CODE);

        cr = new ClassReader(String.class.getName());
        cr.accept(new ClassPrinter(), ClassReader.SKIP_CODE);
    }
}
