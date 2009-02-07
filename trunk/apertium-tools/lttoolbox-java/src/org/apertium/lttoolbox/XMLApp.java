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
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * Created by Nic Cottrell, Feb 6, 2009 10:06:45 PM
 */

public class XMLApp {

  String attrib(org.w3c.dom.Node n, String name) {
    final NamedNodeMap attributes = n.getAttributes();
    final Node item = attributes.getNamedItem(name);
    if (item == null) throw new RuntimeException(MessageFormat.format(
            "Error getting attribute {0} from node {1}", name, n));
    return item.getNodeValue();
  }

  private String xmlTextReaderConstValue(XMLReader reader) {
    // todo
    return null;
  }

  protected int xmlTextReaderNodeType(Node n) {
    return -1;
    // todo
  }

  private int xmlTextReaderRead(XMLReader reader) {
    return -1;
    // todo
  }

  protected int xmlTextReaderGetParserLineNumber(TreeWalker reader) {
    // todo
    return -1;

  }

  protected boolean xmlTextReaderIsEmptyElement(Node n) {
    return StringUtils.isBlank(n.getNodeValue());
  }

  protected TreeWalker xmlReaderForFile(String fichero, Object o, int i) throws IOException, SAXException {
    DOMParser parser = new DOMParser();
         parser.parse(fichero);

         Document doc = parser.getDocument();

         DocumentTraversal docTraversal = (DocumentTraversal)doc;

         TreeWalker iter =
                     docTraversal.createTreeWalker(doc.getDocumentElement(),
                                 NodeFilter.SHOW_ALL,
                                 null,
                                false);

      return iter;
  }
}
