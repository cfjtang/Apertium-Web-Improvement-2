#!/bin/bash
# -*- bidi-display-reordering: nil -*-


# A form like ولا is ambiguous between
# ^ولا/ولا<det><ind><mf><sp>$
# and
# ^و/و$ + ^لا/لا<ij>/لا<adv>$

# In order to catch all analyses, we add ^ول/و+لا<ij>/و+لا<adv>$ to the dictionary.
# We do this for all waw-ambiguous forms.

# We also add waw as an inconditional entry on its own, which should
# deal with any forms that are not waw-ambiguous.

cd $(dirname $0)/..

lt-expand apertium-mt-ar.ar.dix |\
# find all waw-initial forms, strip off the waw:
grep '^و' | sed 's/^و//' | cut -f1 -d: |\
# get all forms that have an analysis (a single lexical unit) without the waw:
lt-proc ar-mt.automorf.bin |grep -ve '/\*' -e '\^.*\^' |\
# turn that into dix format, adding the waw to each analysis:
awk -F'/' '{
sub(/^\^/,"");
for(i=2;i<NF;i++){
  gsub(/</,"<s n=\"", $i);
  gsub(/>/, "\"/>",$i);
  gsub(/\+/,"<j/>",$i);
  print "<e c=\"waw-ambig\"><p><l>و"$1"</l><r>و<s n=\"cnjcoo\"/><j/>"$i"</r></p></e>"
}
}' |\
# remove dupes, sort by <r>
sort -u | sort -k2 -t 'r'


### and fa-ambig. forms:

lt-expand apertium-mt-ar.ar.dix |\
grep '^ف' | sed 's/^ف//' | cut -f1 -d: |\
lt-proc ar-mt.automorf.bin |grep -ve '/\*' -e '\^.*\^' |\
awk -F'/' '{
sub(/^\^/,"");
for(i=2;i<NF;i++){
  gsub(/</,"<s n=\"", $i);
  gsub(/>/, "\"/>",$i);
  gsub(/\+/,"<j/>",$i);
  print "<e c=\"fa-ambig\"><p><l>ف"$1"</l><r>ف<s n=\"cnjcoo\"/><j/>"$i"</r></p></e>"
}
}' |\
sort -u | sort -k2 -t 'r'

