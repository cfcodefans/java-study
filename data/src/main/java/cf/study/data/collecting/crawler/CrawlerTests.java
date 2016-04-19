package cf.study.data.collecting.crawler;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
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
				Map<String, Object> attrMap = node.attributes().asList().stream().collect(Collectors.toMap(Attribute::getKey, (a)->String.format("\"%s\"", a.getValue())));
				attrMap.put("tag", node.nodeName());
				attrMap.put("depth", depth);
				reList.add(Jsons.toJson(attrMap));
			}
			
			@Override
			public void head(Node node, int depth) {}
		};
		
		doc.traverse(nodeVisitor);
		
		reList.forEach(log::info);
		log.info(reList.size());
		
		FileUtils.writeLines(Files.createFile(Paths.get(".\\test\\crawler\\maven.json")).toFile(), 
				reList.stream().map(JsonNode::toString).collect(Collectors.toList()));
	}
}
