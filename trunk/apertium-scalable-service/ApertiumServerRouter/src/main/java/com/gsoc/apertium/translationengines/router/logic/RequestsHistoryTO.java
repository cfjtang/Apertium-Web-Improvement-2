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
    private int numberOfRequests;

    /**
     * Number of characters of each format.
     */
    private Map<Format,MutableInt> numberOfChars;

    /**
     * Constructor. Initially the number of characters of each format and the number of requests are 0.
     */
    public RequestsHistoryTO() {
            this.numberOfChars = numberOfChars=new HashMap<Format, MutableInt>();
    }


    /**
     * Constructor that initializes the object to a given number of requests, but the number
     * of characters of each format is still 0.
     *
     * @param numberOfRequests Initial number of requests
     */
    public RequestsHistoryTO(int numberOfRequests) {
        this.numberOfRequests = numberOfRequests;
        this.numberOfChars = numberOfChars=new HashMap<Format, MutableInt>();
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
        addToNumberOfRequests(1);
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
    public void addToNumberOfRequests(int inc)
    {
        synchronized(this)
        {
        numberOfRequests+=inc;
        }
    }

    public int getNumberOfRequests() {
        return numberOfRequests;
    }

    public void setNumberOfRequests(int numberOfRequests) {
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
            RequestsHistoryTO copy = new RequestsHistoryTO(getNumberOfRequests());
            copy.numberOfChars=new HashMap<Format, MutableInt>(getNumberOfChars());
            return copy;
        }
    }
    

}
