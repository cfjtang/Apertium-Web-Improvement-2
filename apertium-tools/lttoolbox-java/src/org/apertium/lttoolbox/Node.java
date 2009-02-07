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

import org.apertium.lttoolbox.Dest;

import java.util.List;
import java.util.Vector;

public class Node {

  /**
   * The outgoing transitions of this node.
   * Schema: (input symbol, (output symbol, destination))
   */
  List<Dest> transitions;

  Node() {
  }

  Node(Node n) {
    copy(n);
  }

  void copy(Node n) {
    transitions = n.transitions;
  }

  void addTransition(int i, int o, Node d) {
    Dest aux = transitions.get(i);

    aux.size++;
    List<Integer> out_tag = new Vector<Integer>(aux.size);
    List<Node> dest = new Vector<Node>(aux.size);

    for (int it = 0; it < aux.size - 1; it++) {
      out_tag.set(it, aux.out_tag.get(it));
      dest.set(it, aux.dest.get(it));
    }

    out_tag.set(aux.size - 1, o);
    dest.set(aux.size - 1, d);
    aux.out_tag = out_tag;
    aux.dest = dest;
  }

}