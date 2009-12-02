/*
 *  ApertiumServer. Highly scalable web service implementation for Apertium.
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
package com.gsoc.apertium.translationengines.main;

import com.gsoc.apertium.translationengines.rmi.router.IApplicationRouter;
import com.gsoc.apertium.translationengines.rmi.slave.ITranslationEngine;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gsoc.apertium.translationengines.rmi.transferobjects.Format;
import com.gsoc.apertium.translationengines.rmi.transferobjects.LanguagePair;
import com.gsoc.apertium.translationengines.rmi.transferobjects.ServerInformationTO;
import com.gsoc.apertium.translationengines.rmi.transferobjects.TranslationServerId;
import com.gsoc.apertium.translationengines.util.ModesXMLProcessor;
import com.gsoc.apertium.translationengines.util.ServerUtil;
import java.io.BufferedReader;
import java.nio.charset.Charset;

public class Main {

    public static String rminame;
    static Log logger = LogFactory.getLog(Main.class);

    /**
     *
     * Application entry point. Its behaviour depends on the parameters.
     * 
     * <br/>
     * With these parameters it starts Apertium Server and registers with request router:
     * <ul>
     * <li><code>-run</code></li>
     * <li><code>-ownHost</code> Machine public IP or hostname. </li>
     * <li><code>-RMIname</code> Name of the RMI remote object this application will be exported to. </li>
     * <li><code>-RMIport</code> Port of the RMI remote object this application will be exported to. </li>
     * <li><code>-routerHost</code> Name of the host where request router is running. If this parameter is not present,
     * the name of the request router host is taken from the property <code>requestrouter_host</code>, from <code>configuration.properties</code></li>
     * <li><code>-capacityFromConfigFile</code> (with no arguments) Instead of calculating server capacity at startup, it reads the capacity from the file
     * <code>capacity.properties</code></li>
     * </ul>
     * <br/>
     *
     * But if the first parameter is <code>pairsInformation</code> it only
     * perform some tests and writes the translation speed ratio between
     * the supported pairs and their memory requirements So, in this case the parameters are:<br>
     * <ul>
     * <li> <code>-pairsInformation</code> </li>
     * <li> <code>-comparationPair</code> Language pair that will be compared with the other supported pairs. For example, es-ca</li>
     * <li> <code>-speedFile</code> Name of the properties file where the speed ratios will be stored. </li>
     * <li> <code>-memoryFile</code> Name of the properties file where the memory requirements will be stored. </li>
     * </ul>
     * <br/>
     *
     * If we want to measure the time taken to translate a series of files, we can
     * pass the foloowing parameters:
     * <ul>
     * <li> <code>-testTime</code> </li>
     * <li> <code>-testPair</code> Pair to translate with </li>
     * <li> Names of the files to translate </li>
     * </ul>
     * <br/>
     *
     * It is known that applications don't need the same memory in 32-bit operating
     * systems and 64-bit operative systems. Once we have calculated the different pairs
     * memory requirements in both kinds of operative systems with the <code>-pairsInformation</code>
     * option, we can calculate the average ratio between them:
     * <ul>
     * <li> <code>-compareMemory</code> </li>
     * <li> <code>-file32</code> File where 32-bit memory requirements are stored </li>
     * <li> <code>-file64</code> File where 64-bit memory requirements are stored </li>
     * </ul>
     * <br/>
     *
     * Finally this application can be used to process an Apertium modes.xml file and
     * add new modes to it. To work as a daemon, Apertium pipeline programs need to support
     * an option called null flush. With these arguments, the new modes are added to modes.xml file.
     * They are copies of existing modes, but adding the <code>-z</code> option to all the programs
     * of the pipeline to support null flush.
     * <ul>
     * <li> <code>-processModes</code> </li>
     * <li> <code>-inputModes</code> Original modes.xml file </li>
     * <li> <code>-outputModes</code> Modified modes.xml file </li>
     * </ul>
     * <br/>
     *
     * 
     *
     * @param args
     */
    public static void main(String[] args) {

        System.out.println("Charset: "+Charset.defaultCharset().toString());

        Option pairsInformation = OptionBuilder.withDescription("Compute pairs speed and memory requirements").create("pairsInformation");
        Option comparationPair = OptionBuilder.withArgName("pair").hasArg().withDescription("Pair to compare speed with").create("comparationPair");
        Option speedFile = OptionBuilder.withArgName("file").hasArg().withDescription("File while speed ratios will be stored").create("speedFile");
        Option memoryFile = OptionBuilder.withArgName("file").hasArg().withDescription("File while memory requirements will be stored").create("memoryFile");

        Option run = OptionBuilder.withDescription("Run server").create("run");
        Option ownHost = OptionBuilder.withArgName("host name").hasArg().withDescription("Name of the host where the app is running").create("host");
        Option RMIRemoteObjectName = OptionBuilder.withArgName("name").hasArg().withDescription("Name of the RMI remote object representing this application").create("RMIname");
        Option RMIRemoteObjectPort = OptionBuilder.withArgName("port").hasArg().withDescription("Port of the exported RMI remote object representing this application").create("RMIport");
        Option readServerCapacityFromConfigFile = OptionBuilder.withDescription("read server capacity from config file").create("capacityFromConfigFile");
        Option requestRouterHost = OptionBuilder.withArgName("host name").hasArg().withDescription("Name of the host where request router is running").create("routerHost");

        Option testTime = OptionBuilder.withDescription("Tests the time spent to translate the files passed as command line arguments").create("testTime");
        Option testPair = OptionBuilder.withArgName("pair").hasArg().withDescription("Pair to translate with").create("testPair");

        Option compareMemory = OptionBuilder.withDescription("Compares memory requirements between 32-bit and 64-bit systems").create("compareMemory");
        Option file32 = OptionBuilder.withArgName("file").hasArg().withDescription("File while 32-bit memory requirements are stored").create("file32");
        Option file64 = OptionBuilder.withArgName("file").hasArg().withDescription("File while 64-bit memory requirements are stored").create("file64");

        Option modesXMLProcessor = OptionBuilder.withDescription("Adds new modes with null flush to modes.xml").create("processModes");
        Option inputModes = OptionBuilder.withArgName("file").hasArg().withDescription("Original modes.xml file").create("inputModes");
        Option outputModes = OptionBuilder.withArgName("file").hasArg().withDescription("Modified modes.xml file").create("outputModes");
        Option tradubiDictionaryPath = OptionBuilder.withArgName("path").hasArg().withDescription("Directory where Tradubi dictionary files are stored").create("tradubiDictionaryPath");
        Option prefix = OptionBuilder.withArgName("path").hasArg().withDescription("Directory where Tradubi Apertium module is installed").create("prefix");

        Options options = new Options();
        options.addOption(pairsInformation);
        options.addOption(comparationPair);
        options.addOption(speedFile);
        options.addOption(memoryFile);
        options.addOption(run);
        options.addOption(ownHost);
        options.addOption(requestRouterHost);
        options.addOption(RMIRemoteObjectName);
        options.addOption(RMIRemoteObjectPort);
        options.addOption(readServerCapacityFromConfigFile);
        options.addOption(testTime);
        options.addOption(testPair);
        options.addOption(compareMemory);
        options.addOption(file32);
        options.addOption(file64);
        options.addOption(modesXMLProcessor);
        options.addOption(inputModes);
        options.addOption(outputModes);
        options.addOption(tradubiDictionaryPath);
        options.addOption(prefix);

        CommandLineParser parser = new GnuParser();

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("pairsInformation")) {
                ApertiumTranslationEnginePool apertiumTranslationEnginePool = new ApertiumTranslationEnginePool();

                //Get parameters
                LanguagePair pair;
                pair = new LanguagePair(line.getOptionValue("comparationPair", "es-ca"), "-".toCharArray());

                Map<LanguagePair, Double> langConversionRates = apertiumTranslationEnginePool.calculateLanguagesSpeedRatio(pair);
                Map<Format, Double> formatConversionRates = apertiumTranslationEnginePool.calculateFormatSpeedRatio();
                Map<LanguagePair, Integer> memoryRequirements = apertiumTranslationEnginePool.calculateMemoryRequirements();
                apertiumTranslationEnginePool.stop();
                Properties properties = new Properties();
                Properties memProperties = new Properties();

                for (Entry<LanguagePair, Double> entry : langConversionRates.entrySet()) {
                    properties.setProperty(entry.getKey().toString(), entry.getValue().toString());
                }
                for (Entry<LanguagePair, Integer> entry : memoryRequirements.entrySet()) {
                    memProperties.setProperty(entry.getKey().toString(), entry.getValue().toString());
                }
                for (Entry<Format, Double> entry : formatConversionRates.entrySet()) {
                    properties.setProperty("format_" + entry.getKey().toString(), entry.getValue().toString());
                }
                try {
                    properties.store(new FileWriter(new File(line.getOptionValue("speedFile", "LoadConverter.properties"))), "");
                } catch (Exception e) {
                    logger.error("Exception writing results into properties file");
                }
                try {
                    memProperties.store(new FileWriter(new File(line.getOptionValue("memoryFile", "MemoryRequirements.properties"))), "");
                } catch (Exception e) {
                    logger.error("Exception writing memory results into properties file");
                }
            } else if (line.hasOption("testTime")) {
                String spair = line.getOptionValue("testPair", "es-ca");
                LanguagePair pair = new LanguagePair(spair, "-".toCharArray());

                String[] fileNames = line.getArgs();
                ApertiumTranslationEnginePool apertiumTranslationEnginePool = new ApertiumTranslationEnginePool();
                apertiumTranslationEnginePool.testTime(pair, fileNames);
                apertiumTranslationEnginePool.stop();

            } else if (line.hasOption("compareMemory")) {
                String f32 = line.getOptionValue("file32");
                String f64 = line.getOptionValue("file64");
                if (f32 != null && f64 != null) {
                    try {
                        Properties prop32 = new Properties();
                        prop32.load(new FileReader(f32));

                        Properties prop64 = new Properties();
                        prop64.load(new FileReader(f64));

                        List<Double> ratios = new ArrayList<Double>();
                        for (String key : prop32.stringPropertyNames()) {
                            int memory32 = Integer.parseInt(prop32.getProperty(key));
                            if (prop64.getProperty(key) != null) {
                                int memory64 = Integer.parseInt(prop64.getProperty(key));
                                double ratio = (double) memory32 / (double) memory64;
                                ratios.add(ratio);
                            }
                        }
                        logger.debug("ratios.size()=" + ratios.size());
                        double totalratios = 0;
                        for (Double ratio : ratios) {
                            totalratios += ratio;
                        }
                        double avgratios = totalratios / (double) ratios.size();

                        logger.info("Ratio between 32-bit memory requirements and 64-bit memory requirements: " + avgratios);

                    } catch (Exception e) {
                        logger.error("Exception comparing 32-bit and 64-bit memory requirements", e);
                    }
                } else {
                    System.err.println("bad parameters");
                }
            } else if (line.hasOption("processModes")) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(line.getOptionValue("inputModes")));
                    StringBuilder builder = new StringBuilder();
                    String modesLine;
                    while ((modesLine = reader.readLine()) != null) {
                        builder.append(modesLine);
                        builder.append("\n");
                    }
                    reader.close();

                    ModesXMLProcessor modesProcessor = new ModesXMLProcessor(builder.toString());
                    if (line.hasOption("tradubiDictionaryPath")) {
                        modesProcessor.enableTradubi(line.getOptionValue("tradubiDictionaryPath"), line.getOptionValue("prefix"));
                    }
                    String result = modesProcessor.process();
                    FileWriter writer = new FileWriter(line.getOptionValue("outputModes"));
                    writer.write(result);
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    String ownhost = line.getOptionValue("host", "localhost");
                    String remoteObjectName = line.getOptionValue("RMIname", "ApertiumServerSlave");
                    Main.rminame = remoteObjectName;
                    int remoteObjectPort = Integer.parseInt(line.getOptionValue("RMIport", "1331"));

                    String routerHost = line.getOptionValue("routerHost", ServerUtil.readProperty("requestrouter_host"));

                    //start RMI registry
                    Registry registry = null;

                    try {
                        registry = LocateRegistry.getRegistry(Registry.REGISTRY_PORT);
                        registry.list();
                        logger.info("RMI Registry already running on port " + Registry.REGISTRY_PORT);
                    } catch (Exception e) {
                        try {
                            registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
                            registry.list();
                            logger.info("Started RMI Registry on port " + Registry.REGISTRY_PORT);
                        } catch (Exception ex) {
                            logger.error("Couldn't initialise RMI");
                        }
                    }

                    try {


                        //Instantiate and export remote object
                        ApertiumTranslationEnginePool apertiumTranslationEnginePool = new ApertiumTranslationEnginePool();

                        if (line.hasOption("capacityFromConfigFile")) {
                            apertiumTranslationEnginePool.getServerInformation().setCpuCapacity(Integer.parseInt(ServerUtil.readPropertyFromFile("cpu_capacity", "/capacity.properties")));
                            apertiumTranslationEnginePool.getServerInformation().setCpuCapacityPerDaemon(Integer.parseInt(ServerUtil.readPropertyFromFile("cpu_daemon_capacity", "/capacity.properties")));
                            apertiumTranslationEnginePool.getServerInformation().setMemoryCapacity(Integer.parseInt(ServerUtil.readPropertyFromFile("memory_capacity", "/capacity.properties")));

                        } else {
                            apertiumTranslationEnginePool.guessServerInformation();
                            Properties properties = new Properties();
                            properties.setProperty("cpu_capacity", Integer.toString(apertiumTranslationEnginePool.getServerInformation().getCpuCapacity()));
                            properties.setProperty("cpu_daemon_capacity", Integer.toString(apertiumTranslationEnginePool.getServerInformation().getCpuCapacityPerDaemon()));
                            properties.setProperty("memory_capacity", Integer.toString(apertiumTranslationEnginePool.getServerInformation().getMemoryCapacity()));
                            properties.store(new FileWriter("conf/capacity.properties"), "");
                        }
                        ServerInformationTO serverInformationTO = apertiumTranslationEnginePool.getServerInformation();
                        logger.info("Server information. Cpu capacity: " + serverInformationTO.getCpuCapacity() + " Max capacity per daemon: " + serverInformationTO.getCpuCapacityPerDaemon() + " Memory capacity: " + serverInformationTO.getMemoryCapacity());

                        ITranslationEngine obj = apertiumTranslationEnginePool;
                        ITranslationEngine stub = (ITranslationEngine) UnicastRemoteObject.exportObject(obj, remoteObjectPort);

                        //Bind object to registry
                        registry.rebind(remoteObjectName, stub);
                        logger.info("Remote object " + remoteObjectName + " bound OK. Name: " + remoteObjectName + ". Port: " + remoteObjectPort);

                        //Notify request router
                        TranslationServerId serverid = new TranslationServerId(ownhost, Registry.REGISTRY_PORT, remoteObjectName);
                        Registry routerregistry = LocateRegistry.getRegistry(routerHost, Integer.parseInt(ServerUtil.readProperty("requestrouter_port")));
                        IApplicationRouter applicationRouter = (IApplicationRouter) routerregistry.lookup(ServerUtil.readProperty("requestrouter_objectname"));
                        applicationRouter.addServer(serverid);
                        logger.info("Server ready: registered with request router");

                    } catch (Exception e) {
                        logger.error("Exception binding object or notifying request router", e);
                    }


                } catch (Exception e) {
                    System.err.println("Server exception: " + e.toString());
                    e.printStackTrace();
                }

            }

        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }
    }
}
