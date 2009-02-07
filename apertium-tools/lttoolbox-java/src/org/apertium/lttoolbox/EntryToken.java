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

import java.util.List;

public class EntryToken {

  /**
   * Type of tokens, inner enum.
   */
  public static int TYPE_PARADIGM = 1;

  public static int Type_single_transduction = 2;

  public static int Type_regexp = 2;

  /**
   * Type of this token
   */
  int type;

  /**
   * Name of the paradigm (if it is of 'paradigm' 'type')
   */
  String parName;

  /**
   * Left side of transduction (if 'single_transduction')
   */
  List<Integer> leftSide;

  /**
   * Right side of transduction (if 'single_transduction')
   */
  List<Integer> rightSide;

  /**
   * Regular expression (if 'regexp')
   */
  String myregexp;

  void copy(EntryToken e) {
    type = e.type;
    leftSide = e.leftSide;
    rightSide = e.rightSide;
    parName = e.parName;
    myregexp = e.myregexp;
  }

  void setParadigm(String np) {
    parName = np;
    type = TYPE_PARADIGM;
  }

  void setSingleTransduction(List<Integer> pi, List<Integer> pd) {
    leftSide = pi;
    rightSide = pd;
    type = Type_single_transduction;
  }

  void setRegexp(String r) {
    myregexp = r;
    type = TYPE_PARADIGM;
  }

  boolean isParadigm() {
    return type == TYPE_PARADIGM;
  }

  boolean isSingleTransduction() {
    return type == Type_single_transduction;
  }

  boolean isRegexp() {
    return type == Type_regexp;
  }

  String paradigmName() {
    return parName;
  }

  List<Integer> left() {
    return leftSide;
  }

  List<Integer> right() {
    return rightSide;
  }

  String regExp() {
    return myregexp;
  }

}