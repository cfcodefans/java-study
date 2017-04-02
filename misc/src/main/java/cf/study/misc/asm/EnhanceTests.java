package cf.study.misc.asm;

import misc.MiscUtils;

/**
 * Created by fan on 2017/3/30.
 */
public class EnhanceTests {
    public static class Foo {
        public void bar() {
            System.out.println(MiscUtils.invocInfo());
        }
    }
    //crazy asm example
    //http://www.cnblogs.com/liuling/archive/2013/05/25/asm.html
//    public static class MethodEnhancer extends MethodVisitor {
//        public MethodEnhancer(MethodVisitor mv) {
//            super(Opcodes.ASM6, mv);
//        }
//
//
//    }
//
//    public static class EnhanceClzzVisitor extends ClassVisitor {
//        public EnhanceClzzVisitor(ClassVisitor cv) {
//            super(Opcodes.ASM6, cv);
//        }
//
//        @Override
//        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//            final MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
//            if (!name.equals("<init>") && mv != null) {
//
//            }
//        }
//
//    }
}
