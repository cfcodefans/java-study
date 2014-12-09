package misc;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.math.NumberUtils;

public class MiscUtils {

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
		for (byte i = 32; i >= 0; i--) {
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
		for (StackTraceElement ste : stes) {
			sb.append(String.format("%s\t%s.%s\n", ste.getFileName(), ste.getClassName(), ste.getMethodName()));
		}
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

	
}
