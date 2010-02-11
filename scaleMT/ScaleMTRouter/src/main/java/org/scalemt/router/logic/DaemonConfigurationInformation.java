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


import org.scalemt.rmi.transferobjects.DaemonConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Information about a language pair: Cpu demand, memory demand, etc..
 * 
 * @author vmsanchez
 */
public class DaemonConfigurationInformation {

    /**
     * Commons-logging logger
     */
     static Log logger = LogFactory.getLog(DaemonConfigurationInformation.class);

     /**
      * CPU demand, in es-ca chars per second
      */
    private  int cpuDemand;

    /**
     * Memory demand, in megabytes
     */
    private  int memoryDemand;

    /**
     * Constructor.<br/>
     * Reads memory demand from <code>MemoryRequirements.properties</code> and 
     * sets CPU demand to zero.
     *
     * @param pair Pair data will be read from
     */
    public DaemonConfigurationInformation(DaemonConfiguration dc)
    {
        

        //cpuDemand=Integer.parseInt(Util.readConfigurationProperty("cpuDemand"+pair.toString()));
        cpuDemand=0;
        
        memoryDemand=100;
        try
        {
            memoryDemand=Integer.parseInt(Util.readPropertyFromFile(dc.toString(),"/MemoryRequirements.properties"));
        }
        catch(Exception e)
        {
            logger.warn("Cannot find property "+dc);
            try
            {
                memoryDemand=Integer.parseInt(Util.readPropertyFromFile("generic","/MemoryRequirements.properties"));
            }
            catch(Exception ex)
            {
                logger.warn("Cannot find memoryDemand Generic");
            }
        }
    }

    /**
     * Creates an object with the given CPU demand and memory demand.
     * @param cpuDemand CPU demand in es-ca characters per second.
     * @param memoryDemand Memory demand in megabytes
     */
    public DaemonConfigurationInformation(int cpuDemand, int memoryDemand) {
        this.cpuDemand = cpuDemand;
        this.memoryDemand = memoryDemand;
    }

    

    public int getCpuDemand() {
        return cpuDemand;
    }

    public void setCpuDemand(int cpuDemand) {
        this.cpuDemand = cpuDemand;
    }

    public int getMemoryDemand() {
        return memoryDemand;
    }

    public void setMemoryDemand(int memoryDemand) {
        this.memoryDemand = memoryDemand;
    }

    
}
