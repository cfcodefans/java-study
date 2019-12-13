package study.junit;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.util.stream.Stream;

import static java.lang.String.format;

public class RandomParamExt implements ParameterResolver {//, TestTemplateInvocationContextProvider {

//    @Override
//    public boolean supportsTestTemplate(ExtensionContext extCtx) {
//        if (!extCtx.getTestMethod().isPresent()) {
//            return false;
//        }
//
//        return true;
//    }
//
//    @Override
//    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext extCtx) {
//        return null;
//    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface RandParam {
        double start() default Double.NaN;

        double end() default Double.NaN;
    }

    @Override
    public boolean supportsParameter(ParameterContext paramCtx, ExtensionContext extCtx) throws ParameterResolutionException {
        return paramCtx.isAnnotated(RandParam.class);
    }

    @Override
    public Object resolveParameter(ParameterContext paramCtx, ExtensionContext extCtx) throws ParameterResolutionException {
        Parameter param = paramCtx.getParameter();
        Class<?> paramCls = param.getType();

        RandParam randAnno = param.getAnnotation(RandParam.class);
        double d_start = randAnno.start();
        double d_end = randAnno.end();

        Object randVal = genRandVal(paramCls, d_start, d_end);
        extCtx.publishReportEntry(param.getName(), String.valueOf(randVal));
        return randVal;
    }

    private static Object genRandVal(Class<?> paramCls, double d_start, double d_end) {
        if (int.class.equals(paramCls) || Integer.class.equals(paramCls)) {
            long start = Double.isNaN(d_start) ? 0 : (long) d_start;
            long end = Double.isNaN(d_end) ? Long.MAX_VALUE : (long) d_end;
            return (int) (RandomUtils.nextLong(start, end) % Integer.MAX_VALUE);
        }
        if (boolean.class.equals(paramCls) || Boolean.class.equals(paramCls)) {
            return RandomUtils.nextBoolean();
        }
        if (short.class.equals(paramCls) || Short.class.equals(paramCls)) {
            long start = Double.isNaN(d_start) ? 0 : (long) d_start;
            long end = Double.isNaN(d_end) ? Long.MAX_VALUE : (long) d_end;
            return (short) (RandomUtils.nextLong(start, end) % Short.MAX_VALUE);
        }
        if (byte.class.equals(paramCls) || Byte.class.equals(paramCls)) {
            long start = Double.isNaN(d_start) ? 0 : (long) d_start;
            long end = Double.isNaN(d_end) ? Long.MAX_VALUE : (long) d_end;
            return (byte) (RandomUtils.nextLong(start, end) % Byte.MAX_VALUE);
        }
        if (long.class.equals(paramCls) || Long.class.equals(paramCls)) {
            long start = Double.isNaN(d_start) ? 0 : (long) d_start;
            long end = Double.isNaN(d_end) ? Long.MAX_VALUE : (long) d_end;
            return RandomUtils.nextLong(start, end);
        }
        if (float.class.equals(paramCls) || Float.class.equals(paramCls)) {
            float start = Double.isNaN(d_start) ? 0 : (float) d_start;
            float end = Double.isNaN(d_end) ? Float.MAX_VALUE : (float) d_end;
            return RandomUtils.nextFloat(start, end);
        }
        if (double.class.equals(paramCls) || Double.class.equals(paramCls)) {
            return RandomUtils.nextDouble(d_start, d_end);
        }
        if (String.class.equals(paramCls)) {
            long start = Double.isNaN(d_start) ? 0 : (long) d_start;
            long end = Double.isNaN(d_end) ? Long.MAX_VALUE : (long) d_end;
            return RandomStringUtils.randomAscii((int) (RandomUtils.nextLong(start, end) % Integer.MAX_VALUE));
        }

        return null;
    }

    @ExtendWith({RandomParamExt.class})
    public static class SelfTests {

        @Test
        @RepeatedTest(10000)
        public void testSelfInt(@RandParam(start = 100, end = 200) int _int) {
            Assertions.assertTrue(_int >= 100 && _int < 200, format("_int is %d", _int));
        }

        @Test
        @RepeatedTest(1)
        public void testSelfString(@RandParam(start = 100, end = 200) String str) {
            int len = str.length();
            Assertions.assertTrue(len >= 100 && len < 200, format("str.length() is %d", len));
//            System.out.println(format("str is %d", str));
            System.out.println(str);
        }
    }
}
