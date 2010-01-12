/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.daemon;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Regular expression that separates different translations in the flow.
 * 
 * @author vitaka
 */


public class SeparatorRegexp implements Serializable{

    private Pattern regexp;
    private int idField;

    /**
     * Field of the regular expression where the id of the current translation can be found.
     * -1 means no trnslation id in the regular expression.
     *
     * @return
     */
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
