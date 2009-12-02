/*
 *  ScaleMT. Highly scalable framework for machine translation web services
 *  Copyright (C) 2009  Víctor Manuel Sánchez Cartagena
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.scalemt.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Processes an Apertium modes.xml file and adds null-flush-compatible modes.
 * These modes have the same pipeline than standard modes but all the programs have
 * the <code>-z</code> options. Adds null-flush modes based on all the installable  modes.
 *
 * @author vmsanchez
 */
public class ModesXMLProcessor {

    /**
     * Memory representation of XML tree
     */
	private Document document;

    /**
     * Mode name pattern
     */
	private Pattern pattern;

    private boolean tradubiEnabled=false;
    private String tradubiDictionaryPath;
    private String tradubiprefix;

    /**
     * Reguler expression representing mode names
     */
	private final static String STR_PATTERN="[a-z][a-z](_[a-zA-Z]+)?-[a-z][a-z](_[a-zA-Z]+)?";

    private final static String TRADUBI_LAST_PIPELINE_PROGRAM="tradubidu -lastModule";
    private final static String TRADUBI_PIPELINE_MODULE="launchTradubiModule.sh";

    /**
     * Constructor receiving the content of a modes.xml file
     * @param content Content of a modes.xml file
     * @throws java.lang.Exception If an unexpected error happens
     */
	public ModesXMLProcessor(String content) throws Exception {
		
		pattern=Pattern.compile(STR_PATTERN);
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		document = builder.parse(new InputSource(new StringReader(content)));

	}

    public void enableTradubi(String dictionaryPath, String prefix)
    {
        tradubiEnabled=true;
        tradubiDictionaryPath=dictionaryPath;
        tradubiprefix=prefix;
    }

    /**
     * Processes the input modes file and adds the null-flush modes to it.
     * Returns the modified modes file.
     * @return Modified modes file
     * @throws java.lang.Exception   If an unexpected error happens
     */
	public String process() throws Exception {
		
		List<Node> newNodes = new ArrayList<Node>();
		
		Node root = document.getFirstChild();
		if ("modes".equals(root.getNodeName())) {
			NodeList modes = root.getChildNodes();
			for (int i = 0; i < modes.getLength(); i++) {
				Node mode = modes.item(i);
				if("mode".equals(mode.getNodeName()))
				{
					NamedNodeMap attributes = mode.getAttributes();
					Node name = attributes.getNamedItem("name");
					Node install = attributes.getNamedItem("install");
					Matcher matcher =pattern.matcher(name.getNodeValue());
					if(install!=null && "yes".equals(install.getNodeValue()) && matcher.matches())
					{
						newNodes.add(mode.cloneNode(true));
					}
					
				}
			}
			
			for(Node newNode: newNodes)
			{
				Node attributeName = newNode.getAttributes().getNamedItem("name");
				attributeName.setNodeValue(attributeName.getNodeValue()+"-null");
				
				Node pipeline =newNode.getFirstChild();
				while(pipeline!=null && !"pipeline".equals(pipeline.getNodeName()))
					pipeline=pipeline.getNextSibling();
				if(pipeline!=null && "pipeline".equals(pipeline.getNodeName()))
				{
					Node program=pipeline.getFirstChild();
					if(program!=null)
					{
                    Node lastProgram=null;
                    Node nameAttribute=null;
					do
					{
                       
						NamedNodeMap attributes =program.getAttributes();
						if(attributes!=null)
						{
						Node nodeName= attributes.getNamedItem("name");
						if(nodeName!=null)
						{
                            lastProgram=program;
                            nameAttribute=nodeName;
                            boolean isTagger=false;

							String name = nodeName.getNodeValue();
							String[] fragments = name.split(" ");
							StringBuilder builder;
                            if(tradubiEnabled && "apertium-tagger".equals(fragments[0]))
                            {
                                builder=new StringBuilder(fragments[0]+" -z -p ");
                                isTagger=true;
                            }
                            else
                                builder=new StringBuilder(fragments[0]+" -z ");
							for(int i=1;i<fragments.length;i++)
							{
								builder.append(fragments[i]);
								if(i<fragments.length-1)
									builder.append(" ");
							}
							nodeName.setNodeValue(builder.toString());

                            if(isTagger)
                            {
                                Node launchTradubiModuleNode = lastProgram.cloneNode(false);
                                Node attrName = nameAttribute.cloneNode(false);
                                attrName.setNodeValue(TRADUBI_PIPELINE_MODULE+" "+tradubiDictionaryPath+" "+tradubiprefix+" ");
                                launchTradubiModuleNode.getAttributes().setNamedItem(attrName);
                                pipeline.insertBefore(launchTradubiModuleNode, program.getNextSibling());
                                program=program.getNextSibling();

                            }
						}
						}
						program=program.getNextSibling();
					}
					while(program!=null);
                    if(tradubiEnabled)
                    {
                        //Añadir programa al final
                        Node tradubiPostProcessNode = lastProgram.cloneNode(false);
                        Node attrName = nameAttribute.cloneNode(false);
                        attrName.setNodeValue(TRADUBI_LAST_PIPELINE_PROGRAM);
                        tradubiPostProcessNode.getAttributes().setNamedItem(attrName);                     
                        pipeline.appendChild(tradubiPostProcessNode);
                    }
					}
					
				}
				root.appendChild(newNode);
				
				
			}
			

			return doc2String(document);
		} else
			throw new Exception("Wrong format");

	}

    /**
     * Converts a tree representation of a XML file to a text representation
     * @param doc Tree representation of XML file
     * @return Text representation of XML file
     * @throws java.lang.Exception If an unexpected error happens
     */
	private String doc2String(Document doc) throws Exception {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		transformer.transform(source, result);
		String xmlString = sw.toString();
		return xmlString;

	}

}
