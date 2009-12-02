/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.daemon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vitaka
 */
public class TranslationCore implements Serializable{
    private String command;
    private int input=-10;
    private int output=-10;
    private boolean nullFlush=false;
    private boolean separateAfterDeformat=false;
    private String textBefore;
    private List<SeparatorRegexp> regexpsBefore;
    private String textAfter;
    private List<SeparatorRegexp> regexpsAfter;
    private String trash;

    public TranslationCore() {
        regexpsAfter=new ArrayList<SeparatorRegexp>();
        regexpsBefore= new ArrayList<SeparatorRegexp>();
        trash=null;
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

    public boolean isNullFlush() {
        return nullFlush;
    }

    public void setNullFlush(boolean nullFlush) {
        this.nullFlush = nullFlush;
    }

    public List<SeparatorRegexp> getRegexpsAfter() {
        return regexpsAfter;
    }

    public void setRegexpsAfter(List<SeparatorRegexp> regexpsAfter) {
        this.regexpsAfter = regexpsAfter;
    }

    public List<SeparatorRegexp> getRegexpsBefore() {
        return regexpsBefore;
    }

    public void setRegexpsBefore(List<SeparatorRegexp> regexpsBefore) {
        this.regexpsBefore = regexpsBefore;
    }

   

    public String getTextAfter() {
        return textAfter;
    }

    public void setTextAfter(String textAfter) {
        this.textAfter = textAfter;
    }

    public String getTextBefore() {
        return textBefore;
    }

    public void setTextBefore(String textBefore) {
        this.textBefore = textBefore;
    }

    public boolean isSeparateAfterDeformat() {
        return separateAfterDeformat;
    }

    public void setSeparateAfterDeformat(boolean separateAfterDeformat) {
        this.separateAfterDeformat = separateAfterDeformat;
    }

    public String getTrash() {
        return trash;
    }

    public void setTrash(String trash) {
        this.trash = trash;
    }
    
}
