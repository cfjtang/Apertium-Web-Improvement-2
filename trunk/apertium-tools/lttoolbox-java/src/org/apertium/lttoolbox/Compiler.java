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
import java.util.*;

public class Compiler extends XMLApp {

  public static String COMPILER_DICTIONARY_ELEM = "dictionary";

  public static String COMPILER_ALPHABET_ELEM = "alphabet";

  public static String COMPILER_SDEFS_ELEM = "sdefs";

  public static String COMPILER_SDEF_ELEM = "sdef";

  public static String COMPILER_N_ATTR = "n";

  public static String COMPILER_PARDEFS_ELEM = "pardefs";

  public static String COMPILER_PARDEF_ELEM = "pardef";

  public static String COMPILER_PAR_ELEM = "par";

  public static String COMPILER_ENTRY_ELEM = "e";

  public static String COMPILER_RESTRICTION_ATTR = "r";

  public static String COMPILER_RESTRICTION_LR_VAL = "LR";

  public static String COMPILER_RESTRICTION_RL_VAL = "RL";

  public static String COMPILER_PAIR_ELEM = "p";

  public static String COMPILER_LEFT_ELEM = "l";

  public static String COMPILER_RIGHT_ELEM = "r";

  public static String COMPILER_S_ELEM = "s";

  public static String COMPILER_REGEXP_ELEM = "re";

  public static String COMPILER_SECTION_ELEM = "section";

  public static String COMPILER_ID_ATTR = "id";

  public static String COMPILER_TYPE_ATTR = "type";

  public static String COMPILER_IDENTITY_ELEM = "i";

  public static String COMPILER_JOIN_ELEM = "j";

  public static String COMPILER_BLANK_ELEM = "b";

  public static String COMPILER_POSTGENERATOR_ELEM = "a";

  public static String COMPILER_GROUP_ELEM = "g";

  public static String COMPILER_LEMMA_ATTR = "lm";

  public static String COMPILER_IGNORE_ATTR = "i";

  public static String COMPILER_IGNORE_YES_VAL = "yes";

  /**
   * The paradigm being compiled
   */
  String current_paradigm;

  /**
   * The dictionary section being compiled
   */
  String current_section;

  /**
   * The direction of the compilation, 'lr' (left-to-right) or 'rl'
   * (right-to-left)
   */
  String direction;

  /**
   * List of characters to be considered alphabetic
   */
  String letters;

  /**
   * Identifier of all the symbols during the compilation
   */
  Alphabet alphabet = new Alphabet();

  /**
   * List of named transducers-paradigms
   */
  Map<String, Transducer> paradigms = new HashMap<String, Transducer>();

  /**
   * List of named dictionary sections
   */
  Map<String, Transducer> sections;

  /**
   * List of named prefix copy of a paradigm
   */
  Map<String, Map<String, Integer>> prefix_paradigms;

  /**
   * List of named suffix copy of a paradigm
   */
  Map<String, Map<String, Integer>> suffix_paradigms;

  /**
   * List of named endings of a suffix copy of a paradgim
   */
  Map<String, Map<String, Integer>> postsuffix_paradigms;

  /**
   * Mapping of aliases of characters specified in ACX files
   */
  Map<Integer, Set<Integer>> acx_map;

  /**
   * Original char being mapped
   */
  int acx_current_char;

  public Compiler() {
    // LtLocale.tryToSetLocale();
  }

  void parseACX(String fichero, String dir) throws IOException, SAXException {
    if (dir.equals(COMPILER_RESTRICTION_LR_VAL)) {
      reader = xmlReaderForFile(fichero);
      if (reader == null) {
        throw new RuntimeException("Error: cannot open '" + fichero + "'.");
      }
      Node n = reader.nextNode();
      while (n != null) {
        procNodeACX();
        n = reader.nextNode();
      }
    }
  }

  public void parse(String fichero, String dir) throws IOException, SAXException {
    direction = dir;
    reader = xmlReaderForFile(fichero);
    if (reader == null) {
      throw new RuntimeException("Error: Cannot open '" + fichero + "'.");
    }

    Node n = reader.nextNode();
    while (n != null) {
      procNode();
      n = reader.nextNode();
    }

    // Minimize transducers
    for (Transducer transducer : sections.values()) {
      transducer.minimize(0);
    }
  }

  void procAlphabet() {
    Node n = reader.getCurrentNode();
    if (n != null) {
      n = reader.nextNode();
      if (n != null) {
        letters = n.getNodeValue();
      } else {
        throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
                "): Missing alphabet symbols.");

      }
    }
  }

  void procSDef() {
    alphabet.includeSymbol("<" + attrib(COMPILER_N_ATTR) + ">");
  }

  void procParDef() {
    Node n = reader.getCurrentNode();
    if (n != null) {
      current_paradigm = attrib(COMPILER_N_ATTR);
      System.err.println("current_paradigm = " + current_paradigm);
    } else {
      final Transducer transducer = paradigms.get(current_paradigm);
      if (transducer != null) {
        transducer.minimize(0);
        transducer.joinFinals(0);
        current_paradigm = "";
      }
    }
  }

  int matchTransduction(List<Integer> pi, List<Integer> pd, int estado, Transducer t) {

    // indexes in the list
    int izqda, dcha, limizqda, limdcha;

    if (direction.equals(COMPILER_RESTRICTION_LR_VAL)) {
      izqda = 0;
      dcha = 0;
      limizqda = pi.size();
      limdcha = pd.size();
    } else {
      izqda = 0;
      dcha = 0;
      limizqda = pd.size();
      limdcha = pi.size();
    }

    if (pi.size() == 0 && pd.size() == 0) {

      estado = t.insertNewSingleTransduction(Alphabet.A.cast(0, 0), estado);

    } else {
      Set<Integer> acx_map_ptr = null;
      int rsymbol = 0;

      while (true) {
        int etiqueta;

        if (izqda == limizqda && dcha == limdcha) {
          break;
        } else if (izqda == limizqda) {
          etiqueta = Alphabet.A.cast(0, dcha);
          dcha++;
        } else if (dcha == limdcha) {
          etiqueta = Alphabet.A.cast(izqda, 0);
          acx_map_ptr = acx_map.get(izqda);
          rsymbol = 0;
          izqda++;
        } else {
          etiqueta = Alphabet.A.cast(izqda, dcha);
          acx_map_ptr = acx_map.get(izqda);
          rsymbol = dcha;
          izqda++;
          dcha++;
        }

        int nuevo_estado = t.insertSingleTransduction(etiqueta, estado);

        if (acx_map_ptr != null) {
          for (Integer integer : acx_map_ptr) {
            t.linkStates(estado, nuevo_estado, Alphabet.A.cast(integer, rsymbol));
          }
        }
        estado = nuevo_estado;
      }
    }
    return estado;
  }

  void requireEmptyError(String name) {
    final Node n = reader.getCurrentNode();
    if (!xmlTextReaderIsEmptyElement(n)) {
      throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
              "): Non-empty element '<" + name + ">' should be empty.");
    }
  }

  boolean allBlanks() {
    boolean flag = true;
    String text = reader.getCurrentNode().getNodeValue();
    if (text == null) return true;
    for (int i = 0, limit = text.length(); i < limit; i++) {
      flag = flag && Character.isWhitespace(text.charAt(i));
    }
    return flag;
  }

  void readString(List<Integer> result, String name) {
    final Node n = reader.getCurrentNode();
    if (name.equals("#text")) {
      String value = n.getNodeValue();
      for (int i = 0, limit = value.length(); i < limit; i++) {
        result.add((int) (value.charAt(i)));
      }
    } else if (name.equals(COMPILER_BLANK_ELEM)) {
      requireEmptyError(name);
      result.add((int) (' '));
    } else if (name.equals(COMPILER_JOIN_ELEM)) {
      requireEmptyError(name);
      result.add((int) ('+'));
    } else if (name.equals(COMPILER_POSTGENERATOR_ELEM)) {
      requireEmptyError(name);
      result.add((int) ('~'));
    } else if (name.equals(COMPILER_GROUP_ELEM)) {
      if ( n !=null)  result.add((int) ('#'));
    } else if (name.equals(COMPILER_S_ELEM)) {
      requireEmptyError(name);
      String symbol = "<" + attrib(COMPILER_N_ATTR) + ">";
      if (!alphabet.isSymbolDefined(symbol)) {
        throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader)
                + "): Undefined symbol '" + symbol + "'.");
      }
      result.add(Alphabet.A.cast(symbol));
    } else {
      throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
              "): Invalid specification of element '<" + name +
              ">' in this context.");

    }
  }

  String skipBlanksRET(String name) {
    while (name.equals("#text") || name.equals("#comment")) {
      if (!name.equals("#comment")) {
        if (!allBlanks()) {
          throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
                  "): Invalid construction.");
        }
      }
      Node n = reader.nextNode();
      name = n.getNodeName();
    }
    return name;
  }

  String skipRET(String name, String elem) {
    Node n = reader.nextNode();
    name = n.getNodeName();
    while (name.equals("#text") || name.equals("#comment")) {
      if (!name.equals("#comment")) {
        if (!allBlanks()) {
          throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
                  "): Invalid construction.");
        }
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

  EntryToken procIdentity() {
    List<Integer> both_sides = new Vector<Integer>();

    Node n = reader.getCurrentNode();
    if (!xmlTextReaderIsEmptyElement(n)) {
      String name;
      while (true) {
        n = reader.nextNode();
        name = n.getNodeName();
        if (name.equals(COMPILER_IDENTITY_ELEM)) {
          break;
        }
        readString(both_sides, name);
      }
    }

    EntryToken e = new EntryToken();
    e.setSingleTransduction(both_sides, both_sides);
    return e;
  }

  EntryToken procTransduction() {

    List<Integer> lhs = new Vector<Integer>();
    List<Integer> rhs = new Vector<Integer>();
    String name = "";

    name = skipRET(name, COMPILER_LEFT_ELEM);

    Node n = reader.getCurrentNode();
    if (!xmlTextReaderIsEmptyElement(n)) {
      name = "";
      while (true) {
        n = reader.nextNode();
        name = n.getNodeName();
        if (name.equals(COMPILER_LEFT_ELEM)) {
          break;
        }
        readString(lhs, name);
      }
    }

    name = skipRET(name, COMPILER_RIGHT_ELEM);

    if (!xmlTextReaderIsEmptyElement(n)) {
      while (true) {
        n = reader.nextNode();
        name = n.getNodeName();
        if (name.equals(COMPILER_RIGHT_ELEM)) {
          break;
        }
        readString(rhs, name);
      }
    }

    name = skipRET(name, COMPILER_PAIR_ELEM);

    EntryToken e = new EntryToken();
    e.setSingleTransduction(lhs, rhs);
    return e;
  }

  EntryToken procPar() {
    EntryToken e = new EntryToken();
    String nomparadigma = attrib(COMPILER_N_ATTR);
    if (!paradigms.containsKey(nomparadigma)) {
      throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
              "): Undefined paradigm '" + nomparadigma + "'.");
    }
    e.setParadigm(nomparadigma);
    return e;
  }

  void insertEntryTokens(Vector<EntryToken> elements) {
    if (!current_paradigm.equals("")) {
      // compilation of paradigms
      Transducer t = paradigms.get(current_paradigm);
      int e = t.getInitial();

      for (int i = 0, limit = elements.size(); i < limit; i++) {
        if (elements.get(i).isParadigm()) {
          e = t.insertTransducer(e, paradigms.get(elements.get(i).paradigmName()), 0);
        } else if (elements.get(i).isSingleTransduction()) {
          e = matchTransduction(elements.get(i).left(),
                  elements.get(i).right(), e, t);
        } else if (elements.get(i).isRegexp()) {
          RegexpCompiler analyzer = new RegexpCompiler();
          analyzer.initialize(alphabet);
          analyzer.compile(elements.get(i).regExp());
          e = t.insertTransducer(e, analyzer.getTransducer(), Alphabet.A.cast(0, 0));
        } else {
          throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
                  "): Invalid entry token.");
        }
      }
      t.setFinal(e);
    } else {
      // compilaci�n de dictionary

      Transducer t = sections.get(current_section);
      int e = t.getInitial();

      for (int i = 0, limit = elements.size(); i < limit; i++) {
        if (elements.get(i).isParadigm()) {
          final String paradigmName = elements.get(i).paradigmName();
          if (i == elements.size() - 1) {
            // paradigma sufijo
            if (suffix_paradigms.get(current_section).containsKey(paradigmName)) {
              t.linkStates(e, suffix_paradigms.get(current_section).get(paradigmName), 0);
              e = postsuffix_paradigms.get(current_section).get(paradigmName);
            } else {
              e = t.insertNewSingleTransduction(Alphabet.A.cast(0, 0), e);
              suffix_paradigms.get(current_section).put(paradigmName, e);
              e = t.insertTransducer(e, paradigms.get(paradigmName), 0);
              postsuffix_paradigms.get(current_section).put(paradigmName, e);
            }
          } else if (i == 0) {
            // paradigma prefijo
            if (prefix_paradigms.get(current_section).containsKey(paradigmName)) {
              e = prefix_paradigms.get(current_section).get(paradigmName);
            } else {
              e = t.insertTransducer(e, paradigms.get(paradigmName), 0);
              prefix_paradigms.get(current_section).put(paradigmName, e);
            }
          } else {
            // paradigma intermedio
            e = t.insertTransducer(e, paradigms.get(paradigmName), 0);
          }
        } else if (elements.get(i).isRegexp()) {
          RegexpCompiler analyzer = new RegexpCompiler();
          analyzer.initialize(alphabet);
          analyzer.compile(elements.get(i).regExp());
          e = t.insertTransducer(e, analyzer.getTransducer(), Alphabet.A.cast(0, 0));
        } else {
          e = matchTransduction(elements.get(i).left(), elements.get(i).right(), e, t);
        }
      }
      t.setFinal(e);
    }
  }

  void requireAttribute(String value, String attrname, String elemname) {
    if (value.equals("")) {
      throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
              "): '<" + elemname +
              "' element must specify non-void '" +
              attrname + "' attribute.");
    }
  }

  void procSection() {
    // is n ever null??
   Node n = reader.getCurrentNode();
     if (n != null) {
       // or should we be checking is the there is a nextSibling or childnode??
      String id = attrib(COMPILER_ID_ATTR);
      String type = attrib(COMPILER_TYPE_ATTR);
      requireAttribute(id, COMPILER_ID_ATTR, COMPILER_SECTION_ELEM);
      requireAttribute(type, COMPILER_TYPE_ATTR, COMPILER_SECTION_ELEM);

      current_section = id + "@" + type;
    } else {
      current_section = "";
    }
  }

  void procEntry() {

    Node n;

    String atributo = this.attrib(COMPILER_RESTRICTION_ATTR);
    String ignore = this.attrib(COMPILER_IGNORE_ATTR);

    //�if entry is masked by a restriction of direction or an ignore mark
    if ((!atributo.equals("") && !atributo.equals(direction)) || ignore.equals(COMPILER_IGNORE_YES_VAL)) {
      // parse to the end of the entry
      String name = "";

      while (!name.equals(COMPILER_ENTRY_ELEM)) {
        n = reader.nextNode();
        name = n.getNodeName();
      }

      return;
    }

    Vector<EntryToken> elements = new Vector<EntryToken>();

    while (true) {
      n = reader.nextNode();
      if (n == null) {
        throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
                "): Parse error.");
      }
      String name = n.getNodeName();
      name = skipBlanksRET(name);

      if (name.equals(COMPILER_PAIR_ELEM)) {
        elements.add(procTransduction());
      } else if (name.equals(COMPILER_IDENTITY_ELEM)) {
        elements.add(procIdentity());
      } else if (name.equals(COMPILER_REGEXP_ELEM)) {
        elements.add(procRegexp());
      } else if (name.equals(COMPILER_PAR_ELEM)) {
        elements.add(procPar());

        // detecci�n del uso de paradigmas no definidos

        String p = elements.lastElement().paradigmName();

        if (!paradigms.containsKey(p)) {
          throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
                  "): Undefined paradigm '" + p + "'.");

        }
        // descartar entradas con paradigms vac�os (por las direciones,
        // normalmente
        if (paradigms.get(p).isEmpty()) {
          while (!name.equals(COMPILER_ENTRY_ELEM)) {
            n = reader.nextNode();
            name = n.getNodeName();
          }
          return;
        }
      } else if (name.equals(COMPILER_ENTRY_ELEM) && n.getNextSibling() == null) {
        // insertar elements into letter transducer
        insertEntryTokens(elements);
        return;
      } else if (name.equals("#text") && allBlanks()) {
        // just some blank spaces?
      } else {
        throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
                "): Invalid inclusion of '<" + name + ">' into '<" + COMPILER_ENTRY_ELEM +
                ">'.");
      }
    }
  }

  void procNodeACX() {
    Node n = reader.getCurrentNode();
    String nombre = (n.getNodeName());
    if (nombre.equals("#text")) {
      /* ignore */
    } else if (nombre.equals("analysis-chars")) {
      /* ignore */
    } else if (nombre.equals("char")) {
      acx_current_char = (int) (attrib("value").charAt(0));
    } else if (nombre.equals("equiv-char")) {
      acx_map.get(acx_current_char).add((int) (attrib("value").charAt(0)));
    } else if (nombre.equals("#comment")) {
      /* ignore */
    } else {
      throw new RuntimeException("Error in ACX file (" + xmlTextReaderGetParserLineNumber(reader) +
              "): Invalid node '<" + nombre + ">'.");
    }
  }

  void procNode() {

    Node n = reader.getCurrentNode();
    String nombre = n.getNodeName();

    System.err.println("procNode(n = " + nombre);

    // HACER: optimizar el orden de ejecuci�n de esta ristra de "ifs"

    if (nombre.equals("#text")) {
      assert n.getNodeType() == Node.TEXT_NODE;
      /* ignorar */
    } else if (nombre.equals(COMPILER_DICTIONARY_ELEM)) {
      /* ignorar */
    } else if (nombre.equals(COMPILER_ALPHABET_ELEM)) {
      procAlphabet();
    } else if (nombre.equals(COMPILER_SDEFS_ELEM)) {
      /* ignorar */
    } else if (nombre.equals(COMPILER_SDEF_ELEM)) {
      procSDef();
    } else if (nombre.equals(COMPILER_PARDEFS_ELEM)) {
      /* ignorar */
    } else if (nombre.equals(COMPILER_PARDEF_ELEM)) {
      procParDef();
    } else if (nombre.equals(COMPILER_ENTRY_ELEM)) {
      procEntry();
    } else if (nombre.equals(COMPILER_SECTION_ELEM)) {
      procSection();
    } else if (nombre.equals("#comment")) {
      /* ignorar */
    } else {
      throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
              "): Invalid node '<" + nombre + ">'.");
    }
  }

  EntryToken procRegexp() {
    EntryToken et = new EntryToken();
    Node n = reader.nextNode();
    String re = n.getNodeValue();
    et.setRegexp(re);
    n = reader.nextNode();
    return et;
  }

  public void write(Writer output) throws IOException {
    // letters
    output.write(letters);

    // symbols
    alphabet.write(output);

    // transducers
    output.write(sections.size());

    for (String first : sections.keySet()) {
      final Transducer second = sections.get(first);
      output.write(first + " " + second.size() + " " + second.numberOfTransitions());
      output.write(first);
      output.write(second.toString());
    }
  }

}