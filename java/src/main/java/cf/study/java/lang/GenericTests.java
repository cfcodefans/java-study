package cf.study.java.lang;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.function.Function;

public class GenericTests {
    public static class Holder<T> {
        T t;
    }

    public static class StringHolder extends Holder<String> {

    }

    private static <T extends Serializable & Comparable> int compareAnyway(T a, T b) {
        return Objects.compare(a, b, Comparator.naturalOrder());
    }

    @Test
    public void testMultipleBounds() {
        System.out.println(compareAnyway("a", "b"));
    }

    @Test
    public void testSuperTypeParam() {
        StringHolder sh = new StringHolder();
        Type gs = sh.getClass().getGenericSuperclass();
        System.out.println(gs);
        System.out.println(gs instanceof ParameterizedType);
        ParameterizedType pt = (ParameterizedType) gs;
        System.out.println(pt.getActualTypeArguments()[0]);
    }

    @Test
    public void testTypeParam() {
        Holder<String> strHolder = new Holder<String>();
        Class<? extends Holder> clz = strHolder.getClass();
        TypeVariable<?>[] tps = clz.getTypeParameters();
        System.out.println(Arrays.toString(tps));
        TypeVariable<?> tv = tps[0];

        System.out.println(ToStringBuilder.reflectionToString(tv));
        System.out.println(tv.getBounds()[0].getTypeName());

        System.out.println();
        Type genericSuperclass = clz.getGenericSuperclass();
        System.out.println(genericSuperclass);
    }

    public static class Cmp implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }

    @Test
    public void testTypeParam1() {
        Cmp cmp = new Cmp();
        Class<? extends Cmp> clz = cmp.getClass();
        TypeVariable<?>[] tps = clz.getTypeParameters();
        System.out.println(Arrays.toString(tps));
        System.out.println(Arrays.toString(clz.getGenericInterfaces()));
        Type t = clz.getGenericInterfaces()[0];
        ParameterizedType pt = (ParameterizedType) t;
        System.out.println(t.toString());
        System.out.println(Arrays.toString(pt.getActualTypeArguments()));

        System.out.println(ToStringBuilder.reflectionToString(pt));
    }

    @SuppressWarnings("null")
    @Test
    public void loop() {
        StopWatch sw = new StopWatch();
        sw.start();
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0, j = list.size(); i < j; i++) {
            System.out.println(list.get(i));
        }

        String str = "123";
        Integer intVal = Integer.valueOf(str);
        for (Integer i : list) {
            // list.remve
            if (i.equals(intVal)) {
                // ..
            }
        }
        sw.stop();

        System.out.println(sw.getNanoTime());

        Object a = null;
        Object b = "s";
        a.equals(b);

        a.equals(CONST);
        CONST.equals(a);
        "a".equals(a);
    }

    static final String CONST = "";

    @Test
    public void testInstanceof() {
        System.out.println((null instanceof Integer));
        List<Number> numberList = new LinkedList<>();
        List<Integer> intList = new LinkedList<>();
        System.out.println(numberList.getClass().isAssignableFrom(intList.getClass()));
//        System.out.println(intList instanceof List<Number>);
    }

    @Test
    public void testUpperBoundWildcard() {
        List<? extends Number> numList = new LinkedList<>();
//        numList.add(Integer.valueOf(1));
        List<Integer> intList = new LinkedList<>();
//        numList.addAll(intList);
        Function<List<? extends Number>, Number> _sum = (List<? extends Number> _numList) -> {
            double d = 0.0f;
            for (Number n: _numList) {
                d += n.doubleValue();
            }
            return d;
        };
        _sum.apply(intList);
        _sum.apply(numList);
    }

    @Test
    public void testSubTyping() {
        {
            List<Number> numList = new LinkedList<>();
            List<Integer> intList = new LinkedList<>();
//            numList = intList;
        }
        {
            List<? extends Number> numList = new LinkedList<>();
            List<Integer> intList = new LinkedList<>();
            numList = intList;
        }
        {
            List<? extends Number> numList = new LinkedList<>();
            List<? extends Integer> intList = new LinkedList<>();
            numList = intList;
        }

        {
            List<Number> numList = new LinkedList<>();
            List<? super Integer> intList = new LinkedList<>();
//            numList = intList;
            intList = numList;
        }
    }

    @SuppressWarnings("rawtypes")
    public List query(String queryStr) {
        List result = Collections.emptyList();
        if (StringUtils.isBlank(queryStr)) {
            return result;
        }
        // ......

        // ....

        return result;
    }

    public static class User {
    }

    public User byId(int id) {
        return new User();
    }

    public List<User> byIds(int... ids) {
        List<User> list = new ArrayList<User>();

        for (int id : ids) {
            list.add(byId(id));
        }
        return list;
    }

    public List<User> getUserList() {
        return Collections.emptyList();
    }

    @Test
    public void testReturnType() throws Exception {
        Class<GenericTests> cls = GenericTests.class;
        Method md = cls.getMethod("getUserList");
        Type genericReturnType = md.getGenericReturnType();
        System.out.println(genericReturnType);

        if (genericReturnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
            Type rawType = parameterizedType.getRawType();
            System.out.println(rawType);
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            System.out.println(actualTypeArguments[0]);
        }
    }

}
