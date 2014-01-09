Apertium Wiki Tools
===================

The scripts in this folder are designed to perform menial tasks for the [Apertium Wiki](http://wiki.apertium.org/).

- `dixTable.py` creates bidix stem tables for Apertium language pairs by taking the input language codes (ISO639-1 or ISO639-2), scanning through the current Apertium SVN and counting bidix for each pair located. The output includes a table of the form located [here](http://wiki.apertium.org/wiki/Turkic_languages#Table_of_existing_pairs). The script will **not** choke on bad XML, but rather offer you a hint as to where the problem occurs (as LXML sees it). Conventionally, the following text accompanies each table:

      ===Table of Existing Pairs====
      Text in ''italics'' denotes language pairs in the incubator.  Regular text denotes a developing language pair in nursery, while text in '''bold''' denotes a stable well-working language pair in trunk and text in '''''bold and italics''''' denotes a pair in staging. Bidix stems as counted with [[dixcounter]] are displayed below.

- `udhrTable.py` creates UDHR (Universal Declaration of Human Rights) language sample tables by taking the input language codes and attempting to locate UDHR translations on the [Unicode](http://www.unicode.org/udhr) and the [United Nations Human Rights](http://www.ohchr.org/EN/UDHR/Pages/SearchByLang.aspx) website. The output includes a table of the form located [here](http://wiki.apertium.org/wiki/Turkic_languages#Samples). Conventionally, the following text accompanies each table:

      ==Samples==
      Article 1 of the Universal Declaration of Human Rights:

      ''All human beings are born free and equal in dignity and rights. They are endowed with reason and conscience and should act towards one another in a spirit of brotherhood.''

- `langTable.py` creates language vulnerability tables by taking the input language codes (ISO639-2/3, **not** macrolanguage) and looking up the languages in a [UNESCO language vulnerability database](http://unesco.org/culture/languages-atlas/index.php) (Download datasets on the left navigation bar → Download limited dataset) and [Ethnologue](http://www.ethnologue.com/). The output includes a table of the form located [here](http://wiki.apertium.org/wiki/Turkic_languages#Vulnerability). Conventionally, the following text accompanies each table:

      ==Vulnerability==
      This table summarizes the vulnerability of various Dravidian languages. Data is derived from the ‘Atlas of the World’s Languages in Danger, © UNESCO, [http://www.unesco.org/culture/languages-atlas http://www.unesco.org/culture/languages-atlas]’ and [http://www.ethnologue.com/ Ethnologue].