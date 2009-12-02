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
package org.scalemt.transferobjects;

/**
 * Information about the CPU capacity of a server
 * @author vmsanchez
 */
public class ServerCPUInformation {

    /**
     * Absolute CPU capacity of the server, measured in es-ca text characters translated per second
     */
	private int capacity;
    /**
     * Maximum translation speed system can reach using a single daemon
     */
	private int maxCapacityPerDaemon;
	
	public ServerCPUInformation(int capacity,int maxCapacityPerDaemon ,int optimumDaemonNumber) {
		super();
		this.capacity = capacity;
		this.maxCapacityPerDaemon=maxCapacityPerDaemon;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getMaxCapacityPerDaemon() {
		return maxCapacityPerDaemon;
	}
	public void setMaxCapacityPerDaemon(int maxCapacityPerDaemon) {
		this.maxCapacityPerDaemon = maxCapacityPerDaemon;
	}
	
	
	
}
