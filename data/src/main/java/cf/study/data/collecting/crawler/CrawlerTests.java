package cf.study.data.collecting.crawler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import misc.Jsons;

public class CrawlerTests {

	private static final Logger log = Logger.getLogger(CrawlerTests.class);

	@Test
	public void testSimpleCrawler() throws Exception {
		Document doc = Jsoup.connect("http://develop.poppen.lab/").get();

		List<JsonNode> reList = new LinkedList<>();

		NodeVisitor nodeVisitor = new NodeVisitor() {
			@Override
			public void tail(Node node, int depth) {
				String nodeName = node.nodeName();
				if ("#text".equals(nodeName)) {
					return;
				}

				Map<String, Object> attrMap = new HashMap<>();
				if (!"#data".equals(nodeName)) {
					attrMap.putAll(node.attributes().asList().stream()
							.filter(attr -> "comment".equals(attr.getKey()))
							.collect(Collectors.toMap(Attribute::getKey, 
									(a) -> String.format("\"%s\"", StringUtils.replaceChars(StringUtils.trim(a.getValue()), '\n', ' ')))));
				}

				attrMap.put("tag", String.format("\"%s\"", StringUtils.replaceChars(nodeName, '\n', ' ')));
				attrMap.put("depth", depth);
				reList.add(Jsons.toJson(attrMap));
			}

			@Override
			public void head(Node node, int depth) {}
		};

		doc.traverse(nodeVisitor);

		reList.forEach(log::info);
		log.info(reList.size());

		String outputFilePathStr = ".\\test\\crawler\\maven.json";
		Path path = Paths.get(outputFilePathStr);
		Files.deleteIfExists(path);
		FileUtils.writeLines(Files.createFile(path).toFile(), reList.stream().map(JsonNode::toString).collect(Collectors.toList()));
	}
}
