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


#ifdef VERBOSE 
#define TRACE(x) wcerr<<x<<endl
  #define TRACE_SENTENCE(s)  {for (sentence::iterator wd=s.begin(); wd!=s.end(); wd++) {wcerr<<"  "<<wd->get_form()<<" "<<wd->get_lemma()<<" "<<wd->get_parole()<<endl;};}
#else
  #define TRACE(x)
  #define TRACE_SENTENCE(s)
#endif
#define ABORT(x) {cerr<<x<<endl; exit(1);}

//-----------------------------------------------//
//       Proper noun recognizer for APERTIUM     //
//-----------------------------------------------//

#include <cstdlib>  
#include <fstream>  
#include <sstream>  
#include <iostream>  
#include <wctype.h>
#include "apertium-pn-recogniser.h"

using namespace std;

///--------- WORD and SENTENCE methods -------- ///

/// Create an empty new word
word::word() {}  
/// Create a new word with given form.
word::word(const wstring &f) { form=f; locked=false; unknown=false; }  
/// Create a new word with given form, lemma, tag
word::word(const wstring &f, const wstring &l, const wstring &t) { 
  form=f; lemma=l; parole=t;  locked=false; unknown=false;
}  
/// Create a new multiword, including given words.
word::word(const std::wstring &f, const std::list<word> &a) {
  form=f; multiword=a;
  locked=false; unknown=false;
}
/// Copy constructor.
word::word(const word & w) {
  form=w.form; multiword=w.multiword;
  lemma=w.lemma; parole=w.parole;
  locked=w.locked;
  unknown=w.unknown;  
};
/// Assignment.
word& word::operator=(const word& w){
  if(this!=&w) {
    form=w.form; multiword=w.multiword;  
    lemma=w.lemma; parole=w.parole;
    locked=w.locked;
    unknown=w.unknown;  
  }
  return *this;
};

/// Check whether the word is a compound.
bool word::is_multiword(void) const {return(!multiword.empty());}
/// Get number of words in compound.
int word::get_n_words_mw(void) const {return(multiword.size());}
/// Get list of words in compound.
list<word> word::get_words_mw(void) const {return(multiword);}

void word::set_unknown(bool u) {unknown=u;}
bool word::is_unknown() const { return(unknown);}
void word::set_locked(bool l) {locked=l;}
bool word::is_locked() const { return(locked);}

/// get word form
wstring word::get_form(void) const {return (form);}
/// Get lemma value for analysis.
wstring word::get_lemma(void) const {return(lemma);}
/// Get parole PoS tag value for analysis.
wstring word::get_parole(void) const {return(parole);}

/// set word form
void word::set_form(const wstring &f) {form=f;};
/// Set lemma for analysis.
void word::set_lemma(const wstring &l) {lemma=l;}
/// Set parole PoS tag for analysis
void word::set_parole(const wstring &p) {parole=p;}


// --------- UTILITIES ------------ //


/////////////////////////////////////////////////////////////////////////////
/// Lowercase an string, possibly with accents.

wstring lowercase(const wstring &p) {
  wstring::iterator pos;
  wstring s=p;
  for (pos=s.begin(); pos!=s.end(); pos++) {
    if (iswalpha(*pos)) *pos=towlower(*pos);
  }           
  return(s);
}

/////////////////////////////////////////////////////////////////////////////
/// Find out whether a string contains lowercawse chars

bool has_lowercase(const wstring &p) {
  wstring::const_iterator pos;
  bool lc=false;
  for (pos=p.begin(); pos!=p.end() && !lc; pos++) {
    lc =  iswlower(*pos);
  }           
  return(lc);
}


///--------- NER class methods -------- ///

// State codes
// recognize named entities (NE).
#define IN 1   // initial
#define NP 2   // Capitalized word, much likely a NE component
#define FUN 3  // Functional word inside a NE
#define STOP 4

// Token codes
#define TK_sUnkUpp  1   // non-functional, unknown word, begining of sentence, capitalized, with no analysis yet
#define TK_sNounUpp 2   // non-functional, known as noun, begining of sentence, capitalized, with no analysis yet
#define TK_mUpper 3     // capitalized, not at beggining of sentence
#define TK_mFun  4   // functional word, non-capitalized, not at beggining of sentence
#define TK_other 5



///////////////////////////////////////////////////////////////
///     Create a proper noun recognizer
///////////////////////////////////////////////////////////////

ner::ner(const std::string &npFile) {
  int reading;

  wstring line; 
  wifstream fabr(npFile.c_str());
  if (!fabr) ABORT("ERROR: Could not open file "<<npFile);
  fabr.imbue(std::locale(""));

  // default
  Title_length = 0;

  // load list of functional words that may be included in a NE.
  reading=0;
  while (getline(fabr,line)) {
    TRACE(reading<<L" CONFIG ["<<line<<L"]");
    if (line == L"<FunctionWords>") reading=1;
    else if (line == L"</FunctionWords>") reading=0;
    else if (line == L"<SpecialPunct>") reading=2;
    else if (line == L"</SpecialPunct>") reading=0;
    else if (line == L"<TitleLimit>") reading=4;
    else if (line == L"</TitleLimit>") reading=0;
    else if (line == L"<Names>") reading=5;
    else if (line == L"</Names>") reading=0;
    else if (line == L"<Ignore>") reading=6;
    else if (line == L"</Ignore>") reading=0;
    else if (line == L"<NounAdj>") reading=7;
    else if (line == L"</NounAdj>") reading=0;
    else if (line == L"<ClosedCats>") reading=8;
    else if (line == L"</ClosedCats>") reading=0;
    else if (line == L"<DateNumPunct>") reading=9;
    else if (line == L"</DateNumPunct>") reading=0;
    else if (reading==1)  // reading Function words
      func.insert(line);
    else if (reading==2)   // reading special punctuation tags
      punct.insert(line); 
    else if (reading==4) { // reading value for Title_length 
      wistringstream sin; sin.str(line);
      sin>>Title_length;
    }
    else if (reading==5)  // reading list of words to consider names when at line beggining
      names.insert(line);
    else if (reading==6) { // reading list of words to ignore as possible NEs, even if they are capitalized
      wistringstream sin; sin.str(line);
      wstring key; int type;
      sin>>key>>type;
      if (iswupper(key[0])) ignore_tags.insert(make_pair(lowercase(key),type));
      else ignore_words.insert(make_pair(key,type));
    }
    else if (reading==7) {
      NounAdj.insert(line);
    }
    else if (reading==8) {
      ClosedCats.insert(line);
    }
    else if (reading==9) {
      DateNumPunct.insert(line);
    }
  }
  fabr.close(); 
  
  // Initialize special state attributes
  initialState=IN; stopState=STOP;
  // Initialize Final state set 
  Final.insert(NP); 
  
  // Initialize transitions table. By default, stop state
  int s,t;
  for(s=0;s<MAX_STATES;s++) for(t=0;t<MAX_TOKENS;t++) trans[s][t]=STOP;
  
  // Initializing transitions table
  // State IN
  trans[IN][TK_sUnkUpp]=NP; trans[IN][TK_sNounUpp]=NP; trans[IN][TK_mUpper]=NP;
  // State NP
  trans[NP][TK_mUpper]=NP;
  trans[NP][TK_mFun]=FUN;
  // State FUN
  trans[FUN][TK_sUnkUpp]=NP; trans[FUN][TK_sNounUpp]=NP; trans[FUN][TK_mUpper]=NP;
  trans[FUN][TK_mFun]=FUN;

  TRACE(L"analyzer succesfully created");
}

/// Destructor
ner::~ner() {};


///////////////////////////////////////////////////////////////
///  Compute the right token code for word j from given state.
///////////////////////////////////////////////////////////////

int ner::ComputeToken(int state, sentence::iterator &j, sentence &se)
{
  wstring form, formU, tag;
  int token;
  bool sbegin;
  
  formU = j->get_form();
  form = lowercase(formU);

  tag=j->get_parole();
  if (!tag.empty()) tag=tag.substr(1,tag.find(L'>')-1);

  token = TK_other;
  
  // if apertium multiword, is not NE
  if (j->is_locked()) return token;
  
  if (j==se.begin()) {
    // we are the first word in sentence
    sbegin=true;
  }
  else {
    // not the first, locate previous word...
    sentence::const_iterator ant=j; ant--; 
    // ...and check whether it has any of the listed punctuation tags 
    sbegin=false;    
    sbegin = (punct.find(j->get_form())!=punct.end());    
  }
  
  // set ignore flag:  
  //   0= non-ignorable; 1= ignore only if no capitalized neighbours; 2= ignore always
  int ignore=0;
  // check if ignorable word
  map<wstring,int>::const_iterator it=ignore_tags.end();
  map<wstring,int>::const_iterator iw=ignore_words.find(form);
  if (iw!=ignore_words.end()) 
    ignore=iw->second+1;
  else {
    // check if any of word tags are ignorable
    it = ignore_tags.find(tag);
    if (it!=ignore_tags.end()) ignore=it->second+1;
  }
  
  if (ignore==1) {
    TRACE(L"Ignorable word ("+form+L":"+tag+(it!=ignore_tags.end()?L","+it->first+L")":L")")+L". Ignore=0");
    if (state==NP) 
      token=TK_mUpper;  // if inside a NE, do not ignore
    else {
      // if not inside, only form NE if followed by another capitalized word
      sentence::iterator nxt=j; nxt++;
      if (nxt!=se.end() && iswupper(nxt->get_form()[0]))
	// set token depending on if it's first word in sentence
	token= (sbegin? TK_sNounUpp: TK_mUpper);
    }
  }
  else if (ignore==2) {
    // leave it be as TK_other (so it will be ignored)
    TRACE(L"Ignorable word ("+form+L":"+tag+(it!=ignore_tags.end()?L","+it->first+L")":L")")+L". Ignore=1");
  }
  // non-ignorable
  else if (sbegin) { 
    TRACE(L"non-ignorable word, sbegin ("+form+L":"+tag+L")");
    // first word in sentence (or word preceded by special punctuation sign)
    if (iswupper(formU[0]) &&
        func.find(form)==func.end() &&
        !j->is_multiword() && DateNumPunct.find(tag)==DateNumPunct.end()) {
      // capitalized, not in function word list, no analysis except dictionary.
      
      // check for unknown/known word
      if (j->is_unknown()) {
	// not found in dictionary
	token = TK_sUnkUpp;
      }
      else if ( ClosedCats.find(tag)==ClosedCats.end() && (NounAdj.find(tag)!=NounAdj.end() || names.find(form)!=names.end())) {
	// found as noun with no closed category
        // (prep, determiner, conjunction...) readings
	token = TK_sNounUpp;
      }
    }
  }
  else {
    TRACE(L"non-ignorable word ("+form+L":"+tag+L")");
    // non-ignorable, not at sentence beggining
    if (iswupper(formU[0]) && DateNumPunct.find(tag)==DateNumPunct.end())
      // Capitalized and not number/date
      token=TK_mUpper;
    else if (func.find(form)!=func.end())
      // in list of functional words
      token=TK_mFun;
  }
  
  TRACE(L"Next word form is: ["+formU+L"] token="<<token);
  TRACE(L"Leaving state "<<state<<L" with token "<<token); 
  return (token);
}


///////////////////////////////////////////////////////////////
///   Reset flag about capitalized noun at sentence start.
///////////////////////////////////////////////////////////////

void ner::ResetActions() 
{
  initialNoun=false;
}



///////////////////////////////////////////////////////////////
///  Perform necessary actions in "state" reached from state 
///  "origin" via word j interpreted as code "token":
///  Basically, set flag about capitalized noun at sentence start.
///////////////////////////////////////////////////////////////

void ner::StateActions(int origin, int state, int token, sentence::const_iterator j)
{

  // if we found a capitalized noun at sentence beginning, remember it.
  if (state==NP) {
    TRACE(L"actions for state NP");
    initialNoun = (token==TK_sNounUpp);
  }

  TRACE(L"State actions completed. initialNoun="<<initialNoun);
}


///////////////////////////////////////////////////////////////
///   Set the appropriate lemma and parole for the
///   new multiword.
///////////////////////////////////////////////////////////////

void ner::SetMultiwordAnalysis(sentence::iterator i, int fstate) {

  // Setting the analysis for the Named Entity. 
  // The new MW is just created, so its list is empty.
  
    // if the MW has only one word, and is an initial noun, copy its possible analysis.
  //    if (initialNoun && i->get_n_words_mw()==1) {
  //      TRACE(3,"copying first word analysis list");
  //    i->copy_analysis(i->get_words_mw().front());
  //  }
    
    TRACE(L"adding NP analysis");
    // Add an NP analysis, with the compound form as lemma.
    i->set_lemma(lowercase(i->get_form()));
    i->set_parole(L"NP");
}


///////////////////////////////////////////////////////////////
///  Perform last minute validation before effectively building multiword
///////////////////////////////////////////////////////////////

bool ner::ValidMultiWord(const word &w) {
	
  // We do not consider a valid proper noun if all words are capitalized and there 
  // are more than Title_length words (it's probably a news title, e.g. "TITANIC WRECKS IN ARTIC SEAS!!")
  // Title_length==0 deactivates this feature

  list<word> mw = w.get_words_mw();
  if (Title_length>0 && mw.size() >= Title_length) {
    list<word>::const_iterator p;
    bool lw=false;
    for (p=mw.begin(); p!=mw.end() && !lw; p++) lw=has_lowercase(p->get_form());
    // if a word with lowercase chars is found, it is not a title, so it is a valid proper noun.

    return (lw);
  }
  else
    return(true);

}

///////////////////////////////////////////////////////////////
///  Arrange the sentence grouping all words from start to end
///  in a multiword.
///////////////////////////////////////////////////////////////

sentence::iterator ner::BuildMultiword(sentence &se, sentence::iterator start, sentence::iterator end, int fs, bool &built)
{
  sentence::iterator i;
  list<word> mw;
  wstring form;

  TRACE(L"Building multiword");
  for (i=start; i!=end; i++){
    mw.push_back(*i);           
    form += i->get_form()+L"_";
    TRACE(L"added next ["+form+L"]");
  } 
  // don't forget last word
  mw.push_back(*i);           
  form += i->get_form();
  TRACE(L"added last ["+form+L"]");

  // build new word with the mw list, and check whether it is acceptable
  word w(form,mw);

  if (ValidMultiWord(w)) {  
    TRACE(L"Valid Multiword. Modifying the sentence");
    // erasing from the sentence the words that composed the multiword
    end++;
    i=se.erase(start, end);
    // insert new multiword it into the sentence
    i=se.insert(i,w); 
    TRACE(L"New word inserted");
    // Set morphological info for new MW
    SetMultiwordAnalysis(i,fs);
    built=true;
  }
  else {
    TRACE(L"Multiword found, but rejected. Sentence untouched");
    ResetActions();
    i=start;
    built=false;
  }

  return(i);
}


///////////////////////////////////////////////////////////////
///  Main loop of automaton to detect NEs
///////////////////////////////////////////////////////////////

void ner::annotate(sentence &se)
{
  sentence::iterator i,j,sMatch,eMatch; 
  int newstate, state, token, fstate;
   
  fstate=0;

  // check whether there is a match starting at each position i
  for (i=se.begin(); i!=se.end(); i++) {

    // reset automaton
    state=initialState;
    ResetActions();

    sMatch=i; eMatch=se.end();
    for (j=i;state != stopState && j!=se.end(); j++) {
      // request the child class to compute the token
      // code for current word in current state
      token = ComputeToken(state,j,se);
      // do the transition to new state
      newstate = trans[state][token];
      // let the child class perform any actions 
      // for the new state (e.g. computing date value...)
      StateActions(state, newstate, token, j);
      // change state
      state = newstate;
      // if the state codes a valid match, remember it
      //  as the longest match found so long.
      if (Final.find(state)!=Final.end()) {
        eMatch=j;
	fstate=state;
	TRACE(L"New candidate found");
      }
    }

    TRACE(L"STOP state reached. Check longest match");
    // stop state reached. find longest match (if any) and build a multiword
    if (eMatch!=se.end()) {
      TRACE(L"Match found");
      bool found;
      i=BuildMultiword(se,sMatch,eMatch,fstate,found);

      TRACE_SENTENCE(se);
    }
  }

  // Printing partial module results
  TRACE(L"Final sentence:");
  TRACE_SENTENCE(se);
}


/**
 *	Reads from stdin until it finds an instace of "character" not preceeded by '\'
 * 
 */
std::wstring readRemainingWord(const wchar_t readedBefore, const wchar_t character)
{
	wchar_t c;
	std::wstring output;
	output+=readedBefore;
	wchar_t readedB = readedBefore;
	
	c = getwchar();
	
	while( !(c==character && readedB!=L'\\') )
	{
		output+=c;
		if(readedB==L'\\')
			readedB=L'\0';
		else
			readedB=c;
		c = getwchar();
	}
	
	
	
	return output;
}


/**
 *	Reads from stdin until it finds an instace of "character" not preceeded by '\'
 * 
 */
std::wstring readRemainingWordAndCount(const wchar_t readedBefore, const wchar_t character, const wchar_t characterToCount, int &count)
{
	wchar_t c;
	std::wstring output;
	output+=readedBefore;
	wchar_t readedB = readedBefore;
	count=0;
	
	c = getwchar();
	
	while( !(c==character && readedB!=L'\\') )
	{
		if(c==characterToCount)
			count++;
		output+=c;
		if(readedB==L'\\')
			readedB=L'\0';
		else
			readedB=c;
		c = getwchar();
	}
	
	return output;
}


/// Read an Apertium word and returns:
// sup: Superficial form
// lex: First lexical form
// word: Full word
// 
// int: Number of whitespaces in sup
//  

int processWord(std::wstring &sup, std::wstring &lex)
{
  int count;
  std::wstring superficial = readRemainingWordAndCount(L'^',L'/',L' ',count);
  superficial.erase(0,1);
  std::wstring readed = readRemainingWord(L'/',L'$');
  readed.erase(0,1);
  sup=superficial;
  lex = readed;
  
  return count;
}

#define PROPER_NOUN_MARK L"*_Added_by_Proper_Noun_Detector_"

void outputSentence(const sentence &se, const vector<wstring> &blanks) {

  sentence::const_iterator w;
  int posb= 0;

  if (blanks.size()==0)
    ABORT("Fatal error: blanks vector empty in outputSentence");

  for(w=se.begin();w!=se.end();w++) {
    if (w->is_unknown()) {
      wcout<<blanks[posb++];
      wcout<<L'^'<<L'*'<<w->get_form()<<L'$';
    }
    else if (w->get_parole()==L"NP") {
      list<word> mwlist= w->get_words_mw();  // NP are multiwords (even if a single word)
      list<word>::iterator mw;
      for(mw=mwlist.begin();mw!=mwlist.end();mw++) {
        wcout<<blanks[posb++];
        wcout<<L'^'<<PROPER_NOUN_MARK<<mw->get_form()<<L'$';
      }
    }
    else {
        wcout<<blanks[posb++];
        wcout<<L'^'<<w->get_lemma()<<w->get_parole()<<L'$';
    }
  }

}


int main(int argc, char* argv[]) {

  if (argc!=2) ABORT ("ERROR - One file name  (no more, no less) is expected as a parameter");

  ios::sync_with_stdio(false);
  wcout.imbue(locale(""));
  wcerr.imbue(locale(""));
  wcin.imbue(locale(""));
  setlocale(LC_ALL, "");

  string file(argv[1]);
  ner ne_detector(file);

  vector<wstring> lblanc;
  wstring form,lex,lema,tag;
  wstring blank=L"";
  wchar_t c;
  int spaces; 
  sentence se;

  c=getwchar(); 
  while (c!=WEOF) {
    if (c==L'^') {  // new word starting
      // store accumulated interword string 
      lblanc.push_back(blank); blank=L"";
      // read word
      spaces=processWord(form,lex);

      // build FreeLing word
      if (lex[0]==L'*') {
	lema=lex;
	lema.erase(0,1);
	tag=L"";
      }
      else {
	size_t p=lex.find(L'<');
	if (p==wstring::npos) 
	  ABORT("ERROR: no tag in non-asterisked word."); 
	lema=lex.substr(0,p);
	tag=lex.substr(p);
      }
      
      // add word to sentence
      word w(form,lema,tag);
      if (spaces>0) w.set_locked(true);
      if (tag==L"") w.set_unknown(true);
      se.push_back(w);      

      if (w.get_parole()==L"<sent>") {
	ne_detector.annotate(se);
	TRACE("Sentence anotated");
	outputSentence(se,lblanc);
	se.clear();
	lblanc.clear();
      }
    }
    else {
      // entre palabras
      blank = blank + c;
    }

    c=getwchar();
  }

  wcout<<blank;
}
