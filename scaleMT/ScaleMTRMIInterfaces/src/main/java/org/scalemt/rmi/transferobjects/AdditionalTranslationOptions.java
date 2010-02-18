/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.rmi.transferobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vmsanchez
 */
public class AdditionalTranslationOptions implements Serializable{
    private List<Long> dictionaries;
    private Map<String,String> options;

    public AdditionalTranslationOptions() {
        dictionaries=new ArrayList<Long>();
        options=new HashMap<String, String>();
    }

    public AdditionalTranslationOptions(List<Long> dictionaries) {
        this.dictionaries = dictionaries;
        options=new HashMap<String, String>();
    }

    

    public List<Long> getDictionaries() {
        return dictionaries;
    }

    public void setDictionaries(List<Long> dictionaries) {
        this.dictionaries = dictionaries;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    
}
