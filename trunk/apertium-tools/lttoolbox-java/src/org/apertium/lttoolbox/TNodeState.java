package org.apertium.lttoolbox;

import org.apertium.lttoolbox.Node;

import java.util.List;

/**
 * Created by Nic Cottrell, Jan 27, 2009 4:39:03 PM
 */

public class TNodeState {

  Node where;

  List<Integer> sequence;

  boolean dirty;

  public TNodeState(Node w, List<Integer> s, boolean dirty) {
    this.where = w;
    this.sequence = s;
    this.dirty = dirty;
  }

  public TNodeState(TNodeState other) {
    this.where = other.where;
    this.sequence = other.sequence;
    this.dirty = other.dirty;
  }
  
}
