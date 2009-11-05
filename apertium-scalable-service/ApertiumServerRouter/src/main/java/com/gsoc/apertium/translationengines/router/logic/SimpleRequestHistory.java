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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Default implementation of {@link IRequestHistory}.
 * Stores historic information in HashMaps. It doesn't keep track of indivual requests,
 * but simply manages the total amount of requests and characters of each format
 * and language pairs requested.
 *
 * @author vitaka
 */
class SimpleRequestHistory implements IRequestHistory {

    /**
     * Commons-logging logger
     */
    static Log logger = LogFactory.getLog(SimpleRequestHistory.class);

    /**
     * Historic information of each language pair
     */
    private Map<LanguagePair, RequestsHistoryTO> requestsMap;

    public SimpleRequestHistory() {
        requestsMap = new HashMap<LanguagePair, RequestsHistoryTO>();
    }

    @Override
    public void addRequest(LanguagePair pair, int numCharacters, Format format) {

        synchronized(this)
        {
            if (requestsMap.containsKey(pair)) {
                requestsMap.get(pair).addRequest(format, numCharacters);
            } else {
                RequestsHistoryTO rp = new RequestsHistoryTO();
                rp.addRequest(format, numCharacters);
                requestsMap.put(pair, rp);
            }
        }
    }


    @Override
    public Map<LanguagePair, RequestsHistoryTO> getHistoryAndReset() {
         synchronized(this)
        {

            for (Entry<LanguagePair, RequestsHistoryTO> entry : requestsMap.entrySet()) {
                logger.trace("Prediction for " + entry.getKey() + ": Requests" + entry.getValue().getNumberOfRequests() + " Chars: " + entry.getValue().getNumberOfChars());
            }


            Map<LanguagePair,RequestsHistoryTO> returnMap = new HashMap<LanguagePair, RequestsHistoryTO>(requestsMap);
            requestsMap.clear();


            return returnMap;
        }
    }
}
