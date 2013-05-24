/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apertium.lttoolbox.compile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apertium.lttoolbox.Alphabet;
import org.apertium.lttoolbox.Alphabet.IntegerPair;
import org.apertium.lttoolbox.Compression;
import org.apertium.lttoolbox.LTPrint;
import org.apertium.lttoolbox.LTTrim;
import org.apertium.lttoolbox.collections.AbundantIntSet;
import org.apertium.lttoolbox.collections.IntSet;
import org.apertium.lttoolbox.collections.SlowIntegerHashSet;
import org.apertium.lttoolbox.collections.SlowIntegerTreeSet;
import org.apertium.lttoolbox.collections.Transducer;
import static org.apertium.lttoolbox.collections.Transducer.DEBUG;
import org.apertium.lttoolbox.process.BasicFSTProcessor;
import org.apertium.lttoolbox.process.FSTProcessor;
import org.apertium.lttoolbox.process.State;

/**
 * Transducer extended with methods handy for compilation of transducer
 *
 * @author j
 */
public class TransducerComp extends org.apertium.lttoolbox.collections.Transducer {
  /**
   * Clear transducer
   */
  void clear() {
    finals.clear();
    transitions.clear();
    initial = newState();
  }

  /**
   * Check if the transducer is empty
   *
   * @return true if the transducer is empty
   */
  boolean isEmpty() {
    return finals.size() == 0 && transitions.size() == 1;
  }

  /**
   * Transducer minimization
   * Minimize = reverse + determinize + reverse + determinize
   */
  public void minimize() {
    reverse();
    determinize();
    reverse();
    determinize();
  }

  /**
   * Insertion of a single transduction, forcing create a new target
   * state
   *
   * @param tag the tag of the transduction being inserted
   * @param source the source state of the new transduction
   * @return the target state
   */
  Integer insertNewSingleTransduction(Integer tag, Integer source) {
    /*
     * Map<Integer, Set<Integer>> place = transitions.get(source);
     * if (place==null) {
     * place = new HashMap<Integer, Set<Integer>>();
     * transitions.put(source, place);
     * }
     */
    Map<Integer, IntSet> place;
    if (transitions.size() <= source) {
      place = new HashMap<Integer, IntSet>();
      transitions.add(place);
    } else {
      place = transitions.get(source);
    }

    if (DEBUG)
      System.err.println(transitions + "  place = " + place);
    IntSet set = place.get(tag);

    if (set == null) {
      set = new SlowIntegerHashSet();
      place.put(tag, set);
    }
    Integer state = newState();
    set.add(state);
    return state;
  }

  /**
   * Insertion of a transducer in a given source state, unifying their
   * final states using a optionally given epsilon tag
   *
   * @param source the source state
   * @param t the transducer being inserted
   * @return the new target state
   */
  Integer insertTransducer(Integer source, TransducerComp t) {

    // base of node translation
    final int first_state = transitions.size();

    // copy transducer
    for (int i = 0; i < t.transitions.size(); i++) {
      Integer local_source = newState();
      for (Integer tag : t.transitions.get(i).keySet()) {
        IntSet destset = new SlowIntegerHashSet();
        for (Integer destination : t.transitions.get(i).get(tag)) {
          destset.add(destination + first_state);
        }
        transitions.get(local_source).put(tag, destset);
      }
    }

    // link initial state of the new transducer
    // with epsilon_tag in source
    this.linkStates(source, first_state + t.initial, epsilon_tag);

    // return the unique final state of the inserted transducer
    int untranslated_final = t.finals.firstInt();
    return first_state + untranslated_final;
  }

  /**
   * Computes the number of transitions of a transducer
   *
   * @return the number of transitions
   */
  int numberOfTransitions() {
    int counter = 0;
    for (int i = 0; i < transitions.size(); i++) {
      for (IntSet destinations : transitions.get(i).values()) {
        counter += destinations.size();
      }
    }
    return counter;
  }

  /**
   * Computes whether two sets have elements in common
   *
   * @param s1 the firstInt set
   * @param s2 the second set
   * @return true if the sets don't trim
   */
  private boolean isEmptyIntersection(Set<Integer> s1, Set<Integer> s2) {
    for (Integer i : s1) {
      if (s2.contains(i)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Computes whether two sets have elements in common
   *
   * @param s1 the firstInt set
   * @param s2 the second set
   * @return true if the sets don't trim
   */
  private boolean isEmptyIntersection(Set<Integer> s1, IntSet s2) {
    for (Integer i : s1) {
      if (s2.contains(i)) {
        return false;
      }
    }
    return true;
  }

  /**
   * set the epsilon tag
   *
   * @param e the value to which set the epsilon tag
   */
  void setEpsilon_Tag(int e) {
    epsilon_tag = e;
  }

  /**
   * Determinize the transducer
   */
  private void determinize() {
    List<Set<Integer>> R = new ArrayList<Set<Integer>>(2);
    // MUST be TreeSet to retain binary compatibility:
    R.add(new TreeSet<Integer>()); // new SlowIntegerTreeSet() giver problemer. Hvorfor???
    R.add(new TreeSet<Integer>()); // new SlowIntegerTreeSet() giver problemer. Hvorfor???

    Map<Integer, Set<Integer>> Q_prima = new HashMap<Integer, Set<Integer>>();
    Map<Set<Integer>, Integer> Q_prima_inv = new HashMap<Set<Integer>, Integer>(); // setComparator

    // MUST be ordered to retain binary compatibility:
    ArrayList<Map<Integer, IntSet>> transitions_prima = new ArrayList<Map<Integer, IntSet>>();

    int talla_Q_prima = 0;

    Set<Integer> initial_closure = closure(initial);
    Q_prima.put(0, initial_closure);
    Q_prima_inv.put(initial_closure, 0);
    R.get(0).add(0);

    int initial_prima = 0;
    AbundantIntSet finals_prima = new AbundantIntSet();

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

        Map<Integer, Set<Integer>> mymap = new TreeMap<Integer, Set<Integer>>();

        for (Integer it2 : Q_prima.get(it)) {
          if (it2 < transitions.size()) {
            Map<Integer, IntSet> xxx = transitions.get(it2);
            for (Integer it3 : xxx.keySet()) {
              if (!it3.equals(epsilon_tag)) {
                for (Integer it3p : xxx.get(it3)) {
                  Set<Integer> c = closure(it3p);
                  Set<Integer> zzz = mymap.get(it3);
                  if (zzz == null) {
                    mymap.put(it3, c);
                  } else {
                    zzz.addAll(c);
                  }
                }
              }
            }
          }
        }
        // adding new states
        for (Map.Entry<Integer, Set<Integer>> it2 : mymap.entrySet()) {
          if (!Q_prima_inv.containsKey(it2.getValue())) {
            int etiq = Q_prima.size();
            Q_prima.put(etiq, it2.getValue());
            Q_prima_inv.put(it2.getValue(), etiq);
            R.get((t + 1) % 2).add(Q_prima_inv.get(it2.getValue()));
            //transitions_prima.add(new TreeMap<Integer, Set<Integer>>());
            while (transitions_prima.size() <= etiq)
              transitions_prima.add(new TreeMap<Integer, IntSet>());
            transitions_prima.set(etiq, new TreeMap<Integer, IntSet>());

          }

          while (transitions_prima.size() <= it) {
            transitions_prima.add(new TreeMap<Integer, IntSet>());
          }
          transitions_prima.get(it).put(it2.getKey(), new SlowIntegerTreeSet());
          transitions_prima.get(it).get(it2.getKey()).add(Q_prima_inv.get(it2.getValue()));
        }
      }

      t = (t + 1) % 2;
    }

    transitions = transitions_prima;
    finals = finals_prima;
    initial = initial_prima;

  }

  /**
   * Join all finals in one using epsilon transductions
   *
   * @return the only final state
   */
  void joinFinals() {
    if (finals.size() > 1) {
      Integer state = newState();

      for (int it = finals.next(0); it >= 0; it = finals.next(it + 1)) {
        linkStates(it, state, epsilon_tag);
      }
      /*
       * Iterator<Integer> it = finals.iterator();
       * while (it.hasNext()) {
       * linkStates(it.next(), state, epsilon_tag);
       * }
       */
      finals.clear();
      finals.add(state);
      //return state;
    } else if (finals.size() == 0) {
      throw new RuntimeException("Error: empty set of final states");
    } else {
      //return finals.iterator().next();
    }
  }

  /**
   * Make a transducer cyclic (link final states with initial state with
   * empty transductions)
   */
  void oneOrMore() {
    joinFinals();
    int state = newState();
    linkStates(state, initial, epsilon_tag);
    initial = state;

    state = newState();
    linkStates((Integer) finals.firstInt(), state, epsilon_tag);
    finals.clear();
    finals.add(state);
    linkStates(state, initial, epsilon_tag);
  }

  /**
   * Make a transducer optional (link initial state with final states with
   * empty transductions)
   */
  void optional() {
    joinFinals();
    int state = newState();
    linkStates(state, initial, epsilon_tag);
    initial = state;

    state = newState();
    linkStates((Integer) finals.firstInt(), state, epsilon_tag);
    finals.clear();
    finals.add(state);
    linkStates(initial, state, epsilon_tag);
  }

  /**
   * Reverse all the transductions of a transducer
   */
  private void reverse() {
    joinFinals();

    ArrayList<Map<Integer, IntSet>> result = new ArrayList<Map<Integer, IntSet>>();

//        for (Map.Entry<Integer, Map<Integer, Set<Integer>>> it : transitions.entrySet()) {
//            Integer dest = it.getKey();
    for (int i = 0; i < transitions.size(); i++) {
      Integer dest = i;
      for (Map.Entry<Integer, IntSet> it2 : transitions.get(i).entrySet()) {
        Integer tag = it2.getKey();
        for (Integer origin : it2.getValue()) {
          boolean added = result.size() <= origin;
          while (result.size() <= origin)
            result.add(new TreeMap<Integer, IntSet>());
          Map<Integer, IntSet> res_origin = result.get(origin);
          if (added) {
            res_origin.put(tag, new SlowIntegerTreeSet());

            IntSet aux = new SlowIntegerTreeSet();
            aux.add(dest);

            Map<Integer, IntSet> aux2 = new TreeMap<Integer, IntSet>();
            aux2.put(tag, aux);
            result.set(origin, aux2);
          } else {
            IntSet res_origin_tag = res_origin.get(tag);
            if (res_origin_tag == null) {
              res_origin_tag = new SlowIntegerTreeSet();
              res_origin.put(tag, res_origin_tag);
            }
            res_origin_tag.add(dest);
          }
        }
      }
    }
    Integer newInitial = finals.firstInt();
    finals.clear();
    finals.add(initial);
    initial = newInitial;
    transitions = result;
  }

  /**
   * Set the state as a final or not, yes by default
   *
   * @param e the state
   */
  void setFinal(Integer e) {
    if (!finals.contains(e)) {
      finals.add(e);
    }
  }

  /**
   * Compute the number of states that are the source of at least one transition
   *
   * @return this number of states
   */
  int size() {
    return transitions.size();
  }

  /**
   * zeroOrMore = oneOrMore + optional
   */
  void zeroOrMore() {
    oneOrMore();
    optional();
  }

  /**
   * Copies the passed-in Transducer object to this one.
   *
   * @param t - The Transducer object to copy.
   */
  public void shallowCopy(TransducerComp t) {
    initial = t.initial;
    finals = t.finals;
    transitions = t.transitions;
    epsilon_tag = t.epsilon_tag;
  }


  /**
   * Compare the tranducer with another one
   *
   * @param t the transducer to DEBUG_compare to
   * @return true if the two transducers are similar
   */
  public boolean DEBUG_compare(TransducerComp other) {
    System.out.println(("comparing this:\n" + this + "\nwith other:\n " + other)); // .replaceAll("\n", " ")
    if (other == null) {
      throw new RuntimeException("comparing with a null transducer");
    }
    if (!(initial.equals(other.initial))) {
      throw new RuntimeException("the two transducer have different initial states: "+initial+ " != "+ other.initial);
    }
    if (finals.size() != other.finals.size()) {
      throw new RuntimeException("the two transducer have a different number of final states: "+ finals.size()+ " != "+other.finals.size());
    }
    /*
     * for (Integer i : finals) {
     * if (!other.finals.contains(i)) {
     * System.out.println("the state " + i + " is a final state in the firstInt transducer but not in the second one");
     * sameFinals = false;
     * return false;
     * }
     * }
     */
    if (transitions.size() != other.transitions.size()) {
      throw new RuntimeException("the two transducers have different sizes for their attribute transitions: "+transitions.size()+" != "+other.transitions.size());
    }
    /*
     * for (Integer source : transitions.keySet()) {
     * boolean sameTransitionsFromSource = true;
     * if (!other.transitions.containsKey(source)) {
     * System.out.println("key " + source + " exists in this.transitions, but not in t.transitions");
     * sameTransducer = false;
     * break;
     * }
     * if (!(transitions.get(source).size() == other.transitions.get(source).size())) {
     * System.out.println("the transducers have a different number of transitions leaving the state " + source);
     * sameTransducer = false;
     * break;
     * }
     * for (Integer label : transitions.get(source).keySet()) {
     * if (!other.transitions.get(source).containsKey(label)) {
     * System.out.println("the state " + source + " has a transition with label " + label + " in this.transitions, but not in t.transitions");
     * sameTransducer = false;
     * break;
     * }
     * if (!(transitions.get(source).get(label).size() == other.transitions.get(source).get(label).size())) {
     * System.out.println("the transducers have a different number of transitions leaving the state " + source + " with the label " + label);
     * sameTransducer = false;
     * break;
     * }
     * for (Integer destination : transitions.get(source).get(label)) {
     * if (!other.transitions.get(source).get(label).contains(destination)) {
     * System.out.println("there is a transition from the state " + source +
     * " to the state " + destination + " with the label " + label +
     * " which is in this.transitions and not in t.transitions");
     * sameTransducer = false;
     * }
     * }
     * }
     * }
     */
    return true;
  }



  public static void main3(String[] args) throws FileNotFoundException, IOException {
    LTPrint.main(new String[]{"-a", "testdata/trimming/test-en.bin" });
    //LTPrint.main(new String[]{"-s", "testdata/bilingual/eo-en.autobil.bin" });
  }

  public static void xmain(String[] args) throws IOException {
    LTTrim.main(new String[]{"-v", "testdata/compounding/eo-en.automorf.bin","testdata/bilingual/eo-en.autobil.bin","/tmp/x" });
  }
  
  public void show(Alphabet alphabet, PrintStream out) {
    joinFinals();
    
    for (int state=0; state<transitions.size(); state++) {
      Map<Integer, IntSet> it = transitions.get(state);

      for (Map.Entry<Integer, IntSet> it2 : it.entrySet()) {
        Integer it2_first = it2.getKey();
        IntegerPair t = alphabet.decode(it2_first);
        for (Integer target_state : it2.getValue()) {
          out.printf("%d\t"+"%d\t", state, target_state);
          String l = alphabet.getSymbol(t.first);
          if (l=="") { // If we find an epsilon
            out.print("ε\t");
          } else {
            out.print(l+"\t");
          }
          String r = alphabet.getSymbol(t.second);
          if (r=="") { // If we find an epsilon
            out.print("ε\t");
          } else {
            out.print(r+"\t");
          }
          out.println();
        }
      }
    }
    for (Integer it3 : finals) {
      out.println(it3);
    }
  }

  /** Helper class for collecting symbols during expanding/traversing */
  private static class TargetStateLR {
    TargetStateLR next = null;
    TargetStateLR last = null;

    String left, right;
    final Integer target_state;
    
    private TargetStateLR(Integer target_state, String l, String r) {
      left = l;
      right = r;
      this.target_state = target_state;
    }

    private String getLeft() {
      if (next != null) return left+next.getLeft();
      return left;
    }

    private String getRight() {
      if (next != null) return right+next.getRight();
      return right;
    }

    private void addlast(TargetStateLR targetStateLR) {
      if (last == null) next=last=targetStateLR;
      else {
        last.next = targetStateLR;
        last = targetStateLR;
      }
    }
  }

  String ELLIPSIS = "\u2026"; // ELLIPSIS …

  private void showLtExpandish(Alphabet alphabet, PrintStream out, int state, boolean[] visited, String left, String right) {
    if (finals.contains(state)) {
      out.println(left + ":" + right);
      return;
    }
    visited[state] = true;

    Map<Integer, IntSet> it = transitions.get(state);
    LinkedHashMap<Integer, TargetStateLR> targetStatesLR = new LinkedHashMap<Integer,TargetStateLR>();

    // first, run thru and collect transitions according to target state
    for (Map.Entry<Integer, IntSet> it2 : it.entrySet()) {
      Integer it2_first = it2.getKey();
      IntegerPair t = alphabet.decode(it2_first);
      String l = alphabet.getSymbol(t.first);
      String r = alphabet.getSymbol(t.second);
      for (Integer target_state : it2.getValue()) {
        TargetStateLR lr = targetStatesLR.get(target_state);
        if (lr == null) targetStatesLR.put(target_state, new TargetStateLR(target_state, l, r));
        else lr.addlast(new TargetStateLR(target_state, l, r));
      }
    }
    // then recurse one time for each target state
    for (TargetStateLR lr : targetStatesLR.values()) {
      //System.out.println(lr.target_state+"   "+left+" "+lr.ls + ":" + right+" "+lr.rs);
      if (visited[lr.target_state]) {
        out.println("__CYCLE__ "+  left+lr.getLeft()+ELLIPSIS + ":" + right+lr.getRight()+ELLIPSIS);
        return;
      }
      String lettersl = "";
      String lettersr = "";
      Integer target_state = lr.target_state;
      while (lr != null) {
        if (lr.left.length() != 1 || lr.right.length() != 1) {
          // Symbol or empty value. Print out seperately
          showLtExpandish(alphabet, out, lr.target_state, visited, left+lr.left, right+lr.right);
        } else {
          // letter. Collect all of them and show together
          lettersl += lr.left;
          lettersr += lr.right;
        }
        lr = lr.next;
      }
      if (lettersl.length()==1) showLtExpandish(alphabet, out, target_state, visited, left+lettersl, right+lettersr);
      if (lettersl.length()>1) showLtExpandish(alphabet, out, target_state, visited, left+"["+lettersl+"]", right+"["+lettersr+"]");
    }
    visited[state] = false;
  }
  
  public void showLtExpandish(Alphabet alphabet, PrintStream out) {
    joinFinals();
    boolean[] visited = new boolean[transitions.size()];
    showLtExpandish(alphabet, out, 0, visited, "", "");
  }


  public void trim(Alphabet alphabet, BasicFSTProcessor bil) {
    joinFinals();
    //determinize();
    boolean[] visited = new boolean[transitions.size()];
    bil.calc_initial_state();
    State bilstate = bil.initial_state.copy();
    trim(alphabet, 0, visited, "", "", bil, bilstate);
  }




  private void xshowLtExpandish(Alphabet alphabet, PrintStream out, int state, boolean[] visited, String left, String right) {
    if (finals.contains(state)) {
      out.println(left + ":" + right);
      return;
    }
    visited[state] = true;

    Map<Integer, IntSet> it = transitions.get(state);
    for (Map.Entry<Integer, IntSet> it2 : it.entrySet()) {
      Integer it2_first = it2.getKey();
      IntegerPair t = alphabet.decode(it2_first);
      for (Integer target_state : it2.getValue()) {
        String l = alphabet.getSymbol(t.first);
        String r = alphabet.getSymbol(t.second);
        if (visited[target_state]) {
          out.println("__CYCLE__ "+  left+l + ":" + right+r);
          return;
        }
        showLtExpandish(alphabet, out, target_state, visited, left+l, right+r);
      }
    }
    visited[state] = false;
  }

  public void xshowLtExpandish(Alphabet alphabet, PrintStream out) {
    joinFinals();
    boolean[] visited = new boolean[transitions.size()];
    showLtExpandish(alphabet, out, 0, visited, "", "");
  }




  private void trim(Alphabet alphabet, int state, boolean[] visited, String left, String right, BasicFSTProcessor bil, State bilstate) {
    //System.out.println(left + ":" + right + "  "+bilstate.isFinal()+" "+bilstate.size());
    visited[state] = true;

    Map<Integer, IntSet> it = transitions.get(state);
    for (Iterator<Map.Entry<Integer, IntSet>> iter2 = it.entrySet().iterator(); iter2.hasNext();) {
      Map.Entry<Integer, IntSet> it2 = iter2.next();
      Integer it2_first = it2.getKey();
      IntegerPair t = alphabet.decode(it2_first);

      String l = alphabet.getSymbol(t.first);
      String r = alphabet.getSymbol(t.second);
      String left2 = left + l;
      String right2 = right + r;
      int outsym = t.second>=0?t.second : bil.alphabet.cast(r);
      System.out.println("rsym = "+outsym + ", da r="+r);
      State newbilstate = bilstate.copy();
      newbilstate.step(outsym);
      if (newbilstate.isFinal()) {
        if (DEBUG) System.out.println("Keep: " +left2 + ":" + right2);
        continue;
      }
      if (newbilstate.size()==0) {
        if (DEBUG) {
          System.out.println("Trim: " +left2 + ELLIPSIS+":" + right2+ELLIPSIS);
        }
        
        iter2.remove(); // =it.remove(it2_first);
        continue;
      }


      for (Integer target_state : it2.getValue()) {
        if (visited[target_state]) {
          if (DEBUG) System.out.println("Keepc: " +left2 + ":" + right2); // Cyclic, probably a regular expression
          return;
        }
        if (finals.contains(target_state)) {
          if (DEBUG) System.out.println("Keepf: " +left2 + ":" + right2);
          return;
        }


        trim(alphabet, target_state, visited, left2, right2, bil, newbilstate);
      }
    }
    visited[state] = false;
  }

}
