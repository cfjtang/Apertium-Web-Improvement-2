/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.router.logic;

/**
 *
 * @author vmsanchez
 */
public class RegisteredRequester extends Requester{

    private String apiKey;

    public RegisteredRequester(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RegisteredRequester other = (RegisteredRequester) obj;
        if ((this.apiKey == null) ? (other.apiKey != null) : !this.apiKey.equals(other.apiKey)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.apiKey != null ? this.apiKey.hashCode() : 0);
        return hash;
    }

    

}
