# Proofread

- proofread the changes

# Language resources

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

- The authors claim that there are no freely available resources (tools or
corpora) for Serbo-Croatian, however free annotated data and a PoS tagging
model are available (http://nlp.ffzg.hr/resources/).
Similarly, the authors claim that there are no parallel corpora for the
language pair. However, even if of small size, there are a few corpora
available, e.g. the EAC Translation Memory containing 573 translation units and
the EU Bookshop parallel corpus containing 4,222
sentence pairs. Please consider these and if deemed "not adequate" state
clearly why.
}

# Lex selection:

- would it not be easier to encode lexical differences between the hbs
languages regardless of the other language these are being translated to/from?

- I assume that the 42 lexical selection rules cover only lexical differences
between Bosnian, Croatian and Serbian; they probably do not cover all the
differences (beside the yat reflex) of the 13,206 lexemes in the dictionary, so
a comment about that would be in place

- lexical differences between Bosnian, Croatian and Serbian could be extracted
from the SETimes corpus

- lexical selection: the examples show rules lemma by lemma. Is it possible
more general phonological rules? I.e. B- -> V-

- If the lexical selection is based on minor phonological or orthographical
  variances it should be simple to automate.

- I expected lexical selection (Section 3.5) would be used to perform semantic
disambiguation, however from the reading of the section it seems to be used
solely for choosing the correct word according to the variant of the dialect
used in the output (Serbian, Croatian or Bosnian). Please clarify.

- The evaluation is solid (even if the testset size is rather small), but only
covers about hbs and slv, how does this relate to lexical selection dealing
with dialects?

- Lexical selection has an abundance of examples and code snippets
  which makes it seem like a central focus of the article, yet it is
  merely hand-written rules and available in open source repository so
  no need to have multiple code snippets that all look the
  same. However final evaluation says not much work has been done with
  lexical selection.

# Other

- Unbalanced abstract, too much detail about languages of the study and too
little about the study itself. No mention of the results obtained.

- The sections are not balanced. Very short sections for analysis and
disambiguation and quite long lexical selection

- Error analysis could present specific examples

- Should probably be proof-read by a native speaker, at the very least
  spell-checked ("post-editted" etc.).

- Some/More references about language typology and classification, , CG, format
of the rules, similar systems for these languages

- Figures about the ambiguity-rate before CG and after

- Comparative evaluation with other languages with similar resources
in Apertium
