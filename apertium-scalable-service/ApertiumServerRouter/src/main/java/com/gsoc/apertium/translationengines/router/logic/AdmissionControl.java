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

/**
 * Simple admission control mechanism.<br/>
 *
 * Accepts requests if the system load is not bigger than a treshold.
 * System load is calculated by combining real load right now and system load in the previous instant.
 *<br/>
 * Reads the following properties from <code>configuration.properties</code>:
 * <ul>
 * <li><code>admissioncontrol_treshold</code>:  If system load is over this threshold, requests won't be accepted</li>
 * <li><code>admissioncontrol_k</code>:How to combine real load and load in the previous instant: load = real load * k  +  previous load * (1-k) </li>
 * </ul>
 *
 * @see http://en.wikipedia.org/wiki/Admission_control
 * @author vitaka
 */
public class AdmissionControl {

    /**
     * Singleton instance
     */
    private static AdmissionControl instance = null;

    /**
     * Gets the singleton instance
     * @return Singleton instance
     */
    public static AdmissionControl getInstance()
    {
        if(instance==null)
        {
            instance=new AdmissionControl(Double.parseDouble(Util.readConfigurationProperty("admissioncontrol_treshold")), Double.parseDouble(Util.readConfigurationProperty("admissioncontrol_k")));
        }


        return instance;
    }

    /**
     * If system load is over this treshold, requests won't be accepted
     */
    private double acceptTreshold;

    /**
     * Load in the previous instant
     */
    private double predictedLoad;

    /**
     * Accept requests?
     */
    private boolean canAccept;

    /**
     * How to combine real load and load in the previous instant:
     * 
     * load = real load * k  +  previous load * (1-k)
     *
     */
    private final double k;

    public AdmissionControl(double acceptTreshold, double k) {
        this.acceptTreshold = acceptTreshold;
        this.predictedLoad=0;
        this.k=k;
        this.canAccept=true;
    }

    /**
     * Updates admission control system with the real measured load
     * @param measuredLoad Real system load
     */
    public void update(double measuredLoad)
    {
        predictedLoad=(1-k)*predictedLoad+k*measuredLoad;
        updateCanAccept();
    }

    /**
     * Updates the boolean field that deetermines if the the requests are accepted with the result of
     * {@link AdmissionControl#update(double) }
     */
    private void updateCanAccept()
    {
        if(predictedLoad>acceptTreshold)
            canAccept=false;
        else
            canAccept=true;

    }

    /**
     * Tells if a request can be accepted
     * @return <code>true</code> if the system is not overloaded and a request can be accepted, and <code>false</code> otherwise
     */
    public boolean canAcceptRequest()
    {
        return canAccept;
    }
}
