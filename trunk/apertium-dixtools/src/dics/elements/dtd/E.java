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
 * You should have received author copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package dics.elements.dtd;

import dics.elements.utils.DicOpts;
import java.io.IOException;
import java.util.ArrayList;

import dics.elements.utils.ElementList;
import dics.elements.utils.Msg;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Comparator;

/**
 * 
 * @author Enrique Benimeli Bofarull
 * 
 */
public class E extends DixElement implements Cloneable {

    public ElementList children = new ElementList();

    public String restriction;

    public String slr;
    
    public String srl;
    
    public String lemma;
    
    public String author;
    
    public String comment;
    
    public String ignore;
    
    public String aversion;
    
    public String alt;
    
    public boolean shared = false;
    
    public String patternApplied;
    
    public E() {
      super("e");
    }

    public E(String r, String lm, String a,  String c) {
      this();
        this.restriction = r;

        // JimRegan's dubious fix.
        // Should really be in DicFix or somewhere else, and not in the DTD code.
        if (lm != null) lm = lm.replaceAll("\\&", "\\&amp;");

//        String escapedlm = this.lemma;
 //       escapedlm = escapedlm.replaceAll("\\&", "\\&amp;");
  //      this.lemma(escapedlm);

        this.lemma = lm;
        this.author = a;
        this.comment = c;
    }


    public ElementList getChildren() {
        return children;
    }

    public boolean hasPrependorAppendData() {
        return !(prependCharacterData.trim().isEmpty() && appendCharacterData.trim().isEmpty());
    }
    
    /**
     * 
     * @param e
     */
    public void addChild(DixElement e) {
        children.add(e);
    }

    /**
     * 
     * @return Undefined         */
    public P getFirstP() {
        for (DixElement e : children) {
            if (e instanceof P) {
                return (P) e;
            }
        }
        return null;
    }

    /**
     * 
     * @return Undefined         
     */
    public I getFirstI() {
        for (DixElement e : children) {
            if (e instanceof I) {
                return (I) e;
            }
        }
        return null;
    }

    /**
     * 
     * @param side
     * @return Undefined         
     */
    public String getValue(String side) {
        for (DixElement e : children) {
            if (e instanceof I) {
                return ((I) e).getValue();
            }
            if (e instanceof P) {
                if (side.equals("L")) {
                    return ((P) e).l.getValue();
                }
                if (side.equals("R")) {
                    return ((P) e).r.getValue();
                }
            }
        }
        return lemma;
    }

    /**
     * 
     * @param side
     * @return Value without tags
     */
    public String getValueNoTags(String side) {
        for (DixElement e : children) {
            if (e instanceof I) {
                return ((I) e).getValueNoTags();
            }
            if (e instanceof P) {
                if (side.equals("L")) {
                    return ((P) e).l.getValueNoTags();
                }
                if (side.equals("R")) {
                    return ((P) e).r.getValueNoTags();
                }
            }
        }
        return lemma;
    }

    /**
     * Returns the first part of the left or right side of an entry (or the first invariant section).
     * Examples:
     * <pre>
     * <e><ignore>Ameriko</ignore><par n="Barcelono__np"/> </e>   gives 'Ameriko'
     * <e><ignore>Al</ignore><par n="ĝ"/> <ignore>erio</ignore><par n="Barcelono__np"/> </e> gives just 'Al'
     * <e><l>mi</l><restriction>mi<prn><ref><p1><mf><sg></restriction></e> give 'mi'
     * </pre>
     * @param side can be R or L
     * @return A ContentElement object
     */
    public ContentElement getSide(String side) {
        for (DixElement e : children) {
            if (e instanceof I) {
                return ((I) e);
            }
            if (e instanceof P) {
                if (side.equals("L")) {
                    return ((P) e).l;
                }
                if (side.equals("R")) {
                    return ((P) e).r;
                }
            }
        }
        return null;
    }

    /**
     * 
     * @return Undefined         */
    public L getLeft() {
        ContentElement cE = getSide("L");
        L lE = null;
        if (cE instanceof I) {
            lE = new L(cE);
            return lE;
        }
        return (L) cE;
    }

    /**
     * 
     * @return Undefined         */
    public R getRight() {
        ContentElement cE = getSide("R");
        R rE = null;
        if (cE instanceof I) {
            rE = new R(cE);
            return rE;
        }
        return (R) cE;
    }

    /**
     * 
     * @param side
     * @return Undefined         */
    public ElementList getChildren(String side) {
        for (DixElement e : children) {
            if (e instanceof I) {
                return ((I) e).getChildren();
            }
            if (e instanceof P) {
                if (side.equals("L")) {
                    return ((P) e).l.getChildren();
                }
                if (side.equals("R")) {
                    return ((P) e).r.getChildren();
                }
            }
        }
        return null;
    }


    /**
     * 
     * @return Undefined         */
    public boolean isRegularExpr() {
        for (DixElement e : children) {
            if (e instanceof Re) {
                return true;
            }
        }
        return false;
    }

    private static String spaces = "                                                                                                                 ";
    /**
     * 
     * @param dos
     * @throws java.io.IOException
     */
    @Override
    public void printXML(Appendable dos, DicOpts opt) throws IOException {
        // write blank lines and processingComments from original file
        if (opt.stripEmptyLines && prependCharacterData.trim().isEmpty()) {
            ;
        } else {
            dos.append(prependCharacterData);
        }
        
        // prepend processingComments added in this run
        if (!opt.noProcessingComments) {
            String pc = processingComments;
            dos.append(makeCommentIfData(pc));
        }
        
        String attributes = this.getAttrString();
        if (!opt.nowAlign) {
            dos.append(tab(2) + "<e" + attributes + ">\n");

            for (DixElement e : children) {
                e.printXML(dos, opt);
            }

            dos.append(tab(2) + "</e>"+appendCharacterData+"\n\n");
        } else { 
            StringBuilder dosy = new StringBuilder(120);
            dosy.append(spaces.substring(0,opt.alignE));
            dosy.append("<e").append(attributes).append(">");
            int neededSpaces = opt.alignP - dosy.length();
            if (neededSpaces>0) {
              dosy.append(spaces.substring(0, Math.min(spaces.length(), neededSpaces)));
            }        

            if (children != null) {
                for (DixElement e : children) {
                    if (e instanceof P) {
                      ((P) e).printXML1LineAligned(dosy, opt.alignR);
                    } else {
                      e.printXML(dosy, opt);
                    }
                }
            }
            dosy.append("</e>"+appendCharacterData+"\n");
            dos.append(dosy.toString());
        }
    }

    
    /**
     * 
     * @return String of attributes
     */
    private String getAttrString() {
        StringBuilder attributes = new StringBuilder();
        if (restriction != null) {
            attributes.append(" r=\"" + restriction + "\"");
        }
        if (slr != null) {
            attributes.append(" slr=\"" + slr + "\"");
        }
        if (srl != null) {
            attributes.append(" srl=\"" + srl + "\"");
        }
        if (lemma != null) {
            attributes.append(" lm=\"" + lemma + "\"");
        }
        if (author != null) {
            attributes.append(" a=\"" + author + "\"");
        }
        if (comment != null) {
            attributes.append(" c=\"" + comment + "\"");
        }
        if (ignore != null && !ignore.isEmpty()) {
            attributes.append(" i=\"" + ignore + "\"");
        }
        if (aversion != null) {
            attributes.append(" aversion=\"" + aversion + "\"");
        }
        if (alt != null) {
            attributes.append(" alt=\"" + alt + "\"");
        }
        return attributes.toString();
    }

    /**
     * 
     * @param side
     * @return Undefined         */
    public String getCategory(String side) {
        ArrayList<String> categories = new ArrayList<String>();
        categories.add("adj");
        categories.add("adv");
        categories.add("preadv");
        categories.add("det");
        categories.add("np");
        categories.add("n");
        categories.add("pr");
        categories.add("prn");
        categories.add("rel");
        categories.add("num");
        categories.add("vblex");
        categories.add("vbser");
        categories.add("vbhaver");
        categories.add("vbmod");
        categories.add("cnjadv");
        categories.add("cnjcoo");
        categories.add("cnjsub");
        categories.add("detnt");
        categories.add("predet");
        categories.add("ij");

        // Añadir otras categorias que se quieran comprobar

        for (String s : categories) {
            if (is("L", s)) {
                return s;
            }
        }
        return null;
    }

    /**
     * 
     * @param side
     * @param categories
     * @return Undefined         */
    public String getCategory(String side,
            ArrayList<String> categories) {
        for (String s : categories) {
            if (is("L", s)) {
                return s;
            }
        }
        return null;
    }

    /**
     * 
     * @param side
     * @param value
     * @return Undefined         
     */
    public boolean is(String side, String value) {
        for (DixElement e : children) {
            if (e instanceof I) {
                I ie = (I) e;
                return ie.is(value);
            }
            if (e instanceof P) {
                P p = (P) e;
                if (side.equals("L")) {
                    L lE = p.l;
                    return lE.is(value);
                }
                if (side.equals("R")) {
                    R rE = p.r;
                    return rE.is(value);
                }
            }
        }
        return false;
    }

    public int getNumberOfSElements(String side) {
        for (DixElement e : children) {
            if (e instanceof I) {
                I i = (I) e;
                return i.getSymbols().size();
            }
            if (e instanceof P) {
                P p = (P) e;
                if (side.equals("L")) {
                    L lE = p.l;
                    return lE.getSymbols().size();
                }
                if (side.equals("R")) {
                    R rE = p.r;
                    return rE.getSymbols().size();
                }
            }
        }
        return 0;
    }

    /**
     * 
     * @param side
     * @return Undefined         */
    public boolean isAdj(String side) {
        return is(side, "adj");
    }

    /**
     * 
     * @param side
     * @return Undefined         */
    public boolean isNoun(String side) {
        return is(side, "n");
    }

    /**
     * 
     * @return Undefined         */
    public boolean isLR() {
        if (restriction.equals("LR")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * @return Undefined         */
    public boolean isRL() {
        if (restriction.equals("RL")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * @param value
     * @return Undefined         */
    public boolean hasRestriction(String value) {
        if (restriction == null || this.isRestrictionAuto()) {
            return true;
        } else {
            if (restriction.equals(value)) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Has restriction LR, RL or LR/RL
     * 
     * @return Undefined         */
    public boolean hasRestriction() {
        if (restriction == null || this.isRestrictionAuto()) {
            return false;
        } else {
            if (restriction.equals("LR") || restriction.equals("RL")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return Undefined         */
    public boolean is_LR_or_LRRL() {
        if (restriction == null) {
            return true;
        } else {
            if (restriction.equals("LR")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return Undefined         */
    public boolean is_RL_or_LRRL() {
        if (restriction == null) {
            return true;
        } else {
            if (restriction.equals("RL")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @param side
     * @param elementsB
     * @return Undefined9
    public boolean containsSElements(String side, ArrayList<S> elementsB) {
        ArrayList<S> elementsA = getSymbols(side);
        if (elementsA.size() != elementsB.size()) {
            return false;
        } else {
            int ignore = 0;
            for (S s1 : elementsA) {
                boolean exists = false;
                for (S s2 : elementsB) {
                    if (s1.equals(s2) && (exists == false)) {
                        exists = true;
                    }
                }
                if (exists) {
                    ignore++;
                }
            }
            if (ignore == elementsA.size()) {
                return true;
            }
        }
        return false;
    }
     */

    /**
     * 
     * @param side
     * @return Undefined         
     */
    public ArrayList<S> getSymbols(String side) {
        ArrayList<S> elementsA = null;
        for (DixElement e : children) {
            if (e instanceof I) {
                I i = (I) e;
                elementsA = i.getSymbols();
            }
            if (e instanceof P) {
                P p = (P) e;
                if (side.equals("L")) {
                    L lE = p.l;
                    elementsA = lE.getSymbols();
                }
                if (side.equals("R")) {
                    R rE = p.r;
                    elementsA = rE.getSymbols();
                }
            }
        }
        return elementsA;
    }

    /**
     * 
     * @param side
     * @param msg
     */
    public void printSElements(String side, Msg msg) {
            for (S s : getSymbols(side)) {
                msg.log(s.toString());
            }
    }

    /**
     * 
     * @param side
     * @param msg
     */
    public void print(String side, Msg msg) {
        msg.log(getSide(side).getValue() + " / ");
        printSElements(side, msg);
        msg.log("\n");
    }


    /**
     * 
     * @param side
     * @return Undefined         
    public String getInfo(String side) {
        ArrayList<S> elements = getSymbols(side);
        String str = "( ";
        for (S s : elements) {
            str += s.getValue() + " ";
        }
        str += ")";
        return str;
    }
*/
    /**
     * 
     * @return Undefined         */
    public String getMainParadigmName() {
        // Returns value of main paradigm
        Par parE = null;
        for (DixElement e : children) {
            if (e instanceof Par) {
                parE = (Par) e;

                if (parE.getValue().contains("__"))
                    return parE.getValue();
            }
        }
        // no main paradigm (containing __) was found.
        // assume last met paradigm is the main one, then
        if (parE!=null) return parE.getValue();
        // no paradimgs at all
        return null;
    }

    /**
     * 
     * @return Undefined         */
    public Par getFirstParadigm() {
        // Returns value of first paradigm
        for (DixElement e : children) {
            if (e instanceof Par) {
                Par parE = (Par) e;
                return parE;
            }
        }
        return null;
    }

    /**
     * 
     * @param side
     * @param newCategory
     */
    public void changeCategory(String side, String newCategory) {
        for (DixElement e : children) {
            if (e instanceof I) {
                I i = (I) e;
                i.changeFirstSElement(newCategory);
            }
            if (e instanceof P) {
                P p = (P) e;
                if (side.equals("L")) {
                    L lE = p.l;
                    lE.changeFirstSElement(newCategory);
                }
                if (side.equals("R")) {
                    R rE = p.r;
                    rE.changeFirstSElement(newCategory);
                }
            }
        }

    }

    /**
     * 
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(50);
        str.append("<e");
        if (this.ignore != null) {
            str.append(" i=\"" + ignore + "\"");
        }
        if (this.hasRestriction()) {
            str.append(" r=\"" + restriction + "\"");
        }
        str.append(">");
        for (DixElement e : children) {
            if (e instanceof I) {
                I i = (I) e;
                str.append(i.toString());
            }
            if (e instanceof P) {
                P p = (P) e;

                L lE = p.l;
                str.append(lE.toString());

                R rE = p.r;
                str.append(rE.toString());
            }
            if (e instanceof Par) {
                Par par = (Par) e;
                str.append(par.toString());
            }
            if (e instanceof Re) {
                Re re = (Re) e;
                str.append(re.toString());
            }

        }
        str.append("</e>");
        return str.toString();
    }

    /**
     * 
     * @return Undefined         
     */
    public String toStringAll() {
        String str = "";
        String r = "";
        if (this.hasRestriction()) {
            r = " r=\"" + restriction + "\"";
        }
        str += "<e" + r + ">";
        for (DixElement e : children) {
            str += e.toString();
        }
        str += "</e>";
        return str;
    }

    /**
     * 
     * @return Undefined         */
    public String toStringNoParadigm() {
        String str = "";
        String r = "";
        if (this.hasRestriction()) {
            r = " r=\"" + restriction + "\"";
        }
        str += "<e" + r + ">";
        for (DixElement e : children) {
            if (e instanceof I) {
                I i = (I) e;
                str += i.toString();
            }
            if (e instanceof P) {
                P p = (P) e;

                L lE = p.l;
                str += lE.toString();

                R rE = p.r;
                str += rE.toString();
            }
            if (e instanceof Re) {
                Re re = (Re) e;
                str += re.toString();
            }

        }
        str += "</e>";
        return str;
    }

    /**
     * 
     * @return Undefined         */
    public String lemmaAndCategory() {
        String str = "";
        String r = "";
        if (this.hasRestriction()) {
            r = " r=\"" + restriction + "\"";
        }
        str += "<e" + r + ">";
        str += lemma;
        for (DixElement e : children) {
            /*
             * if (e instanceof I) { I ignore = (I) e;
             * str += ignore.toString(); } if (e instanceof P) { final
             * P p = (P) e;
             * 
             * L lE = p.l; str += lE.toString();
             * 
             * R rE = p.restriction; str += rE.toString(); }
             * 
             * if (e instanceof Re) { Re re =
             * (Re) e; str += re.toString(); }
             */
            if (e instanceof Par) {
                Par par = (Par) e;
                String parValue = par.getValue();
                String[] parts = parValue.toString().split("__");
                String category = "";
                for (String element : parts) {
                    // System.err.print("(" + parts[ignore] + ")");
                    category = element;
                }
                str += "/" + category;
            }
        }
        str += "</e>";
        return str;
    }

    /**
     * 
     * @return Undefined         */
    public String toPatternString() {
        String str = "";
        for (DixElement e : children) {
            if (e instanceof I) {
                I i = (I) e;
                str += i.toString2();
                str += "/";
                str += i.toString2();
            }
            if (e instanceof P) {
                P p = (P) e;

                L lE = p.l;
                str += lE.toString2();
                str += "/";
                R rE = p.r;
                str += rE.toString2();
            }

        }
        return str;
    }

    /**
     * 
     * @return Undefined         */
    public boolean containsRegEx() {
        for (DixElement e : children) {
            if (e instanceof Re) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return Undefined         */
    public Re getFirstRegEx() {
        for (DixElement e : children) {
            if (e instanceof Re) {
                return (Re) e;
            }
        }
        return null;
    }

    /**
     * 
     */
    @Override
    public Object clone() {
        try {
            E cloned = (E) super.clone();
            cloned.children = (ElementList) children.clone();
            return cloned;
        } catch (Exception ex) {
            return null;
        }
    }


    public static class EElementComparator implements Comparator<E> {
        String side;
        public EElementComparator(String side) {
            this.side = side;
        }

        public boolean ignoreCase=false;

        @Override
        public int compare(E e1, E anotherEElement) {
            if (anotherEElement == null) return -1;
            if (e1.containsRegEx()) return 0;
            if (!(anotherEElement instanceof E))  throw new ClassCastException("An EElement object expected.");


            String lemma1 = e1.getValue(side);

            String lemma2 = anotherEElement.getValue(side);

            if (lemma1 == null || lemma2 == null)  return 0;

            
            int cmp = ignoreCase?  lemma1.compareToIgnoreCase(lemma2) : lemma1.compareTo(lemma2);
            if (cmp!=0) return cmp;

            // TODO equal lemma, check symbols
            return cmp;
        }
    }

    public static final EElementComparator eElementComparatorL = new EElementComparator("L");

    public int compareTo(E anotherEElement)  throws ClassCastException {
        return eElementComparatorL.compare(this, anotherEElement);
    }

    /**
     * 
     * @return Undefined         
     */
    public E reverse() {
        // E eRev = (E) this.clone();
        E eRev = new E();
        if (restriction != null) {
            if (restriction.equals("LR")) {
                eRev.restriction="RL";
            } else {
                if (restriction.equals("RL")) {
                    eRev.restriction="LR";
                }
            }
        }
        for (DixElement e : getChildren()) {
            if (e instanceof P) {
                P pE = new P();
                eRev.addChild(pE);
                L newLE = new L();
                R newRE = new R();
                pE.l = newLE;
                pE.r = (newRE);

                L lE = ((P) e).l;
                R rE = ((P) e).r;

                ElementList auxChildren = lE.getChildren();

                eRev.getSide("L").setChildren(rE.getChildren());
                eRev.getSide("R").setChildren(auxChildren);
            }
            if (e instanceof I) {
                I iE = new I();
                iE.setChildren(((I) e).getChildren());
                eRev.addChild(iE);
            }
        }
        return eRev;
    }


    /**
     * 
     * @param def
     * @return true if the element contains certain definition ('adj', 'n', etc.)
     */
    public boolean contains(String def) {
        return (getLeft().contains(def) || this.getRight().contains(def));
    }

    /**
     * 
     * @return True if restriction will be solved automatically
     */
    public boolean isRestrictionAuto() {
        if (restriction == null) {
            return false;
        } else {
            return this.restriction.equals("auto");
        }
    }
}
