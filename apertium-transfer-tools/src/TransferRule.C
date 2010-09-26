/*
 * Copyright (C) 2006-2007 Felipe Sánchez-Martínez
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


#include "TransferRule.H"
#include "Utils.H"
#include <apertium/string_utils.h>
#include <algorithm>

set<pair<wstring, wstring> > TransferRule::categories;
set<wstring> TransferRule::attributes;
int TransferRule::rule_counter=0;

TransferRule::TransferRule() {
  source=L"";
  rule_id=rule_counter;
  rule_counter++;
}
  
TransferRule::TransferRule(const TransferRule& tr) {
  source=tr.source;
  ats=tr.ats;
}
    
TransferRule::~TransferRule() {
}

bool 
TransferRule::add_alignment_template(const AlignmentTemplate& at) {
  wstring source_at=StringUtils::vector2wstring(at.source);
  if (source.length()==0)
    source=source_at;

  if (source!=source_at)
    return false;

  ats.push_back(at);

  //Now we revise the categories and the attributes that will be used
  //by the generated rules
  for (unsigned i=0; i<at.source.size(); i++) {
    wstring s=at.source[i];
    if (s.length()==0) {
      cerr<<"Error in TransferRule::add_alignment_template: an empty source word was found\n";
      exit(EXIT_FAILURE);
    }
    pair<wstring, wstring> cat;

    wstring tags=Utils::tags2transferformat(Utils::get_tags(s));

    cat.first=Utils::get_lemma(s);
    cat.second=tags;
    categories.insert(cat);
    attributes.insert(tags);
  }

  return true;
}
  
int 
TransferRule::get_number_alignment_templates() {
  return ats.size();
}
  
wstring 
TransferRule::gen_apertium_transfer_rule(bool debug) {
  wstring rule=L"";
  bool include_otherwise=true;

  if (ats.size()==0) {
    cerr<<"Error in TransferRule::gen_apertium_transfer_rule: No alignment templates available\n";
    exit(EXIT_FAILURE);
  }

  //Sort the AT so as to apply always the most frequent AT that
  //satisfies the restrictions
  AlignmentTemplateGreaterThanByCount atcomparer;
  sort(ats.begin(), ats.end(), atcomparer);

  rule+=L"<rule>\n";

  //The pattern to detect is the same for all AT within this transfer rule
  rule+=L"  <pattern>\n";
  for(unsigned i=0; i<ats[0].source.size(); i++) {
    wstring lemma=Utils::get_lemma(ats[0].source[i]);
    wstring tags=Utils::tags2transferformat(Utils::get_tags(ats[0].source[i]));
    rule+=L"    <pattern-item n=\"CAT__"+category_name(lemma,tags)+L"\"/>\n";
  }
  rule+=L"  </pattern>\n";

  rule+=L"  <action>\n";
  rule+=L"    <choose>\n";

  //There is a set of different actions depending on the TL side of
  //each AT. Consequently, there's one <when> statement per AT 
  for(unsigned i=0; i<ats.size(); i++) {
    rule+=L"      <when>";
    rule+=L"<!--"+ats[i].to_wstring()+L"-->\n";
    rule+=L"        <test>\n";

    int nconditions=0;
    wstring teststr=L"";
    //This AT can be applied if all restrictions are met
    for(unsigned j=0; j<ats[i].restrictions.size(); j++){
      if (ats[i].restrictions[j]!=L"__CLOSEWORD__") {
	nconditions++;
	teststr+=L"          <or>\n";

	teststr+=L"            <equal>\n";
	teststr+=L"              <clip pos=\""+Utils::itoa(j+1)+L"\" side=\"tl\" part=\"tags\" queue=\"no\"/>\n";
	teststr+=L"              <lit-tag v=\""+Utils::tags2transferformat(ats[i].restrictions[j])+L"\"/>\n";
	teststr+=L"            </equal>\n";

	teststr+=L"            <equal>\n";
	teststr+=L"              <clip pos=\""+Utils::itoa(j+1)+L"\" side=\"tl\" part=\"tags\" queue=\"yes\"/>\n";
	teststr+=L"              <lit-tag v=\""+Utils::tags2transferformat(Utils::get_tags(ats[i].target[ats[i].get_open_target_word_pos(j)]))+L"\"/>\n";
	teststr+=L"            </equal>\n";

	teststr+=L"          </or>\n";
      }
    }

    if (nconditions==0) { //All words were close words. We introduce a
			  //condition that is always true
      teststr+=L"          <equal>\n";
      teststr+=L"            <lit v=\"TRUE\"/>\n";
      teststr+=L"            <lit v=\"TRUE\"/>\n";
      teststr+=L"          </equal>\n";
      include_otherwise=false;
    }

    if (nconditions>1) // There are more than one restriction to test
      rule+=L"        <and>\n";

    rule+=teststr;

    if (nconditions>1)
      rule+=L"        </and>\n";

    rule+=L"        </test>\n";

    if (debug) {
      wstring s=StringUtils::substitute(ats[i].to_wstring(), L"><", L".");
      s=StringUtils::substitute(s,L"<",L"-");
      s=StringUtils::substitute(s,L">",L"");
      rule+=L"        <out>\n";
      rule+=L"          <lu><lit v=\"(rid:"+Utils::itoa(rule_id)+L" at:"+s+L")\"/></lu>\n";
      rule+=L"        </out>\n";
    }

    int blank_pos=0;
    for(unsigned j=0; j<ats[i].target.size(); j++) {      
      if (ats[i].target[j][0]!='<') { //It's a lexicalized word, we copy it as is
	rule+=L"        <out>\n";
	rule+=L"          <lu>\n";
	wstring target_tags=Utils::tags2transferformat(Utils::get_tags(ats[i].target[j]));
	rule+=L"            <lit v=\""+StringUtils::substitute(Utils::get_lemma_without_queue(ats[i].target[j]),L"_",L" ")+L"\"/>\n";
	rule+=L"            <lit-tag v=\""+target_tags+L"\"/>\n";
	rule+=L"            <lit v=\""+StringUtils::substitute(Utils::get_queue(ats[i].target[j]),L"_",L" ")+L"\"/>\n";
	rule+=L"          </lu>\n";
	rule+=L"        </out>\n";

	wstring genre=Utils::get_tag_value(target_tags,L"m|f");
	if(genre.length()>0)
	  rule+=L"        <let><var n=\"genre\"/><lit-tag v=\""+genre+L"\"/></let>\n";

      } else {
	int pos=ats[i].get_open_source_word_pos(j);
	rule+=L"        <out>\n";
	rule+=L"          <lu>\n";
	rule+=L"            <clip pos=\""+Utils::itoa(pos+1)+L"\" side=\"tl\" part=\"lemh\"/>\n";
	rule+=L"            <lit-tag v=\""+Utils::tags2transferformat(Utils::get_tags(ats[i].target[j]))+L"\"/>\n";
	rule+=L"            <clip pos=\""+Utils::itoa(pos+1)+L"\" side=\"tl\" part=\"lemq\"/>\n";
	rule+=L"          </lu>\n";
	rule+=L"        </out>\n";

        rule+=L"        <call-macro n=\"f_genre_num\">\n";
        rule+=L"          <with-param pos=\""+Utils::itoa(pos+1)+L"\"/>\n";
        rule+=L"        </call-macro>\n";
      }

      if (blank_pos<(int)(ats[i].source.size()-1)) {
	rule+=L"        <out>\n";
	rule+=L"          <b pos=\""+Utils::itoa(blank_pos+1)+L"\"/>\n";
	rule+=L"        </out>\n";
	blank_pos++;
      } else if (j<(ats[i].target.size()-1)) {
	//TL output string has more words than the SL pattern detected
	rule+=L"        <out>\n";
	rule+=L"          <b/>\n";
	rule+=L"        </out>\n";
      }
    }

    if (debug) {
      rule+=L"        <out>\n";
      rule+=L"          <lu><lit v=\"(END)\"/></lu>\n";
      rule+=L"        </out>\n";
    }

    //If there are remaining blanks we print them out if they have
    //format information inside. This is caused by a SL input string
    //longer than the TL output one
    for (unsigned j=ats[i].target.size(); j<ats[i].source.size(); j++) {
      rule+=L"        <call-macro n=\"f_bcond\">\n";
      rule+=L"          <with-param pos=\""+Utils::itoa(j)+L"\"/>\n";
      rule+=L"          <with-param pos=\""+Utils::itoa(j+1)+L"\"/>\n";
      rule+=L"        </call-macro>\n";
    }

    rule+=L"      </when>\n";

    if(!include_otherwise) {
      //As the condition will always be met it has no sense to include
      //further ATs
      break;
    }
  }

  //Actions to perform when none of the ATs can be applied
  //word-for-word translation
  if(include_otherwise) {
    rule+=L"      <otherwise><!--Word-for-word translation-->\n";
    if (debug) {
      rule+=L"        <out>\n";
      rule+=L"          <lu><lit v=\"(rid:"+Utils::itoa(rule_id)+L" at:word-for-word)\"/></lu>\n";
      rule+=L"        </out>\n";
    }

    for(unsigned i=0; i<ats[0].source.size(); i++) {
      rule+=L"        <call-macro n=\"f_genre_num\">\n";
      rule+=L"          <with-param pos=\""+Utils::itoa(i+1)+L"\"/>\n";
      rule+=L"        </call-macro>\n";

      rule+=L"        <call-macro n=\"f_set_genre_num\">\n";
      rule+=L"          <with-param pos=\""+Utils::itoa(i+1)+L"\"/>\n";
      rule+=L"        </call-macro>\n";

      rule+=L"        <out>\n";
      rule+=L"          <lu>\n";

      rule+=L"            <clip pos=\""+Utils::itoa(i+1)+L"\" side=\"tl\" part=\"whole\"/>\n";
      //rule+=L"            <clip pos=\""+Utils::itoa(i+1)+L"\" side=\"tl\" part=\"lemh\"/>\n";
      //rule+=L"            <clip pos=\""+Utils::itoa(i+1)+L"\" side=\"tl\" part=\"tags\"/>\n";
      //rule+=L"            <clip pos=\""+Utils::itoa(i+1)+L"\" side=\"tl\" part=\"lemq\"/>\n";

      rule+=L"          </lu>\n";
      if (i<(ats[0].source.size()-1))
	rule+=L"          <b pos=\""+Utils::itoa(i+1)+L"\"/>\n";
      rule+=L"        </out>\n";
    }
    if (debug) {
      rule+=L"        <out>\n";
      rule+=L"          <lu><lit v=\"(END)\"/></lu>\n";
      rule+=L"        </out>\n";
    }

    rule+=L"      </otherwise>\n";
  }

  rule+=L"    </choose>\n";
  rule+=L"  </action>\n";
  rule+=L"</rule>\n";

  return rule;
}

wstring 
TransferRule::gen_apertium_transfer_head(bool debug) {
  wstring head=L"";

  head+=L"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
  head+=L"<transfer>\n";

  head+=L"<section-def-cats>\n";

  set<pair<wstring,wstring> >::iterator it;
  for(it=categories.begin(); it!=categories.end(); it++) {
    head+=L"  <def-cat n=\"CAT__"+category_name(it->first,it->second)+L"\">\n";
    if (it->first.length()>0) //There is a lemma
      head+=L"    <cat-item lemma=\""+StringUtils::substitute(it->first,L"_",L" ")+L"\" tags=\""+it->second+L"\"/>\n";
    else
      head+=L"    <cat-item tags=\""+it->second+L"\"/>\n";
    head+=L"  </def-cat>\n";
  }

  head+=L"  <def-cat n=\"CAT__ND_GD\">\n";
  head+=L"    <cat-item tags=\"*.mf.*\"/>\n";
  head+=L"    <cat-item tags=\"*.sp.*\"/>\n";
  head+=L"    <cat-item tags=\"*.mf.sp.*\"/>\n";
  head+=L"    <cat-item tags=\"*.sp.mf.*\"/>\n";
  head+=L"    <cat-item tags=\"*.mf.*.sp.*\"/>\n";
  head+=L"    <cat-item tags=\"*.sp.*.mf.*\"/>\n";
  head+=L"  </def-cat>\n";

  head+=L"</section-def-cats>\n";

  head+=L"<section-def-attrs>\n";

  //set<string>::iterator it2;
  //for(it2=attributes.begin(); it2!=attributes.end(); it2++) {
  //  head+="  <def-attr n=\"ATTR__"+category_name("",(*it2))+"\">\n";
  //  head+="    <attr-item tags=\""+(*it2)+"\"/>\n";
  //  head+="  </def-attr>\n";
  //}

  head+=L"  <def-attr n=\"gen\">\n";
  head+=L"    <attr-item tags=\"m\"/>\n";
  head+=L"    <attr-item tags=\"f\"/>\n";
  head+=L"    <attr-item tags=\"mf\"/>\n";
  head+=L"    <attr-item tags=\"GD\"/>\n";
  head+=L"  </def-attr>\n";

  head+=L"  <def-attr n=\"num\">\n";
  head+=L"    <attr-item tags=\"sg\"/>\n";
  head+=L"    <attr-item tags=\"pl\"/>\n";
  head+=L"    <attr-item tags=\"sp\"/>\n";
  head+=L"    <attr-item tags=\"ND\"/>\n";
  head+=L"  </def-attr>\n";

  //head+="  <def-attr n=\"ATTR__notused\">\n";
  //head+="    <attr-item tags=\"this.attr.will.not.be.used\"/>\n";
  //head+="  </def-attr>\n";

  head+=L"</section-def-attrs>\n";

  head+=L"<section-def-vars>\n";
  head+=L"  <def-var n=\"genre\"/>\n";
  head+=L"  <def-var n=\"number\"/>\n";
  head+=L"</section-def-vars>\n";

  head+=L"<section-def-macros>\n";

  head+=L"<def-macro n=\"f_bcond\" npar=\"2\">\n";
  head+=L"<!--To test whether a blank contains format information.\n";
  head+=L"If no format information is present it is removed. -->\n";
  head+=L"  <choose>\n";
  head+=L"    <when>\n";
  head+=L"      <test>\n";
  head+=L"        <not>\n";
  head+=L"	 <equal>\n";
  head+=L"	   <b pos=\"1\"/>\n";
  head+=L"	   <lit v=\" \"/>\n";
  head+=L"	 </equal>\n";
  head+=L"	 </not>\n";
  head+=L"      </test>\n";
  head+=L"      <out>\n";
  head+=L"        <b pos=\"1\"/>\n";
  head+=L"      </out>\n";
  head+=L"    </when>\n";
  head+=L"  </choose>\n";
  head+=L"</def-macro>\n";

  head+=L"<def-macro n=\"f_genre_num\" npar=\"1\">\n";
  head+=L"<!--To set the global value storing the TL genre of the last seen word. -->\n";
  head+=L"  <choose>\n";
  head+=L"    <when>\n";
  head+=L"      <test>\n";
  head+=L"        <equal>\n";
  head+=L"          <clip pos=\"1\" side=\"tl\" part=\"gen\"/>\n";
  head+=L"          <lit-tag v=\"m\"/>\n";
  head+=L"        </equal>\n";
  head+=L"      </test>\n";
  head+=L"      <let><var n=\"genre\"/><lit-tag v=\"m\"/></let>\n";
  head+=L"    </when>\n";
  head+=L"    <when>\n";
  head+=L"      <test>\n";
  head+=L"        <equal>\n";
  head+=L"          <clip pos=\"1\" side=\"tl\" part=\"gen\"/>\n";
  head+=L"          <lit-tag v=\"f\"/>\n";
  head+=L"        </equal>\n";
  head+=L"      </test>\n";
  head+=L"      <let><var n=\"genre\"/><lit-tag v=\"f\"/></let>\n";
  head+=L"    </when>\n";
  head+=L"    <when>\n";
  head+=L"      <test>\n";
  head+=L"        <equal>\n";
  head+=L"          <clip pos=\"1\" side=\"tl\" part=\"num\"/>\n";
  head+=L"          <lit-tag v=\"sg\"/>\n";
  head+=L"        </equal>\n";
  head+=L"      </test>\n";
  head+=L"      <let><var n=\"number\"/><lit-tag v=\"sg\"/></let>\n";
  head+=L"    </when>\n";
  head+=L"    <when>\n";
  head+=L"      <test>\n";
  head+=L"        <equal>\n";
  head+=L"          <clip pos=\"1\" side=\"tl\" part=\"num\"/>\n";
  head+=L"          <lit-tag v=\"pl\"/>\n";
  head+=L"        </equal>\n";
  head+=L"      </test>\n";
  head+=L"      <let><var n=\"number\"/><lit-tag v=\"pl\"/></let>\n";
  head+=L"    </when>\n";
  head+=L"  </choose>\n";
  head+=L"</def-macro>\n";

  head+=L"<def-macro n=\"f_set_genre_num\" npar=\"1\">\n";
  head+=L"<!--To set the genre of those words with GD, and the number of those words with ND. -->\n";
  head+=L"<!--This is only used in no alignment template at all is applied. -->\n";
  head+=L"  <choose>\n";
  head+=L"    <when>\n";
  head+=L"      <test>\n";
  head+=L"        <equal>\n";
  head+=L"          <clip pos=\"1\" side=\"tl\" part=\"gen\"/>\n";
  head+=L"          <lit-tag v=\"GD\"/>\n";
  head+=L"        </equal>\n";
  head+=L"      </test>\n";
  head+=L"      <choose>\n";
  head+=L"        <when>\n";
  head+=L"          <test>\n";
  head+=L"            <equal>\n";
  head+=L"              <var n=\"genre\"/>\n";
  head+=L"              <lit-tag v=\"f\"/>\n";
  head+=L"            </equal>\n";
  head+=L"          </test>\n";
  head+=L"          <let><clip pos=\"1\" side=\"tl\" part=\"gen\"/><lit-tag v=\"f\"/></let>\n";
  head+=L"        </when>\n";
  head+=L"        <otherwise>\n";
  head+=L"          <let><clip pos=\"1\" side=\"tl\" part=\"gen\"/><lit-tag v=\"m\"/></let>\n";
  head+=L"        </otherwise>\n";
  head+=L"      </choose>\n";
  head+=L"    </when>\n";
  head+=L"  </choose>\n";
  head+=L"  <choose>\n";
  head+=L"    <when>\n";
  head+=L"      <test>\n";
  head+=L"        <equal>\n";
  head+=L"          <clip pos=\"1\" side=\"tl\" part=\"num\"/>\n";
  head+=L"          <lit-tag v=\"ND\"/>\n";
  head+=L"        </equal>\n";
  head+=L"      </test>\n";
  head+=L"      <choose>\n";
  head+=L"        <when>\n";
  head+=L"          <test>\n";
  head+=L"            <equal>\n";
  head+=L"              <var n=\"number\"/>\n";
  head+=L"              <lit-tag v=\"pl\"/>\n";
  head+=L"            </equal>\n";
  head+=L"          </test>\n";
  head+=L"          <let><clip pos=\"1\" side=\"tl\" part=\"num\"/><lit-tag v=\"pl\"/></let>\n";
  head+=L"        </when>\n";
  head+=L"        <otherwise>\n";
  head+=L"          <let><clip pos=\"1\" side=\"tl\" part=\"num\"/><lit-tag v=\"sg\"/></let>\n";
  head+=L"        </otherwise>\n";
  head+=L"      </choose>\n";
  head+=L"    </when>\n";
  head+=L"  </choose>\n";
  head+=L"</def-macro>\n";

  head+=L"</section-def-macros>\n";

  head+=L"<section-rules>\n";

  return head;
}

wstring 
TransferRule::gen_apertium_transfer_foot(bool debug) {
  wstring foot=L"";

  foot+=L"<rule>\n";
  foot+=L"  <pattern>\n";
  foot+=L"    <pattern-item n=\"CAT__ND_GD\"/>\n";
  foot+=L"  </pattern>\n";
  foot+=L"  <action>\n";

  if(debug) {
    foot+=L"  <out>\n";
    foot+=L"    <lu><lit v=\"(default)\"/></lu>\n";
    foot+=L"  </out>\n";
  }

  foot+=L"  <call-macro n=\"f_set_genre_num\">\n";
  foot+=L"    <with-param pos=\"1\"/>\n";
  foot+=L"  </call-macro>\n";
  foot+=L"  <out>\n";
  foot+=L"    <lu>\n";
  foot+=L"      <clip pos=\"1\" side=\"tl\" part=\"whole\"/>\n";
  foot+=L"    </lu>\n";
  foot+=L"  </out>\n";
  foot+=L"  </action>\n";
  foot+=L"</rule>\n";

  foot+=L"</section-rules>\n";
  foot+=L"</transfer>\n";

  return foot;
}

wstring 
TransferRule::category_name(const wstring& lemma, const wstring& tags) {
  wstring catname=L"";

  if (lemma.length()>0)
    catname+=StringUtils::substitute(lemma,L"#",L"_")+L"_";

  catname+=StringUtils::substitute(tags,L".",L"");

  return catname;
}

