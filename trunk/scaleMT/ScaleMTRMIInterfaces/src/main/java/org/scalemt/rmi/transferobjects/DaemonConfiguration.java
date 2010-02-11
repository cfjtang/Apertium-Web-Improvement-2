/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.rmi.transferobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author vitaka
 */
public class DaemonConfiguration implements Serializable{
    private LanguagePair languagePair;
    private Set<Format> formats;

    public DaemonConfiguration() {
        formats=new HashSet<Format>();
    }

    public DaemonConfiguration(LanguagePair languagePair, Format format) {
        this.languagePair = languagePair;
        formats=new HashSet<Format>();
        formats.add(format);
    }

    public DaemonConfiguration(LanguagePair languagePair, Set<Format> formats) {
        this.languagePair = languagePair;
        this.formats = formats;
    }

    
  

    public Set<Format> getFormats() {
        return formats;
    }

    public void setFormats(Set<Format> formats) {
        this.formats = formats;
    }


    public LanguagePair getLanguagePair() {
        return languagePair;
    }

    public void setLanguagePair(LanguagePair languagePair) {
        this.languagePair = languagePair;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DaemonConfiguration)
        {
            DaemonConfiguration dc = (DaemonConfiguration) obj;
            return (languagePair.equals(dc.languagePair) && formats.containsAll(dc.formats) && dc.formats.containsAll(formats));
        }
        else return false;
    }


    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(languagePair+"|");
        List<String> formatsList = new ArrayList<String>();
        for(Format f: formats)
        {
            formatsList.add(f.toString());
        }
        Collections.sort(formatsList);
        for(String f: formatsList)
        {
            str.append(f);
            str.append(",");
        }
        str.deleteCharAt(str.length()-1);
        return str.toString();
    }

    public static DaemonConfiguration parse(String str)
    {
        //System.out.println("DaemonConfiguration. Parsing "+str);
         String[] fr0 = str.split("\\|");
         //System.out.println("DaemonConfiguration. fr0[0] "+fr0[0]);
        LanguagePair pair = new LanguagePair( fr0[0], "-".toCharArray());
        String[] fr1 = fr0[1].split(",");
        //System.out.println("DaemonConfiguration. fr0[1] "+fr0[1]);
        Set<Format> formats=new HashSet<Format>();
        for(String fs: fr1)
        {
            //System.out.println("DaemonConfiguration. fr1[-] "+fs);
            try
            {
            Format f=Format.valueOf(fs);
            formats.add(f);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        DaemonConfiguration c = new DaemonConfiguration(pair, formats);
        return c;

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.languagePair != null ? this.languagePair.hashCode() : 0);
        hash = 19 * hash + (this.formats != null ? this.formats.hashCode() : 0);
        return hash;
    }

    



}
