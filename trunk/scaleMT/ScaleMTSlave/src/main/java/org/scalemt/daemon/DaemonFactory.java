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
package org.scalemt.daemon;

import org.scalemt.rmi.transferobjects.DaemonConfiguration;
import org.scalemt.rmi.transferobjects.Format;
import org.scalemt.rmi.transferobjects.LanguagePair;
import org.scalemt.util.ServerUtil;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Singleton factory that creates {@link ApertiumDaemon} instances.<br/>
 * Reads the following properties from <code>configuration.properties</code>:
 * <ul>
 * <li><code>apertium_path</code>: Directory where the Apertium executable can be found</li>
 * <li><code>apertium_null_mode_suffix</code>: Suffix that, added to the language pairs, makes the name
 *     of the mode of Apertium that will be invoked. For example, if pair is es-en and mode "-null",
 *    Apertium will be invoked with the mode <code>es-en-null</code></li>
 * </ul>
 *
 * @author vmsanchez
 */
public class DaemonFactory {

    /**
     * Singleton instance
     */
	private static DaemonFactory instance=null;

          /**
     * Commons-logging logger
     */
    static Log logger = LogFactory.getLog(DaemonFactory.class);


    /**
     * Gets the singleton instance
     * @return Singleton instance
     */
	public static DaemonFactory getInstance()
	{
		if(instance==null)
			instance=new DaemonFactory();
		return instance;
	}
	
	
	private long lastId;
	//private String modeSuffix;
	//private String apertiumPath;
        private List<TranslationEngineInfo> enginesInfo;

    /**
     * Default contructor
     */
	private DaemonFactory() {
		lastId=0;
		
		//Read properties
		/*
		apertiumPath = ServerUtil.readProperty("apertium_path");
		if (apertiumPath == null)
			apertiumPath = "/usr/local/bin";
		
		modeSuffix= ServerUtil.readProperty("apertium_null_mode_suffix");
		if(modeSuffix==null)
			modeSuffix="-null";
                 * */
                enginesInfo=new ArrayList<TranslationEngineInfo>();
                readConfiguration();
	}

        private void readConfiguration()
        {
            //List<TranslationEngine> enginesInfo = new ArrayList<TranslationEngine>();
            try
            {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document =builder.parse(new InputSource(new StringReader(ServerUtil.readFileFromClasspath("/translation-engines.xml"))));

                NodeList translationEngines =document.getElementsByTagName("engine");
                for(int i=0; i<translationEngines.getLength();i++)
                {
                    enginesInfo.add(parseEngine(translationEngines.item(i)));
                }
                
            }
            catch(IOException ioe){
                logger.fatal("Cannot read translation-engines.xml",ioe);
            }
            catch(ParserConfigurationException pce){}
            catch(SAXException se){}
        }

        private TranslationEngineInfo parseEngine(Node node)
        {
            TranslationEngineInfo translationEngine = new TranslationEngineInfo();
            TranslationCore core = new TranslationCore();
            translationEngine.setTranslationCore(core);
            Set<LanguagePair> setPairs = new HashSet<LanguagePair>();
            Set<Format> setFormats = new HashSet<Format>();

            //Get name of the translation engine
            String name ="";
            try
            {
                name=node.getAttributes().getNamedItem("name").getNodeValue();
            }
            catch(Exception e)
            {
                //TODO: Show error message
            }
            translationEngine.setName(name);

            NodeList children = node.getChildNodes();

            for(int i=0; i<children.getLength(); i++)
            {
                Node child=children.item(i);
                
                //Parse language pairs
                if( "pairs".equals(child.getNodeName()))
                {
                    NodeList pairNodes =child.getChildNodes();
                    for(int j=0; j<pairNodes.getLength();j++)
                    {
                        Node pair = pairNodes.item(j);
                        if("pair".equals(pair.getNodeName()))
                        {
                            String source=null,target=null;
                            NodeList st = pair.getChildNodes();
                            for(int k=0; k<st.getLength();k++)
                            {
                                Node n=st.item(k);
                                if("source".equals(n.getNodeName()))
                                    source=n.getTextContent();
                                else if ("target".equals(n.getNodeName()))
                                    target=n.getTextContent();
                            }
                            if(source!=null && target!=null)
                            {
                                LanguagePair l = new LanguagePair(source, target);
                                setPairs.add(l);
                            }
                            
                        }
                    }
                }
                //Parse supported formats
                else if("formats".equals(child.getNodeName()))
                {
                    NodeList formatNodes =child.getChildNodes();
                    for(int j=0; j<formatNodes.getLength();j++)
                    {
                        Node format = formatNodes.item(j);
                        if("format".equals(format.getNodeName()))
                        {
                            try
                            {
                            Format formatObj = Format.valueOf(format.getTextContent());
                            setFormats.add(formatObj);
                            }
                            catch(IllegalArgumentException iae)
                            {
                                //TODO: mensaje: el formato no está soportado
                            }
                        }
                    }
                }
                //Parse processes section
                else if("processes".equals(child.getNodeName()))
                {
                    //Check if deformatters and reformatters run out from the pipeline
                     NamedNodeMap attrs =child.getAttributes();
                     Node finode =attrs.getNamedItem("format-indp");
                     if(finode!=null)
                     {
                         if("yes".equals(finode.getNodeValue()))
                             translationEngine.setDeformatOutFromPipeline(true);
                     }

                     NodeList processesChildren = child.getChildNodes();
                     for(int j=0; j<processesChildren.getLength();j++)
                     {
                         Node cpnode = processesChildren.item(j);
                         
                         //Parse variables
                         if("variables".equals(cpnode.getNodeName()))
                         {
                             NodeList variables = cpnode.getChildNodes();
                             for(int k=0; k<variables.getLength();k++)
                             {
                                 Node variable = variables.item(k);
                                 try
                                 {
                                   VariableType vtype = VariableType.valueOf(variable.getNodeName());
                                   NamedNodeMap vattrs = variable.getAttributes();
                                   Node nodeid = vattrs.getNamedItem("id");
                                   if(nodeid!=null)
                                   {
                                       try{
                                           int id = Integer.parseInt(nodeid.getNodeValue());
                                           translationEngine.getVariables().put(id, vtype);
                                       }
                                       catch(NumberFormatException nfe)
                                       {
                                           //TODO:
                                       }
                                   }
                                   
                                 }
                                 catch(IllegalArgumentException iae)
                                 {
                                    //TODO: el tipo de variable bla bla bla
                                 }
                             }
                         }

                         //Parse pipeline
                         else if ("pipeline".equals(cpnode.getNodeName()))
                         {
                             NodeList pipeElements = cpnode.getChildNodes();
                             for(int k=0; k<pipeElements.getLength(); k++)
                             {
                                 Node pipeNode = pipeElements.item(k);

                                 if("program".equals(pipeNode.getNodeName()))
                                 {
                                     Program program=new Program();
                                     NodeList programElements = pipeNode.getChildNodes();
                                     NamedNodeMap attribs =pipeNode.getAttributes();
                                     Node ofNode= attribs.getNamedItem("onlyFormats");
                                     if(ofNode!=null)
                                     {
                                         program.getOnlyFormats().clear();
                                         String formats=ofNode.getNodeValue();
                                         String[] pieces = formats.split(",");
                                         for(String piece: pieces)
                                         {
                                             try
                                             {
                                                 program.getOnlyFormats().add(Format.valueOf(piece));
                                             }
                                             catch(IllegalArgumentException e){}
                                         }
                                     }

                                     ofNode= attribs.getNamedItem("onlyIf");
                                     if(ofNode!=null)
                                     {
                                         String restriction=ofNode.getNodeValue();
                                         String[] pieces = restriction.split("=");
                                         if(pieces.length==2)
                                         {
                                             program.getRestrictions().put(pieces[0],pieces[1]);
                                         }
                                     }

                                     for(int l=0; l<programElements.getLength();l++)
                                     {
                                         Node programEl=programElements.item(l);
                                         if("exec".equals(programEl.getNodeName()))
                                         {
                                             program.setCommand(programEl.getTextContent());
                                         }
                                         else if("in-filter".equals(programEl.getNodeName()))
                                         {
                                             program.setInFilter(programEl.getTextContent());
                                         }
                                         else if("in".equals(programEl.getNodeName()))
                                         {
                                             String value = programEl.getTextContent();
                                             if("in".equals(value))
                                                 program.setInput(-1);
                                             else if("out".equals(value))
                                                 program.setInput(-2);
                                             else
                                             {
                                                 try{
                                                     int num = Integer.parseInt(value);
                                                     if(num>=0)
                                                         program.setInput(num);
                                                 }
                                                 catch(NumberFormatException nfe)
                                                 {
                                                     //TODO: aaaa
                                                 }
                                             }
                                         }
                                         else if("out".equals(programEl.getNodeName()))
                                         {
                                              String value = programEl.getTextContent();
                                             if("in".equals(value))
                                                 program.setOutput(-1);
                                             else if("out".equals(value))
                                                 program.setOutput(-2);
                                             else
                                             {
                                                 try{
                                                     int num = Integer.parseInt(value);
                                                     if(num>=0)
                                                         program.setOutput(num);
                                                 }
                                                 catch(NumberFormatException nfe)
                                                 {
                                                     //TODO: aaaa
                                                 }
                                             }

                                         }
                                     }
                                     if(!program.getCommand().equals("") && program.getInput()>=-2 && program.getOutput()>=-2)
                                         translationEngine.getPrograms().add(program);
                                 }
                                 else if("translation-core".equals(pipeNode.getNodeName()))
                                 {
                                     TranslationCore translationCore=core;
                                     NodeList tcElements = pipeNode.getChildNodes();
                                     for(int l=0; l<tcElements.getLength();l++)
                                     {
                                         Node programEl=tcElements.item(l);
                                         if("exec".equals(programEl.getNodeName()))
                                         {
                                             translationCore.setCommand(programEl.getTextContent());
                                         }
                                         else if("in".equals(programEl.getNodeName()))
                                         {
                                             String value = programEl.getTextContent();
                                             if("in".equals(value))
                                                translationCore.setInput(-1);
                                             else if("out".equals(value))
                                                 translationCore.setInput(-2);
                                             else
                                             {
                                                 try{
                                                     int num = Integer.parseInt(value);
                                                     if(num>=0)
                                                         translationCore.setInput(num);
                                                 }
                                                 catch(NumberFormatException nfe)
                                                 {
                                                     //TODO: aaaa
                                                 }
                                             }
                                         }
                                         else if("out".equals(programEl.getNodeName()))
                                         {
                                              String value = programEl.getTextContent();
                                             if("in".equals(value))
                                                 translationCore.setOutput(-1);
                                             else if("out".equals(value))
                                                 translationCore.setOutput(-2);
                                             else
                                             {
                                                 try{
                                                     int num = Integer.parseInt(value);
                                                     if(num>=0)
                                                         translationCore.setOutput(num);
                                                 }
                                                 catch(NumberFormatException nfe)
                                                 {
                                                     //TODO: aaaa
                                                 }
                                             }

                                         }
                                     }

                                     

                                 }
                             }
                         }
                         
                     }

                     
                }
                else if ("separation".equals(child.getNodeName()))
                         {
                             NamedNodeMap atrrs =child.getAttributes();
                             Node afterDeformat = atrrs.getNamedItem("in-core");
                             if(afterDeformat!=null && afterDeformat.getNodeValue().equals("yes"))
                             {
                                 core.setSeparateAfterDeformat(true);
                             }

                             NodeList sepChildren = child.getChildNodes();
                             for(int k=0; k<sepChildren.getLength();k++)
                             {
                                 Node sepChild= sepChildren.item(k);

                                 if(sepChild.getNodeName().equals("null-flush"))
                                     core.setNullFlush(true);
                                 else if("before".equals(sepChild.getNodeName()))
                                 {
                                     NodeList beforeNodes = sepChild.getChildNodes();
                                     for(int l=0; l<beforeNodes.getLength();l++)
                                     {
                                         Node n = beforeNodes.item(l);
                                         if("text".equals(n.getNodeName()))
                                         {
                                            core.setTextBefore(n.getTextContent());
                                         }
                                         else if("regexps".equals(n.getNodeName()))
                                         {
                                             NodeList regexps = n.getChildNodes();
                                             for(int m=0; m<regexps.getLength(); m++)
                                             {
                                                 Node regexpNode = regexps.item(m);
                                                 if(regexpNode.getNodeName().equals("regexp"))
                                                 {
                                                      int fieldNumber=-1;
                                                    NamedNodeMap idfattrs = regexpNode.getAttributes();
                                                    Node fieldNode = idfattrs.getNamedItem("id-field");
                                                    if(fieldNode!=null)
                                                    {
                                                        try
                                                        {
                                                            fieldNumber=Integer.parseInt(fieldNode.getNodeValue());
                                                        }
                                                        catch(NumberFormatException nfe)
                                                        {
                                                            //TODO: aaa
                                                        }
                                                    }
                                                    core.getRegexpsBefore().add( new SeparatorRegexp(Pattern.compile(regexpNode.getTextContent()) , fieldNumber) );
                                                 }

                                             }

                                         }
                                     }

                                 }
                                 else if("after".equals(sepChild.getNodeName()))
                                 {
                                    NodeList afterNodes = sepChild.getChildNodes();
                                    for(int l=0; l<afterNodes.getLength();l++)
                                     {
                                         Node n = afterNodes.item(l);
                                         if("text".equals(n.getNodeName()))
                                         {
                                            core.setTextAfter(n.getTextContent());
                                         }
                                          else if("regexps".equals(n.getNodeName()))
                                         {
                                             NodeList regexps = n.getChildNodes();
                                             for(int m=0; m<regexps.getLength(); m++)
                                             {
                                                 Node regexpNode = regexps.item(m);
                                                 if(regexpNode.getNodeName().equals("regexp"))
                                                 {
                                                    int fieldNumber=-1;
                                                    NamedNodeMap idfattrs = regexpNode.getAttributes();
                                                    Node fieldNode = idfattrs.getNamedItem("id-field");
                                                    if(fieldNode!=null)
                                                    {
                                                        try
                                                        {
                                                            fieldNumber=Integer.parseInt(fieldNode.getNodeValue());
                                                        }
                                                        catch(NumberFormatException nfe)
                                                        {
                                                            //TODO: aaa
                                                        }
                                                    }
                                                    core.getRegexpsAfter().add( new SeparatorRegexp(Pattern.compile(regexpNode.getTextContent()), fieldNumber) );
                                                 }
                                             }

                                         }
                                     }
                                 }
                                 else if("padding".equals(sepChild.getNodeName()))
                                 {
                                     core.setTrash(sepChild.getTextContent());
                                 }
                             }
                         }

            }

            if(translationEngine.isDeformatOutFromPipeline())
            {
                for(LanguagePair p: setPairs)
                {
                    DaemonConfiguration c = new DaemonConfiguration(p, setFormats);
                    translationEngine.getConfigurations().add(c);
                }
            }
            else
            {
                for(LanguagePair p: setPairs)
                {
                    for(Format f: setFormats)
                    {
                        DaemonConfiguration c = new DaemonConfiguration(p, f);
                        translationEngine.getConfigurations().add(c);
                    }
                }
            }

            return translationEngine;
        }

    /**
     * Creates a new daemon that can translate the given pair. The daemon ID is
     * generated.
     * @param pair Pair the new daemon will be able to translate.
     * @return New Apertium daemon.
     */
	public synchronized Daemon newDaemon(DaemonConfiguration d)
	{
            TranslationEngineInfo engineInfo=null;
            for(TranslationEngineInfo engine: enginesInfo)
                if(engine.getConfigurations().contains(d))
                    engineInfo=engine;

            return new Daemon(lastId++,d,engineInfo);
	}

        public DaemonConfiguration searchConfiguration(LanguagePair p, Format format)
        {
             DaemonConfiguration chosenc=null;
             for(DaemonConfiguration c: getSupportedConfigs())
             {
                 if(c.getLanguagePair().equals(p) && c.getFormats().contains(format))
                     chosenc=c;
             }
             return chosenc;
        }
        

        public Set<DaemonConfiguration> getSupportedConfigs()
        {
            /*
            Set<LanguagePair> supPairs=new HashSet<LanguagePair>();
            for(TranslationEngineInfo te: enginesInfo)
                supPairs.addAll(te.getLanguagePairs());
            return  supPairs;
             * */
            Set<DaemonConfiguration> configs = new HashSet<DaemonConfiguration>();
            for(TranslationEngineInfo te: enginesInfo)
                configs.addAll(te.getConfigurations());
            return  configs;
        }
	
}
