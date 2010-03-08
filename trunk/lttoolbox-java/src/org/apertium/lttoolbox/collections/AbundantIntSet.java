/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.apertium.lttoolbox.collections;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * A set where perhaps 10-20% of the integers from 0 to a given max is present.
 * contains() method must be fast
 * @author Jacob Nordfalk
 */
public class AbundantIntSet implements IntSet { //extends TreeSet<Integer> {
  private BitSet bs = new BitSet(4096);
  private int size = 0;

  public AbundantIntSet() {
  }

  public void clear() {
    bs.clear();
  }

  public int size() {
    return size;
  }

  public void add(int state) {
    bs.set(state);
    size++;
  }

  public void remove(int state) {
    bs.clear(state);
    size--;
  }

  public boolean contains(int state) {
    return bs.get(state);
  }

  public int firstInt() {
    return bs.nextSetBit(0);
  }

  public int next(int i) {
    return bs.nextSetBit(i);
  }

  /** Iterators work thru Integer objects, therefore its not recommended to use them */
  @Deprecated
  @Override
  public Iterator<Integer> iterator() {
    return new Iterator() {
      int i=0;
      @Override
      public boolean hasNext() {
        return bs.nextSetBit(i)==-1;
      }

      @Override
      public Object next() {
        return (i=bs.nextSetBit(i));
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException("Not supported.");
      }

    };
  }

  @Override
  public boolean addAll(Collection<? extends Integer> c) {
    Iterator<? extends Integer> e = c.iterator();
    while (e.hasNext()) {
       add(e.next());
    }
    return true;
  }

  @Override
  public boolean addAll(IntSet c) {
    Iterator<? extends Integer> e = c.iterator();
    while (e.hasNext()) {
       add(e.next());
    }
    return true;
  }
}
