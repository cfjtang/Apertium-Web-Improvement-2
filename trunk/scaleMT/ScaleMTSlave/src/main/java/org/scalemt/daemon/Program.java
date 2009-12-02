/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.daemon;

import java.io.Serializable;

/**
 *
 * @author vitaka
 */
class Program implements Serializable{

    private String command;
    private String inFilter;
    private int input=-10;
    private int output=-10;

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

    
}
