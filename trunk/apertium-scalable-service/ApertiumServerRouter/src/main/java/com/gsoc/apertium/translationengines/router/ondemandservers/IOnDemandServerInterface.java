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
package com.gsoc.apertium.translationengines.router.ondemandservers;

/**
 * Interface for starting and stopping servers running Apertium.
 * <br/>
 * The number of servers started through this Interface can be limited.
 * The limit is read from the property <code>maxServers</code>, from <code>OnDemandServerInterface.properties</code>.
 * <br/>
 * The working implementation of this interface is read from the same configuration file. The property
 * is <code>class</code> and contains the fully qualified name of the implementation class. We
 * can choose between <code>com.gsoc.apertium.translationengines.router.ondemandservers.LocalNetworkOnDemandServer</code>, for
 * local networks and <code>com.gsoc.apertium.translationengines.router.ondemandservers.AmazonOnDemandServer</code> if the application
 * is deployed in Amazon EC2
 *
 *
 *
 * @author vitaka
 */
public interface IOnDemandServerInterface {
    /**
     * Gets the number of servers that can be started.
     * @return Number of servers that can be started.
     */
    public int getAvailableServers();

    /**
     * Stops a running server. <br/>
     * This method is usually called when the conection to an ApertiumServerSlave instance is lost.
     *
     * @param serverhost Host of the server to stop.
     * @throws com.gsoc.apertium.translationengines.router.ondemandservers.OnDemandServerException
     */
    public void stopServer(String serverhost) throws OnDemandServerException;

    /**
     * Starts a new server. Depending on the implementing interface, it simply connects by SSH to an existing computer
     * in the local network and starts <code>ApertiumServerSlave</code> or starts a new virtual machine instance in
     * Amazon EC2.
     * @return New servers' host name.
     * @throws com.gsoc.apertium.translationengines.router.ondemandservers.OnDemandServerException If there are no available servers, a <code>NotAvailableServersException</code> is thrown. <br/>
     * If there is an unexpected error a generic <code>OnDemandServerException</code> is thrown.
     */
    public String startServer() throws OnDemandServerException;
}
