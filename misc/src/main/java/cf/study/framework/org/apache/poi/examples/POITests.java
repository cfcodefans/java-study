package cf.study.framework.org.apache.poi.examples;

import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.POIDocument;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLProperties;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.HeaderStories;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Section;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageProperties;
import org.apache.poi.openxml4j.util.Nullable;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.junit.Assert;
import org.junit.Test;



public class POITests {
	@Test
	public void readDir() {
		
	}
	
	@Test
	public void readVersion() {
		// docx file with OOXMLHeader is word 2007+XML
		try {
			InputStream docInputStream = POITests.class.getResourceAsStream("sample.docx");
			
			boolean hasOOXMLHeader = POIXMLDocument.hasOOXMLHeader(docInputStream);
			System.out.println("hasOOXMLHeader: " + hasOOXMLHeader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		// doc file is word 97-2003
		try {
			InputStream docInputStream = POITests.class.getResourceAsStream("sample.doc");
			
			boolean hasOOXMLHeader = POIXMLDocument.hasOOXMLHeader(docInputStream);
			System.out.println("hasOOXMLHeader: " + hasOOXMLHeader);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPOIFSFileSystem() {
		try {
			InputStream docInputStream = POITests.class.getResourceAsStream("sample.doc");
			Assert.assertNotNull(docInputStream);
			
			POIFSFileSystem fs = new POIFSFileSystem(docInputStream);
			System.out.println(fs.getShortDescription());
			
			System.out.println(fs.getViewableArray().length);
			Stream.of(fs.getViewableArray()).forEach(System.out::println);
			
			DirectoryNode docRoot = fs.getRoot();
			docRoot.forEach(System.out::println);
			
			Entry smyEntry = docRoot.getEntry(SummaryInformation.DEFAULT_STREAM_NAME);
			PropertySet ps = PropertySetFactory.create(docRoot.createDocumentInputStream(smyEntry));
			
			System.out.println(ps);
			SummaryInformation si = (SummaryInformation)ps;
			System.out.println(si.getApplicationName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testHWPFDocument() throws Exception {
		InputStream docInputStream = POITests.class.getResourceAsStream("sample.doc");
		Assert.assertNotNull(docInputStream);
		
		HWPFDocument hwpf = new HWPFDocument(docInputStream);
		Assert.assertNotNull(hwpf);
		
//		System.out.println(hwpf.getText());
	}
	
	@Test
	public void testPOIDocument() throws Exception {
		InputStream docInputStream = POITests.class.getResourceAsStream("sample.doc");

		Assert.assertNotNull(docInputStream);

		POIDocument poiDoc = new HWPFDocument(docInputStream);
		
		System.out.println("POIDocument: " + poiDoc);
		System.out.println(StringUtils.repeat('\n', 3));
		
		
		SummaryInformation smy = poiDoc.getSummaryInformation();
		System.out.println("SummaryInformation: ");
		BeanUtils.describe(smy).forEach((k, v)->System.out.println(String.format("%s:\t%s", k, v)));
		
		System.out.println(smy.getOSVersion());
		
		System.out.println(StringUtils.repeat('\n', 3));
		
		
		
		DocumentSummaryInformation docSmy = poiDoc.getDocumentSummaryInformation();
		System.out.println("DocumentSummaryInformation: ");
		
		Stream.of(PropertyUtils.getPropertyDescriptors(docSmy)).map(PropertyDescriptor::getName).forEach(pdName->{
			try {System.out.println(String.format("%s:\t%s",pdName, PropertyUtils.getSimpleProperty(docSmy, pdName)));} catch(Exception e) {};
		});
		
		System.out.println(StringUtils.repeat('\n', 3));
	}
	
	@Test
	public void testFalseDoc() throws Exception {
		InputStream docInputStream = POITests.class.getResourceAsStream("false.xls");
		Assert.assertNotNull(docInputStream);

		POIDocument poiDoc = new HWPFDocument(docInputStream);
		System.out.println(poiDoc.getSummaryInformation().getApplicationName());
	}
	
	@Test
	public void testOPCPackage() throws Exception {
		{// can't parse Office 97-2003
			InputStream docInputStream = POITests.class.getResourceAsStream("sample.xlsx");
			Assert.assertNotNull(docInputStream);
			
			OPCPackage opc = OPCPackage.open(docInputStream);
			System.out.println(opc);
			
			PackageProperties pkgProps = opc.getPackageProperties();
			System.out.println(pkgProps);
			Stream.of(PropertyUtils.getPropertyDescriptors(pkgProps)).map(PropertyDescriptor::getName)
					.forEach(pdName -> {
						try {
							Object val = PropertyUtils.getSimpleProperty(pkgProps, pdName);
							if (val instanceof Nullable) {
								Nullable n = (Nullable) val;
								System.out.println(String.format("%s:\t%s", pdName, n.getValue()));
								return;
							} 
							System.out.println(String.format("%s:\t%s", pdName, val));
						} catch (Exception e) {
						}
					} );
			
			System.out.println(StringUtils.repeat('\n', 2));
			
			opc.getParts().forEach(System.out::println);
			System.out.println(StringUtils.repeat('\n', 2));
			
			POIXMLProperties poiXmlProps = new POIXMLProperties(opc);
			BeanUtils.describe(poiXmlProps.getCoreProperties()).forEach((k, v)->System.out.println(String.format("%s:\t%s", k, v)));
			System.out.println(StringUtils.repeat('\n', 2));
			
			BeanUtils.describe(poiXmlProps.getExtendedProperties()).forEach((k, v)->System.out.println(String.format("%s:\t%s", k, v)));
			System.out.println(StringUtils.repeat('\n', 2));
		}
	}
	

	@Test
	public void testHWPFHeaderAndFooter() throws Exception {
		final InputStream is = this.getClass().getResourceAsStream("sample.doc");
		final HWPFDocument wd = new HWPFDocument(is);
		
		Range hsr = wd.getHeaderStoryRange();
		System.out.println(hsr);
		BeanUtils.describe(hsr).forEach((k, v)->System.out.println(String.format("%s:\t%s", k, v)));
		
		System.out.println();
		System.out.println(hsr.numCharacterRuns());
		IntStream.range(0, hsr.numCharacterRuns()).forEach(i->{
			CharacterRun p0 = hsr.getCharacterRun(i);
			System.out.println(p0);
			System.out.println(p0.text());
			System.out.println();
		});
		
		System.out.println();
		System.out.println(hsr.numSections());
		IntStream.range(0, hsr.numSections()).forEach(i->{
			Section p0 = hsr.getSection(i);
			System.out.println(p0);
			System.out.println(p0.text());
			System.out.println();
		});
		
		System.out.println();
		System.out.println(hsr.numParagraphs());
		IntStream.range(0, hsr.numParagraphs()).forEach(i->{
			Paragraph p0 = hsr.getParagraph(i);
			System.out.println(p0);
			System.out.println(p0.text());
			System.out.println();
		});
		
//		System.out.println();
//		Section h11 = h1.getSection(hsr.getStartOffset());
//		System.out.println(h11);
//		System.out.println(h11.text());
		
//		System.out.println();
//		Range dr = wd.getRange();
//		System.out.println(dr);
		
//		System.out.println();
		HeaderStories hs = new HeaderStories(wd);
		System.out.println(hs.getEvenFooterSubrange());
		System.out.println(hs.getOddFooterSubrange());
		System.out.println(hs.getOddFooterSubrange().text());
		System.out.println(hs.getOddHeaderSubrange());
		System.out.println(hs.getOddHeaderSubrange().text());
//		Range fhs = hs.getFirstHeaderSubrange();
//		System.out.println(fhs);
//		System.out.println(fhs.text());
//		Range r = hs.getRange();
//		System.out.println(r);
//		System.out.println(r.text());
	}
}

