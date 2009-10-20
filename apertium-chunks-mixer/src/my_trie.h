/*
 * Copyright (C) 2009 Universitat d'Alacant / Universidad de Alicante
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
#ifndef __MY_TRIE_H_
#define __MY_TRIE_H_

using namespace std;

#include <string>
#include <vector>
#include <iostream>
#include <map>

//Forward declaration
template <typename TKey, typename TData> class MyTrie;
template <typename TKey, typename TData> ostream& operator << (ostream& os, const MyTrie<TKey, TData>& trie);

template <typename TKey, typename TData>
class MyTrie {
private:
  bool has_key;
  TKey key_value;
  int data_length;

  int state_id;
  static int id_counter;

  //First element of the pair contains the label or the arc, 
  //Second element contains a pointer to another Trie.

  map<TData, MyTrie<TKey, TData>* > childs;

public: 

  MyTrie();

  MyTrie(const MyTrie<TKey, TData> &tr);

  ~MyTrie();

  //Returns false if data is already in the trie
  bool insert(TKey key, const vector<TData> &data, unsigned i_data=0);

  //Returns true if data is found in the trie, key is returned via the key parameter
  //If not found, key is not modified
  bool find(const vector<TData> &data, TKey &key, unsigned i_data=0);

  friend ostream& operator << <TKey,TData>(ostream& os, const MyTrie<TKey, TData>& trie);
  
  friend class SentenceTranslator;
};

///////////////////////////////////////////////////////////////////

template <typename TKey, typename TData> int MyTrie<TKey, TData>::id_counter=0;

template <typename TKey, typename TData>
  MyTrie<TKey, TData>::MyTrie() {

  has_key=false;
  state_id=id_counter++;
  data_length=0;
}

template <typename TKey, typename TData>
  MyTrie<TKey, TData>::MyTrie(const MyTrie<TKey, TData> &tr) {

  key_value=tr.key_value;
  has_key=tr.has_key;
  state_id=tr.state_id;
  data_length=tr.data_length;

  typename map<TData, MyTrie<TKey, TData>* >::iterator it_childs;
  for(it_childs=tr.childs.begin(); it_childs!=tr.childs.end(); it_childs++) 
    childs[it_childs->first] = new MyTrie<TKey, TData>(*(it_childs->second));
}

template <typename TKey, typename TData>
  MyTrie<TKey, TData>::~MyTrie() {

  typename map<TData, MyTrie<TKey, TData>* >::iterator it_childs;
  for(it_childs=childs.begin(); it_childs!=childs.end(); it_childs++)
    delete it_childs->second;
}

template <typename TKey, typename TData> bool  
  MyTrie<TKey, TData>::insert(TKey key, const vector<TData> &data, unsigned i_data) {

  if (i_data==data.size()) {
    bool return_value=!has_key;
    has_key=true;
    key_value=key;
    data_length=data.size();
    return return_value;
  }

  if (childs.find(data[i_data]) != childs.end()) {
    //Tells the child to insert the remaining data
    MyTrie<TKey, TData> *child_trie=childs[data[i_data]];
    //cerr<<"  ask the child to insert remaining data, mem. address: "<<child_trie<<endl;
    return child_trie->insert(key, data, ++i_data);
  }

  MyTrie<TKey, TData> *new_trie=new MyTrie<TKey, TData>();

  if (new_trie == NULL) {
    cerr<<"Error: Cannot allocate memory for new MyTrie, token: '"<<data[i_data]<<"' of key '";
    for(unsigned i=0; i<data.size(); i++) {
      if (i==i_data)
        cerr<<"["<<data[i]<<"]";
      else
        cerr<<data[i];
    }
    cerr<<"'\n";
  }

  new_trie->insert(key, data, i_data+1);

  childs[data[i_data]]=new_trie;

  return true;
}

template <typename TKey, typename TData> bool  
  MyTrie<TKey, TData>::find(const vector<TData> &data, TKey &key, unsigned i_data) {

  if (i_data==data.size()) {
    if (has_key)
      key=key_value;
    return has_key;
  }

  if (childs.find(data[i_data]) != childs.end()) {
    //Tells the child to look for the remaining data
    MyTrie<TKey, TData> *child_trie=childs[data[i_data]];
    return child_trie->find(data, key, ++i_data);
  }

  //If execution reach this point is because data is not in the trie
  return false;
}

template <typename TKey, typename TData>
  ostream& operator << (ostream& os, const MyTrie<TKey, TData>& trie) {

  os<<"( ID:"<<trie.state_id<<" ";

  if (trie.has_key) 
    os<<"KEY:"<<trie.key_value<<"; LENGTH:"<<trie.data_length<<" ";

  typename map<TData, MyTrie<TKey, TData>* >::const_iterator it_childs;
  for(it_childs=trie.childs.begin(); it_childs!=trie.childs.end(); it_childs++) 
    os<<it_childs->first<<" "<<*(it_childs->second);

  os<<") ";

  return os;
}

#endif
