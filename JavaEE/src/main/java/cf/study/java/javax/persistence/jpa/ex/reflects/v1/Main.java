package cf.study.java.javax.persistence.jpa.ex.reflects.v1;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;

import cf.study.java.javax.cdi.weld.WeldTest;
import cf.study.java.lang.reflect.Reflects;

public class Main {
	
	static long counter = 0;
	
	public static void main(String[] args) {
		
		try {
			WeldTest.setUp();
			ReflectDao dao = WeldTest.getBeanInReqScope(ReflectDao.class);

			File _f = new File(String.format("%s/lib/rt.jar", SystemUtils.JAVA_HOME));
			List<Class<?>> re = Reflects.loadClzzFromJar(_f, ClassLoader.getSystemClassLoader());
//			System.out.println(StringUtils.join(re, '\n'));
			long size = re.size();
			System.out.println(size);

//			re.stream().forEach((cls) -> {
//				System.out.println(String.format("%d/%d %f%% %s", ++counter, size, counter/(double)size * 100, cls.getSimpleName()));
//				dao.createClazz(cls);
//			});
//			System.out.println("result: " + dao.queryCount("select count(be.id) from BaseEn be"));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
