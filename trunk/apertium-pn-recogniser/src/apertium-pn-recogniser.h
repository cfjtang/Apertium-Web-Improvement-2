//////////////////////////////////////////////////////////////////
//
//    
//    Apertium-pn-recogniser - Proper Noun Recogniser for Apertium 
//
//    Copyright (C) 2009   TALP Research Center
//                         Universitat Politecnica de Catalunya
//
//    Copyright (C) 2009   Transducens
//                         Universitat d'Alacant
//
//    This program is free software; you can redistribute it and/or
//    modify it under the terms of the GNU General Public
//    License as published by the Free Software Foundation; either
//    version 2.1 of the License, or (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
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


#ifndef _NER
#define _NER

#include <string>
#include <list>
#include <vector>
#include <set>
#include <map>

////////////////////////////////////////////////////////////////
///   Class word stores all info related to a word: 
///  form, list of analysis, list of tokens (if multiword).
////////////////////////////////////////////////////////////////

class word {
   private:
     /// lexical form
     std::wstring form;                 
     /// lemma
     std::wstring lemma;
     /// PoS tag
     std::wstring parole;
     /// empty list if not a multiword
     std::list<word> multiword;
     /// unknown in apertium 
     bool unknown;
     /// locked by apertium
     bool locked;

   public:
      /// constructor
      word();
      /// constructor
      word(const std::wstring &);
      word(const std::wstring &, const std::wstring &, const std::wstring &);
      /// constructor
      word(const std::wstring &, const std::list<word> &);

      /// Copy constructor
      word(const word &);
      /// assignment
      word& operator=(const word&);

      /// true iff the word is a multiword compound
      bool is_multiword(void) const;
      /// get number of words in compound
      int get_n_words_mw(void) const;
      /// get word objects that compound the multiword
      std::list<word> get_words_mw(void) const;

      void set_unknown(bool);
      bool is_unknown() const;

      void set_locked(bool);
      bool is_locked() const;

      /// get word form
      std::wstring get_form(void) const;
      /// get lemma for the selected analysis in list
      std::wstring get_lemma(void) const;
      /// get parole for the selected analysis  
      std::wstring get_parole(void) const;

      /// set word form
      void set_form(const std::wstring &);
      void set_lemma(const std::wstring &);
      void set_parole(const std::wstring &);      
};



////////////////////////////////////////////////////////////////
///   Class sentence is just a list of words that someone
/// (the splitter) has validated it as a complete sentence.
/// It may include a parse tree.
////////////////////////////////////////////////////////////////

class sentence : public std::vector<word> {};



////////////////////////////////////////////////////////////////
///  The class ner implements a dummy proper noun recognizer.
////////////////////////////////////////////////////////////////

#define MAX_STATES 20
#define MAX_TOKENS 10

class ner {
  
  private: 
    /// state code of initial state
    int initialState;
    /// state code for stop State
    int stopState;
    /// Transition tables
    int trans[MAX_STATES][MAX_TOKENS];
    /// set of final states
    std::set<int> Final;
    
    /// set of function words
    std::set<std::wstring> func;
    /// set of special punctuation tags
    std::set<std::wstring> punct;
    /// set of words to be considered possible NEs at sentence beggining
    std::set<std::wstring> names;
    /// set of words/tags to be ignored as NE parts, even if they are capitalized
    std::map<std::wstring,int> ignore_tags;
    std::map<std::wstring,int> ignore_words;

    /// length beyond which a multiword made of all capitialized words ("WRECKAGE: TITANIC 
    /// DISAPPEARS IN NORTHERN SEA") will be considered a title and not a proper noun.
    /// A value of zero deactivates this behaviour.
    unsigned int Title_length;
    
    /// Tag to assign to detected NEs
    std::wstring NE_tag;
    
    /// it is a noun at the beggining of the sentence
    bool initialNoun;

    /// list of tags that behave like a Noun or Adj at sentence begin
    std::set<std::wstring> NounAdj;
    /// list of tags that are considered closed categories
    std::set<std::wstring> ClosedCats;
    /// list of tags that are non-words.
    std::set<std::wstring> DateNumPunct;

    int ComputeToken(int,sentence::iterator &, sentence &);
    void ResetActions();
    void StateActions(int, int, int, sentence::const_iterator);
    void SetMultiwordAnalysis(sentence::iterator, int);
    bool ValidMultiWord(const word &);
    sentence::iterator BuildMultiword(sentence &, sentence::iterator,sentence::iterator, int, bool &);
  public:
    /// Constructor
    ner(const std::string &); 
    // Destructor
    ~ner();
    /// Detect patterns in sentence
    void annotate(sentence &);
};



#endif

