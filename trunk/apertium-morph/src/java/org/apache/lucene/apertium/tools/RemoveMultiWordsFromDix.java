package org.apache.lucene.apertium.tools;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;

/**
 * Reads a .dix file (<a href="http://www.apertium.org">Apertium</a>
 * monolingual dictionary, in XML format) and prints out the content
 * of that dictionary but omitting multiword units. Then the resulting
 * dictionary can be compiled using the Apertium tools and used to
 * prerpocess a text file so as to index it.
 *
 * @author Felipe Sanchez Martinez (fsanchez [at] dlsi [dot] ua [dot]
 * es)<br/>&copy; 2008 Universitat d'Alacant/Universidad de Alicante 
 */
public class RemoveMultiWordsFromDix extends DefaultHandler {
  private PrintStream out;
  private boolean reading_e;
  private String content_e;
  private boolean printout_e;
  private String last_element;

  public RemoveMultiWordsFromDix(String encoding) {
    reading_e = false;
    content_e = "";
    printout_e = true;
    last_element = "";

    try {
      out = new PrintStream(System.out, true, encoding);
    } catch (UnsupportedEncodingException e) {
      System.err.println("Error: Enconding '" + encoding + "' not supported\n" + e.getMessage());
      System.exit(1);
    }

    out.println("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
  }

  private XMLReader getXMLReader() {
    XMLReader reader = null;
    try {
      reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
      reader.setContentHandler(this);
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }

    return reader;
  }

  /**
   * Receive notification of character data inside an element. 
   */
  public void characters(char[] c, int start, int length) {
    String str = "";

    if (length > 0) {
      if (last_element.length() > 0)
        str += ">";
      last_element = "";

      for (int i = start; i < (start + length); i++)
        str += c[i];

      if (reading_e)
        content_e += str;
      else
        out.print(str);
    }
  }

  /**
   * Receive notification of the start of an element.
   */
  public void startElement(String uri, String localName, String tag, Attributes atts) {
    String str = "";

    if (last_element.length() > 0)
      str += ">";
    str += "<" + tag;

    for (int i = 0; i < atts.getLength(); i++)
      str += " " + atts.getLocalName(i) + "=\"" + atts.getValue(i) + "\"";

    last_element = tag;

    if (tag.equals("e"))
      reading_e = true;

    if (reading_e) {
      content_e += str;
      if (tag.equals("b"))
        printout_e = false;
    } else
      out.print(str);
  }

  /**
   * Receive notification of the end of an element.
   */
  public void endElement(String uri, String localName, String tag) {
    String str;
    if (last_element.equals(tag))
      str = "/>";
    else
      str = "</" + tag + ">";
    last_element = "";

    if (reading_e) {
      content_e += str;
      if (tag.equals("e")) {
        reading_e = false;
        if (printout_e)
          out.print(content_e);
        printout_e = true;
        content_e = "";
      }
    } else {
      out.print(str);
    }
  }

  private static String usage() {
    String usage = "Usage:\n\tjava " + RemoveMultiWordsFromDix.class.getName();
    usage += " --dix|-d dixfile [--encoding|-e encoding] [--help|-h]\n";
    return usage;
  }

  private static String getParamValue(String[] args, int i) {
    if (i >= args.length) {
      System.err.println("Error: wrong number of parameters");
      System.out.println(usage());
      System.exit(1);
    } 

    return args[i];   
  }

  public static void main(String[] args) throws IOException {
    String dixfile = "";
    String encoding = "";

    for (int i=0; i<args.length; i++) {
      if (args[i].equals("--dix") || args[i].equals("-d")) 
        dixfile = getParamValue(args, ++i);

      else if (args[i].equals("--encoding") || args[i].equals("-e")) 
	encoding = getParamValue(args, ++i);

      else if (args[i].equals("--help") || args[i].equals("-h")) {
        System.out.print(usage());
        System.exit(0);
      } 

      else
        System.err.println("Error: Unknown option '" + args[i] + "'");      
    }

    if (dixfile.length() == 0) {
      System.err.println("Error: No dictionary was given, use --dix to do that");
      System.err.print(usage());
      System.exit(1);
    }

    if (encoding.length() == 0) {
      encoding = "iso-8859-1";
      System.err.println("Warning: No output encoding was provided. The default one will be used: '" + encoding + "'");
    }

    RemoveMultiWordsFromDix parser = new RemoveMultiWordsFromDix(encoding);

    XMLReader reader = parser.getXMLReader();

    try {
      reader.parse(dixfile);
    } catch (SAXException e) {
      System.err.println("Error parsing " + dixfile + ": " + e.getMessage());
    }
  }
}
