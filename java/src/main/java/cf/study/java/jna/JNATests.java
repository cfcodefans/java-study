package cf.study.java.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import org.junit.Test;

public class JNATests {
    public static interface CLib extends Library {
        CLib INSTANCE = (CLib) Native.loadLibrary(Platform.isWindows() ? "msvcrt" : "c", CLib.class);

        void printf(String format, Object... args);
    }

    @Test
    public void testPrintf() {
        CLib.INSTANCE.printf("test printf with %ld", System.currentTimeMillis());
    }
}
