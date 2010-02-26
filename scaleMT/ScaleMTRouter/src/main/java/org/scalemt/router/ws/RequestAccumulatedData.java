/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.router.ws;

import java.util.Date;

/**
 *
 * @author vmsanchez
 */
class RequestAccumulatedData {
    private Date startingHour;
    private long duration;
    private String pair;
    private String format;
    private String user;
    private String ip;
    private String referer;
    private int numCharacters;
    private int cpuCost;
    private  String resultCode;


    public int getCpuCost() {
        return cpuCost;
    }

    public void setCpuCost(int cpuCost) {
        this.cpuCost = cpuCost;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getNumCharacters() {
        return numCharacters;
    }

    public void setNumCharacters(int numCharacters) {
        this.numCharacters = numCharacters;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public Date getStartingHour() {
        return startingHour;
    }

    public void setStartingHour(Date startingHour) {
        this.startingHour = startingHour;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

}
