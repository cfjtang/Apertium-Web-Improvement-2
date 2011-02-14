/*
 * Copyright 2010 Jimmy O'Regan
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

package misc.termcat.guessers;
import dics.elements.dtd.E;
import dics.elements.dtd.P;
import dics.elements.dtd.L;
import dics.elements.dtd.R;
import dics.elements.dtd.G;
import dics.elements.dtd.I;
import dics.elements.dtd.Par;
import java.util.ArrayList;

/**
 *
 * @author jimregan
 */
public class Catalan extends Guesser {

    private ArrayList<E> buildSimpleEntry (String lem, String i, String par) {
        ArrayList<E> list = new ArrayList<E>();
        list.add(buildSimpleIEntry(lem, i, par));
        String left = i;
        if (left.contains("l·l")) {
            left = left.replaceAll("l·l", "ŀl");
            list.add(buildSimpleLREntry(lem, left, i, par));
        }

        return list;
    }

    @Override
    public E[] guess(String para, String tags) {
        ArrayList<E> list = new ArrayList<E>();
        String stem = "";

        if ("ij".equals(tags)) {
            list.addAll(buildSimpleEntry(para, para, "a_reveure__ij"));
        }
        if ("adv".equals(tags)) {
            list.addAll(buildSimpleEntry(para, para, "sempre__adv"));
        }
//        if ("adj".equals(tags)) {
//            list.addAll(buildSimpleEntry(para, para, "expensive__adj"));
//        }

        if ("n.f".equals(tags)) {
            if (para.endsWith("ió")) {
                stem = para.substring(0, para.length()-2);
                list.addAll(buildSimpleEntry(para, stem, "acci/ó__n"));
            } else if (para.endsWith("tat")) {
                stem = para;
                list.addAll(buildSimpleEntry(para, stem, "accessibilitat__n"));
            } else if (para.endsWith("a")) {
                stem = para.substring(0, para.length()-2);
                list.addAll(buildSimpleEntry(para, stem, "abell/a__n"));
//            } else {
//                stem = para;
//                list.addAll(buildSimpleEntry(para, stem, "house__n"));
            }
        }

        return list.toArray(new E[list.size()]);
    }

    boolean isGroupStartN (String para) {
        if (para.startsWith("d'")) {
            return true;
        } else if ("de".equals(para)) {
            return true;
        } else if ("en".equals(para)) {
            return true;
        } else if ("al".equals(para)) {
            return true;
        } else if ("del".equals(para)) {
            return true;
        } else if ("per".equals(para)) {
            return true;
        } else {
            return false;
        }
    }

    boolean isGroupStartV (String para) {
        if ("la".equals(para)) {
            return true;
        } else if ("el".equals(para)) {
            return true;
        } else {
            return isGroupStartN(para);
        }
    }

}
