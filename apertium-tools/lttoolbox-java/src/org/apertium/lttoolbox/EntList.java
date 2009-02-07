package org.apertium.lttoolbox;

import java.util.Vector;
import java.util.Collection;

/**
 * Created by Nic Cottrell, Jan 27, 2009 10:55:24 PM
 */

public class EntList extends Vector<SPair> {

  public EntList() {

  }

  public EntList(Collection<? extends SPair> sPairs) {
    super(sPairs);
  }
  
}
