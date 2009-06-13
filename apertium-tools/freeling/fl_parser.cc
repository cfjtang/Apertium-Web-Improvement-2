///
// To compile:  
//
// Copy to the directory freeling/src/utilities and run
//
//   /bin/sh ../../libtool --tag=CXX   --mode=link g++  -O3 -Wall  -L$prefix/lib -L/usr/lib -o fl-parser fl_parser.o -lcfg+ -lmorfo -lfries -lomlet -lpcre  -ldb_cxx 
//   g++ -O3 -Wall -o fl-parser fl_parser.o  -L$prefix/lib -L/usr/lib -lcfg+ $prefix//lib/libmorfo.so $prefix//lib/libfries.so $prefix//lib/libomlet.so -lpcre -ldb_cxx   -Wl,--rpath -Wl,$prefix//lib -Wl,--rpath -Wl,$prefix//lib
//
// Where $prefix is your prefix set with --prefix /usr/local by default
//
//////////////////////////////////////////////////////////////////
//
//    FreeLing - Open Source Language Analyzers
//
//    Copyright (C) 2004   TALP Research Center
//                         Universitat Politecnica de Catalunya
//
//    This library is free software; you can redistribute it and/or
//    modify it under the terms of the GNU General Public
//    License as published by the Free Software Foundation; either
//    version 2.1 of the License, or (at your option) any later version.
//
//    This library is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//    General Public License for more details.
//
//    You should have received a copy of the GNU General Public
//    License along with this library; if not, write to the Free Software
//    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//
//    contact: Lluis Padro (padro@lsi.upc.es)
//             TALP Research Center
//             despatx C6.212 - Campus Nord UPC
//             08034 Barcelona.  SPAIN
//
////////////////////////////////////////////////////////////////

using namespace std;

#include <sstream>
#include <iostream>

#include <map>
#include <vector>

/// headers to call freeling library
#include "freeling.h"

void 
PrintDepTree (dep_tree::iterator n, int depth, const document &doc=document()) 
{
  dep_tree::sibling_iterator d, dm;
  int last, min;
  bool trob;

  cout << string (depth*2, ' ');

  cout << n->info.get_link()->info.get_label(); 
  cout <<"/" << n->info.get_label() << "/";

  word w = n->info.get_word();
  cout << "(" << w.get_form() << " " << w.get_lemma() << " " << w.get_parole ();
  cout << ")";
  
  if (n->num_children () > 0) {
    cout << " [" << endl;
    
    // Print Nodes
    for (d = n->sibling_begin (); d != n->sibling_end (); ++d)
      if (!d->info.is_chunk ())
        PrintDepTree (d, depth + 1, doc);
    
    // print CHUNKS (in order)
    last = 0;
    trob = true;
    while (trob) {
      // while an unprinted chunk is found look, for the one with lower chunk_ord value
      trob = false;
      min = 9999;
      for (d = n->sibling_begin (); d != n->sibling_end (); ++d) {
        if (d->info.is_chunk ()) {
          if (d->info.get_chunk_ord () > last
              && d->info.get_chunk_ord () < min) {
            min = d->info.get_chunk_ord ();
            dm = d;
            trob = true;
          }
        }
      }
      if (trob)
        PrintDepTree (dm, depth + 1, doc);
      last = min;
    }
    
    cout << string (depth * 2, ' ') << "]";
  }
  cout << endl;
}


void
PrintResults (list<sentence> &ls, const document &doc=document())
{
  word::const_iterator ait;
  sentence::const_iterator w;
  list < sentence >::iterator is;
  int nsentence = 0;

  for (is = ls.begin (); is != ls.end (); is++, ++nsentence) {
      dep_tree & dep = is->get_dep_tree ();
      PrintDepTree (dep.begin (), 0, doc);
      cout << endl;
  }
}


void
ProcessSplitted (chart_parser *parser, dependency_parser * dep, const document &doc=document())
{
  string text, form, lemma, tag, sn, spr;
  sentence av;
  list < sentence > ls;
  unsigned long totlen = 0;

  while (std::getline (std::cin, text))
    {
      if (text != "")
        {                        // got a word line
          istringstream sin;
          sin.str (text);
          // get word form
          sin >> form;

          // build new word
          word w (form);
          w.set_span (totlen, totlen + form.size ());
          totlen += text.size () + 1;

          // process word line, according to input format.
          // add all analysis in line to the word.
          w.clear ();

              sin >> lemma >> tag;
              analysis an (lemma, tag);
              an.set_prob (1.0);
              w.add_analysis (an);


          // append new word to sentence
          av.push_back (w);
        }
      else
        {                        // blank line, sentence end.
          totlen += 2;

          ls.push_back (av);

            parser->analyze (ls);
            dep->analyze (ls);

          PrintResults (ls, doc);

          av.clear ();                // clear list of words for next use
          ls.clear ();                // clear list of sentences for next use
        }
    }

  // process last sentence in buffer (if any)
  ls.push_back (av);                // last sentence (may not have blank line after it)

    parser->analyze (ls);
    dep->analyze (ls);

  PrintResults (ls, doc);
}




//---------------------------------------------
// Sample main program
//---------------------------------------------
int
main (int argc, char **argv)
{
  dependency_parser *dep = NULL;
  chart_parser *parser = NULL;
  document doc;

  if(argc < 3) { 
    cout << "fl-parser" << endl;
    cout << "Usage: fl-parser [chunk grammar] [dep grammar]" << endl;
    cout << endl; 
    return 1;
  }
 
//DepParser=txala
//DepTxalaFile=$FREELINGSHARE/es/dep/dependences.dat

  parser = new chart_parser(argv[1]);

  dep = new dep_txala (argv[2], parser->get_start_symbol ());

  ProcessSplitted (parser, dep, doc);

  // clean up. Note that deleting a null pointer is a safe (yet useless) operation
  delete dep;

  return 0;
}
