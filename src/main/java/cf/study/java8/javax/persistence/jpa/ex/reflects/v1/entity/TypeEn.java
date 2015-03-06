package cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity;

import java.util.LinkedHashSet;
import java.util.Set;

public enum TypeEn {
	INTERFACE, CLASS, ENUM, ANNOTATION, PRIMITIVE, ARRAY, SYNTHETIC, ANONYMOUS, LOCAL, MEMBER_CLASS;
	public static Set<TypeEn> by(Class<?> clz) {
		Set<TypeEn> types = new LinkedHashSet<TypeEn>();
		if (clz == null)
			return types;

		if (clz.isAnnotation()) types.add(ANNOTATION);
		if (clz.isAnonymousClass()) types.add(ANONYMOUS);
		if (clz.isArray()) types.add(ARRAY);
		if (clz.isEnum()) types.add(ENUM);
		if (clz.isInterface()) types.add(INTERFACE);
		if (clz.isLocalClass()) types.add(LOCAL);
		if (clz.isMemberClass()) types.add(MEMBER_CLASS);
		if (clz.isPrimitive()) types.add(PRIMITIVE);
		if (clz.isSynthetic()) types.add(SYNTHETIC);
		
		return types;
	}
}