package cf.study.framework.apache.poi.examples;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;

import javax.activation.UnsupportedDataTypeException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLProperties;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.HeaderStories;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Section;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.XMLHelper;
import org.docx4j.Docx4J;
import org.docx4j.Docx4jProperties;
import org.docx4j.convert.in.Doc;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.Test;
import org.w3c.dom.Document;

public class POIUtils {

	static {
		Docx4jProperties.setProperty("docx4j.Convert.Out.HTML.OutputMethodXML", true);
	}
	
	public static enum Version {
		OFFICE_97_2003, OFFICE_2007;
	}

	public static class DocInfo {
		public Version version;
		public String appName;

		@Override
		public String toString() {
			return "DocInfo [version=" + version + ", appName=" + appName + "]";
		}
	}

	public static DocInfo getDocInfo(final InputStream is) throws Exception {
		if (is == null) {
			return null;
		}

		final DocInfo di = new DocInfo();

		if (isPOIXML(is)) {
			di.version = Version.OFFICE_2007;
			OPCPackage opc = OPCPackage.open(is);
			POIXMLProperties poiXmlProps = new POIXMLProperties(opc);
			di.appName = poiXmlProps.getExtendedProperties().getApplication();
		} else {
			di.version = Version.OFFICE_97_2003;
			POIFSFileSystem fs = new POIFSFileSystem(is);
			DirectoryNode docRoot = fs.getRoot();
			Entry smyEntry = docRoot.getEntry(SummaryInformation.DEFAULT_STREAM_NAME);
			PropertySet ps = PropertySetFactory.create(docRoot.createDocumentInputStream(smyEntry));
			SummaryInformation si = (SummaryInformation) ps;
			di.appName = si.getApplicationName();
		}

		return di;
	}

	public static boolean isPOIXML(final InputStream is) throws Exception {
		return POIXMLDocument.hasOOXMLHeader(is);
	}

	private static final String RELATEDS = "./test/docx4j/related";
	
	static class TestWordToHtmlConverter extends WordToHtmlConverter {
		private static final POILogger logger = POILogFactory.getLogger(TestWordToHtmlConverter.class);
		
		private HeaderStories headerStories = null;
		
		public TestWordToHtmlConverter(Document document) {
			super(document);
		}
		
		@Override
	    protected void afterProcess() {
			super.afterProcess();
			headerStories = null;
		}
		
		public void processDocument(HWPFDocument wordDocument) {
			headerStories = new HeaderStories(wordDocument);
			super.processDocument(wordDocument);
		}
	
		protected void processHeader(HWPFDocumentCore wordDocument) {
			
		}
		
		protected void processSection(HWPFDocumentCore wordDocument, Section section, int sectionCounter) {
			super.processSection(wordDocument, section, sectionCounter);
		}

		@Override
		protected void processSingleSection(HWPFDocumentCore wordDocument, Section section) {
			super.processSingleSection(wordDocument, section);
		}
	}
	
	public static String convertWordToHtmlByPOI(final InputStream is) throws Exception {
		final HWPFDocument wordDocument = new HWPFDocument(is);// WordToHtmlUtils.loadDoc(is);
		WordToHtmlConverter wordToHtmlConverter = new TestWordToHtmlConverter(XMLHelper.getDocumentBuilderFactory().newDocumentBuilder().newDocument());
		
		wordToHtmlConverter.processDocument(wordDocument);
		
		return domToString(wordToHtmlConverter.getDocument());
	}
	
	public static String convertWordToHtmlByDocx4J(final InputStream bis, DocInfo docInfo) throws Exception {
		WordprocessingMLPackage wordMLPackage = null;
		if (docInfo.version == Version.OFFICE_97_2003) {
			wordMLPackage = Doc.convert(bis);
		}
		
		if (docInfo.version == Version.OFFICE_2007) {
			wordMLPackage = Docx4J.load(bis);
		}
		
		// HTML exporter setup (required)
		// .. the HTMLSettings object
		HTMLSettings htmlSettings = Docx4J.createHTMLSettings();

		htmlSettings.setImageDirPath(RELATEDS + "_files");
		htmlSettings.setImageTargetUri(RELATEDS.substring(RELATEDS.lastIndexOf("/") + 1) + "_files");
		htmlSettings.setWmlPackage(wordMLPackage);

		/*
		 * CSS reset, see
		 * http://itumbcom.blogspot.com.au/2013/06/css-reset
		 * -how-complex-it-should-be.html
		 * 
		 * motivated by vertical space in tables in Firefox and Google
		 * Chrome.
		 * 
		 * If you have unwanted vertical space, in Chrome this may be
		 * coming from -webkit-margin-before and -webkit-margin-after
		 * (in Firefox, margin-top is set to 1em in html.css)
		 * 
		 * Setting margin: 0 on p is enough to fix it.
		 * 
		 * See further
		 * http://www.css-101.org/articles/base-styles-sheet-
		 * for-webkit-based-browsers/
		 */
//			String userCSS = "html, body, div, span, h1, h2, h3, h4, h5, h6, p, a, img,  ol, ul, li, table, caption, tbody, tfoot, thead, tr, th, td " + "{ margin: 0; padding: 0; border: 0;}"
//					+ "body {line-height: 1;} ";
//			htmlSettings.setUserCSS(userCSS);

		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		Docx4J.toHTML(htmlSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);
		return new String(os.toByteArray());
	}
	
	public static String convertWordToHtml(final InputStream is) throws Exception {
		if (is == null || is.available() == 0) {
			return null;
		}

		byte[] bytes = IOUtils.toByteArray(is);
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		System.out.println(bytes.length);

		final DocInfo docInfo = getDocInfo(bis);
		if (docInfo == null) {
			throw new UnsupportedDataTypeException("unknown doc type");
		}

		bis.reset();

		if ("Microsoft Office Word".equals(docInfo.appName)) {
			if (docInfo.version == Version.OFFICE_97_2003) {
				return convertWordToHtmlByPOI(bis);
			}
			return convertWordToHtmlByDocx4J(bis, docInfo);
		}

		return null;
	}
	
	static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	static {
		factory.setNamespaceAware(true);
	}

	private static Document byteArray2Document(byte[] documentoXml) throws Exception {
		System.out.println("processed: " + documentoXml.length);
//		System.out.println(new String(documentoXml));
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new ByteArrayInputStream(documentoXml));
	}

	public static String domToString(final Document doc) {
		if (doc == null) {
			return StringUtils.EMPTY;
		}

		StringWriter out = new StringWriter();
		try {
			StreamResult streamResult = new StreamResult(out);
			DOMSource domSource = new DOMSource(doc);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer serializer = tf.newTransformer();
			// TODO set encoding from a command argument
			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty(OutputKeys.METHOD, "html");
			serializer.transform(domSource, streamResult);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return out.getBuffer().toString();
	}

	@Test
	public void testGetDocInfo() {
		try {
			System.out.println(getDocInfo(this.getClass().getResourceAsStream("false.xls")));
			System.out.println(getDocInfo(this.getClass().getResourceAsStream("sample.xls")));
			System.out.println(getDocInfo(this.getClass().getResourceAsStream("sample.xlsx")));
			System.out.println(getDocInfo(this.getClass().getResourceAsStream("sample.doc")));
			System.out.println(getDocInfo(this.getClass().getResourceAsStream("sample.docx")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testConversion() {
		try {
			String htmlDocStr = convertWordToHtml(this.getClass().getResourceAsStream("sample.doc"));
			String domToString = new String(htmlDocStr);
//			System.out.println(domToString);
			FileUtils.writeStringToFile(new File("./test/docs/sample_doc.html"), domToString);
		} catch (Exception e) {
			e.printStackTrace();
		}

//		try {
//			String htmlDocStr = convertWordToHtml(this.getClass().getResourceAsStream("sample.docx"));
//			String domToString = new String(htmlDocStr);
////			System.out.println(domToString);
//			FileUtils.writeStringToFile(new File("./test/docs/sample_docx.html"), domToString);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
