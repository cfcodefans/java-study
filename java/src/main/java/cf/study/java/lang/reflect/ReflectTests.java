package cf.study.java.lang.reflect;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.jboss.weld.util.reflection.Reflections;
import org.junit.Assert;
import org.junit.Test;

public class ReflectTests {

    public static class Sample<T> {
        public T genericField;
        public String stringField;

        class InstanceClz {
        }

        static class StaticClz {
        }

        public void method() {
            @SuppressWarnings("unused")
            class MethodClz {
            }
            ;
        }
    }

    public static class StringSample extends Sample<String> {

    }

    @Test
    public void testFields() {
        Stream.of(Sample.class.getDeclaredFields()).forEach((fd) -> {
//			System.out.println(ToStringBuilder.reflectionToString(fd, ToStringStyle.MULTI_LINE_STYLE));
            System.out.println(fd.getDeclaringClass());
            System.out.println(fd.getGenericType().getClass());
        });

        Stream.of(Sample.class.getDeclaredMethods()).forEach((md) -> {
//			System.out.println(ToStringBuilder.reflectionToString(fd, ToStringStyle.MULTI_LINE_STYLE));
            System.out.println(md.getDeclaringClass());
            System.out.println(md.getName());
        });
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testClzMeta() {
        Class<Sample[]> arrayClz = Sample[].class;
        System.out.println(arrayClz.getDeclaringClass());
        System.out.println(Sample.class.getDeclaringClass());
        System.out.println(Arrays.toString(Sample.class.getDeclaredClasses()));
        System.out.println(Arrays.toString(arrayClz.getDeclaredClasses()));
        System.out.println(arrayClz.getComponentType());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testGeneric() throws Exception {
        Sample<String> ss = new Sample<String>();
        Class<? extends Sample> cls = ss.getClass();

//		System.out.println(cls.getName());
        System.out.println(Arrays.toString(cls.getTypeParameters()));

        TypeVariable<?> t = cls.getTypeParameters()[0];
        System.out.println(t.getGenericDeclaration());
        System.out.println(t.getTypeName());
        Field f = cls.getField("genericField");
        System.out.println(f);
        System.out.println(f.getGenericType());

//		System.out.println(Sample.class);

        System.out.println(TypeUtils.determineTypeArguments(cls, TypeUtils.parameterize(Sample.class, cls.getTypeParameters())));
        System.out.println(Arrays.toString(Reflections.getActualTypeArguments(cls)));
    }

    @SuppressWarnings("unused")
    @Test
    public void testGenericSuper() throws Exception {
        StringSample _ss = new StringSample();
        Type _type = StringSample.class.getGenericSuperclass();
        System.out.println(_type);

//		Assert.assertTure(ParameterizedType.class.gett _type);

        ParameterizedType pt = (ParameterizedType) _type;
        Stream.of(pt.getActualTypeArguments()).forEach(System.out::println);
    }

    @Test
    public void testAssignedFrom() {
        Assert.assertTrue(Collection.class.isAssignableFrom(List.class));
    }


    @Test
    public void testMetods() {
        Class student = String.class;//~~~Complete this line~~~;
        Method[] methods = student.getMethods();//~~~Complete this line~~~;

        ArrayList<String> methodList = new ArrayList<>();
        for (Method m : methods) {
            methodList.add(m.getName());
        }
        Collections.sort(methodList);
        for (String name : methodList) {
            System.out.println(name);
        }
    }
}
