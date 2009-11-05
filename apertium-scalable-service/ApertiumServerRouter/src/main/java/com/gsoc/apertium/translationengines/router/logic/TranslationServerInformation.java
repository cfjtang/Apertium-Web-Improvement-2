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

import com.gsoc.apertium.translationengines.rmi.transferobjects.ServerInformationTO;
import com.gsoc.apertium.translationengines.rmi.transferobjects.ServerStatusTO;


/**
 * Static (CPU capacity, memory capacity, etc.) and dynamic (running daemons, etc.) information
 * of a translation server running <code>ApertiumServerSlave</code>.
 *
 * @author vitaka
 */
public class TranslationServerInformation {

    private ServerInformationTO serverInformation;
    private ServerStatusTO serverStatus;

    public ServerInformationTO getServerInformation() {
        return serverInformation;
    }

    public void setServerInformation(ServerInformationTO serverInformation) {
        this.serverInformation = serverInformation;
    }

    public ServerStatusTO getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(ServerStatusTO serverStatus) {
        this.serverStatus = serverStatus;
    }
}
