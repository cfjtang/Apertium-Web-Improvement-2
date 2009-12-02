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
package org.scalemt.router.logic;


import org.scalemt.rmi.transferobjects.DaemonConfiguration;
import org.scalemt.rmi.transferobjects.Format;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Converts load between different language pairs and formats.
 * <br/>
 * The time needed to translate a fixed amount of characters is not constant
 * among the different language pairs and formats. This class converts an amount
 * of characters of a given language pair and format to the amount of characters
 * of the reference pair and format that needs the same time to be translated.
 * The ratios between the reference pair and format and the other pairs and formats
 * is computed by <code>ApertiumServerSlave</code> and stored in <code>LoadConverter.properties</code>.
 *
 * @author vmsanchez
 */
public class LoadConverter {

     /**
   * Commons-logging logger
   */
  static Log logger = LogFactory.getLog(LoadConverter.class);

    /**
     * Reference language pair
     */
    private DaemonConfiguration referenceConfiguration;

    /**
     * Reference format
     */
    private Format referenceFormat;

    /**
     * Configuration conversion rates
     */
    private Map<DaemonConfiguration,Double> conversionRates;

    /**
     * Configuration conversion rates
     */
    private Map<DaemonConfiguration,Integer> constantCosts;

    /**
     * Format conversion rates
     */
    private Map<Format,Double> formatConversionRates;

    /**
     * Constructor. Reference pair and reference format must be provided to
     * create the object.
     * @param rPair Reference pair.
     * @param rFormat Reference format.
     */
    public LoadConverter(DaemonConfiguration rConfiguration, Format rFormat) {
        referenceConfiguration=rConfiguration;
        referenceFormat=rFormat;
        conversionRates=new HashMap<DaemonConfiguration, Double>();
        formatConversionRates = new HashMap<Format, Double>();
        constantCosts = new HashMap<DaemonConfiguration, Integer>();
        loadRatesFromConfigurationFile("/LoadConverter.properties");
    }


    /**
     * Converts an amount of characters from the given pair and format to the reference pair
     * and format.<br/>
     * The returned amount of characters should need the same time to be translated with the
     * reference pair and format than the given amount of characters with the given pair and
     * format.
     *
     * @param numCharacters Number of characters to be converted.
     * @param orignalPair Language pair of the number of characters to be converted.
     * @param format Format of the number of characters to be converted.
     * @return Amount of characters that needs the same time to be translated with the reference pair
     * and format than the original amount of characters with the original pair and format.
     */
    public int convert(int numCharacters, DaemonConfiguration dc, Format format)
    {
        double conversionRate;
        if(dc.equals(referenceConfiguration))
            conversionRate=1;
        else
        {
            Double rate = conversionRates.get(dc);
            if(rate==null)
                conversionRate=1;
            else
                conversionRate=rate;
        }
        //TODO: ¿Only if daemonconfig has more than one format?
        if(!format.equals(referenceFormat))
        {
            Double rate = formatConversionRates.get(format);
            if(rate!=null)
                conversionRate*=rate;
        }

        return (int) (numCharacters * conversionRate);
    }

    public int convert(int numCharacters,Format format )
    {
        return convert(numCharacters, referenceConfiguration,format);
    }

    public int convert(int numCharacters, DaemonConfiguration dc)
    {
        return convert(numCharacters, dc, referenceFormat);
    }

    public Integer getConstantCost(DaemonConfiguration dc)
    {
        return constantCosts.get(dc);
    }

    /**
     * Loads conversion rates from the given configuration file
     * @param file 
     */
    private void loadRatesFromConfigurationFile(String file) {
        conversionRates.clear();
        formatConversionRates.clear();
        constantCosts.clear();
        
        Properties properties=new Properties();
        try
        {
        properties.load(new InputStreamReader(LoadConverter.class.getResourceAsStream(file)));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        logger.trace("Reading "+file);
        for(Object pairObj: properties.keySet())
        {
            
            logger.trace("Read property "+(String) pairObj);
            if(((String)pairObj).startsWith("format_"))
            {
                Format format = Format.valueOf(((String) pairObj).substring("format_".length()));
                formatConversionRates.put(format, Double.parseDouble(properties.getProperty((String) pairObj) ));

               
            }
            else if(((String)pairObj).startsWith("constant_k_"))
            {
                try
                {
                    DaemonConfiguration c = DaemonConfiguration.parse(((String)pairObj).substring("constant_k_".length()));
                    constantCosts.put(c, Integer.parseInt(properties.getProperty((String) pairObj)));
                }catch(Exception e)
                {
                   logger.warn("Exception parsing daemonconfig property", e);
                }
            }
            else
            {
               try
                {
                    DaemonConfiguration c = DaemonConfiguration.parse((String)pairObj);
                    conversionRates.put(c, Double.parseDouble(properties.getProperty((String) pairObj)));
                }catch(Exception e)
                {
                    logger.warn("Exception parsing daemonconfig property", e);
                }
            }
        }
        
    }

}
