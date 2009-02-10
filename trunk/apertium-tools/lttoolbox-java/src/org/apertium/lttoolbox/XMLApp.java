package org.apertium.lttoolbox;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * Created by Nic Cottrell, Feb 6, 2009 10:06:45 PM
 */

public class XMLApp {

  /**
   * The libxml2's XML reader
   */
  protected TreeWalker reader;

  String attrib(String name) {
    Node n = reader.getCurrentNode();
    final NamedNodeMap attributes = n.getAttributes();
    final Node item = attributes.getNamedItem(name);
    if (item == null) {
      // throw new RuntimeException(MessageFormat.format("Error getting attribute {0} from node {1}", name, n));
      return "";
    }
    return item.getNodeValue();
  }

  protected String xmlTextReaderGetParserLineNumber(TreeWalker reader) {
    StringBuffer path = new StringBuffer();
    Node n = reader.getCurrentNode();
    while (n != null) {
      path.insert(0, n.getNodeName() + ">");
      n = n.getParentNode();
    }
    return path.toString();
  }

  protected boolean xmlTextReaderIsEmptyElement(Node n) {
      return n.getChildNodes().getLength()==0;

      //return StringUtils.isWhitespace(n.getNodeValue());
  }

  protected TreeWalker xmlReaderForFile(String fichero) throws IOException, SAXException {
    System.err.print("Reading "+fichero);
    DOMParser parser = new DOMParser();
    parser.parse(fichero);
    Document doc = parser.getDocument();

    System.err.println(" ... done"+doc);

    DocumentTraversal docTraversal = (DocumentTraversal) doc;
    return docTraversal.createTreeWalker(doc.getDocumentElement(),
            NodeFilter.SHOW_ALL, null, false);
  }

}
