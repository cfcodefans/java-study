package study.commons;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MiscUtils {
    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    public static long getPropertyNumber(String name, long defaultValue) {
        String str = System.getProperty(name);
        if (StringUtils.isNumeric(str)) {
            return Long.parseLong(str);
        }
        return defaultValue;
    }

    public static String invocationInfo() {
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        int i = 2;
        return String.format("%s\t%s.%s", ste[i].getFileName(), ste[i].getClassName(), ste[i].getMethodName());
    }

    public static String invocInfo() {
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        int i = 2;
        return String.format("%s\t%s.%s", ste[i].getFileName(), StringUtils.substringAfterLast(ste[i].getClassName(), "."), ste[i].getMethodName());
    }

    public static String invocationInfo(final int i) {
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        ste = ArrayUtils.subarray(ste, 0, i);
        return Stream.of(ste)
            .map(st -> String.format("%s\t%s.%s", st.getFileName(), st.getClassName(), st.getMethodName()))
            .collect(Collectors.joining("\n"));
    }

    public static String byteCountToDisplaySize(long size) {
        if (size > 1073741824L) return String.valueOf(size / 1073741824L) + " GB";
        if (size > 1048576L) return String.valueOf(size / 1048576L) + " MB";
        if (size > 1024L) return String.valueOf(size / 1024L) + " KB";
        return String.valueOf(size) + " bytes";
    }

    public static List<Long> pi2Longs(Integer n) {
        return pi(n).toString().substring(0, n).chars().filter(i -> CharUtils.isAsciiNumeric((char) i)).map(i -> i - 48).mapToObj(Long::valueOf).collect(Collectors.toList());
    }

    public static List<Long> pi2Longs(Integer n, int range) {
        List<Long> nums = pi2Longs(n);
        List<Long> _nums = new ArrayList<>();
        Random rand = new Random();
        for (ListIterator<Long> it = nums.listIterator(); it.hasNext(); ) {
            Long num = it.next();
            for (int i = 0, step = rand.nextInt(range); i < step && it.hasNext(); i++) {
                num = num * 10 + it.next();
            }
//            System.out.println(step + "\t" + num);
            _nums.add(num);
        }
        return _nums;
    }

    public static BigDecimal pi(Integer n) {
        if (n == null) {
            n = 0;
        }

        if (n == 0)
            return null;
        if (n == 1)
            return new BigDecimal(3);
        if (n == 2)
            return new BigDecimal(3.1);
        if (n == 3)
            return new BigDecimal(3.14);

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

        // pi.setScale(n, BigDecimal.ROUND_UP);
        pi.scaleByPowerOfTen(n);

        return pi;
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

    public static void easySleep(long i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
        }
    }

    public static String stackInfo() {
        final StringBuilder sb = new StringBuilder();
        final StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        ArrayUtils.reverse(stes);
        Stream.of(stes).forEach(ste -> sb.append(String.format("%s\t%s.%s\n", ste.getFileName(), ste.getClassName(), ste.getMethodName())));
        return sb.toString();
    }

    public static String now() {
        return DateFormatUtils.format(Calendar.getInstance(), "yy-MM-dd hh:mm:ss");
    }

    public static String toBinStr(final byte _b) {
        final StringBuilder sb = new StringBuilder();
        for (byte i = 7; i >= 0; i--) {
            sb.append((_b & (1 << i)) != 0 ? 1 : 0);
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

    public static String toBinStr(final short _s) {
        final StringBuilder sb = new StringBuilder();
        for (byte i = 16; i >= 0; i--) {
            sb.append((_s & (1 << i)) != 0 ? 1 : 0);
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

    public static class LoopingArrayIterator<E> extends ObjectArrayIterator<E> {
        @SafeVarargs
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map map(Object... keyAndVals) {
        return MapUtils.putAll(new HashMap(), keyAndVals);
    }

    public static long HOST_HASH = System.currentTimeMillis();

    static {
        try {
            HOST_HASH = InetAddress.getLocalHost().getHostAddress().hashCode();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static long uniqueLong() {
        return Math.abs(UUID.randomUUID().hashCode());
    }

    public static ThreadFactory namedThreadFactory(final String name) {
        return new BasicThreadFactory.Builder().namingPattern(name + "_%d").build();
    }

    public static String lineNumber(final String str) {
        if (str == null) {
            return null;
        }

        final StringReader sr = new StringReader(str);
        final BufferedReader br = new BufferedReader(sr);

        final StringBuilder sb = new StringBuilder(0);
        AtomicLong lineNumber = new AtomicLong(0);
        br.lines().forEach(line -> sb.append(lineNumber.incrementAndGet()).append("\t").append(line).append('\n'));

        return sb.toString();
    }

    private static final Logger log = LoggerFactory.getLogger(MiscUtils.class);

    public static String mapToJson(Map<String, String> paramMap) {
        if (MapUtils.isEmpty(paramMap)) {
            return "{}";
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(paramMap);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return paramMap.toString();
        }
    }

    public static String toXML(final Object bean) {
        final StringWriter sw = new StringWriter();

        try {
            JAXBContext jc = JAXBContext.newInstance(bean.getClass());
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FRAGMENT, true);
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(bean, sw);
        } catch (Exception e) {
            log.error(String.valueOf(bean), e);
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
            log.error(xmlStr, e);
        }

        return null;
    }

//    public static Map<String, String> extractParams(MultivaluedMap<String, String> params) {
//        Map<String, String> paramsMap = new HashMap<String, String>();
//        params.keySet().forEach(key -> paramsMap.put(key, params.getFirst(key)));
//        return paramsMap;
//    }
//
//    public static Map<String, String[]> toParamMap(MultivaluedMap<String, String> params) {
//        Map<String, String[]> paramsMap = new HashMap<String, String[]>();
//        params.keySet().forEach(key -> paramsMap.put(key, params.get(key).toArray(new String[0])));
//        return paramsMap;
//    }

    public static Map<String, String> extractParams(Map<String, String[]> params) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        params.keySet().forEach(key -> {
            final String[] vals = params.get(key);
            paramsMap.put(key, ArrayUtils.isEmpty(vals) ? null : vals[0]);
        });
        return paramsMap;
    }

    public static String generate(final String text) {
        final StringBuffer sb = new StringBuffer();
        try {
            final byte[] intext = text.getBytes();
            final MessageDigest md5 = MessageDigest.getInstance("MD5");
            final byte[] md5rslt = md5.digest(intext);
            for (int i = 0; i < md5rslt.length; i++) {
                final int val = 0xff & md5rslt[i];
                if (val < 16) {
                    sb.append("0");
                }
                sb.append(Integer.toHexString(val));
            }
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }
        return sb.toString();
    }

    public static String loadResAsString(final Class<?> cls, final String fileName) {
        if (cls == null || StringUtils.isBlank(fileName)) {
            return StringUtils.EMPTY;
        }

        try {
            return IOUtils.toString(cls.getClassLoader().getResourceAsStream(fileName));
        } catch (IOException e) {
            log.error("", e);
        }
        return StringUtils.EMPTY;
    }

    public static String getEncodedText(String plainText) {
        String encodedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            encodedPassword = new String(Hex.encodeHex(md.digest()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encodedPassword;
    }

    public static void writeToMappedFile(File file, byte[] data) throws IOException {
        try (FileChannel fc = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.READ)) {
            MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_WRITE, 0, data.length);
            mbb.put(data);
            mbb.force();
        }
    }

    public static byte[] readFromMappedFile(File file) throws IOException {
        try (FileChannel fc = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
            long length = file.length();
            MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, length);
            if (!mbb.load().isLoaded()) {
                throw new IllegalStateException(String.format("%s can not be loaded", file.getAbsolutePath()));
            }

            byte[] data = new byte[(int) length];
            mbb.get(data);
            return data;
        }
    }

    public static Class[] getParameterizedClzz(Type type) {
        if (type == null) return null;
        if (type instanceof Class) {
            Class aClass = (Class) type;
            return new Class[]{aClass};
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            return Stream.of(pt.getActualTypeArguments()).filter(_type -> _type instanceof Class).toArray(Class[]::new);
        }
        return null;
    }

    public static HttpResponse easyGet(String urlStr, String... params) {
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
                bhr.setEntity(new InputStreamEntity(new ByteArrayInputStream(EntityUtils.toByteArray(hr.getEntity()))));
                return bhr;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HttpResponse easyPost(String urlStr, String... params) {
        if (StringUtils.isBlank(urlStr)) {
            return null;
        }

        try (CloseableHttpClient hc = HttpClients.createDefault()) {
            URIBuilder ub = new URIBuilder(urlStr);
            HttpPost hp = new HttpPost(ub.build());
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            if (!ArrayUtils.isEmpty(params)) {
                IntStream.range(0, params.length).filter(i -> i % 2 == 1).forEach(i -> nvps.add(new BasicNameValuePair(params[i - 1], params[i])));
            }
            hp.setEntity(new UrlEncodedFormEntity(nvps));

            try (CloseableHttpResponse hr = hc.execute(hp)) {
                BasicHttpResponse bhr = new BasicHttpResponse(hr.getStatusLine());
                bhr.setEntity(new InputStreamEntity(new ByteArrayInputStream(EntityUtils.toByteArray(hr.getEntity()))));
                return bhr;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
