#!/bin/sh

lt-proc sl-mk.automorf.bin | \
apertium-tagger -g sl-mk.prob  | \
apertium-pretransfer | \
apertium-transfer trules-sl-mk.xml trules-sl-mk.bin sl-mk.autobil.bin  | \
apertium-transfer -n  apertium-sl-mk.postprocessrules_n_-sl-mk.xml postprocessrules-sl-mk_n.bin | \
apertium-transfer -n  apertium-sl-mk.postprocessrules_adj_-sl-mk.xml postprocessrules-sl-mk_adj.bin | \
apertium-transfer -n  apertium-sl-mk.postprocessrules_adv_-sl-mk.xml postprocessrules-sl-mk_adv.bin | \
apertium-transfer -n  apertium-sl-mk.postprocessrules_vblex_-sl-mk.xml postprocessrules-sl-mk_vblex.bin | \
apertium-transfer -n  apertium-sl-mk.postprocessrules_vbser_-sl-mk.xml postprocessrules-sl-mk_vbser.bin | \
apertium-transfer -n  apertium-sl-mk.postprocessrules_n_-mk-sl_2.xml postprocessrules-sl-mk_n_2.bin| \
apertium-transfer -n  apertium-sl-mk.postprocessrules_adj_-mk-sl_2.xml postprocessrules-sl-mk_adj_2.bin | \
apertium-transfer -n  apertium-sl-mk.postprocessrules_adv_-mk-sl_2.xml postprocessrules-sl-mk_adv_2.bin | \
apertium-transfer -n  apertium-sl-mk.postprocessrules_vblex_-mk-sl_2.xml postprocessrules-sl-mk_vblex_2.bin | \
apertium-transfer -n  apertium-sl-mk.postprocessrules_vbser_-mk-sl_2.xml postprocessrules-sl-mk_vbser_2.bin 
#lt-proc -g sl-mk.autogen.bin
