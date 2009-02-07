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

import org.apertium.lttoolbox.Alphabet;
import org.apertium.lttoolbox.Dest;
import org.apertium.lttoolbox.Pool;
import org.apertium.lttoolbox.Node;

import java.util.List;
import java.util.Set;
import java.util.Vector;

public class State {

  List<TNodeState> state;

  /**
   * Pool of wchar_t vectors, for efficience (static class)
   */
  static Pool<List<Integer>> pool;

  public State() {
  }

  public State(State s) {
    copy(s);
  }

  void copy(State s) {
    // release references
    for (int i = 0, limit = state.size(); i != limit; i++) {
      pool.release(state.get(i).sequence);
    }

    state = s.state;

    for (int i = 0, limit = state.size(); i != limit; i++) {
      List<Integer> tmp = pool.get();
      tmp = (state.get(i).sequence);
      state.get(i).sequence = tmp;
    }
  }

  int size() {
    return state.size();
  }

  public void init(Node initial) {
    state.clear();
    state.add(new TNodeState(initial, pool.get(), false));
    state.get(0).sequence.clear();
    epsilonClosure();
  }

  void apply(int input) {

    List<TNodeState> new_state = new Vector<TNodeState>();

    for (int i = 0, limit = state.size(); i != limit; i++) {

      Dest it = state.get(i).where.transitions.get(input);
      if (it != null) {
        for (int j = 0; j != it.size; j++) {
          List<Integer> new_v = pool.get();
          new_v = (state.get(i).sequence);
          if (input != 0) {
            new_v.add(it.out_tag.get(j));
          }
          new_state.add(new TNodeState(it.dest.get(j), new_v, state.get(i).dirty));
        }
      }
      pool.release(state.get(i).sequence);
    }

    state = new_state;
  }

  void apply(int input, int alt) {

    List<TNodeState> new_state = new Vector<TNodeState>();

    for (int i = 0, limit = state.size(); i != limit; i++) {

      Dest it = state.get(i).where.transitions.get(input);
      if (it != null) {
        for (int j = 0; j != it.size; j++) {
          List<Integer> new_v = pool.get();
          new_v = (state.get(i).sequence);
          if (input != 0) {
            new_v.add(it.out_tag.get(j));
          }
          new_state.add(new TNodeState(it.dest.get(j), new_v, state.get(i).dirty));
        }
      }
      it = state.get(i).where.transitions.get(alt);
      if (it != null) {
        for (int j = 0; j != it.size; j++) {
          List<Integer> new_v = pool.get();
          new_v = (state.get(i).sequence);
          if (alt != 0) {
            new_v.add(it.out_tag.get(j));
          }
          new_state.add(new TNodeState(it.dest.get(j), new_v, true));
        }
      }
      pool.release(state.get(i).sequence);
    }

    state = new_state;
  }

  void epsilonClosure() {
    for (int i = 0; i != state.size(); i++) {
      Dest it2 = state.get(i).where.transitions.get(0);
      if (it2 != null) {
        for (int j = 0; j != it2.size; j++) {
          List<Integer> tmp = pool.get();
          tmp = (state.get(i).sequence);
          if (it2.out_tag.get(j) != 0) {
            tmp.add(it2.out_tag.get(j));
          }
          state.add(new TNodeState(it2.dest.get(j), tmp, state.get(i).dirty));
        }
      }
    }
  }

  void step(int input) {
    apply(input);
    epsilonClosure();
  }

  void step(int input, int alt) {
    apply(input, alt);
    epsilonClosure();
  }

  boolean isFinal(Set<Node> finals) {
    for (int i = 0, limit = state.size(); i != limit; i++) {
      if (finals.contains(state.get(i).where)) {
        return true;
      }
    }
    return false;
  }

  String filterFinals(Set<Node> finals, Alphabet alphabet, Set<Character> escaped_chars,
                      boolean uppercase, boolean firstupper) {
    int firstchar= 0;
    return filterFinals(finals, alphabet, escaped_chars, uppercase, firstupper, firstchar);
  }

  String filterFinals(Set<Node> finals, Alphabet alphabet, Set<Character> escaped_chars,
                      boolean uppercase, boolean firstupper, int firstchar) {

    StringBuffer result = new StringBuffer();

    for (int i = 0, limit = state.size(); i != limit; i++) {

      if (finals.contains(state.get(i).where)) {
        if (state.get(i).dirty) {
          result.append('/');
          int first_char = result.length() + firstchar;

          for (int j = 0, limit2 = state.get(i).sequence.size(); j != limit2; j++) {
            if (escaped_chars.contains((char) ((state.get(i).sequence)).get(j).intValue())) {
              result.append('\\');
            }
            alphabet.getSymbol(result.toString(), ((state.get(i).sequence)).get(j), uppercase);
          }
          if (firstupper) {

            if (result.charAt(first_char) == '~') {
              // skip post-generation mark
              result.setCharAt(first_char + 1, Character.toUpperCase(result.charAt(first_char + 1)));
            } else {
              result.setCharAt(first_char, Character.toUpperCase(result.charAt(first_char)));
            }
          }
        } else {
          result.append('/');
          for (int j = 0, limit2 = state.get(i).sequence.size(); j != limit2; j++) {
            if (escaped_chars.contains((char) ((state.get(i).sequence).get(j)).intValue())) {
              result.append('\\');
            }
            alphabet.getSymbol(result.toString(), ((state.get(i).sequence)).get(j));
          }
        }
      }
    }

    return result.toString();
  }

  String filterFinalsSAO(Set<Node> finals,
                         Alphabet alphabet,
                         Set<Character> escaped_chars,
                         boolean uppercase, boolean firstupper, int firstchar) {
    StringBuffer result = new StringBuffer("");

    for (int i = 0, limit = state.size(); i != limit; i++) {
      if (finals.contains(state.get(i).where)) {
        result.append('/');
        int first_char = result.length() + firstchar;
        for (int j = 0, limit2 = state.get(i).sequence.size(); j != limit2; j++) {
          if (escaped_chars.contains((char) (state.get(i).sequence).get(j).intValue())) {
            result.append('\\');
          }
          if (alphabet.isTag(((state.get(i).sequence)).get(j))) {
            result.append('&');
            alphabet.getSymbol(result.toString(), state.get(i).sequence.get(j));
            result.setCharAt(result.length() - 1, ';');
          } else {
            alphabet.getSymbol(result.toString(), ((state.get(i).sequence)).get(j), uppercase);
          }
        }
        if (firstupper) {
          if (result.charAt(first_char) == '~') {
            // skip post-generation mark
            result.setCharAt(first_char + 1, Character.toUpperCase(result.charAt(first_char + 1)));
          } else {
            result.setCharAt(first_char, Character.toUpperCase(result.charAt(first_char)));
          }
        }
      }
    }

    return result.toString();
  }
}

