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
package org.scalemt.rmi.transferobjects;

import java.io.Serializable;


/**
 * Information about a running daemon in a translation engine.
 *
 * 
 * @author vmsanchez
 */
public class DaemonInformation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4498269392124983112L;

    /**
     * Unique identification. There is no daemon with the same ID in the same translation engine.
     */
	private long id;

    /**
     * Language pair the daemon is able to translate
     */
	private DaemonConfiguration configuration;

    /**
     * Number of characters currently being processed by the daemon
     */
	private long charactersInside;

    /**
     * pid of the process
     */
	private int pid;

    /**
     * % of CPU usage
     */
	private float cpuUsage;

    /**
     * % of memory usage
     */
	private float memoryUsage;

   
    /**
     * Contructor that needs the daemon unique id and the language pair
     * @param id Daemon id
     * @param pair Language pair
     */
	public DaemonInformation(long id, DaemonConfiguration c) {
		super();
		this.id = id;
		this.configuration = c;
		this.charactersInside=0;
		this.cpuUsage=-1;
		this.memoryUsage=-1;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

    public DaemonConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(DaemonConfiguration configuration) {
        this.configuration = configuration;
    }

	
	public long getCharactersInside() {
		return charactersInside;
	}

	public void setCharactersInside(long charactersInside) {
		this.charactersInside = charactersInside;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public float getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(float cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	public float getMemoryUsage() {
		return memoryUsage;
	}

	public void setMemoryUsage(float memoryUsage) {
		this.memoryUsage = memoryUsage;
	}
	
	public DaemonInformation copy()
	{
		DaemonInformation copy = new DaemonInformation(this.getId(),this.getConfiguration());
		copy.setCharactersInside(this.getCharactersInside());
		copy.setCpuUsage(this.getCpuUsage());
		copy.setMemoryUsage(this.getMemoryUsage());
		copy.setPid(this.getPid());
		return copy;
	}
	
}
