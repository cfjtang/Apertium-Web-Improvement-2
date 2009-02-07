package org.apertium.lttoolbox;

import java.util.List;
import java.util.Vector;

/**
 * Created by Nic Cottrell, Jan 27, 2009 4:42:10 PM
 */

public class Dest {

  int size;

  List<Integer> out_tag;

  List<Node> dest;

  void copy(Dest d) {
    size = d.size;
    out_tag = new Vector<Integer>(d.out_tag);
    dest = new Vector<Node>(dest);
  }

  void init() {
    size = 0;
    out_tag = new Vector<Integer>();
    dest = new Vector<Node>();
  }

 public Dest() {
    init();
  }

 public Dest(Dest d) {
    init();
    copy(d);
  }

}
