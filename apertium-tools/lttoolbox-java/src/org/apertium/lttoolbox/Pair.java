package org.apertium.lttoolbox;

/**
 * Created by Nic Cottrell, Jan 27, 2009 6:05:38 PM
 */

public class Pair<P,Q> {

  public P first;

  public Q second;

  public Pair(P obj1, Q obj2) {
    this.first = obj1;
    this.second = obj2;
  }

  public P getFirst() {
    return first;
  }

  public void setFirst(P first) {
    this.first = first;
  }

  public Q getSecond() {
    return second;
  }

  public void setSecond(Q second) {
    this.second = second;
  }
}
