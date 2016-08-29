/*
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
 * along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
#ifndef _CONSTANTMANAGER_
#define _CONSTANTMANAGER_

#include <cstdio>
#include <map>
#include <string>

using namespace std;

class ConstantManager
{
private:
  map<wstring, int> constants; 

  void copy(ConstantManager const &o);
  void destroy();
public:
  ConstantManager();
  ~ConstantManager();
  ConstantManager(ConstantManager const &o);
  ConstantManager & operator =(ConstantManager const &o);
  
  void setConstant(wstring const &constant, int const value);
  int getConstant(wstring const &constant);
  void write(FILE *output);
  void read(FILE *input);
  void serialise(std::ostream &serialised) const;
  void deserialise(std::istream &serialised);
};

#endif
