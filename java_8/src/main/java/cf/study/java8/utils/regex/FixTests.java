package cf.study.java8.utils.regex;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class FixTests {

	public static final String pathStr = "D:\\dev";
	
	public List<File> getAllLogs() {
		List<File> fileList = Stream.of(Paths.get(pathStr).toFile().listFiles()).filter(f->f.getName().contains("server.log_2015-06-27")).collect(Collectors.toList());
		Collections.sort(fileList);
		return fileList;
	}
	
	@Test
	public void fix() throws Exception {
		List<File> fileList = getAllLogs();
//		fileList.forEach(System.out::println);
		
		List<String> tmpList = new LinkedList<String>();
		
		fileList.stream().map(this::extract).forEach(tmpList::addAll);
		
//		tmpList.forEach(System.out::println);
		
		FileUtils.writeLines(new File("tmp.txt"), tmpList);
	}
	
	
	
	@SuppressWarnings("unchecked")
	private List<String> extract(File f) {
		List<String> tmpList = new LinkedList<String>();
		
		try {
			List<String> lines = FileUtils.readLines(f);
			
			for (int i = 0, j = lines.size(); i < j; i++) {
				String line = lines.get(i);
				if (line.contains("javax.jms.Session.mqjmsra")) {
					String threadID = StringUtils.substringBetween(line, "ThreadID=", ";");
					if (StringUtils.isBlank(threadID)) {
						continue;
					}
					
					String tmp = "";
					for (int _i = i - 1; _i > 0; _i--) {
						String _line = lines.get(_i);
						tmp = _line + "\n" + tmp;
						if ((_line.contains("ThreadID=" + threadID) && !_line.contains("CAB") && !_line.contains("ERROR"))
								&& (tmp.contains("USERNR") || tmp.contains("uid-"))){
							tmpList.add(StringUtils.substringBefore(tmp, "|#]") + "|#]\n");
							System.out.println(tmpList.size());
							break;
						}
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return tmpList;
	}
}
