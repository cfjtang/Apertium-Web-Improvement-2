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
import java.util.Set;


/**
 * Transfer object that contains server's static information, i.e., that doesn't change during server's lifecycle.
 *
 * @author vmsanchez
 */
public class ServerInformationTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8569713428262385673L;
	
	/**
     * Language pairs that can be translated on this server
     */
	private Set<LanguagePair> supportedPairs;

    /**
     * CPU capacity, in es-ca text characters per second
     */
	private int cpuCapacity;

    /**
     * Maximum capacity of a single daemon, in es-ca text characters per second
     */
	private int cpuCapacityPerDaemon;

    /**
     * Memory capacity, in MegaBytes
     */
	private int memoryCapacity;

	public Set<LanguagePair> getSupportedPairs() {
		return supportedPairs;
	}

	public void setSupportedPairs(Set<LanguagePair> supportedPairs) {
		this.supportedPairs = supportedPairs;
	}

	public int getCpuCapacity() {
		return cpuCapacity;
	}

	public void setCpuCapacity(int cpuCapacity) {
		this.cpuCapacity = cpuCapacity;
	}

	public int getMemoryCapacity() {
		return memoryCapacity;
	}

	public void setMemoryCapacity(int memoryCapacity) {
		this.memoryCapacity = memoryCapacity;
	}

	public int getCpuCapacityPerDaemon() {
		return cpuCapacityPerDaemon;
	}

	public void setCpuCapacityPerDaemon(int cpuCapacityPerDaemon) {
		this.cpuCapacityPerDaemon = cpuCapacityPerDaemon;
	}

	
	
	
}
