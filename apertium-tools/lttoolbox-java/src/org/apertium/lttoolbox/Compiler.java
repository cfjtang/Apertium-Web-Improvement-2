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
import org.w3c.dom.traversal.TreeWalker;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.OutputStreamWriter;
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
   * The libxml2's XML reader
   */
  TreeWalker reader;

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
  Map<String, Transducer> paradigms = new HashMap<String,Transducer>();

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

  // todo
  private static final int XML_READER_TYPE_END_ELEMENT = -1;

  Compiler() {
    // LtLocale.tryToSetLocale();
  }

  void parseACX(String fichero, String dir) throws IOException, SAXException {
    if (dir.equals(COMPILER_RESTRICTION_LR_VAL)) {
      reader = xmlReaderForFile(fichero, null, 0);
      if (reader == null) {
        throw new RuntimeException("Error: cannot open '" + fichero + "'.");
      }
     Node n = reader.getRoot().getFirstChild();
      while (n != null) {
        procNodeACX(n);
        n = n.getFirstChild();
      }
    }
  }

  void parse(String fichero, String dir) throws IOException, SAXException {
    direction = dir;
    reader = xmlReaderForFile(fichero, null, 0);
    if (reader == null) {
      throw new RuntimeException("Error: Cannot open '" + fichero + "'.");
    }

     org.w3c.dom.Node n = null;
       while ((n=reader.nextNode())!=null){

         procNode(n);
       }
   
    // Minimize transducers
    for (Transducer transducer : sections.values()) {

      transducer.minimize(0);
    }
  }

  void procAlphabet(Node n) {
    int tipo = xmlTextReaderNodeType(n);

    if (tipo != XML_READER_TYPE_END_ELEMENT) {
      n = n.getFirstChild();
      if (n != null) {
        letters = (n.getNodeValue());
      } else {
        throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
                "): Missing alphabet symbols.");

      }
    }
  }

  void  procSDef(Node n) {
    alphabet.includeSymbol("<" + attrib(n, COMPILER_N_ATTR) + ">");
  }

  void procParDef(Node n) {
    int tipo = xmlTextReaderNodeType(n);
    if (tipo != XML_READER_TYPE_END_ELEMENT) {
      current_paradigm =  attrib(n, COMPILER_N_ATTR);
    } else {
      final Transducer transducer = paradigms.get(current_paradigm);
      if (transducer != null) {
        transducer.minimize(0);
        transducer.joinFinals(0);
        current_paradigm = "";
      }
    }
  }

  int matchTransduction(List<Integer> pi,
                        List<Integer> pd,
                        int estado, Transducer t) {

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


  void requireEmptyError(Node n, String name) {
    if (!xmlTextReaderIsEmptyElement(n)) {
      throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
              "): Non-empty element '<" + name + ">' should be empty.");

    }
  }

  boolean   allBlanks(Node n) {
    boolean flag = true;
    String text = (n.getNodeValue());

    for (int i = 0, limit = text.length(); i < limit; i++) {
      flag = flag && Character.isSpace(text.charAt(i));
    }

    return flag;
  }

  void readString(Node n, List<Integer> result, String name) {
    if (name.equals("#text")) {
      String value = (n.getNodeValue());
      for (int i = 0, limit = value.length(); i < limit; i++) {
        result.add((int) (value.charAt(i)));
      }
    } else if (name.equals(COMPILER_BLANK_ELEM)) {
      requireEmptyError(n, name);
      result.add((int) (' '));
    } else if (name.equals(COMPILER_JOIN_ELEM)) {
      requireEmptyError(n, name);
      result.add((int) ('+'));
    } else if (name.equals(COMPILER_POSTGENERATOR_ELEM)) {
      requireEmptyError(n, name);
      result.add((int) ('~'));
    } else if (name.equals(COMPILER_GROUP_ELEM)) {
      int tipo = xmlTextReaderNodeType(n);
      if (tipo != XML_READER_TYPE_END_ELEMENT) {
        result.add((int) ('#'));
      }
    } else if (name.equals(COMPILER_S_ELEM)) {
      requireEmptyError(n, name);
      String symbol = "<" + attrib(n, COMPILER_N_ATTR) + ">";

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

  void   skipBlanks(Node n, String name) {
    while (name.equals("#text") || name.equals("#comment")) {
      if (!name.equals("#comment")) {
        if (!allBlanks(n)) {
          throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
                  "): Invalid construction.");

        }
      }

      n = n.getFirstChild();
      name = (n.getNodeName());
    }
  }

  void skip(Node n, String name, String elem) {
    n = n.getFirstChild();
    name = (n.getNodeName());

    while (name.equals("#text") || name.equals("#comment")) {
      if (!name.equals("#comment")) {
        if (!allBlanks(n)) {
          throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
                  "): Invalid construction.");

        }
      }
      n = n.getFirstChild();
      name = (n.getNodeName());
    }

    if (!name.equals(elem)) {
      throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
              "): Expected '<" + elem + ">'.");

    }
  }

  EntryToken procIdentity(Node n) {
    List<Integer> both_sides = new Vector<Integer>();

    if (!xmlTextReaderIsEmptyElement(n)) {
      String name = "";
      while (true) {
        name = (n.getNodeName());
        if (name.equals(COMPILER_IDENTITY_ELEM)) {
          break;
        }
        readString(n, both_sides, name);
      }
    }

    EntryToken e = new EntryToken();
    e.setSingleTransduction(both_sides, both_sides);
    return e;
  }

  EntryToken procTransduction(Node n) {

    List<Integer> lhs = new Vector<Integer>();
    List<Integer> rhs = new Vector<Integer>();
    String name = "";

    skip(n, name, COMPILER_LEFT_ELEM);

    if (!xmlTextReaderIsEmptyElement(n)) {
      name = "";
      while (true) {
        n = n.getFirstChild();
        name = (n.getNodeName());
        if (name.equals(COMPILER_LEFT_ELEM)) {
          break;
        }
        readString(n, lhs, name);
      }
    }

    skip(n, name, COMPILER_RIGHT_ELEM);

    if (!xmlTextReaderIsEmptyElement(n)) {
      name = "";
      while (true) {
        n = n.getFirstChild();
        name = (n.getNodeName());
        if (name.equals(COMPILER_RIGHT_ELEM)) {
          break;
        }
        readString(n, rhs, name);
      }
    }

    skip(n,name, COMPILER_PAIR_ELEM);

    EntryToken e = new EntryToken();
    e.setSingleTransduction(lhs, rhs);
    return e;
  }

  EntryToken  procPar(Node n) {
    EntryToken e = new EntryToken();
    String nomparadigma = attrib(n, COMPILER_N_ATTR);

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

  void requireAttribute(String value, String attrname,
                   String elemname) {
    if (value.equals("")) {
      throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
              "): '<" + elemname +
              "' element must specify non-void '" +
              attrname + "' attribute.");

    }
  }

  void procSection(Node n) {
    int tipo = xmlTextReaderNodeType(n);

    if (tipo != XML_READER_TYPE_END_ELEMENT) {
      String id = attrib(n, COMPILER_ID_ATTR);
      String type = attrib(n, COMPILER_TYPE_ATTR);
      requireAttribute(id, COMPILER_ID_ATTR, COMPILER_SECTION_ELEM);
      requireAttribute(type, COMPILER_TYPE_ATTR, COMPILER_SECTION_ELEM);

      current_section = id;
      current_section += "@";
      current_section += type;
    } else {
      current_section = "";
    }
  }

  void procEntry(Node n) {
    String atributo = this.attrib(n, COMPILER_RESTRICTION_ATTR);
    String ignore = this.attrib(n, COMPILER_IGNORE_ATTR);

    //�if entry is masked by a restriction of direction or an ignore mark
    if ((!atributo.equals("") && !atributo.equals(direction)) || ignore.equals(COMPILER_IGNORE_YES_VAL)) {
      // parse to the end of the entry
      String name = "";

      while (!name.equals(COMPILER_ENTRY_ELEM)) {

        name = (n.getNodeName());
      }

      return;
    }

    Vector<EntryToken> elements = new Vector<EntryToken>();

    while (true) {
      n = n.getFirstChild();
      if (n == null) {
        throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
                "): Parse error.");

      }
      String name = (n.getNodeName());
      skipBlanks(n, name);

      int tipo = xmlTextReaderNodeType(n);
      if (name.equals(COMPILER_PAIR_ELEM)) {
        elements.add(procTransduction(n));
      } else if (name.equals(COMPILER_IDENTITY_ELEM)) {
        elements.add(procIdentity(n));
      } else if (name.equals(COMPILER_REGEXP_ELEM)) {
        elements.add(procRegexp(n));
      } else if (name.equals(COMPILER_PAR_ELEM)) {
        elements.add(procPar(n));

        // detecci�n del uso de paradigmas no definidos

        String p = elements.lastElement().paradigmName();

        if (!paradigms.containsKey(p)) {
          throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
                  "): Undefined paradigm '" + p + "'.");

        }
        // descartar entradas con paradigms vac�os (por las direciones,
        // normalmente
        if (paradigms.get(p).isEmpty()) {
          while (!name.equals(COMPILER_ENTRY_ELEM) || tipo != XML_READER_TYPE_END_ELEMENT) {
            n = n.getFirstChild();
            name = (n.getNodeName());
            tipo = xmlTextReaderNodeType(n);
          }
          return;
        }
      } else if (name.equals(COMPILER_ENTRY_ELEM) && tipo == XML_READER_TYPE_END_ELEMENT) {
        // insertar elements into letter transducer
        insertEntryTokens(elements);
        return;
      } else if (name.equals("#text") && allBlanks(n)) {
      } else {
        throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
                "): Invalid inclusion of '<" + name + ">' into '<" + COMPILER_ENTRY_ELEM +
                ">'.");

      }
    }
  }

  void procNodeACX(Node n) {
    String xnombre = n.getNodeName();
    String nombre = (xnombre);
    if (nombre.equals("#text")) {
      /* ignore */
    } else if (nombre.equals("analysis-chars")) {
      /* ignore */
    } else if (nombre.equals("char")) {
      acx_current_char = (int) (attrib(n, "value").charAt(0));
    } else if (nombre.equals("equiv-char")) {
      acx_map.get(acx_current_char).add((int) (attrib(n, "value").charAt(0)));
    } else if (nombre.equals("#comment")) {
      /* ignore */
    } else {
      throw new RuntimeException("Error in ACX file (" + xmlTextReaderGetParserLineNumber(reader) +
              "): Invalid node '<" + nombre + ">'.");

    }
  }

  void procNode(org.w3c.dom.Node n) {
    String xnombre = n.getNodeName();
    String nombre = n.getNodeName();
    // HACER: optimizar el orden de ejecuci�n de esta ristra de "ifs"

    if (nombre.equals("#text")) {
      /* ignorar */
    } else if (nombre.equals(COMPILER_DICTIONARY_ELEM)) {
      /* ignorar */
    } else if (nombre.equals(COMPILER_ALPHABET_ELEM)) {
      procAlphabet(n);
    } else if (nombre.equals(COMPILER_SDEFS_ELEM)) {
      /* ignorar */
    } else if (nombre.equals(COMPILER_SDEF_ELEM)) {
      procSDef(n);
    } else if (nombre.equals(COMPILER_PARDEFS_ELEM)) {
      /* ignorar */
    } else if (nombre.equals(COMPILER_PARDEF_ELEM)) {
      procParDef(n);
    } else if (nombre.equals(COMPILER_ENTRY_ELEM)) {
      procEntry(n);
    } else if (nombre.equals(COMPILER_SECTION_ELEM)) {
      procSection(n);
    } else if (nombre.equals("#comment")) {
      /* ignorar */
    } else {
      throw new RuntimeException("Error (" + xmlTextReaderGetParserLineNumber(reader) +
              "): Invalid node '<" + nombre + ">'.");

    }
  }

  EntryToken procRegexp(Node n) {
    EntryToken et = new EntryToken();
    n = n.getFirstChild();
    String re = (n.getNodeValue());
    et.setRegexp(re);
    n = n.getFirstChild();
    return et;
  }

  void write(OutputStreamWriter output) throws IOException {
    // letters
    output.write(letters);

    // symbols
    alphabet.write(output);

    // transducers
    output.write(sections.size());

    int conta = 0;
    for (String first : sections.keySet()) {

      final Transducer second = sections.get(first);
      conta++;
      output.write(first + " " + second.size() + " " + second.numberOfTransitions());
      output.write(first);
      output.write(second.toString());
    }
  }

}