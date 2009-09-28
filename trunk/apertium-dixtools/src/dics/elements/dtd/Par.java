/*
 * Copyright (C) 2007 Universitat d'Alacant / Universidad de Alicante
 * Author: Enrique Benimeli Bofarull
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package dics.elements.dtd;

import java.io.IOException;

import dics.elements.utils.DicOpts;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class Par extends DixElement {

    
    public String sa;

    public String[] prm;

    
    public Par() {
        super("par");
    }

    /**
     * 
     * @param value
     */
    public Par(String value) {
      this();
       setValue(value);
    }

    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    @Override
    public void printXML(Appendable dos, DicOpts opt) throws IOException {
        // write blank lines and processingComments from original file
        dos.append(prependCharacterData);
        if (!opt.noProcessingComments) dos.append(makeTabbedCommentIfData(processingComments));


        if (opt.nowAlign) {
          dos.append(toString());
        } else {
          dos.append(tab(4) + toString() +  " \n");
        }
    }


    
    @Override
    public String toString() {
        String saAttr = sa==null?"":" sa=\"" + sa + "\" ";
        return "<" + TAGNAME + " n=\"" + getValue() + "\"" + prmToString() + saAttr + "/>" ;
    }

  public void setPrm(int n, String v) {
    if (prm==null) prm=new String[10];
    prm[n] = v;
  }

    private String prmToString() {
        String prms="";
        if (prm!=null) {
            for (int i=0; i<prm.length; i++) {
                if (prm[i]!=null) {
                    prms+=" prm"+(i==0?"":i)+"=\""+prm[i]+"\"";
                }
            }
        }
        return prms;
    }
}
