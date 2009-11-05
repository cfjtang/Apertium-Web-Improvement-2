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
package com.gsoc.apertium.translationengines.router.logic;

import com.gsoc.apertium.translationengines.rmi.transferobjects.Format;
import com.gsoc.apertium.translationengines.rmi.transferobjects.LanguagePair;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.mutable.MutableInt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Predicts the load the system will have to process based on requests history.<br/>
 * Reads the following options from <code>configuration.properties</code>:
 * <ul>
 * <li><code>request_k</code>: CPU cost in es-ca text characters per second needed to process a translation request.
 * The CPU cost of a translation request is calculated by adding this value to the number
 * of characters of the request.</li>
 * <li><code>load_prediction_alpha</code>: How to combine measured load and load predicted in the previous instant:
 * predicted load = measured load * alpha  +  previous prediceted load * (1-alpha)</li>
 * </ul>
 *
 * @author vmsanchez
 */
public class LoadPredictor {

  /**
   * Commons-logging logger
   */
  static Log logger = LogFactory.getLog(LoadBalancer.class);

    /**
     * Requests history.
     */
    private final IRequestHistory requestHistory;

    /**
     * Converts load between different pairs and formats
     */
    private final LoadConverter loadConverter;

    /**
     * CPU cost in es-ca text characters per second needed to process a translation request.
     * The CPU cost of a translation request is calculating by adding this value to the number
     * of characters of the request.
     */
    private int constantCpuCostPerRequest;

    /**
     * Last prediction did.
     */
    private Map<LanguagePair, Integer> lastPrediction;

    /**
     * How to combine measured load and load predicted in the previous instant:
     *
     * predicted load = measured load * alpha  +  previous prediceted load * (1-alpha)
     *
     */
    private double alpha;

    /**
     * Constructor
     */
    LoadPredictor() {
        requestHistory = RequestHistoryFactory.getRequestHistory();
        loadConverter = new LoadConverter(new LanguagePair("es", "ca"), Format.text);
        lastPrediction=null;
        try {
                this.constantCpuCostPerRequest = Integer.parseInt(Util.readConfigurationProperty("request_k"));
            } catch (Exception e) {
                logger.warn("Error reading request_k. using default value: 700", e);
                this.constantCpuCostPerRequest = 700;
            }

        try {
                this.alpha = Double.parseDouble(Util.readConfigurationProperty("load_prediction_alpha"));
            } catch (Exception e) {
                logger.warn("Error reading alpha. using default value: 1", e);
                this.alpha = 1;
            }
    }

    /**
     * Gets internal request history. This method must be called every time a request
     * arrives and the request history must be updated with the incoming request.
     *
     * @return Request history
     */
   IRequestHistory getRequestHistory()
   {
       return requestHistory;
   }

   /**
    * Predicts the load, in es-ca text characters per second, for the next time period.<br/>
    * Prediction is based on requests received in the last period and the last prediction made,
    * following this formula:<br/>
    * predicted load = measured load * alpha  +  previous prediceted load * (1-alpha)
    * 
    * @param period Load prediction is made for this period
    * @param queues Queues for the different language pairs.
    * @return Load prediction. Map associating each language pair with the amount of es-ca text chars
    * per second of that language pairs that the system will have to translate in the next period of time.
    */
   Map<LanguagePair, Integer> predictLoad(long period, Map<LanguagePair, QueueScheduler> queues) {
        Map<LanguagePair, RequestsHistoryTO> loadHistory = requestHistory.getHistoryAndReset();

        logger.trace("Requests received during "+period+" milliseconds");
        for(Entry<LanguagePair,RequestsHistoryTO> entry: loadHistory.entrySet())
            logger.trace(entry.getKey()+": "+entry.getValue().getNumberOfRequests()+" requests. "+entry.getValue().getNumberOfChars().values()+" chars.");

        Map<LanguagePair,Integer> loadPrediction = new HashMap<LanguagePair, Integer>();

        for (Entry<LanguagePair, QueueScheduler> entry : queues.entrySet()) {
            if (!loadHistory.containsKey(entry.getKey())) {
                loadHistory.put(entry.getKey(), entry.getValue().getRequestsSummary());
            } else {
                RequestsHistoryTO queueRequests = entry.getValue().getRequestsSummary();
                loadHistory.get(entry.getKey()).mergeNumberOfChars(queueRequests.getNumberOfChars());
                loadHistory.get(entry.getKey()).addToNumberOfRequests(queueRequests.getNumberOfRequests());
            }
        }

        for(LanguagePair pair: queues.keySet())
        {
            int cpuDemand=0;
            int oldCpuDemand=0;

            if(loadHistory.containsKey(pair))
            {
                RequestsHistoryTO requestsHistoryTO = loadHistory.get(pair);
                int loadConverted = 0;
                Map<Format, MutableInt> numberOfChars = requestsHistoryTO.getNumberOfChars();
                for (Entry<Format, MutableInt> ncEntry : numberOfChars.entrySet()) {
                    loadConverted += loadConverter.convert(ncEntry.getValue().intValue(), pair, ncEntry.getKey());
                }
                loadConverted += requestsHistoryTO.getNumberOfRequests() * constantCpuCostPerRequest;

               cpuDemand = loadConverted > 0 ? Math.max((int) (((long) loadConverted * 1000) / (long) period), 1) : 0;
            }

            double ddemand;
            if(lastPrediction!=null)
            {
                if(lastPrediction.containsKey(pair))
                    oldCpuDemand=lastPrediction.get(pair);
                ddemand = ((double)cpuDemand)*alpha+((double)oldCpuDemand)*(1.0-alpha);
            }
            else
            {
                ddemand = (double) cpuDemand;
            }
            loadPrediction.put(pair, cpuDemand > 0 ? Math.max(1, (int) ddemand) : (int) ddemand);
           
        }

        lastPrediction=loadPrediction;

        return loadPrediction;
    }

    public int getConstantCpuCostPerRequest() {
        return constantCpuCostPerRequest;
    }

   
}
