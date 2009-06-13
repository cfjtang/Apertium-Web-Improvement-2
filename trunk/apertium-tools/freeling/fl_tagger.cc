///
// To compile:  
//
// Copy to the directory freeling/src/utilities and run
//
//   /bin/sh ../../libtool --tag=CXX   --mode=link g++  -O3 -Wall  -L$prefix/lib -L/usr/lib -o fl-tagger fl_tagger.o -lcfg+ -lmorfo -lfries -lomlet -lpcre  -ldb_cxx 
//   g++ -O3 -Wall -o fl-tagger fl_tagger.o  -L$prefix/lib -L/usr/lib -lcfg+ $prefix//lib/libmorfo.so $prefix//lib/libfries.so $prefix//lib/libomlet.so -lpcre -ldb_cxx   -Wl,--rpath -Wl,$prefix//lib -Wl,--rpath -Wl,$prefix//lib
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
PrintResults (list<sentence> &ls)
{
  word::const_iterator ait;
  sentence::const_iterator w;
  list < sentence >::iterator is;
  int nsentence = 0;

  for (is = ls.begin (); is != ls.end (); is++, ++nsentence) {

      for (w = is->begin (); w != is->end (); w++) {
	cout << w->get_form ();
	
	  for (ait = w->selected_begin (); ait != w->selected_end (); ait++) {
	    if (ait->is_retokenizable ()) {
	      list < word > rtk = ait->get_retokenizable ();
	      list < word >::iterator r;
	      string lem, par;
	      for (r = rtk.begin (); r != rtk.end (); r++) {
		lem = lem + "+" + r->get_lemma ();
		par = par + "+" + r->get_parole ();
	      }
	      cout << " " << lem.substr (1) << " " 
		   << par.substr (1) << " " << ait->get_prob ();
	    }
	    else {
	      cout << " " << ait->get_lemma () << " " << ait->
		get_parole () << " " << ait->get_prob ();
	    }
	  }
	cout << endl;	
      }
    // sentence separator: blank line.
    cout << endl;
  }
}

void
ProcessSplitted (POS_tagger * tagger)
{
  string text, form, lemma, tag, sn, spr;
  sentence av;
  double prob;
  list < sentence > ls;
  unsigned long totlen = 0;

  while (std::getline (std::cin, text))
    {

      if (text != "")
	{			// got a word line
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
	      while (sin >> lemma >> tag >> spr)
		{
		  analysis an (lemma, tag);
		  prob = util::string2double (spr);
		  an.set_prob (prob);
		  w.add_analysis (an);
		}

	  // append new word to sentence
	  av.push_back (w);
	}
      else
	{			// blank line, sentence end.
	  totlen += 2;

	  ls.push_back (av);

	    tagger->analyze (ls);

	  PrintResults (ls);

	  av.clear ();		// clear list of words for next use
	  ls.clear ();		// clear list of sentences for next use
	}
    }

  // process last sentence in buffer (if any)
  ls.push_back (av);		// last sentence (may not have blank line after it)

  tagger->analyze (ls);

  PrintResults (ls);
}





//---------------------------------------------
// Sample main program
//---------------------------------------------
int
main (int argc, char **argv)
{
  POS_tagger *tagger = NULL;

  if(argc < 2) { 
    cout << "fl-tagger" << endl;
    cout << "Usage: fl-tagger [relax file]" << endl;
    cout << endl; 
    return 1;
  }
 
//TaggerRelaxFile=constr_gram.dat
//TaggerRelaxMaxIter=500
//TaggerRelaxScaleFactor=670.0
//TaggerRelaxEpsilon=0.001
//TaggerRetokenize=no
//TaggerForceSelect=retok

  tagger = new relax_tagger (argv[1], 500, 670.0, 0.001, false, true);

  ProcessSplitted (tagger);

  // clean up. Note that deleting a null pointer is a safe (yet useless) operation
  delete tagger;

  return 0;
}
