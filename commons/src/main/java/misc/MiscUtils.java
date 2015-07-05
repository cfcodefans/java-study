package misc;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;


public class MiscUtils {
	
	public static String loadResAsString(final Class<?> cls, final String fileName) {
		if (cls == null || StringUtils.isBlank(fileName)) {
			return StringUtils.EMPTY;
		}
		
		try {
			return IOUtils.toString(cls.getResourceAsStream(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return StringUtils.EMPTY;
	}

	public static class LoopingArrayIterator<E> extends ObjectArrayIterator<E> {
		public LoopingArrayIterator(final E... array) {
			super(array, 0, array.length);
		}
	
		public LoopingArrayIterator(final E array[], final int start) {
			super(array, start, array.length);
		}
	
		public E loop() {		
			final E[] array = this.getArray();
			loopIdx.compareAndSet(array.length, 0);
			return array[loopIdx.getAndIncrement() % array.length];
		}
		
		private AtomicInteger loopIdx = new AtomicInteger();
	}

	public static String toBinStr(final byte _b) {
		final StringBuilder sb = new StringBuilder();
		for (byte i = 7; i >= 0; i--) {
			sb.append((_b & (1 << i)) != 0 ? 1 : 0);
		}
		return sb.toString();
	}
	
	public static String toBinStr(final short _s) {
		final StringBuilder sb = new StringBuilder();
		for (byte i = 16; i >= 0; i--) {
			sb.append((_s & (1 << i)) != 0 ? 1 : 0);
		}
		return sb.toString();
	}
	
	public static String toBinStr(final int _i) {
		final StringBuilder sb = new StringBuilder();
		sb.append(_i < 0 ? 1 : 0);
		for (byte i = 31; i >= 0; i--) {
			sb.append((_i & (1 << i)) != 0 ? 1 : 0);
		}
		return sb.toString();
	}
	
	public static String toBinStr(long _l) {
		final StringBuilder sb = new StringBuilder();
		for (byte i = 64; i >= 0; i--) {
			sb.append((_l & (1 << i)) != 0 ? 1 : 0);
		}
		return sb.toString();
	}

	public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
	private static long HOST_HASH = System.currentTimeMillis();
	private static long IDX = 0;

	static {
		try {
			HOST_HASH = InetAddress.getLocalHost().getHostAddress().hashCode();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public static String invocationInfo() {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		int i = 2;
		return String.format("%s\t%s.%s", ste[i].getFileName(), ste[i].getClassName(), ste[i].getMethodName());
	}

	public static String invocInfo() {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		int i = 2;
		return String.format("%s\t%s.%s", ste[i].getFileName(), StringUtils.substringAfterLast(ste[i].getClassName(), ".") , ste[i].getMethodName());
	}

	public static String invocationInfo(final int i) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		return String.format("%s\t%s.%s", ste[i].getFileName(), ste[i].getClassName(), ste[i].getMethodName());
	}
	
	public static String stackInfo() {
		final StringBuilder sb = new StringBuilder();
		final StackTraceElement[] stes = Thread.currentThread().getStackTrace();
		ArrayUtils.reverse(stes);
		
		Stream.of(stes).forEach(ste->sb.append(String.format("%s\t%s.%s\n", ste.getFileName(), ste.getClassName(), ste.getMethodName())));
		return sb.toString();
	}

	public static String byteCountToDisplaySize(long size) {
		String displaySize;
		if (size / 1073741824L > 0L) {
			displaySize = String.valueOf(size / 1073741824L) + " GB";
		} else {
			if (size / 1048576L > 0L) {
				displaySize = String.valueOf(size / 1048576L) + " MB";
			} else {
				if (size / 1024L > 0L)
					displaySize = String.valueOf(size / 1024L) + " KB";
				else
					displaySize = String.valueOf(size) + " bytes";
			}
		}
		return displaySize;
	}

	public static long getProcessId() {
		// Note: may fail in some JVM implementations
		// therefore fallback has to be provided
	
		// something like '<pid>@<hostname>', at least in SUN / Oracle JVMs
		final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
		final int index = jvmName.indexOf('@');
		String pidStr = jvmName.substring(0, index);
	
		if (index < 1 || !NumberUtils.isNumber(pidStr)) {
			// part before '@' empty (index = 0) / '@' not found (index = -1)
			return 0;
		}
	
		return Long.parseLong(pidStr);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map map(Object...keyAndVals) {
		return MapUtils.putAll(new HashMap(), keyAndVals);
	}

	public static String toXML(final Object bean) {
		final StringWriter sw = new StringWriter();
		try {
			JAXBContext jc = JAXBContext.newInstance(bean.getClass());
	
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FRAGMENT, true);
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			// marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-16");
	
			m.marshal(bean, sw);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sw.toString();
	}

	public static <T> T toObj(final String xmlStr, final Class<T> cls) {
		if (StringUtils.isBlank(xmlStr) || cls == null) {
			return null;
		}
		
		try {
			JAXBContext jc = JAXBContext.newInstance(cls);
			Unmarshaller um = jc.createUnmarshaller();
			return um.unmarshal(new StreamSource(new StringReader(xmlStr)), cls).getValue();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static long uniqueLong() {
		return Math.abs(UUID.randomUUID().hashCode());
	}

	public static ThreadFactory namedThreadFactory(final String name) {
		return new BasicThreadFactory.Builder().namingPattern(name + "_%d").build();
	}

	public static HttpResponse easyGet(String urlStr, String...params) {
		if (StringUtils.isBlank(urlStr)) {
			return null;
		}
		
		try (CloseableHttpClient hc = HttpClients.createDefault()) {
			URIBuilder ub = new URIBuilder(urlStr);
			if (!ArrayUtils.isEmpty(params)) {
				IntStream.range(0, params.length).filter(i -> i % 2 == 1).forEach(i -> ub.addParameter(params[i - 1], params[i]));
			}
			
			HttpGet hg = new HttpGet(ub.build());
			try (CloseableHttpResponse hr = hc.execute(hg)) {
				BasicHttpResponse bhr = new BasicHttpResponse(hr.getStatusLine());
//				bhr.setEntity(entity);(new InputStreamEntity(new ByteArrayInputStream(EntityUtils.toByteArray(hr.getEntity()))));
				return bhr;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static HttpResponse easyPost(String urlStr, String...params) {
		if (StringUtils.isBlank(urlStr)) {
			return null;
		}
		
		try (CloseableHttpClient hc = HttpClients.createDefault()) {
			URIBuilder ub = new URIBuilder(urlStr);
			HttpPost hp = new HttpPost(ub.build());
			List<NameValuePair> nvps = new ArrayList <NameValuePair>();
			if (!ArrayUtils.isEmpty(params)) {
				IntStream.range(0, params.length).filter(i -> i % 2 == 1).forEach(i -> nvps.add(new BasicNameValuePair(params[i - 1], params[i])));
			}
			hp.setEntity(new UrlEncodedFormEntity(nvps));
			
			try (CloseableHttpResponse hr = hc.execute(hp)) {
				BasicHttpResponse bhr = new BasicHttpResponse(hr.getStatusLine());
//				bhr.setEntity(new InputStreamEntity(new ByteArrayInputStream(EntityUtils.toByteArray(hr.getEntity()))));
				return bhr;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void easySleep(long i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void callClassInProc(Class<?> cls) {
		if (cls == null) {
			System.err.println("try to run a null class");
			return;
		}
		
		Method mainMd = MethodUtils.getMatchingAccessibleMethod(cls, "main", String[].class);
		if (!(mainMd != null 
				&& mainMd.isAccessible() 
				&& ((mainMd.getModifiers() & Modifier.STATIC) != 0))) {
			System.err.println(String.format("%s doesn't have public void main(String[] args)", cls.getName()));
			return;
		}
		
//		StringUtils.join(classPaths.toArray(), SystemUtils.PATH_SEPARATOR)
		String javaPath = SystemUtils.JAVA_HOME + SystemUtils.FILE_SEPARATOR + "bin" + SystemUtils.FILE_SEPARATOR + "java";
		ProcessBuilder pb = new ProcessBuilder(javaPath,// "java",
				"-cp", '"' + SystemUtils.JAVA_CLASS_PATH + '"',
				// "-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8765",
				"-Xmx256m", "-Xms256m", "-Xmn192m", "-Xss128k",
				cls.getName());
	}
	
	public static class JavaProcBuilder {
		private ProcessBuilder procBuilder;
		
		private Map<String, String> args = new LinkedHashMap<String, String>();
		
		private String className = StringUtils.EMPTY;
		private String javaHome = SystemUtils.JAVA_HOME;
		private String classPath = SystemUtils.JAVA_CLASS_PATH;
		
		public JavaProcBuilder(String className) {
			this.className = className;
		}
		
		public JavaProcBuilder setJavaHome(String javaHome) {
			this.javaHome = javaHome;
			return this;
		}
		
		public JavaProcBuilder setClassPath(String classPath) {
			this.classPath = classPath;
			return this;
		}
		
		
	}

	public static BigDecimal pi(Integer n) {
			if (n == null) {
				n = 0;
			}
			
			if (n == 0) return null;
			if (n == 1) return new BigDecimal(3);
			if (n == 2) return new BigDecimal(3.1);
			if (n == 3) return new BigDecimal(3.14);
			
			MathContext mc = new MathContext(n);
			BigDecimal pi = new BigDecimal(BigInteger.ZERO, n, mc);
			BigDecimal one = new BigDecimal(BigInteger.ONE, n, mc);
			BigDecimal two = new BigDecimal(new BigInteger("2"), n, mc);
			BigDecimal four = new BigDecimal(new BigInteger("4"), n, mc);
			for (int k = 0; k < n; k++) {
				BigDecimal tmp1 = one.divide(new BigDecimal(16).pow(k, mc), mc);
				
				BigDecimal tmp2_1 = four.divide(new BigDecimal(8 * k + 1, mc), mc);
				BigDecimal tmp2_2 = two.divide(new BigDecimal(8 * k + 4, mc), mc);
				BigDecimal tmp2_3 = one.divide(new BigDecimal(8 * k + 5, mc), mc);
				BigDecimal tmp2_4 = one.divide(new BigDecimal(8 * k + 6, mc), mc);
				
				BigDecimal tmp2 = tmp2_1.subtract(tmp2_2, mc).subtract(tmp2_3, mc).subtract(tmp2_4, mc);
				pi = pi.add(tmp1.multiply(tmp2, mc));
			}
			
	//		pi.setScale(n, BigDecimal.ROUND_UP);
			pi.scaleByPowerOfTen(n);
			
			return pi;
		}

	public static List<Long> pi2Longs(Integer n) {
		BigDecimal pi = pi(n);
		
		List<Long> list = new ArrayList<Long>(n);
		pi.toString().substring(0, n).chars().forEach((i)->{
			char c = (char)i;
			if (CharUtils.isAsciiNumeric(c)) {
				list.add((long)(c - 48));
			}
		});
		
		return list;
	}
	
	public static <T> Predicate<T> predicate(Predicate<T> pre, boolean defaultValue) {
		return pre == null ? ((T t)->defaultValue) : pre;
	}
	
	public static class TraverseMatcher<T> {
		public Predicate<T> condition = null;
		public Function<T, Collection<T>> getChildren = null;
		public Predicate<T> after = null;
		
		public boolean traverse(T t) {
			if (condition != null && !condition.test(t)) 
				return false;
			
			if (getChildren == null) {
				return predicate(after, true).test(t);
			}
			
			Collection<T> children = getChildren.apply(t);
			if (children == null) {
				return predicate(after, true).test(t);
			}
			
			for (T _t : children) {
				if (!traverse(_t)) {
					break;
				}
			}
			
			return predicate(after, true).test(t);
		}
	}
}
