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
 * Bean class containing all the information needed to connect via SSH to a remote machine and
 * start <code>ApertiumServerSlave</code>
 *
 * @author vmsanchez
 */
public class SSHHost implements HasHost{

    /**
     * Remote machine host name.
     */
    private String host;

    /**
     * User
     */
    private String user;

    /**
     * Password
     */
    private String password;

    /**
     * Path where <code>ApertiumServerSlave</code> is installed and the script "run-apertium-server.sh" can be found.
     */
    private String path;

    public SSHHost(String host, String user, String password, String path) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.path = path;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    

}
