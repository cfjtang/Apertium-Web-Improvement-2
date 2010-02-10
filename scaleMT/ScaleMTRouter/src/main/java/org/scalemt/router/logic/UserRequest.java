/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.router.logic;

import java.util.Date;

/**
 *
 * @author vmsanchez
 */
class UserRequest {

    private Date time;
    private int cpuCost;

    public UserRequest(Date time, int cpuCost) {
        this.time = time;
        this.cpuCost = cpuCost;
    }

    public int getCpuCost() {
        return cpuCost;
    }

    public void setCpuCost(int cpuCost) {
        this.cpuCost = cpuCost;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

}
