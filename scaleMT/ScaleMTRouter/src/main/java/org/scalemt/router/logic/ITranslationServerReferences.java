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




/**
 * Manages references to remote translation servers running <code>ApertiumServerSlave</code>.
 *
 * @author vitaka
 */
public interface ITranslationServerReferences {
    /**
     * Tries to add a new remote translation engine. Connects by RMI
     * to the given host and port, and looks for a remote object.
     *
     * @param Server information: host, port and remote object name
     * @return True if the remote object can be retrieved, false otherwise
     */
    boolean addTranslationEngine(TranslationServerId serverid);

    /**
     * Gets an already working translation engine.
     *
     * @param url Translation engine url
     * @return The translation engine, or null if it wasn't created before
     * with the method <code>addTranslationEngine</code>
     */
    public ITranslationEngine getTranslationEngine(TranslationServerId serverid);

    /**
     * Removes the translation engine identified by the given server information.
     * @param serverid
     * @return If the translation engine doesn't exist, returns false. If it can be removed, returns true.
     */
    public boolean removeTranslationEngine(TranslationServerId serverid);

}
