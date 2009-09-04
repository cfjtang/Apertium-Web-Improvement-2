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

import java.io.FileInputStream;
import org.apache.commons.collections15.MultiMap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.*;

public class Transducer {

  /**
   * Initial state
   */
  int initial;

  /**
   * Final state set
   */
  List<Integer> finals;

  /**
   * Transitions of the transducer
   */
  Map<Integer, MultiMap<Integer, Integer>> transitions;

  int newState() {

    int nstate = transitions.size();

    while (transitions.containsKey(nstate)) {
      nstate++;
    }
    transitions.get(nstate).clear();  // force creating

    return nstate;
  }

  Transducer() {
    initial = newState();
  }

  int insertSingleTransduction(int tag, int source) {
    if (transitions.containsKey(source)) {
      if (transitions.get(source).get(tag).size() == 1) {
        return transitions.get(source).get(tag).iterator().next();
      } else if (transitions.get(source).get(tag).isEmpty()) {
        // new state
        int state = newState();
        transitions.get(source).put(tag, state);
        return state;
      } else if (transitions.get(source).get(tag).size() == 2) {
        // there's a local cycle, must be ignored and treated like in '1'
        final Collection<Integer> range = transitions.get(source).get(tag);
        for (Integer r : range) {
          if (tag != r) {
            if (r != source) {
              return r;
            }
          }
        }
        return -1;
      } else {
        return -1;
      }
    } else {
      return -1;
    }
  }

  int insertNewSingleTransduction(int tag, int source) {
    int state = newState();
    transitions.get(source).put(tag, state);
    return state;
  }

  int insertTransducer(int source, Transducer t, int epsilon_tag) {

    Map<Integer, Integer> relacion = new HashMap<Integer, Integer>();

    t.joinFinals(epsilon_tag);

    for (Integer key : t.transitions.keySet()) {
      relacion.put(key, newState());
    }

    for (Integer key : t.transitions.keySet()) {
      MultiMap<Integer, Integer> it2 = t.transitions.get(key);
      for (Integer val : it2.values()) {
        transitions.get(relacion.get(key)).put(key, relacion.get(val));
      }
    }

    transitions.get(source).put(epsilon_tag, relacion.get(t.initial));

    return relacion.get((t.finals.get(0)));
  }

  void linkStates(int source, int destino, int etiqueta) {

    if (!transitions.containsKey(source) && !transitions.containsKey(destino)) {
      // new code

      final Collection<Integer> range = transitions.get(source).get(etiqueta);
      for (int integer : range) {
        if (integer == destino) return;
      }
      // end of new code
      transitions.get(source).put(etiqueta, destino);
    } else {
      throw new RuntimeException("Error: Trying to link nonexistent states (" + source +
              ", " + destino + ", " + etiqueta + ")");

    }
  }

  public boolean isFinal(int state) {
    return finals.contains(state);
  }

  public void setFinal(int state) {
    setFinal(state, true);
  }

  public void setFinal(int state, boolean valor) {
    if (valor) {
      finals.add(state);
    } else {
      finals.remove(state);
    }
  }

  public int getInitial() {
    return initial;
  }

  public List<Integer> closure(int state, int epsilon_tag) {
    List<Integer> nonvisited = new Vector<Integer>();
    List<Integer> result = new Vector<Integer>();

    nonvisited.add(state);
    result.add(state);

    while (nonvisited.size() > 0) {
      int auxest = nonvisited.get(0);

      final MultiMap<Integer, Integer> mm = transitions.get(auxest);
      final Collection<Integer> rango = mm.get(epsilon_tag);
      for (Integer r_second : rango) {
        while (epsilon_tag != r_second) {
          if (!result.contains(auxest)) {
            result.set(0, r_second);
            nonvisited.set(0, r_second);
          }
        }
      }
      nonvisited.remove((Integer) auxest);
    }
    return result;
  }

  void joinFinals(int epsilon_tag) {
    if (finals.size() > 1) {
      int state = newState();

      for (Integer it : finals) {
        linkStates(it, state, epsilon_tag);
      }

      finals.clear();
      finals.add(state);
    } else if (finals.size() == 0) {
      throw new RuntimeException("Error: empty set of final states");

    }
  }

  boolean isEmptyIntersection(List<Integer> s1, List<Integer> s2) {

    if (s1.size() < s2.size()) {
      for (Integer it : s1) {

        if (s2.contains(it)) {
          return false;
        }
      }
    } else {
      for (Integer it : s2) {
        if (s1.contains(it)) {
          return false;
        }
      }
    }

    return true;
  }

  void determinize(int epsilon_tag) {

    List<Set<Integer>> R = new Vector<Set<Integer>>(2);
    Map<Integer, List<Integer>> Q_prima = new HashMap<Integer, List<Integer>>();
    Map<List<Integer>, Integer> Q_prima_inv = new HashMap<List<Integer>, Integer>();

    Map<Integer, MultiMap<Integer, Integer>> transitions_prima = new HashMap<Integer, MultiMap<Integer, Integer>>();

    int talla_Q_prima = 0;
    Q_prima.put(0, closure(initial, epsilon_tag));

    Q_prima_inv.put(Q_prima.get(0), 0);
    R.get(0).add(0);

    int initial_prima = 0;
    List<Integer> finals_prima = new Vector<Integer>();

    if (finals.contains(initial)) {
      finals_prima.add(0);
    }

    int t = 0;

    while (talla_Q_prima != Q_prima.size()) {
      talla_Q_prima = Q_prima.size();
      R.get((t + 1) % 2).clear();

      for (Integer it : R.get(t)) {

        if (!isEmptyIntersection(Q_prima.get(it), finals)) {
          finals_prima.add(it);
        }

        Map<Integer, List<Integer>> mymap = new HashMap<Integer, List<Integer>>();

        for (Integer it2 : Q_prima.get(it)) {

          MultiMap<Integer, Integer> mm = transitions.get(it2);
          for (int it3First : mm.keySet()) {
            for (int it3Second : mm.get(it3First)) {
              if (it3First != epsilon_tag) {
                List<Integer> c = closure(it3Second, epsilon_tag);
                for (Integer it4 : c) {
                  mymap.get(it3First).add(it4);
                }
              }
            }
          }
        }

        // adding new states
        for (Map.Entry<Integer, List<Integer>> it2 : mymap.entrySet()) {
          if (Q_prima_inv.containsKey(it2.getValue())) {
            int etiq = Q_prima.size();
            Q_prima.put(etiq, it2.getValue());
            Q_prima_inv.put(it2.getValue(), etiq);
            R.get((t + 1) % 2).add(Q_prima_inv.get(it2.getValue()));
            transitions_prima.get(etiq).clear();
          }
          transitions_prima.get(it).put(it2.getKey(), Q_prima_inv.get(it2.getValue()));
        }
      }

      t = (t + 1) % 2;
    }

    transitions = transitions_prima;
    finals = finals_prima;
    initial = initial_prima;
  }


  void minimize(int epsilon_tag) {
    reverse(epsilon_tag);
    determinize(epsilon_tag);
    reverse(epsilon_tag);
    determinize(epsilon_tag);
  }

  void optional(int epsilon_tag) {
    joinFinals(epsilon_tag);
    int state = newState();
    linkStates(state, initial, epsilon_tag);
    initial = state;

    state = newState();
    linkStates(finals.get(0), state, epsilon_tag);
    finals.clear();
    finals.add(state);
    linkStates(initial, state, epsilon_tag);
  }

  void oneOrMore(int epsilon_tag) {
    joinFinals(epsilon_tag);
    int state = newState();
    linkStates(state, initial, epsilon_tag);
    initial = state;

    state = newState();
    linkStates(finals.get(0), state, epsilon_tag);
    finals.clear();
    finals.add(state);
    linkStates(state, initial, epsilon_tag);
  }

  void zeroOrMore(int epsilon_tag) {
    oneOrMore(epsilon_tag);
    optional(epsilon_tag);
  }

  void
  clear() {
    finals.clear();
    transitions.clear();
    initial = newState();
  }

  boolean isEmpty() {
    return finals.size() == 0 && transitions.size() == 1;
  }

  int size() {
    return transitions.size();
  }

  int numberOfTransitions() {
    int counter = 0;
    for (MultiMap<Integer, Integer> integerIntegerMultiMap : transitions.values()) {
      counter += integerIntegerMultiMap.size();
    }
    return counter;
  }

  public boolean isEmpty(int state) {
    return !(transitions.containsKey(state) && transitions.get(state).size() > 0);
  }

  void write(Writer output, int decalage) throws IOException {
    output.write(initial);
    output.write(finals.size());

    int base = 0;
    for (Integer it : finals) {

      output.write(it - base);
      base = it;
    }

    base = transitions.size();
    output.write(base);

    for (Map.Entry<Integer, MultiMap<Integer, Integer>> it : transitions.entrySet()) {

      Integer itFirst = it.getKey();
      output.write(it.getValue().size());
      int tagbase = 0;

      for (Integer it2First : it.getValue().keySet()) {
        for (Integer it2Second : it.getValue().get(it2First)) {
          output.write(it2First - tagbase + decalage);
          tagbase = it2First;

          if (it2Second >= itFirst) {
            output.write(it2Second - itFirst);
          } else {
            output.write(it2Second + base - itFirst);
          }
        }
      }
    }
  }

  public static Transducer read(FileInputStream input, int decalage) throws IOException {
    Transducer new_t = new Transducer();

    new_t.initial = input.read();
    int finals_size = input.read();

    int base = 0;
    while (finals_size > 0) {
      finals_size--;

      base += input.read();
      new_t.finals.add(base);
    }

    base = input.read();
    int number_of_states = base;
    int current_state = 0;
    while (number_of_states > 0) {
      int number_of_local_transitions = input.read();
      int tagbase = 0;
      while (number_of_local_transitions > 0) {
        number_of_local_transitions--;
        tagbase += input.read() - decalage;
        int state = (current_state + input.read()) % base;
        if (!new_t.transitions.containsKey(state)) {
          new_t.transitions.get(state).clear(); // force create
        }
        new_t.transitions.get(current_state).put(tagbase, state);
      }
      number_of_states--;
      current_state++;
    }

    return new_t;
  }

  void copy(Transducer t) {
    initial = t.initial;
    finals = t.finals;
    transitions = t.transitions;
  }

  void reverse(int epsilon_tag) {
    joinFinals(epsilon_tag);

    SortedMap<Integer, SortedMap<Integer, Integer>> temporal = new TreeMap<Integer, SortedMap<Integer, Integer>>();

    for (Map.Entry<Integer, MultiMap<Integer, Integer>> it : transitions.entrySet()) {

      Integer itFirst = it.getKey();

      MultiMap<Integer, Integer> aux = it.getValue();
      aux.clear();

      for (Integer it2First : aux.keySet()) {
        for (Integer it2Second : aux.get(it2First)) {
          if (it2Second >= itFirst) {
            transitions.get(it2Second).put(it2First, itFirst);
          } else {
            temporal.get(it2Second).put(it2First, itFirst);
          }
        }
      }

      if (temporal.containsKey(itFirst)) {
        final SortedMap<Integer, Integer> iimap = temporal.get(itFirst);
        it.getValue().putAll(iimap);
        temporal.remove(itFirst);
      }
    }

    final List<Integer> keys = new Vector<Integer>(temporal.keySet());
    Collections.sort(keys, Collections.reverseOrder());
    for (Integer it : keys) {
      final SortedMap<Integer, Integer> map = temporal.get(it);
      for (Map.Entry<Integer, Integer> it2 : map.entrySet()) {
        transitions.get(it).put(it2.getKey(), it2.getValue());
      }
    }

    int tmp = initial;
    initial = finals.get(0);
    finals.clear();
    finals.add(tmp);

  }

}