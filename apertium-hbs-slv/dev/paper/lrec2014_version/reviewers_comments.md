============================================================================
                            REVIEWER #1
============================================================================


---------------------------------------------------------------------------
Reviewer's Scores
---------------------------------------------------------------------------

                Originality of the Work:: 2
                        Overall Quality:: 4


---------------------------------------------------------------------------
Comments
---------------------------------------------------------------------------

The paper presents a rule-based machine translation approach between Slovenian
and the Bosnian, Croatian and Serbian languages. The authors describe
the development of the overall standard pipeline of the Apertium RBMT system.
Although the resulting system is, as they state themselves, in its infancy, the
paper is overall informative and well written. I still have a few comments:

- it is not clear to me why you are showing dialects in Figure 1, they do not
add to clarity for the reader since Slovenian is closer to the Kajkavian
dialect than the Kajkavian to the Štokavian dialect, listing languages in the
Serbo-Croatian language group would be much simpler

- it is not clear to me what Table 1 shows for Kajkavian, you note that -ije-
-je- and -i- correspond to the yat reflex, what about -ie- and -ei-?

- it could be that the paper was written some time ago, but it is not true for
some time now that there are no free tools for morphosyntactic tagging and
disambiguation of those languages
        - Croatian (evaluated on Serbian as well) (CC-BY-SA v3.0):
                SETimes.HR corpus
http://nlp.ffzg.hr/resources/corpora/setimes-hr/
                paper http://aclweb.org/anthology/W/W13/W13-2408.pdf
        - Slovenian (if you consider CC-BY-NC-SA free)
                ccKres and ccGigafida
http://www.slovenscina.eu/korpusi/proste-zbirke

- it would be very informative to see some evaluation of the CG-approach to
morphosyntactic disambiguation since this is a crucial step

- you should put a reference to Table 2 somewhere in the text

- would it not be easier to encode lexical differences between the hbs
languages regardless of the other language these are being translated to/from?

- reference to Table 3 is missing as well

- I assume that the 42 lexical selection rules cover only lexical differences
between Bosnian, Croatian and Serbian; they probably do not cover all the
differences (beside the yat reflex) of the 13,206 lexemes in the dictionary, so
a comment about that would be in place

- lexical differences between Bosnian, Croatian and Serbian could be extracted
from the SETimes corpus

- you state that the morphological dictionary covers both the analytic and
synthetic forms of the future for analysis, is it possible to generate both in
the slv-hbs direction as well?, just grepped through the Serbian web corpus
srWaC (containing 1.5% of Croatian text), in the first 100M tokens, "gledati?
ću" appears once, "gledaću" 487 times.

============================================================================
                            REVIEWER #2
============================================================================


---------------------------------------------------------------------------
Reviewer's Scores
---------------------------------------------------------------------------

                Originality of the Work:: 3
                        Overall Quality:: 3


---------------------------------------------------------------------------
Comments
---------------------------------------------------------------------------

The paper describes the development of a bidirectional machine translation
system for the western branch of South-Slavic languages — Serbo-Croatian and
Slovenian.

PROS
- The paper fit very well in the conference
- Implementation for interesting languages
- Interesting transfer rules

CONS
- The evaluation is very basic
- The sections are not balanced. Very short sections for analysis and
disambiguation and quite long lexical selection
- # of rules unbalanced (table 3). Why?

REVISIONS
- Facing CONS
- Some/More references about language typology and classification, , CG, format
of the rules, similar systems for these languages
- Figures about the ambiguity-rate before CG and after
- lexical selection: the examples show rules lemma by lemma. Is it possible
more general phonological rules? I.e. B* -> V*
- Transfer rules: similar question: more general rules are possible? How is the
syntax of these rules
- Comparative evaluation with other languages with similar resources in
Apertium
- misspelling: "Note that two compare"

============================================================================
                            REVIEWER #3
============================================================================


---------------------------------------------------------------------------
Reviewer's Scores
---------------------------------------------------------------------------

                Originality of the Work:: 1
                        Overall Quality:: 3


---------------------------------------------------------------------------
Comments
---------------------------------------------------------------------------

The paper describes a rule-based machine translation system between Slovenian
and Serbo-Croatian. While new language resources are always important the paper
lacks some clarity as to what are the novel methods, resources or descriptions
and what are just parts reused from previous work. From what can be seen the
result mostly consist of manual work and resource acquisition with little
originality or reuse value for other projects, so the main contribution would
be the
finalisation of a new language pair. Perhaps there is also some novel ideas
about handling of closely related dialects as translation but this is not very
obvious and makes the paper seem to lack a bit of focus.

Main comments:

* Unbalanced abstract, too much detail about languages of the study and too
little about the study itself. No mention of the results obtained.

* The authors claim that due to the highly inflected nature of the languages,
Apertium's statistical tagger would not provide satisfactory results. Evidence
that supports this claim is required.

* The authors claim that there are no freely available resources (tools or
corpora) for Serbo-Croatian, however free annotated data and a PoS tagging
model are available (http://nlp.ffzg.hr/resources/).
Similarly, the authors claim that there are no parallel corpora for the
language pair. However, even if of small size, there are a few corpora
available, e.g. the EAC Translation Memory containing 573 translation units and
the EU Bookshop parallel corpus containing 4,222
sentence pairs. Please consider these and if deemed "not adequate" state
clearly why.

* It is not said explicitly, but apparently tag mismatches were handled by
  manually writing bidix descriptions. Tag mismatches are a common problem in
  RBMT and a more exact description to their handling would have been
beneficial

* I expected lexical selection (Section 3.5) would be used to perform semantic
disambiguation, however from the reading of the section it seems to be used
solely for choosing the correct word according to the variant of the dialect
used in the output (Serbian, Croatian or Bosnian). Please clarify.

* Lexical selection has an abundance of examples and code snippets
  which makes it seem like a central focus of the article, yet it is
  merely hand-written rules and available in open source repository so
  no need to have multiple code snippets that all look the
  same. However final evaluation says not much work has been done with
  lexical selection.

* If the lexical selection is based on minor phonological or orthographical
  variances it should be simple to automate.

* As a stark contrast to lexical selection, transfer rules contain no code
  snippets but linguistic examples, which is good. It leads to think however if
  this means that they are planned future work?

* The evaluation is solid (even if the testset size is rather small), but only
covers about hbs and slv, how does this relate to lexical selection dealing
with dialects?

* Error analysis could present specific examples

* Politico-linguistic may be not a very formal term. If you want to deal with
this issue be more specific and formal.

Minor details / typography / grammar

* For apertium in section 2.1 reference to a paper and / or URL would be useful

* For CG reference paper such as Karlsson's ""

* Should probably be proof-read by a native speaker, at the very least
  spell-checked ("post-editted" etc.).

* Some figures and tables are not refered to in the text

* Italian and German language -> languages

* Table 4 is placed under a footnote (!)

* That is word is -> that word is

* much work much

* References have both full names and initials. If it uses bibtex you may need
  to add full names to everyone.
