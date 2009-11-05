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
package com.gsoc.apertium.translationengines.rmi.transferobjects;

import java.io.Serializable;
import java.util.List;


/**
 * Transfer object that contains server's dynamic information, i.e., that changes during server's lifecycle.
 *
 * @author vmsanchez
 */
public class ServerStatusTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5955328728028786639L;
	
	/**
     * Daemons currently running on the server
     */
	private List<DaemonInformation> daemonsInformation;

    /**
     * Server's load
     */
	private double load;
	
	public List<DaemonInformation> getDaemonsInformation() {
		return daemonsInformation;
	}

	public void setDaemonsInformation(List<DaemonInformation> daemonsInformation) {
		this.daemonsInformation = daemonsInformation;
	}

	public double getLoad() {
		return load;
	}

	public void setLoad(double load) {
		this.load = load;
	}
	
	
}
