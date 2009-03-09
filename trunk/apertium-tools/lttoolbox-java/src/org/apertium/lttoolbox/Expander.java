package org.apertium.lttoolbox;/*
 * Copyright (C) 2005 Universitat d'Alacant / Universidad de Alicante
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


import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * An expander of dictionaries
 */
public class Expander extends XMLApp {

  /**
   * The paradigm being compiled
   */
  String current_paradigm;

  /**
   * The direction of the compilation, 'lr' (left-to-right) or 'rl'
   * (right-to-left)
   */
  String direction;

  /**
   * Paradigms
   */
  Map<String, EntList> paradigm;

  Map<String, EntList> paradigm_lr;

  Map<String, EntList> paradigm_rl;

  public Expander() {

  }

  void expand(String fichero, Writer output) throws IOException, SAXException {
    reader = xmlReaderForFile(fichero);
    if (reader == null) {
      throw new RuntimeException("Error: Cannot open '" + fichero + "'.");
    }

    Node n = reader.nextNode();
    while (n != null) {
      procNode(output);
      n = reader.nextNode();
    }

  }

  void procParDef() {
    Node n = reader.getCurrentNode();
    if (n != null) {
      current_paradigm = attrib(Compiler.COMPILER_N_ATTR);
      System.err.println("current_paradigm = " + current_paradigm);
    } else {
      current_paradigm = "";
    }
  }

  void requireEmptyError(String name) {
    Node n = reader.getCurrentNode();
    if (!xmlTextReaderIsEmptyElement(n)) {
      throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
              "): Non-empty element '<" + name + ">' should be empty .");

    }
  }


  boolean allBlanks() {
    Node n = reader.getCurrentNode();
    boolean flag = true;
    String text = n.getNodeValue();
    if (text==null) return true;
    for (int i = 0, limit = text.length(); i < limit; i++) {
      flag = flag && Character.isWhitespace(text.charAt(i));
    }

    return flag;
  }


  void readString(StringBuilder result, String name) {
    Node n = reader.getCurrentNode();
    if (name.equals("#text")) {
      result.append(n.getNodeValue());
    } else if (name.equals(Compiler.COMPILER_BLANK_ELEM)) {
      requireEmptyError(name);
      result.append( ' ');
    } else if (name.equals(Compiler.COMPILER_JOIN_ELEM)) {
      requireEmptyError(name);
      result.append( '+');
    } else if (name.equals(Compiler.COMPILER_POSTGENERATOR_ELEM)) {
      requireEmptyError(name);
      result.append( '~');
    } else if (name.equals(Compiler.COMPILER_GROUP_ELEM)) {
      result.append( '#');
    } else if (name.equals(Compiler.COMPILER_S_ELEM)) {
      requireEmptyError(name);
      result.append( '<' + (attrib(Compiler.COMPILER_N_ATTR)) + '>');
    } else {
      throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
              "): Invalid specification of element '<" + name +
              ">' in this context.");

    }
  }

  String skipBlanksRET(String name) {

    Node n = reader.getCurrentNode();
    if (name.equals("#text")) {
      if (!allBlanks()) {
        throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
                "): Invalid construction:" + n.getTextContent());

      }
      n = reader.nextNode();;
      name = n.getNodeName();
    }
    return name;
  }

  String skipRET(String name, String elem) {
    Node n = reader.getCurrentNode();
    n = reader.nextNode();
    name = n.getNodeName();

    if (name.equals("#text")) {
      if (!allBlanks()) {
        throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
                "): Invalid construction.");

      }
      n = reader.nextNode();
      name = n.getNodeName();
    }

    if (!name.equals(elem)) {
      throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
              "): Expected '<" + elem + ">'.");

    }
    return name;
  }

  StringBuilder procIdentity() {

    Node n = reader.getCurrentNode();
    StringBuilder both_sides = new StringBuilder();

    if (!xmlTextReaderIsEmptyElement(n)) {
      String name = "";

      while (true) {
        n = reader.nextNode();
        name = n.getNodeName();
        if (name.equals(Compiler.COMPILER_IDENTITY_ELEM)) {
          break;
        }
        readString(both_sides, name);
      }
    }
    return both_sides;
  }
/*
   <sdef n="vblex" />
    <sdef n="adv" />
    <sdef n="guio" />
  </sdefs>

  <pardefs>
    <pardef n="MF_GD">
    <e r="RL">
      <p>
        <l><s n="GD"/></l>
        <r></r>
      </p>
    </e>
 */
  SPair procTransduction() {
    Node n = reader.getCurrentNode();

    StringBuilder lhs = new StringBuilder(), rhs = new StringBuilder();
    String name = "";

    name = skipRET(name, Compiler.COMPILER_LEFT_ELEM);
    n = reader.getCurrentNode();
        System.err.println("name1 = " + name+ "  val "+n.getNodeValue()) ;

    if (!xmlTextReaderIsEmptyElement(n)) {
      name = "";
      while (true) {
        n = reader.nextNode();
        name = n.getNodeName();

        System.err.println("name2 = " + name+ "  val "+n.getNodeValue()) ;
        if (name.equals(Compiler.COMPILER_LEFT_ELEM)) {
          break;
        }
        readString(lhs, name);
      }
    }

    name = skipRET(name, Compiler.COMPILER_RIGHT_ELEM);

    if (!xmlTextReaderIsEmptyElement(n)) {
      name = "";
      while (true) {
        n = reader.nextNode();
        name = n.getNodeName();
        if (name.equals(Compiler.COMPILER_RIGHT_ELEM)) {
          break;
        }
        readString(rhs, name);
      }
    }

    name = skipRET(name, Compiler.COMPILER_PAIR_ELEM);

    return new SPair(lhs.toString(), rhs.toString());
  }

  String procPar() {
    return attrib(Compiler.COMPILER_N_ATTR);
  }

  void requireAttribute(String value, String attrname,
                        String elemname) {
    if (value.equals("")) {
      throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) + "): '<" + elemname +
              "' element must specify non-void '" + attrname + "' attribute.");

    }
  }

  void procEntry(Writer output) throws IOException {

    Node n = reader.getCurrentNode();

    String atributo = this.attrib(Compiler.COMPILER_RESTRICTION_ATTR);
    // String entrname = this.attrib( org.apertium.lttoolbox.Compiler.COMPILER_LEMMA_ATTR);

    String myname = "";
    if (this.attrib(Compiler.COMPILER_IGNORE_ATTR).equals("yes")) {
      do {
        n = reader.nextNode();
        if (n == null) {
          throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) + "): Parse error.");
        }
        myname = n.getNodeName();
        // throw new RuntimeException("Hola " + myname + " " + org.apertium.lttoolbox.Compiler.COMPILER_ENTRY_ELEM);
      }
      while (!myname.equals(Compiler.COMPILER_ENTRY_ELEM));
      return;
    }

    EntList items = new EntList();
    EntList items_lr = new EntList();
    EntList items_rl = new EntList();
    if (atributo.equals(org.apertium.lttoolbox.Compiler.COMPILER_RESTRICTION_LR_VAL)) {
      items_lr.add(new SPair("", ""));
    } else if (atributo.equals(Compiler.COMPILER_RESTRICTION_RL_VAL)) {
      items_rl.add(new SPair("", ""));
    } else {
      items.add(new SPair("", ""));
    }

    while (true) {
      n = reader.nextNode();
      if (n == null) {
        throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
                "): Parse error.");

      }
      String name = n.getNodeName();
     name = skipBlanksRET(name);

      if (name.equals(Compiler.COMPILER_PAIR_ELEM)) {
        SPair p = procTransduction();
        items.add(p);
        items_lr.add(p);
        items_rl.add(p);
      } else if (name.equals(Compiler.COMPILER_IDENTITY_ELEM)) {
        String val = procIdentity().toString();
        items.add(new SPair(val, val));
        items_lr.add(new SPair(val, val));
        items_rl.add(new SPair(val, val));
      } else if (name.equals(Compiler.COMPILER_REGEXP_ELEM)) {
        String val = "__REGEXP__" + procRegexp(n);
        items.add(new SPair(val, val));
        items_lr.add(new SPair(val, val));
        items_rl.add(new SPair(val, val));
      } else if (name.equals(Compiler.COMPILER_PAR_ELEM)) {
        String p = procPar();
        // detecci�n del uso de paradigmas no definidos

        if (!paradigm.containsKey(p) &&
                !paradigm_lr.containsKey(p) &&
                !paradigm_rl.containsKey(p)) {
          throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader)
                  + "): Undefined paradigm '" + p + "'.");
        }

        if (atributo.equals(Compiler.COMPILER_RESTRICTION_LR_VAL)) {
          if (paradigm.get(p).size() == 0 && paradigm_lr.get(p).size() == 0) {
            name = skipRET(name, Compiler.COMPILER_ENTRY_ELEM);
            return;
          }
          EntList first = new EntList(items_lr);
          append(first, paradigm.get(p));
          append(items_lr, paradigm_lr.get(p));
          items_lr.addAll(first);
        } else if (atributo.equals(Compiler.COMPILER_RESTRICTION_RL_VAL)) {
          if (paradigm.get(p).size() == 0 && paradigm_rl.get(p).size() == 0) {
            name = skipRET(name, Compiler.COMPILER_ENTRY_ELEM);
            return;
          }

          EntList first = new EntList(items_rl);
          append(first, paradigm.get(p));
          append(items_rl, paradigm_rl.get(p));
          items_rl.addAll(first);
        } else {
          if (paradigm_lr.get(p).size() > 0) {
            items_lr.addAll(items);
          }
          if (paradigm_rl.get(p).size() > 0) {
            items_rl.addAll(items);
          }
          EntList aux_lr = new EntList(items_lr);
          EntList aux_rl = new EntList(items_rl);
          append(aux_rl, paradigm.get(p));
          append(aux_rl, paradigm.get(p));
          append(items_lr, paradigm_lr.get(p));
          append(items_rl, paradigm_rl.get(p));
          append(items, paradigm.get(p));
          items_rl.addAll(aux_rl);
          items_lr.addAll(aux_lr);
        }
      } else if (name.equals(Compiler.COMPILER_ENTRY_ELEM) && n.getNextSibling() == null) {
        if (current_paradigm.equals("")) {
          for (Pair<String, String> it : items) {
            output.write(it.first);
            output.write(':');
            output.write(it.second);
            output.write('\n');
          }
          for (Pair<String, String> it : items_lr) {
            output.write(it.first);
            output.write(':');
            output.write('>');
            output.write(':');
            output.write(it.second);
            output.write('\n');
          }
          for (Pair<String, String> it : items_rl) {
            output.write(it.first);
            output.write(':' + '<' + ':' + it.second + '\n');
          }
        } else {
          paradigm_lr.get(current_paradigm).addAll(items_lr);
          paradigm_rl.get(current_paradigm).addAll(items_rl);
          paradigm.get(current_paradigm).addAll(items);
        }

        return;
      } else if (name.equals("#text") && allBlanks()) {
      } else if (name.equals("#comment")) {
      } else {
        throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
                "): Invalid inclusion of '<" + name + ">' into '<" + Compiler.COMPILER_ENTRY_ELEM +
                ">'.");

      }
    }
  }


  void procNode(Writer output) throws IOException {

    Node n = reader.getCurrentNode();
    String nombre = n.getNodeName();

    System.err.println("procNode((n = " + nombre);

    // HACER: optimizar el orden de ejecuci�n de esta ristra de "ifs"

    if (nombre.equals("#text")) {
      /* ignorar */
    } else if (nombre.equals(Compiler.COMPILER_DICTIONARY_ELEM)) {
      /* ignorar */
    } else if (nombre.equals(Compiler.COMPILER_ALPHABET_ELEM)) {
      /* ignorar */
    } else if (nombre.equals(Compiler.COMPILER_SDEFS_ELEM)) {
      /* ignorar */
    } else if (nombre.equals(Compiler.COMPILER_SDEF_ELEM)) {
      /* ignorar */
    } else if (nombre.equals(Compiler.COMPILER_PARDEFS_ELEM)) {
      /* ignorar */
    } else if (nombre.equals(Compiler.COMPILER_PARDEF_ELEM)) {
      procParDef();
    } else if (nombre.equals(Compiler.COMPILER_ENTRY_ELEM)) {
      procEntry(output);
    } else if (nombre.equals(Compiler.COMPILER_SECTION_ELEM)) {
      /* ignorar */
    } else if (nombre.equals("#comment")) {
      /* ignorar */
    } else {
      throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) + "): Invalid node '<" + nombre + ">'.");

    }
  }

  String procRegexp(Node n) {
    n = reader.nextNode();
    String re = n.getNodeValue();
    n = reader.nextNode();
    return re;
  }

  EntList append(EntList result, EntList endings) {

    EntList temp = new EntList();

    for (SPair it : result) {
      for (SPair it2 : endings) {
        temp.add(new SPair(it.first + it2.first, it.second + it2.second));
      }
    }

    return temp;
  }

  void append(EntList result, String endings) {
    for (Pair<String, String> it : result) {
      it.first += endings;
      it.second += endings;
    }
  }

  void append(EntList result, Pair<String, String> endings) {
    for (Pair<String, String> it : result) {
      it.first += endings.first;
      it.second += endings.second;
    }
  }

}