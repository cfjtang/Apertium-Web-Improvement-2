/*
 * Copyright (C) 2015 Jacob Nordfalk
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package apertiumview.sourceeditor;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
* Stripped PositionalXMLReader.java from http://stackoverflow.com/questions/4915422/get-line-number-from-xml-node-java
*/
public class PositionalXMLReader {
	final static String LINE_NUMBER_KEY_NAME = "lineNumber";

	public static Document createDocumentWithLineNumbers(Reader reader) throws IOException, SAXException, ParserConfigurationException {
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

		DefaultHandler handler = new DefaultHandler() {
			private Locator locator;
			Stack<Element> elementStack = new Stack<Element>();

			@Override
			public void setDocumentLocator(final Locator locator) {
				this.locator = locator; // Save the locator, so that it can be used later for line tracking when traversing nodes.
			}

			@Override
			public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
					throws SAXException {
				final Element el = doc.createElement(qName);
				el.setUserData(LINE_NUMBER_KEY_NAME, this.locator.getLineNumber(), null);
				elementStack.push(el);
			}

			@Override
			public void endElement(final String uri, final String localName, final String qName) {
				final Element closedEl = elementStack.pop();
				if (elementStack.isEmpty()) { // Is this the root element?
					doc.appendChild(closedEl);
				} else {
					final Element parentEl = elementStack.peek();
					parentEl.appendChild(closedEl);
				}
			}
		};
		parser.parse(new InputSource(reader), handler);

		return doc;
	}

	/**
	Parses an input stream as XML and finds a specific tag
	@param reader input
	@param tagName name of tag
	@param tagNumber how many of tagName should be skipped
	@return line number of the the tagNumer-th instance of tagName.
	@throws IOException
	@throws ParserConfigurationException
	@throws SAXException
	*/
	public static int findLinenumberOfTag(Reader reader, String tagName, int tagNumber) throws IOException, ParserConfigurationException, SAXException {
		Document doc = PositionalXMLReader.createDocumentWithLineNumbers(reader);
		reader.close();
		Node node = doc.getElementsByTagName(tagName).item(tagNumber);
		int lineNumber = (Integer) node.getUserData("lineNumber");
		return lineNumber;
	}

	public static void main(final String[] args) throws Exception {

		String xmlString = "<foo>\n"
				+ "    <bar>\n"
				+ "        <moo>Hello World!</moo>\n"
				+ "    </bar>\n"
				+ "    <bar>\n"
				+ "        <moo>Hello World!</moo>\n"
				+ "    </bar>\n"
				+ "</foo>";

		int lineNumber = findLinenumberOfTag(new StringReader(xmlString), "bar", 1);

		System.out.println("Line number: " + lineNumber);
	}
}
