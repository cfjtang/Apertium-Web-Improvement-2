/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.router.logic;

/**
 *
 * @author vmsanchez
 */
public class AnonymousRequester extends Requester{

    private String ip;

    public AnonymousRequester(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AnonymousRequester other = (AnonymousRequester) obj;
        if ((this.ip == null) ? (other.ip != null) : !this.ip.equals(other.ip)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 6;
        hash = 79 * hash + (this.ip != null ? this.ip.hashCode() : 0);
        return hash;
    }
    
    

}
