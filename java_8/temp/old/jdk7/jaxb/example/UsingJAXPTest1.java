package cf.study.jdk7.jaxb.example;

/**
 * $Id: UsingJAXPTest1.java,v 1.1 2003/01/01 03:18:32 bhakti Exp $
 * Copyright (c) 2003 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * This shows how to parse an XML File using SAX and display the same
 * information from the xml file by implementing the ContentHandler interface
 */

public class UsingJAXPTest1 {

	public static void main(String args[]) {

		FileInputStream instream = null;
		InputSource is = null;
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			XMLReader xmlReader = spf.newSAXParser().getXMLReader();

			xmlReader.setContentHandler(new MyContentHandler());

			instream = new FileInputStream("books.xml");
			is = new InputSource(instream);

			xmlReader.parse(is);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (SAXException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			System.exit(0);
		}

	}
}

class MyContentHandler extends XMLFilterImpl {
	boolean printMe = false;
	static int authorCount = 0;

	private boolean getPrintValue() {
		return printMe;
	}

	private void setPrintValue(boolean printValue) {
		printMe = printValue;
	}

	public void characters(char[] ch, int start, int length) {
		String gotString = new String(ch, start, length);
		if (printMe) {
			if (!gotString.trim().equals("")) {
				System.out.println(gotString.trim());
			}
		}
	}

	public void startElement(String namespaceURI, String localName, String qName, Attributes attributes) {

		if (localName.equals("book")) {
			authorCount = 0;
			System.out.println("\nBook Details");
			int attrsLength = attributes.getLength();
			for (int i = 0; i < attrsLength; i++) {
				String itemId = attributes.getValue(i);
				System.out.println("ItemId " + itemId);

			}
		}

		if (localName.equals("ISBN") || localName.equals("price") || localName.equals("description") || localName.equals("Discount") || localName.equals("bookCategory")
				|| localName.equals("name")) {

			System.out.println("Book " + localName + ": ");
			printMe = true;

		} else if (localName.equals("authorName")) {
			System.out.println("Book " + localName + ": ");
			authorCount++;
			printMe = true;

		} else

			printMe = false;

	}

	public void endElement(String namespaceURI, String localName, String qName) {
		if (localName.equals("authors"))
			System.out.println("No of authors :" + authorCount);
	}

}
