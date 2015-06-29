import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class FixTests {

	public static final Path LOG_PATH = Paths.get("test");
	
	@Test
	public void fix() throws Exception {
		List<File> fileList = Arrays.asList(LOG_PATH.toFile().listFiles());
		Collections.sort(fileList);
		
		List<String> lines = new ArrayList<String>(10000);
		fileList.stream().filter(f->f.getName().contains("server.log")).map(this::extract).forEach(lines::addAll);
		
		FileUtils.writeLines(new File("test/output.txt"), lines);
	}
	
	@Test
	public void getSubscriptions() throws Exception {
		List<String> logs = combine(FileUtils.readLines(new File("test/output.txt")));
		
		logs.stream().filter(log->log.contains("SubsConfirmServlet")).map(log->{
			String uid = StringUtils.substringBetween(log, "uid-", "\n");
			String transaction = StringUtils.substringBetween(log, "tan-", "\n");
			String article = StringUtils.substringBetween(log, "article-", "\n");
			String p_datetime = StringUtils.substringBetween(log, "p_datetime-", "\n");
			String actionType = StringUtils.substringBetween(log, "actionType-", "\n");
			String status = StringUtils.substringBetween(log, "status-", "\n");
			
			return StringUtils.join(Arrays.asList(uid, transaction, article, p_datetime, actionType, status), '\t');
		}).forEach(System.out::println);
	}
	
	@Test
	public void getBookings() throws Exception {
		List<String> logs = combine(FileUtils.readLines(new File("test/output.txt")));
		
		logs.stream().filter(log->log.contains("STATUS:2")).map(log->{
			String uid = StringUtils.substringBetween(log, "USERNR:", "\n");
			String transaction = StringUtils.substringBetween(log, "TANR:", "\n");
			String article = StringUtils.substringBetween(log, "ARTICLENR:", "\n");
			String p_datetime = StringUtils.substringBetween(log, "DATE:", "\n");
			
			return StringUtils.join(Arrays.asList(uid, transaction, article, p_datetime), '\t');
		}).forEach(System.out::println);
	}
	
	@Test
	public void getCancels() throws Exception {
		List<String> logs = combine(FileUtils.readLines(new File("test/output.txt")));
		
		logs.stream().filter(log->log.contains("STATUS:30")).map(log->{
			String uid = StringUtils.substringBetween(log, "USERNR:", "\n");
			String transaction = StringUtils.substringBetween(log, "TANR:", "\n");
			String article = StringUtils.substringBetween(log, "ARTICLENR:", "\n");
			String p_datetime = StringUtils.substringBetween(log, "DATE:", "\n");
			
			return StringUtils.join(Arrays.asList(uid, transaction, article, p_datetime), '\t');
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
