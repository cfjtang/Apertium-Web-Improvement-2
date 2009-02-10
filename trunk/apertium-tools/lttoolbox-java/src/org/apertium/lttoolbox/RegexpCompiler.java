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

import java.util.Set;

public class RegexpCompiler {

  private static final int FIN_FICHERO = -1;

  /**
   * Last token
   */
  int token;

  /**
   * Input string
   */
  String input;

  /**
   * Alphabet to encode symbols
   */
  Alphabet alphabet;

  /**
   * Transducer to store analysis
   */
  Transducer transducer;

  /**
   * Current state
   */
  int state;

  /**
   * Current letter
   */
  int letter;

  /**
   * Post-operator: '+', '?', '*'
   */
  String postop;

  /**
   *
   */
  Set<Integer> brackets;

  RegexpCompiler() {

  }

  RegexpCompiler(RegexpCompiler rec) {
    copy(rec);
  }

  void copy(RegexpCompiler rec) {
    token = rec.token;
    input = rec.input;
    transducer = rec.transducer;
    letter = rec.letter;
    alphabet = rec.alphabet;
    state = rec.state;
    letter = rec.letter;
    postop = rec.postop;
  }


  boolean isReserved(int t) {
    switch (t) {
      case '(':
      case ')':
      case '[':
      case ']':
      case '*':
      case '?':
      case '+':
      case '-':
      case '^':
      case '\\':
      case '|':
      case FIN_FICHERO:
        return true;

      default:
        return false;
    }
  }

  void error() {
    throw new RuntimeException("Error parsing regexp");
  }

  void errorConsuming(int t) {
    throw new RuntimeException("Error parsing regexp");
  }

  void consume(int t) {
    if (token == t) {
      input = input.substring(1);
      if (input.equals("")) {
        token = FIN_FICHERO;
      } else {
        token = input.charAt(0);
      }
    } else {
      errorConsuming(t);
    }
  }

  void compile(String er) {
    input = er;
    token = (int) (input.charAt(0));
    state = transducer.getInitial();
    S();
    transducer.setFinal(state);
  }

  void S() {
    if (token == '(' || token == '[' || !isReserved(token) || token == '\\') {
      RExpr();
      Cola();
    } else {
      error();
    }
  }

  void RExpr() {
    if (token == '(' || token == '[' || !isReserved(token) || token == '\\') {
      Term();
      RExprp();
    } else {
      error();
    }
  }

  void Cola() {
    if (token == FIN_FICHERO || token == ')') {
    } else if (token == '|') {
      int e = state;
      state = transducer.getInitial();
      consume('|');
      RExpr();
      Cola();
      state = transducer.insertNewSingleTransduction(Alphabet.A.cast(0, 0), state);
      transducer.linkStates(e, state, Alphabet.A.cast(0, 0));
    } else {
      error();
    }
  }

  void Term() {
    if (!isReserved(token) || token == '\\') {
      Transducer t = new Transducer();
      int e = t.getInitial();
      Letra();
      e = t.insertNewSingleTransduction(Alphabet.A.cast(letter, letter), e);
      t.setFinal(e);
      Postop();
      if (postop.equals("*")) {
        t.zeroOrMore(Alphabet.A.cast(0, 0));
      } else if (postop.equals("+")) {
        t.oneOrMore(Alphabet.A.cast(0, 0));
      } else if (postop.equals("?")) {
        t.optional(Alphabet.A.cast(0, 0));
      }
      postop = "";
      state = transducer.insertTransducer(state, t, Alphabet.A.cast(0, 0));
    } else if (token == '(') {
      Transducer t = transducer;
      int e = state;
      transducer.clear();
      state = transducer.getInitial();
      consume('(');
      S();
      consume(')');
      transducer.setFinal(state);
      Postop();
      if (postop.equals("*")) {
        transducer.zeroOrMore(Alphabet.A.cast(0, 0));
      } else if (postop.equals("+")) {
        transducer.oneOrMore(Alphabet.A.cast(0, 0));
      } else if (postop.equals("?")) {
        transducer.optional(Alphabet.A.cast(0, 0));
      }
      postop = "";
      state = t.insertTransducer(e, transducer, Alphabet.A.cast(0, 0));
      transducer = t;
    } else if (token == '[') {
      consume('[');
      Esp();
    } else {
      error();
    }
  }

  void RExprp() {
    if (token == '(' || token == '[' || !isReserved(token) || token == '\\') {
      Term();
      RExprp();
    } else if (token == '|' || token == FIN_FICHERO || token == ')') {
    } else {
      error();
    }
  }

  void Letra() {
    if (!isReserved(token)) {
      letter = token;
      consume(token);
    } else if (token == '\\') {
      consume('\\');
      letter = token;
      Reservado();
    } else {
      error();
    }
  }

  void Postop() {
    if (token == '*') {
      consume('*');
      postop = "*";
    } else if (token == '?') {
      consume('?');
      postop = "?";
    } else if (token == '+') {
      consume('+');
      postop = "+";
    } else if (token == '(' || token == '[' || !isReserved(token) ||
            token == '\\' || token == '|' || token == FIN_FICHERO ||
            token == ')') {
    } else {
      error();
    }
  }

  void
  Esp() {
    Transducer t = new Transducer();
    if (!isReserved(token) || token == '\\' || token == ']') {
      Lista();
      consume(']');
      Postop();

      for (Integer it : brackets) {

        int mystate = t.getInitial();
        mystate = t.insertNewSingleTransduction(Alphabet.A.cast(0, 0), mystate);
        mystate = t.insertNewSingleTransduction(Alphabet.A.cast(it, it), mystate);
        t.setFinal(mystate);
      }

      t.joinFinals(Alphabet.A.cast(0, 0));
    } else if (token == '^') {
      consume('^');
      Lista();
      consume(']');
      Postop();

      for (int i = 0; i < 256; i++) {
        if (!brackets.contains(i)) {
          int mystate = t.getInitial();
          mystate = t.insertNewSingleTransduction(Alphabet.A.cast(0, 0), mystate);
          mystate = t.insertNewSingleTransduction(Alphabet.A.cast(i, i), mystate);
          t.setFinal(mystate);
        }
      }

      t.joinFinals(Alphabet.A.cast
              (0, 0));
    } else {
      error();
    }

    if (postop.equals("+")) {
      t.oneOrMore(Alphabet.A.cast
              (0, 0));
    } else if (postop.equals("*")) {
      t.zeroOrMore(Alphabet.A.cast
              (0, 0));
    } else if (postop.equals("?")) {
      t.optional(Alphabet.A.cast
              (0, 0));
    }
    brackets.clear();
    postop = "";

    state = transducer.insertTransducer(state, t, Alphabet.A.cast
            (0, 0));
  }

  void Lista() {
    if (!isReserved(token) || token == '\\') {
      Elem();
      Lista();
    } else if (token == ']') {
    } else {
      error();
    }
  }

  void Reservado() {
    if (isReserved(token)) {
      consume(token);
    } else {
      error();
    }
  }

  void Elem() {
    if (!isReserved(token) || token == '\\') {
      Letra();
      int rango1 = letter;
      ColaLetra();
      int rango2 = letter;

      if (rango1 > rango2) {
        error();
      } else {
        for (int i = rango1; i <= rango2; i++) {
          brackets.add(i);
        }
      }
    } else {
      error();
    }
  }

  void ColaLetra() {
    if (token == '-') {
      consume('-');
      Letra();
    } else if (!isReserved(token) || token == '\\' || token == ']') {
    } else {
      error();
    }
  }

  public void setAlphabet(Alphabet a) {
    alphabet = a;
  }

  public Transducer getTransducer() {
    return transducer;
  }

  void initialize(Alphabet a) {
    setAlphabet(a);
    transducer.clear();
    brackets.clear();
    postop = "";
  }

}