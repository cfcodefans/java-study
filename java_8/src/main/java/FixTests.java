import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import com.google.common.base.Predicate;

public class FixTests {

	public static final Path LOG_PATH = Paths.get("D:\\dev");
	
	@Test
	public void fix() throws Exception {
		List<File> fileList = Arrays.asList(LOG_PATH.toFile().listFiles());
		Collections.sort(fileList);
		
		List<String> lines = new ArrayList<String>(10000);
		fileList.stream().filter(f->f.getName().contains("server.log_2015-06-2")).map(this::extract).forEach(lines::addAll);
		
		FileUtils.writeLines(new File("D:\\dev\\output.txt"), lines);
	}
	
	@Test
	public void getSubscriptions() throws Exception {
		List<String> logs = combine(FileUtils.readLines(new File(outputPath)));
		
		logs.stream().filter(log->log.contains("SubsConfirmServlet")).map(log->{
			String uid = StringUtils.substringBetween(log, "uid-", "\n");
			String transaction = StringUtils.substringBetween(log, "tan-", "\n");
			String article = StringUtils.substringBetween(log, "article-", "\n");
			String p_datetime = StringUtils.substringBetween(log, "p_datetime-", "\n");
			String actionType = StringUtils.substringBetween(log, "actionType-", "\n");
			String status = StringUtils.substringBetween(log, "status-", "\n");
			
			return "http://10.20.0.254:8080/payment/callback?uid=" + uid 
					+ "&tan=" + transaction 
					+ "&status=" + status 
					+ "&article="+ article
					+ "&provider=LIBE&actionType=" + actionType 
					+ "&DOMMAINID=POPP&p_datetime=" + p_datetime;
//			return StringUtils.join(Arrays.asList(uid, transaction, article, p_datetime, actionType, status), '\t');
		}).forEach(System.out::println);
	}
	
	String outputPath = "D:\\dev\\output.txt";
	@Test
	public void getBookings() throws Exception {
		List<String> logs = combine(FileUtils.readLines(new File(outputPath)));
		
		Integer[] excludedIds = new Integer[] {
				4009168, 4589066, 5598903, 5696252, 5760015, 5763298
		};
		
		logs.stream()
		.filter(log->log.contains("STATUS:2"))
		.filter(log->noContains(log, excludedIds))
		.map(log->{
			String uid = StringUtils.substringBetween(log, "USERNR:", "\n");
			String transaction = StringUtils.substringBetween(log, "TANR:", "\n");
			String article = StringUtils.substringBetween(log, "ARTICLENR:", "\n");
			String p_datetime = StringUtils.substringBetween(log, "DATE:", "\n");
			
//			return "http://10.20.0.254:8080/payment/callback?uid=4961484&tan=30436489&status=0&article=2&provider=CAB&actionType=15&DOMMAINID=POPP&p_datetime=1433386196000";
//			return uid + ",";
//			return StringUtils.join(Arrays.asList(uid, transaction, article, p_datetime), '\t');
			try {
				return "http://10.20.0.254:8080/payment/callback?uid=" + uid
						+ "&tan=" + transaction
						+ "&status=1"
						+ "&article=" + article
						+ "&provider=LIBE"
						+ "&actionType=12"
						+ "&DOMMAINID=POPP"
						+ "&p_datetime=" + DateUtils.parseDate(p_datetime, "dd.MM.yyyy hh:mm:ss").getTime();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}).forEach(System.out::println);
	}
	
	@Test
	public void getChargebackLift() throws Exception {
		List<String> logs = combine(FileUtils.readLines(new File(outputPath)));
		
		logs.stream().filter(log->log.contains("STATUS:9")).map(log->{
			String uid = StringUtils.substringBetween(log, "USERNR:", "\n");
			String transaction = StringUtils.substringBetween(log, "TANR:", "\n");
			String article = StringUtils.substringBetween(log, "ARTICLENR:", "\n");
			String p_datetime = StringUtils.substringBetween(log, "DATE:", "\n");
			
			try {
				return "http://10.20.0.254:8080/payment/callback?uid=" + uid
						+ "&tan=" + transaction
						+ "&status=1"
						+ "&article=" + article
						+ "&provider=LIBE"
						+ "&actionType=15"
						+ "&DOMMAINID=POPP"
						+ "&p_datetime=" + DateUtils.parseDate(p_datetime, "dd.MM.yyyy hh:mm:ss").getTime();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
//			return StringUtils.join(Arrays.asList(uid, transaction, article, p_datetime), '\t');
		}).forEach(System.out::println);
	}
	
	@Test
	public void getReverse() throws Exception {
		List<String> logs = combine(FileUtils.readLines(new File(outputPath)));
		
		logs.stream().filter(log->log.contains("STATUS:11")).map(log->{
			String uid = StringUtils.substringBetween(log, "USERNR:", "\n");
			String transaction = StringUtils.substringBetween(log, "TANR:", "\n");
			String article = StringUtils.substringBetween(log, "ARTICLENR:", "\n");
			String p_datetime = StringUtils.substringBetween(log, "DATE:", "\n");
			
			try {
				return "http://10.20.0.254:8080/payment/callback?uid=" + uid
						+ "&tan=" + transaction
						+ "&status=0"
						+ "&article=" + article
						+ "&provider=LIBE"
						+ "&actionType=16"
						+ "&DOMMAINID=POPP"
						+ "&p_datetime=" + DateUtils.parseDate(p_datetime, "dd.MM.yyyy hh:mm:ss").getTime();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
//			return StringUtils.join(Arrays.asList(uid, transaction, article, p_datetime), '\t');
		}).forEach(System.out::println);
	}
	
	private boolean contains(String log, Object...targets) {
		return Stream.of(targets).map(String::valueOf).anyMatch(log::contains);
	}
	
	private boolean noContains(String log, Object...targets) {
		return !Stream.of(targets).map(t -> "USERNR:" + String.valueOf(t)).anyMatch(log::contains);
	}
	
	@Test
	public void getCancels() throws Exception {
		List<String> logs = combine(FileUtils.readLines(new File(outputPath)));
		//1981955
		//2678929
		//4918197
		//5365259
		//5578734
		//5725266
//		String[] targetIds = new String[] {"1981955", "2678929", "4918197", "5365259", "5578734", "5725266"};
//		Stream.of(targetIds).anyMatch(id-> log.contains(id));
		Integer[] ids = new Integer[]{214099,
				275454,
				390385,
				428543,
				453903,
				469475,
				557298,
				581496,
				602324,
				699802,
				781686,
				807469,
				847266,
				872218,
				934189,
				943276,
				961999,
				1047710,
				1254526,
				1268442,
				1293163,
				1307608,
				1395622,
				1399415,
				1470307,
				1531791,
				1616438,
				1625182,
				1685681,
				1767763,
				1906247,
				1935994,
				1957170,
				2021731,
				2069477,
				2122231,
				2130086,
				2295397,
				2349494,
				2366723,
				2416931,
				2437504,
				2488024,
				2611731,
				2645550,
				2649795,
				2665576,
				2865401,
				2898421,
				3047111,
				3146040,
				3179690,
				3192272,
				3212153,
				3247384,
				3258958,
				3269469,
				3313202,
				3329646,
				3376265,
				3559204,
				3699525,
				3715471,
				3750785,
				3771708,
				3819300,
				3874641,
				3926353,
				3940414,
				3960542,
				3987526,
				4128409,
				4162319,
				4165415,
				4311659,
				4382036,
				4423001,
				4473804,
				4476831,
				4480588,
				4507525,
				4560802,
				4573613,
				4582024,
				4589066,
				4590151,
				4633282,
				4692133,
				4712984,
				4718527,
				4736901,
				4746556,
				4751412,
				4751797,
				4757263,
				4776546,
				4777833,
				4782375,
				4784722,
				4792561,
				4794984,
				4811273,
				4815215,
				4842615,
				4847433,
				4867115,
				4868972,
				4881158,
				4890712,
				4920058,
				4972591,
				4973920,
				4994928,
				5013217,
				5047077,
				5070247,
				5102289,
				5128973,
				5130841,
				5138497,
				5138975,
				5156054,
				5170755,
				5175723,
				5183978,
				5191829,
				5226159,
				5239926,
				5243242,
				5243926,
				5258217,
				5310117,
				5329543,
				5334316,
				5360449,
				5410193,
				5411643,
				5460358,
				5474009,
				5484267,
				5484863,
				5494058,
				5508769,
				5518652,
				5530136,
				5542859,
				5548052,
				5559353,
				5561077,
				5572875,
				5578287,
				5588619,
				5590047,
				5597772,
				5598222,
				5598903,
				5600454,
				5605463,
				5609126,
				5620740,
				5631707,
				5633514,
				5648869,
				5660284,
				5670344,
				5675378,
				5677333,
				5679296,
				5682893,
				5693984,
				5694924,
				5695241,
				5696252,
				5702904,
				5704494,
				5708937,
				5709282,
				5714728,
				5719523,
				5726275,
				5732802,
				5734342,
				5749217,
				5749782,
				5755477,
				5758188,
				5759984,
				5763298,
				5763921,
				5774474,
				5775778,
				5776742,
				5777402,
				5778130,
				5780383,
				5784082,
				5784177,
				5787953,
				5788325,
				5789798,
				5789947,
				5792184,
				5792336,
				5796026,
				5796191,
				5797040,
				5801736,
				5801984,
				5802886,
				5804336,
				5804849,
				5805116,
				5806589,
				5807152};
		
		
		
		logs.stream()
		.filter(log->log.contains("STATUS:30"))
		.filter(log->contains(log, ids))
		.map(log->{
			String uid = StringUtils.substringBetween(log, "USERNR:", "\n");
			String transaction = StringUtils.substringBetween(log, "TANR:", "\n");
			String article = StringUtils.substringBetween(log, "ARTICLENR:", "\n");
			String p_datetime = StringUtils.substringBetween(log, "DATE:", "\n");
			
//			return StringUtils.join(Arrays.asList(uid, transaction, article, p_datetime), '\t');
			try {
//				return uid;
				return "http://10.20.0.254:8080/payment/callback?uid=" + uid
						+ "&tan=" + transaction
						+ "&status=1"
						+ "&article=" + article
						+ "&provider=LIBE"
						+ "&actionType=17"
						+ "&DOMMAINID=POPP"
						+ "&p_datetime=" + DateUtils.parseDate(p_datetime, "dd.MM.yyyy hh:mm:ss").getTime();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}).forEach(System.out::println);
	}
	
	private List<String> extract(File file) {
		List<String> lines = Collections.emptyList();
		try {
			lines = FileUtils.readLines(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return lines;
		}
		List<String> changeList = new ArrayList<String>(1000);
		
		List<String> logList = combine(lines);
		
		for (int i = 0, s = logList.size(); i < s; i++) {
			String log = logList.get(i);
			if (!log.contains("javax.jms.Session.mqjmsra")) {
				continue;
			}
			
			String threadID = StringUtils.substringBetween(log, "ThreadID=", ";");
			
			for (int _i = i - 1; _i > 0; _i--) {
				String _log = logList.get(_i);
				if (_log.contains("ThreadID=" + threadID)
						&& (_log.contains("USERNR") || _log.contains("uid-"))) {
					changeList.add(_log);
					break;
				}
			}
		}
		
		return changeList;
	}

	private List<String> combine(List<String> lines) {
		List<String> logList = new ArrayList<String>(1000);
		StringBuilder sb = null;
		for (int i = 0, s = lines.size(); i < s; i++) {
			String line = lines.get(i).trim();
			if (StringUtils.isBlank(line)) {
				continue;
			}
			
			if (line.startsWith("[#")) {
				sb = new StringBuilder();
			}
			sb.append(line).append('\n');
			if (line.contains("#]")) {
				logList.add(sb.toString());
				sb = null;
			}
		}
		return logList;
	}
}
