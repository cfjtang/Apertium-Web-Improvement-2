package org.apertium.lttoolbox;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Nic Cottrell, Jan 27, 2009 4:14:40 PM
 */

/**
 * Alphabet class.
 * Encodes pairs of symbols into an integer.
 */
public class Alphabet {

  public static final Alphabet A = new Alphabet();

  /**
   * Symbol-identifier relationship.
   */
  Map<String, Integer> slexic = new HashMap<String,Integer>();

  /**
   * Identifier-symbol relationship.
   */
  List<String> slexicinv=new Vector<String>();

  Map<Pair<Integer, Integer>, Integer> spair;

  Vector<Pair<Integer, Integer>> spairinv;

  public Alphabet() {
    spair = new HashMap<Pair<Integer, Integer>, Integer>();
    spairinv = new Vector<Pair<Integer, Integer>>();
  }

  void copy(Alphabet a) {
    slexic = a.slexic;
    slexicinv = a.slexicinv;
    spair = a.spair;
    spairinv = a.spairinv;
  }

  void includeSymbol(String s) {
    if (!slexic.containsKey(s)) {
      int slexic_size = slexic.size();
      slexic.put(s, -(slexic_size + 1));
      slexicinv.add(s);
    }
  }

  int cast(int c1, int c2) {
    Pair<Integer, Integer> tmp = new Pair<Integer, Integer>(c1, c2);
    if (!spair.containsKey(tmp)) {
      int spair_size = spair.size();
      spair.put(tmp, spair_size);
      spairinv.add(tmp);
    }

    return spair.get(tmp);
  }

  int cast(String s) {
    return slexic.get(s);
  }

  boolean isSymbolDefined(String s) {
    return slexic.containsKey(s);
  }

  int size() {
    return slexic.size();
  }

  void write(Writer output) throws IOException {
    // First, we write the taglist
   Compression.multibyte_write(slexicinv.size(), output);  // taglist size
    for (int i = 0, limit = slexicinv.size(); i < limit; i++) {
      Compression.String_write(slexicinv.get(i).substring(1, slexicinv.get(i).length() - 2), output);
    }

    // Then we write the list of pairs
    // All numbers are biased + slexicinv.size() to be positive or zero
    int bias = slexicinv.size();
    output.write(spairinv.size());
    for (int i = 0, limit = spairinv.size(); i != limit; i++) {
      Compression.multibyte_write(spairinv.get(i).first + bias, output);
      Compression.multibyte_write(spairinv.get(i).second + bias, output);
    }
  }

  public Alphabet read(FileInputStream input) throws IOException {
    Alphabet a_new = new Alphabet();
    a_new.spairinv.clear();
    a_new.spair.clear();

    // Reading of taglist
    int tam = Compression.multibyte_read(input);
    while (tam > 0) {
      tam--;
      String mytag = "<" + (char)  Compression.multibyte_read(input) + ">";
      a_new.slexicinv.add(mytag);
      a_new.slexic.put(mytag, -a_new.slexicinv.size());
    }

    // Reading of pairlist
    int bias = a_new.slexicinv.size();
    tam =  Compression.multibyte_read(input);
    while (tam > 0) {
      tam--;
      int first =  Compression.multibyte_read(input);
      int second =  Compression.multibyte_read(input);
      Pair<Integer, Integer> tmp2 = new Pair<Integer, Integer>(first - bias, second - bias);
      int spair_size = a_new.spair.size();
      a_new.spair.put(tmp2, spair_size);
      a_new.spairinv.add(tmp2);
    }

    return a_new;
  }

  void writeSymbol(int symbol, Writer output) throws IOException {
    if (symbol < 0) {
      output.write(slexicinv.get(-symbol - 1));
    } else {
      output.write((char) (symbol));
    }
  }

  void getSymbol(String result, int symbol) {
    getSymbol(result, symbol, false);
  }

  void getSymbol(String result, int symbol, boolean uppercase) {
    if (symbol == 0) {
      return;
    }

    if (!uppercase) {
      if (symbol >= 0) {
        result += (char) (symbol);
      } else {
        result += (slexicinv.get(-symbol - 1));
      }
    } else if (symbol >= 0) {
      result += Character.toUpperCase((char) (symbol));
    } else {
      result += (slexicinv.get(-symbol - 1));
    }
  }

  boolean isTag(int symbol) {
    return symbol < 0;
  }

  public Pair<Integer, Integer> decode(int code) {
    return spairinv.get(code);
  }

}
