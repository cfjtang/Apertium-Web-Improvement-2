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
package org.scalemt.rmi.router;


import org.scalemt.rmi.transferobjects.TranslationServerId;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI interface of Request Router.
 * @author vitaka
 */
public interface IApplicationRouter extends Remote {

    /**
     * Adds a recently started server to the list of available servers of the request router.
     * Probably new daemons will be started an it will receive translation requests.
     * @param id Server identification.
     * @throws java.rmi.RemoteException If there is a communication error.
     */
    public void addServer(TranslationServerId id) throws RemoteException;
}
