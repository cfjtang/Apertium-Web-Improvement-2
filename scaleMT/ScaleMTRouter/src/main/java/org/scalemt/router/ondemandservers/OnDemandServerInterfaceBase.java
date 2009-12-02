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
package org.scalemt.router.ondemandservers;

import org.scalemt.router.logic.Util;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Base class of all {@link IOnDemandServerInterface } implementations.
 *
 * @author vmsanchez
 */
public abstract class OnDemandServerInterfaceBase<T extends HasHost> implements IOnDemandServerInterface{

    /**
     * <code>Commons-logging logger</code>
     */
    static Log logger = LogFactory.getLog(OnDemandServerInterfaceBase.class);

    /**
     * Information about available servers.
     */
    protected Set<T> availableServers;

    /**
     * Map containing running servers. Keys are the servers's hosts.
     */
    protected Map<String, T> runningServers;

    /**
     * Limit of servers started by the systems. Read from <code>maxServers</code>, from <code>OnDemandServerInterface.properties</code>.
     */
    protected int maxAvailableServers;

    public OnDemandServerInterfaceBase() {
        maxAvailableServers=0;
        availableServers=new HashSet<T>();
        runningServers=new HashMap<String, T>();
        try
        {
            maxAvailableServers=Integer.parseInt(Util.readPropertyFromFile("maxServers", "/OnDemandServerInterface.properties"));
        }
        catch(Exception e)
        {
            logger.warn("Cannot read on-demand server limit. On-demand server management will be disabled", e);
        }
    }

    @Override
    public int getAvailableServers() {
        return Math.min(maxAvailableServers, availableServers.size());
    }

    @Override
    public String startServer() throws OnDemandServerException {

        if(runningServers.size()>=maxAvailableServers)
             throw new NotAvailableServersException();

        T t=null;
       try
       {

         Iterator<T> it=availableServers.iterator();
         t = it.next();
         it.remove();
         
         //availableServers.remove(t);
         String host=startServerImpl(t);
         runningServers.put(host, t);

         return host;
         
       }
       catch(NoSuchElementException nsee)
       {
            throw new NotAvailableServersException();
       }
        catch(OnDemandServerException odee)
        {
            availableServers.add(t);
            throw odee;
        }
        
    }

    /**
     * Starts the server identified by t
     *
     * @param t Server identification.
     * @return Server host
     * @throws com.gsoc.apertium.translationengines.router.ondemandservers.OnDemandServerException If an unexpected error happens
     */
    protected abstract String startServerImpl(T t) throws OnDemandServerException;

    @Override
    public void stopServer(String serverhost) throws OnDemandServerException {
       
        T t = runningServers.get(serverhost);
        if(t==null)
        {
            throw new NotAvailableServersException();
        }
        else
        {
           runningServers.remove(serverhost);
           availableServers.add(t);
           stopServerImpl(t);           
        }
    }

    /**
     * Stops the server identified by t.
     *
     * @param t Server identification
     * @throws com.gsoc.apertium.translationengines.router.ondemandservers.OnDemandServerException If an unexpected error happens
     */
    protected abstract void stopServerImpl(T t) throws OnDemandServerException;

    

    




}
