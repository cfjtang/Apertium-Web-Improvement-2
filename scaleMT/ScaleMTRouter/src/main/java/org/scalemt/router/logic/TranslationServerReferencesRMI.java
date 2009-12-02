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



import org.scalemt.rmi.slave.ITranslationEngine;
import org.scalemt.rmi.transferobjects.TranslationServerId;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Manages a set of remote translation engine references. Connects to remove servers using
 * RMI protocol
 *
 * @author vitaka
 */
public class TranslationServerReferencesRMI implements ITranslationServerReferences {

    /**
     * Commons-logging logger
     */
    static Log logger = LogFactory.getLog(TranslationServerReferencesRMI.class);

    /**
     * Map associating each server id with its proxy object
     */
    private Map<TranslationServerId,ITranslationEngine> translationEngines;

    /**
     * Default constructor.  Instantiates an object with no remote servers
     */
    public TranslationServerReferencesRMI() {
        translationEngines=new HashMap<TranslationServerId,ITranslationEngine>();
    }

   
    @Override
    public synchronized boolean addTranslationEngine(TranslationServerId serverid)
    {
        try
        {
        Registry registry = LocateRegistry.getRegistry(serverid.getHost(),serverid.getPort());
        ITranslationEngine translationEngine = (ITranslationEngine) registry.lookup(serverid.getObjectName());
        if(translationEngine!=null)
            translationEngines.put(serverid, translationEngine);
        else throw new RuntimeException("Remote object not found");
        return true;
        }
        catch(Exception e)
        {
           logger.warn("Can't add translation engine",e);
            return false;
        }
    }

  
    @Override
    public ITranslationEngine getTranslationEngine(TranslationServerId serverid)
    {
        return translationEngines.get(serverid);
    }

    @Override
    public boolean removeTranslationEngine(TranslationServerId serverid) {
        return translationEngines.remove(serverid)!=null;
    }

    



}
