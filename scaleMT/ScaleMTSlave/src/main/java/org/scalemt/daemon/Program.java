/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.daemon;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.scalemt.rmi.transferobjects.Format;

/**
 *
 * @author vitaka
 */
class Program implements Serializable{

    private String command;
    private String inFilter;
    private int input=-10;
    private int output=-10;
    private Set<Format> onlyFormats;
    private Map<String,String> restrictions;

    public Program() {
        onlyFormats=new HashSet<Format>();
        for(Format f:Format.values())
            onlyFormats.add(f);
        restrictions=new HashMap<String, String>();
    }



    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getInput() {
        return input;
    }

    public void setInput(int input) {
        this.input = input;
    }

    public int getOutput() {
        return output;
    }

    public void setOutput(int output) {
        this.output = output;
    }

    public String getInFilter() {
        return inFilter;
    }

    public void setInFilter(String inFilter) {
        this.inFilter = inFilter;
    }

    public Set<Format> getOnlyFormats() {
        return onlyFormats;
    }

    public void setOnlyFormats(Set<Format> onlyFormats) {
        this.onlyFormats = onlyFormats;
    }

    public Map<String, String> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(Map<String, String> restrictions) {
        this.restrictions = restrictions;
    }
}
