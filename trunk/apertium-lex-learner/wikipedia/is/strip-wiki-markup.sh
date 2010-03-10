###
# Script to process the Icelandic Wikipedia into a plain text corpus.
# 
#
#

CATEGORY=Flokkur
IMAGE=Mynd
USER=Notandi
ANALYSER=$2

if [ $# -lt 2 ]; then
	echo "strip-wiki-markup.sh <dump bz2> <analyser bin>";
	exit;
fi

bzcat $1 |\

### HTML entities
# '&gt;' to '>'
sed 's/&amp;gt;/>/g' | sed 's/&amp;lt;/</g' | sed 's/&amp;/\&/g' | sed 's/&amp;nbsp;/ /g' | sed 's/&quot;/"/g' |\
sed 's/&gt;/>/g' | sed 's/&lt;/</g' | sed 's/&amp;/\&/g' | sed 's/&nbsp;/ /g' | sed 's/&quot;/"/g' |\
### Try and remove line beginning tags when they are followed by a sentence
# <text xml:space="preserve">'''Áreitisvaldur''' í læknisfræði er hvert það áreiti kallað sem að raskar jafnvægi líkamans.
# <onlyinclude>'''Finnland''' (finnska: '''Suomi, Suomen tasavalta''', sænska: '''Republiken Finland''')
# <br />Um þrír fjórðu Ástrala eru kristnir, flestir tilheyra Biskupa- eða Kaþólsku kirkjunum með fjórðung hvor.
# <includeonly>'''Sólkerfið''' er heiti á sólkerfi því sem sólin og jörðin tilheyra. 
# </onlyinclude>
# <blockquote><small>„Samninganefndir Alcoa og Landsvirkjunar
# </ref> Alls liggja fyrir 11 útgáfur Biblíunnar á íslensku
# <br>Kolskeggur fór fyrst til Víkurinnar í Noregi, þaðan til Danmerkur
sed 's/<text xml:space="preserve">/<text xml:space="preserve">\n/g' | sed 's/<[a-z]*include[a-z]*>/<onlyinclude>\n/g'  | sed 's/<br *\/>/<br\/>\n/g' | sed 's/^<\/ref>/<\/ref>\n/g' | sed 's/^<blockquote>/<blockquote>\n/g' | sed 's/^<small>/<small>\n/g' | sed 's/^<br>/<br\/>\n/g' | sed 's/<\/onlyinclude>/\n<\/onlyinclude>/g' |\
### Strip lines starting with '<'
#      <comment>/* Ítarefni */</comment>
#      <text xml:space="preserve">
# <onlyinclude>
grep -v '^ *<' |\
### Strip out lines starting with __
# __NOTOC____NOEDITSECTION__
grep -v '^_' |\
### Remove lines starting with '|', '{{' and '}}', these are parts of templates and tables
grep -v -e '^ *{{' -e '^ *|' -e '^}}' -e '^!' -e '^{|' -e '^<[Tt]' -e '</[Tt]' -e '^ *\[' |\
### Lines _ending_ with '|' are also templates
#Flatarmál=143|
#Mannfjöldasæti=3|
#Mannfjöldi=25.000|
grep -v -e '| *$' |\
### Remove lines starting with '[[Image:', [[Category: and those starting with link and lowercase (interwikis)
grep -v -e "^\[\[$IMAGE:" -e "^\[\[$CATEGORY:" -e '^\[\[Category:' -e '^\[\[Image:' -e '^\[\[File:' -e '\[\[[a-z]\+:' |\
grep -v -e "^$IMAGE:" -e "^$CATEGORY:" -e '^Category:' -e '^Image:' -e '^File:' |\
### Remove lines starting with '-', parser functions usually
# -->{{#switch: náttúruvísindi | {{lc:{{{1}}}}} | {{lc:{{{2}}}}} | {{lc:{{{3}}}}} = Flokkur:Náttúruvísindastubbar }}<!--
# -->{{#switch: noregur | {{lc:{{{1}}}}} | {{lc:{{{2}}}}} | {{lc:{{{3}}}}} = Flokkur:Stubbar sem tengjast Noregi }}<!--
grep -v -e "^ *-" |\
### Remove lines starting with '.' or ending with '{'
#.infobox.bordered .mergedrow th {
#window.onload = function() {
#@media print {                           /* Do not print article message box */
#table.ambox.ambox-mini {                 /* Small floating box variant */
#blockquote.templatequote div.templatequotecite { 
#var reCache = {};
#public class KarlMaður extends Maður{  //extends er skipunin í java sem segir erfir
grep -v -e '^@' -e '^\.' -e '{ *$' -e '\/ *$' -e '\/\/' -e '; *$' |\
### Remove lines starting with '*' and '#', usually lists.
# * Sálfræði [http://www.google.com/search?hl=en&amp;q=s%C3%A1lfr%C3%A6%C3%B0i&amp;btnG=Google+Search]
grep -v -e '^*' -e '^#' |\
### Remove lines starting with ':' and ';' these are also some inds of lists
#; Hverfi 1 – Vesturbær
#: Gamli Vesturbærinn, Bráðræðisholt, Grandar,  Hagar, Melar, Skjól, Grímsstaðaholt, Skildinganes og Litli Skerjafjörður. 
grep -v -e '^:' -e '^;' |\
### Strip links
# Kóbaltsprengja hefur [[kóbalt]] í skel sinni, og samrunanifteindirnar breyta kóbaltinu í [[samsæta|samsætuna]] kóbalt-60, sem er kraftmikill [[gamma-geislar|gamma-geislagjafi]] með helmingunartímann 5 ár. Landin liggur að tveimur flóum úr [[Eystrasalt]]i, [[Helsingjabotn]]i í vestri og [[Kirjálabotn]]i í suðri
sed "s/\[\[[ \'0-9A-ZÆÐÞÝÖÀÈÌÒÙÁÉÍÓÚa-zýöæðþzáéíóúàèìòù\-\(\)#\.:·\/_-]\+|//g"  | sed 's/\]\]//g' | sed 's/\[\[//g' |\
### Remove references
# <ref name=blomgunartimi>{{vefheimild|url=http://www.eyjafjoll.is/plontur/brennisoley.htm|titill=Brennisóley|mánuðurskoðað=6. ágúst|árskoðað=2005}}</ref>
# <ref>{{vefheimild|url=http://www.mbl.is/mm/frettir/erlent/2008/11/26/sjalfstaedi_fra_donum_fyrir_2021/|titill=Sjálfstæði frá Dönum fyrir 2021|mánuður=26. nóvember|ár=2008}}</ref>
# <ref>[http://servefir.ruv.is/passiusalmar/hformali.htm FORMÁLI HALLGRÍMS]</ref>
# <ref>[http://timarit.is/view_page_init.jsp?pageId=2009634 Skírnir 1915]</ref>
# <ref name="islanditolum"/>
# <ref>[http://antoine.frostburg.edu/chem/senese/101/glossary/m.shtml#molecule Molecule Definition]  (Frostburg State University)</ref>
# <ref> Íslenzka Kalevala-þýðingin; grein í Lesbók Morgunblaðsins 1959]
sed "s/\[\(http:\/\/\|www\)[0-9A-ZÆÐÞÝÖÀÈÌÒÙÁÉÍÓÚa-zýöæðþzáéíóúàèìòù_?|\/%+=#&–,;\.\-]\+ //g" |  sed 's/\],\?<\/ref>/<\/ref>/g' |\
sed "s/<\(ref\|ref name=[a-z]\+\|ref name=\"[a-z]\+\"\)>[ \'0-9A-ZÆÐÞÝÖÀÈÌÒÙÁÉÍÓÚa-zýöæðþ^Céíóúàèìòù\-\(\){}?\|%+=“„#&–\.\"\'><:·,;\/_-]\+<\/ref>//g" |\
sed 's/<\(ref name=[0-9A-Za-z]\+\|ref name=\"[0-9A-Za-z]\+\"\)\/>//g' |\
### Remove talk page comments
grep -v -e "$USER:" -e 'User:'  |\
### Remove lines starting with '=', headers
# ===Greinar úr Morgunblaðinu===
grep -v -e '^=' |\
### Remove lines with too much punctuation
# LineData=
# color:fyrsta
# width:5
#  at:1950 frompos:40 tillpos:90 #2
# Backgroundcolors = canvas:canvas
# color                = #B0C4DE |	
# .editlink, .noprint, .metadata, .dablink { display: none }
# #content { background: #fff; } /* white background on print */
# c = \sqrt{ \frac{C}{\rho} }
# a(qw%]} [while($p[$p]){ -$p[$p]-- +$p[$p]++
sed 's/^ *//g' | grep '^.* .* .* .*$' | grep -v '^.*\(:\||\|=\|>\|&\|<\|{\|+\|#\|\\\).*\(:\||\|=\|<\|&\|+\|>\|#\|\\\).*$' |\
### Strip formatting for bold and italic
# '''Núverandi skipting þingsæta á Alþingi'''
# '''''[[Snið:Gátlistinn|Gátlistinn]]''''':
sed "s/'''//g" | sed "s/''//g" |\
### Remove lines from templates
# Í þessum flokki eru bækur gefnar út árið {{{1}}}.
grep -v -e '{{{' -e '}}}' |\
### Remove lines with more than two templates
# Arkesilás{{·}} Lakýdes{{·}} Telekles{{·}} Evandros{{·}} Hegesínos
grep -v '{{.*{{.*' |\
### Remove templates
#  skráveifur aðrar. {{heimild vantar}}
# ZWE - Simbabve - {{ZWE}}
# (d. 159 f.Kr.{{óvís}})
sed 's/{{[- 0-9A-ZÁÉÍÓÚÝÖÆÐÞa-záéíóúýöæðþ|]\+}}//g' |\
### Remove comments
#Bjarni dó aðfaranótt 5. október 2005 í bílslysi við bæinn Thisted í Danmörku.<!--, slysið bar þannig að að 
#Þetta heiti er nánast horfið.<!--
sed 's/\.<!--.*$/\./g'  |\
sed 's/--> *$//g' |\
### Remove programming fragments
#if x < pivot then append x to less
#where c.SomeProperty < x
#if a[root] < a[child] then       (out of max-heap order)
#Efnisástand = Fast form}}
grep -v '^\(if\|where\|for\|Efnisástand\|int \|void \)' |\
### Replace HTML entities
# Suðursveit var frá miðri 18. öld sérstakt hreppsfélag &ndash; Borgarhafnarhreppur. 
# Fallið er táknað með gríska bókstafnum &delta;
sed "s/&alpha;/α/g" |sed "s/&beta;/β/g" |sed "s/&gamma;/γ/g" |sed "s/&delta;/δ/g" |sed "s/&epsilon;/ε/g" |sed "s/&zeta;/ζ/g" |sed "s/&eta;/η/g" |sed "s/&theta;/θ/g" |sed "s/&iota;/ι/g" |sed "s/&kappa;/κ/g" |sed "s/&lambda;/λ/g" |sed "s/&mu;/μ/g" |sed "s/&nu;/ν/g" |sed "s/&xi;/ξ/g" |sed "s/&omicron;/ο/g" |sed "s/&pi;/π/g" |sed "s/&rho;/ρ/g" |sed "s/&sigmaf;/ς/g" |sed "s/&sigma;/σ/g" |sed "s/&tau;/τ/g" |sed "s/&upsilon;/υ/g" |sed "s/&phi;/φ/g" |sed "s/&chi;/χ/g" |sed "s/&psi;/ψ/g" |sed "s/&omega;/ω/g" |\
sed "s/&Alpha;/Α/g" |sed "s/&Beta;/Β/g" |sed "s/&Gamma;/Γ/g" |sed "s/&Delta;/Δ/g" |sed "s/&Epsilon;/Ε/g" |sed "s/&Zeta;/Ζ/g" |sed "s/&Eta;/Η/g" |sed "s/&Theta;/Θ/g" |sed "s/&Iota;/Ι/g" |sed "s/&Kappa;/Κ/g" |sed "s/&Lambda;/Λ/g" |sed "s/&Mu;/Μ/g" |sed "s/&Nu;/Ν/g" |sed "s/&Xi;/Ξ/g" |sed "s/&Omicron;/Ο/g" |sed "s/&Pi;/Π/g" |sed "s/&Rho;/Ρ/g" |sed "s/&Sigma;/Σ/g" |sed "s/&Tau;/Τ/g" |sed "s/&Upsilon;/Υ/g" |sed "s/&Phi;/Φ/g" |sed "s/&Chi;/Χ/g" |sed "s/&Psi;/Ψ/g" |sed "s/&Omega;/Ω/g" |\
sed 's/&\(minus\|ndash\);/-/g' | sed 's/&deg;/°/g' | sed 's/&frac12;/½/g' | sed 's/&sup2;/²/g' | sed 's/&mdash;/—/g' |\
### Now try and tokenise into sentences
apertium-destxt |\
lt-proc $ANALYSER |\
apertium-retxt |\
sed 's/\^.\/.<sent>\$ *\^[A-ZÆÐÞÝÍÓÚÉÁ][a-záéíóúýæðþ]/\^.\/.<sent>\$\n&/g' |\
sed 's/^\^.\/.<sent>\$ *//g' |\
python untag-corpus.py |\
### And now clean up punctuation
#  Sænskir kóngar leituðust eftir að flytja landamæri ríkisins lengra austur á bóginn og voru meira og minna stöðugar erjur við Rússa gegnum aldirnar af þeim sökum . Á 18 . öld snerust leikar og rússneskur her hertók nánast allt það sem nú er Finnland tvisvar ( 1714 — 1721 og svo aftur 1742 – 1743 ) . 
sed 's/  */ /g' | sed 's/^ *//g' | sed 's/[0-9]\+ \. /&.NXNZ/g' | sed 's/ \. \.NXNZ/. /g' | sed 's/ , /, /g' | sed 's/ \. *$/./g' | sed 's/ : *$/:/g' |\
sed 's/ ( / (/g' | sed 's/ )/)/g'
