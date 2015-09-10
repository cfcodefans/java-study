package cf.study.jdk7.text.html;

import java.io.StringWriter;

import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.junit.Test;


public class HtmlTest {

	@Test
	public void testCreateHtml() throws Exception {
		HTMLEditorKit htmlKit = new HTMLEditorKit();
		HTMLDocument hd = (HTMLDocument) htmlKit.createDefaultDocument();
		
		hd.insertBeforeEnd(hd.getBidiRootElement(), HTML.Tag.TABLE.toString());
		System.out.println(HTML.Tag.TABLE);
		System.out.println(hd.getBidiRootElement());
		
		StringWriter out = new StringWriter();
		htmlKit.write(out , hd, 0, hd.getLength());
		out.flush();
		
		System.out.println(out.getBuffer());
	}
}
