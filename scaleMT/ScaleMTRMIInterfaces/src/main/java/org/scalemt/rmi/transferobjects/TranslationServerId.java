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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.Serializable;

/**
 * Information that identifies a remote translation server.
 *
 * @author vitaka
 */
public class TranslationServerId implements Serializable{

	private static final long serialVersionUID = 7426035901460906178L;

		/**
         * RMI registry host
         */
        private String host;

        /**
         * RMI registry port
         */
        private int port;

        /**
         * Remote object name
         */
        private String objectName;

    public TranslationServerId(String host, int port, String objectName) {
        this.host = host;
        this.port = port;
        this.objectName = objectName;
    }

    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof TranslationServerId))
            return false;
        else
        {
            TranslationServerId comp = (TranslationServerId) obj;
            return ( this.getHost()==null ? this.getHost()==comp.getHost() : this.getHost().equals(comp.getHost())  && ( this.getObjectName()==null ? this.getObjectName()==comp.getObjectName() : this.getObjectName().equals(comp.getObjectName())) && this.getPort()==comp.getPort()) ;
        }
            
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.host != null ? this.host.hashCode() : 0);
        hash = 53 * hash + this.port;
        hash = 53 * hash + (this.objectName != null ? this.objectName.hashCode() : 0);
        return hash;
    }



        

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return getHost()+":"+getPort()+"/"+getObjectName();
    }


        
}
