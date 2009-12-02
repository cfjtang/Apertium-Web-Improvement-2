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


import org.scalemt.rmi.transferobjects.Format;
import org.scalemt.rmi.transferobjects.LanguagePair;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.mutable.MutableInt;

/**
 * Object storing relevant data about a the requests received by the system
 * in a period of time. For example, it stores the number of requests and
 * the number of characters of source text of each format.
 *
 * @author vmsanchez
 */
public class RequestsHistoryTO {

    /**
     * number of requests
     */
    private Map<Format,MutableInt> numberOfRequests;

    /**
     * Number of characters of each format.
     */
    private Map<Format,MutableInt> numberOfChars;

    /**
     * Constructor. Initially the number of characters of each format and the number of requests are 0.
     */
    public RequestsHistoryTO() {
            this.numberOfChars = numberOfChars=new HashMap<Format, MutableInt>();
            this.numberOfRequests= new HashMap<Format, MutableInt>();
    }


    /**
     * Adds information of a new requests, i.e. increases the number of requests by 1
     * and the number of characters of the request's format by the number of characters of the request.
     * @param format Request format
     * @param numchars Request's number of characters
     */
    public void addRequest(Format format,int numchars)
    {
        addToNumberOfChars(format, numchars);
        addToNumberOfRequests(format,1);
    }

    /**
     * Increases the number of characters of the specified format by the specified amount.
     * @param format Format to be increased
     * @param inc Amount to be increased
     */
    public void addToNumberOfChars(Format format,int inc)
    {
        synchronized(this)
        {
        if(numberOfChars.containsKey(format))
            numberOfChars.get(format).add(inc);
        else
            numberOfChars.put(format, new MutableInt(inc));
        }
    }

    /**
     * Decreases the number of characters of the specified format by the specified amount.
     * @param format Format to be decreased
     * @param dec Amount to be decreased
     */
    public void removeFromNumberOfChars(Format format,int dec)
    {
        synchronized(this)
        {
        if(numberOfChars.containsKey(format))
            numberOfChars.get(format).subtract(dec);
        }
    }

    /**
     * Increases the number of characters of each format by the amount
     * specified in the input map
     * 
     * @param pNumberOfChars Map with formats associated with increments.
     */
    public void mergeNumberOfChars(Map<Format,MutableInt> pNumberOfChars)
    {
        synchronized(this)
        {
        for(Entry<Format,MutableInt> entry: pNumberOfChars.entrySet())
            addToNumberOfChars(entry.getKey(), entry.getValue().intValue());
        }
    }

    /**
     * Increments the number of reuqests by the given amount
     * @param inc Number of requests increment
     */
    public void addToNumberOfRequests(Format format,int inc)
    {
        synchronized(this)
        {
        if(numberOfRequests.containsKey(format))
            numberOfRequests.get(format).add(inc);
        else
            numberOfRequests.put(format, new MutableInt(inc));
        }
    }

    public Map<Format,MutableInt> getNumberOfRequests() {
        return numberOfRequests;
    }

    public void setNumberOfRequests(Map<Format, MutableInt> numberOfRequests) {
        synchronized(this)
        {
            this.numberOfRequests = numberOfRequests;
        }
    }

    public Map<Format, MutableInt> getNumberOfChars() {
        synchronized(this)
        {
            return numberOfChars;
        }
    }

    @Override
    public RequestsHistoryTO clone()
    {
        synchronized(this)
        {
            RequestsHistoryTO copy = new RequestsHistoryTO();
            copy.numberOfChars=new HashMap<Format, MutableInt>(getNumberOfChars());
            copy.numberOfRequests=new HashMap<Format, MutableInt>(getNumberOfRequests());
            return copy;
        }
    }
    

}
