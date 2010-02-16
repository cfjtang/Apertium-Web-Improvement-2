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

    private String email;

    public RegisteredRequester(String apiKey) {
        this.email = apiKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String apiKey) {
        this.email = apiKey;
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
        if ((this.email == null) ? (other.email != null) : !this.email.equals(other.email)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.email != null ? this.email.hashCode() : 0);
        return hash;
    }

    

}
