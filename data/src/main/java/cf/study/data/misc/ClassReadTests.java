package cf.study.data.misc;

import cf.study.data.mining.entity.ClassEn;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

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

    @Test
    public void readByAsm() {
        class ClzzVisitor extends ClassVisitor {
            public ClassEn ce = new ClassEn();

            public ClzzVisitor() {
                super(Opcodes.ASM5);
            }

            public void visit(int version, int access, String name, String signature,
                              String superName, String[] interfaces) {
                ce.name = name;

            }
        }
    }
}
