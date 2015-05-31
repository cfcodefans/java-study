package cf.study.framework.apache.poi.examples;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;

import javax.activation.UnsupportedDataTypeException;
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
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.converter.WordToHtmlUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.XMLHelper;
import org.junit.Test;
import org.w3c.dom.Document;

public class POIUtils {

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
			SummaryInformation si = (SummaryInformation)ps;
			di.appName = si.getApplicationName();
		}
		
		return di;
	}
	
	public static boolean isPOIXML(final InputStream is) throws Exception {
		return POIXMLDocument.hasOOXMLHeader(is);
	}
	
	public static Document convertOfficFileToHtml(final InputStream is) throws Exception {
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
		
		if (docInfo.version == Version.OFFICE_97_2003) {
			if ("Microsoft Office Word".equals(docInfo.appName)) {
				final HWPFDocumentCore wordDocument = WordToHtmlUtils.loadDoc(bis);
				WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
						XMLHelper.getDocumentBuilderFactory().newDocumentBuilder().newDocument());
				wordToHtmlConverter.processDocument(wordDocument);
				return wordToHtmlConverter.getDocument();
			}
		} else if (docInfo.version == Version.OFFICE_2007) {
			
		}
		
		return null;
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
			Document htmlDoc = convertOfficFileToHtml(this.getClass().getResourceAsStream("sample.doc"));
			String domToString = domToString(htmlDoc);
			System.out.println(domToString);
			FileUtils.writeStringToFile(new File("./test/docs/sample.html"), domToString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
