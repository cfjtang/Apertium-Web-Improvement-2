/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.daemon;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 *
 * @author vitaka
 */
public class SeparatorRegexp implements Serializable{

    private Pattern regexp;
    private int idField;

    public int getIdField() {
        return idField;
    }

    public void setIdField(int idField) {
        this.idField = idField;
    }

    public Pattern getRegexp() {
        return regexp;
    }

    public void setRegexp(Pattern regexp) {
        this.regexp = regexp;
    }

    public SeparatorRegexp(Pattern regexp, int idField) {
        this.regexp = regexp;
        this.idField = idField;
    }

    

}
