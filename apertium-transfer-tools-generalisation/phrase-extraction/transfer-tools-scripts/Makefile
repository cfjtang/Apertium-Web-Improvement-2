# author Felipe Sánchez Martínez

#PAR_MY_EXTRACTING
#PAR_ENDS_ALIGNED
#PAR_SAME_STRUCTURE

L1=es
L2=ca
TESTCORPUS=/work/vmsanchez/rules/corpora/consumer-eroski
ifdef PAR_TESTCORPUS
TESTCORPUS:=$(PAR_TESTCORPUS)
endif

ifdef PAR_L1
L1:=$(PAR_L1)
endif

ifdef PAR_L2
L2:=$(PAR_L2)
endif

ifdef PAR_TESTCORPUS
TESTCORPUS:=$(PAR_TESTCORPUS)
endif

FILTERING_START=alignment-templates.withalllemmas.togeneralise.uniq


FILTERING_OPTION=onlyslpos
ifdef PAR_FILTERING_OPTION
FILTERING_OPTION:=$(PAR_FILTERING_OPTION)
endif

FILTERING_CRITERION=count
ifdef PAR_FILTERING_CRITERION
FILTERING_CRITERION:=$(PAR_FILTERING_CRITERION)
endif

FILTERING_TH_COUNT=1
ifdef PAR_FILTERING_TH_COUNT
FILTERING_TH_COUNT:=$(PAR_FILTERING_TH_COUNT)
endif

#if value = noconflicts 
# removes conclicts from representants
FILTERING_REMOVE_CONFLICTS=
ifdef PAR_FILTERING_REMOVE_CONFLICTS
FILTERING_REMOVE_CONFLICTS:=$(PAR_FILTERING_REMOVE_CONFLICTS)
endif

#ignorar el de arriba.
#El bueno es: PAR_DONT_REMOVE_CONFLICTS

THRESHOLD_IN_BILPHRASE_CONTRADICTIONS=
ifdef PAR_THRESHOLD_IN_BILPHRASE_CONTRADICTIONS
THRESHOLD_IN_BILPHRASE_CONTRADICTIONS:=--remove_all_biphrases_if_freq_best_is_lower_than $(PAR_THRESHOLD_IN_BILPHRASE_CONTRADICTIONS) --print_all_passing_threshold
endif

SHUFFLE_PREFIX=
ifdef PAR_SHUFFLE_PREFIX
SHUFFLE_PREFIX:=$(PAR_SHUFFLE_PREFIX)
endif

DISCARD_RULE_OPTION=
ifdef PAR_DISCARD_RULE
DISCARD_RULE_OPTION:=--usediscardrule
endif

MARKER_PARAMETER=
ifdef PAR_BILPHRASES_MARKER
MARKER_PARAMETER=--marker ./markers
endif

ifdef PAR_BILPHRASES_MARKER_SOFT
MARKER_PARAMETER=--soft-marker ./markers
endif

ifdef PAR_BILPHRASES_MARKER_HARD
MARKER_PARAMETER=--hard-marker ./markers
endif

DATADIR=~/hybridmt/tools/apertium-$(L1)-$(L2)
ifdef PAR_DATADIR
DATADIR:=$(PAR_DATADIR)
endif

GIZADIR=~/wmt/tools/giza-pp/GIZA++-v2
ifdef PAR_GIZADIR
GIZADIR:=$(PAR_GIZADIR)
endif

RULELEARNINGLIBDIR=/work/vmsanchez/rules/ruleLearning
ifdef PAR_RULELEARNINGLIBDIR
RULELEARNINGLIBDIR:=$(PAR_RULELEARNINGLIBDIR)
endif


ORIGINAL_APERTIUM_MODES_DIR=/home/vmsanchez/rulesinteractive/local/share/apertium/modes/

#Alignment symmetrizartion method to use: union|intersection|refined
SIMMETRIZATION_METHOD=refined

#Minimum and maximum length of the alignment templates
MIN=1

MAX=7
ifdef PAR_MAX
MAX:=$(PAR_MAX)
endif


#Criterion to use whe interpreting the count of an AT count|log|prod
CRITERION=log
ifdef PAR_CRITERION
CRITERION:=$(PAR_CRITERION)
endif
CRITERION_WITH_LEMMAS=count

#Minimum number of occurrences for an AT to be taken into account
TH_COUNT=1
ifdef PAR_TH_COUNT
TH_COUNT:=$(PAR_TH_COUNT)
endif
TH_COUNT_WITH_LEMMAS=2

################################################################

TRAINGIZA_PREFIX=train-giza++

TRAINGIZA_L1_L2=$(TRAINGIZA_PREFIX)-$(L1)-$(L2)
TRAINGIZA_L2_L1=$(TRAINGIZA_PREFIX)-$(L2)-$(L1)

PROB_L1=$(DATADIR)/$(L1)-$(L2).prob
PROB_L2=$(DATADIR)/$(L2)-$(L1).prob

MORF_L1=$(DATADIR)/$(L1)-$(L2).automorf.bin
MORF_L2=$(DATADIR)/$(L2)-$(L1).automorf.bin

ifdef PAR_NO_SHORT_RESTRICTIONS
BIL_L1_L2=$(DATADIR)/$(L1)-$(L2).autobil.bin
BIL_L2_L1=$(DATADIR)/$(L2)-$(L1).autobil.bin
else
BIL_L1_L2=$(DATADIR)/$(L1)-$(L2).autobil.shortrestrictions.bin
BIL_L2_L1=$(DATADIR)/$(L2)-$(L1).autobil.shortrestrictions.bin
endif

GIZA_ALIGNMENTS_L1_L2_LR=$(TRAINGIZA_L1_L2)/LR/GIZA++.A3.final
GIZA_ALIGNMENTS_L1_L2_RL=$(TRAINGIZA_L1_L2)/RL/GIZA++.A3.final

GIZA_ALIGNMENTS_L2_L1_LR=$(TRAINGIZA_L2_L1)/LR/GIZA++.A3.final
GIZA_ALIGNMENTS_L2_L1_RL=$(TRAINGIZA_L2_L1)/RL/GIZA++.A3.final

ATXFILE_L1_L2=apertium-$(L1)-$(L2).transfer-at.$(L1)-$(L2).atx
ATXFILE_L2_L1=apertium-$(L1)-$(L2).transfer-at.$(L2)-$(L1).atx

CLEANFILES=alignments.$(L1)-$(L2).LR.gz alignments.$(L1)-$(L2).RL.gz \
           alignments.$(L2)-$(L1).LR.gz alignments.$(L2)-$(L1).RL.gz \
           alignments.$(L1)-$(L2).gz alignments.$(L2)-$(L1).gz \
           alignment-templates.uniq.$(L1)-$(L2).gz alignment-templates.uniq.$(L2)-$(L1).gz \
           bilphrases.$(L1)-$(L2).gz bilphrases.$(L2)-$(L1).gz \
           alignment-templates.$(L1)-$(L2).gz alignment-templates.$(L2)-$(L1).gz log.* \
           $(TRAINGIZA_L1_L2)/*.vcb $(TRAINGIZA_L1_L2)/*.snt $(TRAINGIZA_L1_L2)/*.txt \
           $(TRAINGIZA_L1_L2)/*.classes $(TRAINGIZA_L1_L2)/*.cats $(TRAINGIZA_L1_L2)/*log* \
           $(TRAINGIZA_L2_L1)/*.vcb $(TRAINGIZA_L2_L1)/*.snt $(TRAINGIZA_L2_L1)/*.txt \
           $(TRAINGIZA_L2_L1)/*.classes $(TRAINGIZA_L2_L1)/*.cats $(TRAINGIZA_L2_L1)/*log* \
           $(TRAINGIZA_L1_L2)/LR/* $(TRAINGIZA_L1_L2)/RL/* \
           $(TRAINGIZA_L2_L1)/LR/* $(TRAINGIZA_L2_L1)/RL/*\
           alignmentTemplatesPlusLemmas.$(L1)-$(L2).txt alignmentTemplatesPlusLemmas.$(L2)-$(L1).txt \
           trules.$(L1)-$(L2).bin trules.$(L2)-$(L1).bin \
           /modes/$(L1)-$(L2).mode /modes/$(L2)-$(L1).mode /modes/$(L1)-$(L2)-norules.mode /modes/$(L2)-$(L1)-norules.mode \
           test.* evaluation.* \
           alignment-templates.togeneralise.$(L1)-$(L2).gz alignment-templates.togeneralise.$(L2)-$(L1).gz \
           alignment-templates.togeneralise.uniq.$(L1)-$(L2).gz alignment-templates.togeneralise.uniq.$(L2)-$(L1).gz \
           alignment-templates.withalllemmas.togeneralise.uniq.$(L1)-$(L2).gz  alignment-templates.withalllemmas.togeneralise.$(L1)-$(L2).gz \
           alignment-templates.withalllemmas.togeneralise.uniq.$(L2)-$(L1).gz alignment-templates.withalllemmas.togeneralise.$(L2)-$(L1).gz \
           alignmentTemplatesPlusLemmas.withalllemmas.filtered-alllemmas.$(L1)-$(L2).gz alignmentTemplatesPlusLemmas.withalllemmas.filtered-alllemmas.$(L2)-$(L1).gz \
           alignmentTemplatesPlusLemmas.withalllemmas.onlyslpos.filtered-1-count.$(L2)-$(L1).gz \
           alignmentTemplatesPlusLemmas.withalllemmas.onlyslpos.filtered-1-count.$(L1)-$(L2).gz \
           alignment-templates.withalllemmas.togeneralise.uniq.onlyslpos.filtered-1-count.$(L2)-$(L1).tllemmasfrombiling \
           alignment-templates.withalllemmas.togeneralise.uniq.onlyslpos.filtered-1-count.$(L1)-$(L2).tllemmasfrombiling
           

.PHONY: all step4 onlyalignments


ifdef PAR_ONLY_STANDARD

all: evaluation.$(L1)-$(L2) evaluation.$(L2)-$(L1) \
     report-rules-$(L1)-$(L2)-words-sum report-rules-$(L2)-$(L1)-words-sum
     #	 evaluation.$(L1)-$(L2)-norules evaluation.$(L2)-$(L1)-norules\
     
else

all: $(SHUFFLE_PREFIX)alignmentTemplatesPlusLemmas.withalllemmas.$(FILTERING_OPTION).filtered$(FILTERING_REMOVE_CONFLICTS)-$(FILTERING_TH_COUNT)-$(FILTERING_CRITERION).$(L1)-$(L2).gz \
     $(SHUFFLE_PREFIX)alignmentTemplatesPlusLemmas.withalllemmas.$(FILTERING_OPTION).filtered$(FILTERING_REMOVE_CONFLICTS)-$(FILTERING_TH_COUNT)-$(FILTERING_CRITERION).$(L2)-$(L1).gz
     
endif


step4: bilphrases.$(L1)-$(L2).gz bilphrases.$(L2)-$(L1).gz

onlyalignments: alignments.$(L1)-$(L2).gz alignments.$(L2)-$(L1).gz

ifdef PAR_PROVIDED_ANALYSED_CORPUS
$(TRAINGIZA_L1_L2)/$(L1).$(L1)-$(L2).pos.txt:
else
$(TRAINGIZA_L1_L2)/$(L1).$(L1)-$(L2).pos.txt: $(L1).txt.gz $(MORF_L1) $(PROB_L1) preprocess_$(L1)_$(L1)-$(L2).sh
endif
	@echo "Analysing "$(L1)".txt.gz to train '"$(L1)"-"$(L2)"' alignments ... "
	mkdir -p $(TRAINGIZA_L1_L2)
	zcat $(L1).txt.gz | awk '{print $$0 " ."}' | apertium-destxt | lt-proc -a $(MORF_L1) | \
	apertium-tagger -g $(PROB_L1) | apertium-pretransfer | apertium-retxt | \
	sed -re "s/ \^[.]<sent>\\$$\$$//g"  | apertium-preprocess-corpus-transfer-at |\
	./preprocess_$(L1)_$(L1)-$(L2).sh > $@
	@echo ""

ifdef PAR_PROVIDED_ANALYSED_CORPUS
$(TRAINGIZA_L1_L2)/$(L2).$(L1)-$(L2).pos.txt:
else
$(TRAINGIZA_L1_L2)/$(L2).$(L1)-$(L2).pos.txt: $(L2).txt.gz $(MORF_L2) $(PROB_L2) preprocess_$(L2)_$(L1)-$(L2).sh 
endif
	@echo "Analysing "$(L2)".txt.gz to train '"$(L1)"-"$(L2)"' alignments ... "
	mkdir -p $(TRAINGIZA_L1_L2)
	zcat $(L2).txt.gz | awk '{print $$0 " ."}' | apertium-destxt | lt-proc -a $(MORF_L2) |\
	apertium-tagger -g $(PROB_L2) | apertium-pretransfer | apertium-retxt |\
	sed -re "s/ \^[.]<sent>\\$$\$$//g"  | apertium-preprocess-corpus-transfer-at |\
	./preprocess_$(L2)_$(L1)-$(L2).sh > $@
	@echo ""

ifdef PAR_PROVIDED_ANALYSED_CORPUS
$(TRAINGIZA_L2_L1)/$(L2).$(L2)-$(L1).pos.txt:
else
$(TRAINGIZA_L2_L1)/$(L2).$(L2)-$(L1).pos.txt: $(L2).txt.gz $(MORF_L2) $(PROB_L2) preprocess_$(L2)_$(L2)-$(L1).sh
endif
	@echo "Analysing "$(L2)".txt.gz to train '"$(L2)"-"$(L1)"' alignments ... "
	mkdir -p $(TRAINGIZA_L2_L1)
	zcat $(L2).txt.gz | awk '{print $$0 " ."}' | apertium-destxt | lt-proc -a $(MORF_L2) |\
        apertium-tagger -g $(PROB_L2) | apertium-pretransfer | apertium-retxt |\
        sed -re "s/ \^[.]<sent>\\$$\$$//g"  | apertium-preprocess-corpus-transfer-at |\
	./preprocess_$(L2)_$(L2)-$(L1).sh > $@
	@echo ""

ifdef PAR_PROVIDED_ANALYSED_CORPUS
$(TRAINGIZA_L2_L1)/$(L1).$(L2)-$(L1).pos.txt:
else
$(TRAINGIZA_L2_L1)/$(L1).$(L2)-$(L1).pos.txt: $(L1).txt.gz $(MORF_L1) $(PROB_L1) preprocess_$(L1)_$(L2)-$(L1).sh
endif

	@echo "Analysing "$(L1)".txt.gz to train '"$(L2)"-"$(L1)"' alignments ... "
	mkdir -p $(TRAINGIZA_L2_L1)
	zcat $(L1).txt.gz | awk '{print $$0 " ."}' | apertium-destxt | lt-proc -a $(MORF_L1) |\
        apertium-tagger -g $(PROB_L1) | apertium-pretransfer | apertium-retxt |\
        sed -re "s/ \^[.]<sent>\\$$\$$//g"  | apertium-preprocess-corpus-transfer-at |\
	./preprocess_$(L1)_$(L2)-$(L1).sh > $@
	@echo ""

$(TRAINGIZA_L1_L2)/$(L1).$(L1)-$(L2).pos_$(L2).$(L1)-$(L2).pos.snt: $(TRAINGIZA_L1_L2)/$(L1).$(L1)-$(L2).pos.txt  $(TRAINGIZA_L1_L2)/$(L2).$(L1)-$(L2).pos.txt
	@echo "Preparing corpora to train ("$(L1)"-"$(L2)") with GIZA++ ..."
	$(GIZADIR)/plain2snt.out $(TRAINGIZA_L1_L2)/$(L1).$(L1)-$(L2).pos.txt  $(TRAINGIZA_L1_L2)/$(L2).$(L1)-$(L2).pos.txt 2> $(TRAINGIZA_L1_L2)/log.plain2snt
	@echo ""

$(TRAINGIZA_L1_L2)/$(L2).$(L1)-$(L2).pos_$(L1).$(L1)-$(L2).pos.snt: $(TRAINGIZA_L1_L2)/$(L1).$(L1)-$(L2).pos.txt  $(TRAINGIZA_L1_L2)/$(L2).$(L1)-$(L2).pos.txt
	@echo "Preparing corpora to train ("$(L1)"-"$(L2)") with GIZA++ ..."
	$(GIZADIR)/plain2snt.out $(TRAINGIZA_L1_L2)/$(L1).$(L1)-$(L2).pos.txt  $(TRAINGIZA_L1_L2)/$(L2).$(L1)-$(L2).pos.txt 2> $(TRAINGIZA_L1_L2)/log.plain2snt
	@echo ""

$(TRAINGIZA_L2_L1)/$(L2).$(L2)-$(L1).pos_$(L1).$(L2)-$(L1).pos.snt: $(TRAINGIZA_L2_L1)/$(L2).$(L2)-$(L1).pos.txt  $(TRAINGIZA_L2_L1)/$(L1).$(L2)-$(L1).pos.txt
	@echo "Preparing corpora to train ("$(L2)"-"$(L1)") with GIZA++ ..."
	$(GIZADIR)/plain2snt.out $(TRAINGIZA_L2_L1)/$(L2).$(L2)-$(L1).pos.txt  $(TRAINGIZA_L2_L1)/$(L1).$(L2)-$(L1).pos.txt 2> $(TRAINGIZA_L2_L1)/log.plain2snt
	@echo ""

$(TRAINGIZA_L2_L1)/$(L1).$(L2)-$(L1).pos_$(L2).$(L2)-$(L1).pos.snt: $(TRAINGIZA_L2_L1)/$(L2).$(L2)-$(L1).pos.txt  $(TRAINGIZA_L2_L1)/$(L1).$(L2)-$(L1).pos.txt
	@echo "Preparing corpora to train ("$(L2)"-"$(L1)") with GIZA++ ..."
	$(GIZADIR)/plain2snt.out $(TRAINGIZA_L2_L1)/$(L2).$(L2)-$(L1).pos.txt  $(TRAINGIZA_L2_L1)/$(L1).$(L2)-$(L1).pos.txt 2> $(TRAINGIZA_L2_L1)/log.plain2snt
	@echo ""

$(GIZA_ALIGNMENTS_L1_L2_LR): $(TRAINGIZA_L1_L2)/$(L1).$(L1)-$(L2).pos_$(L2).$(L1)-$(L2).pos.snt
	@echo "Performing GIZA++ training ("$(L1)"-"$(L2)") from left to right ..."
	cd $(TRAINGIZA_L1_L2) &&  bash ../trainGIZA++.sh $(L1).$(L1)-$(L2).pos.vcb  $(L2).$(L1)-$(L2).pos.vcb $(L1).$(L1)-$(L2).pos_$(L2).$(L1)-$(L2).pos.snt "$(GIZADIR)" &&\
	mkdir -p LR &&\
	mv GIZA++.* mkcls1.log mkcls2.log LR
	@echo ""

$(GIZA_ALIGNMENTS_L1_L2_RL): $(TRAINGIZA_L1_L2)/$(L2).$(L1)-$(L2).pos_$(L1).$(L1)-$(L2).pos.snt
	@echo "Performing GIZA++ training ("$(L1)"-"$(L2)") from right to left ..."
	cd $(TRAINGIZA_L1_L2) &&  bash ../trainGIZA++.sh $(L2).$(L1)-$(L2).pos.vcb  $(L1).$(L1)-$(L2).pos.vcb $(L2).$(L1)-$(L2).pos_$(L1).$(L1)-$(L2).pos.snt "$(GIZADIR)" &&\
	mkdir -p RL &&\
	mv GIZA++.* mkcls1.log mkcls2.log RL
	@echo ""
#*.vcb.classes.cats *.vcb.classes

$(GIZA_ALIGNMENTS_L2_L1_LR): $(TRAINGIZA_L2_L1)/$(L2).$(L2)-$(L1).pos_$(L1).$(L2)-$(L1).pos.snt
	@echo "Performing GIZA++ training ("$(L2)"-"$(L1)") from left to right ..."
	cd $(TRAINGIZA_L2_L1) &&  bash ../trainGIZA++.sh $(L2).$(L2)-$(L1).pos.vcb  $(L1).$(L2)-$(L1).pos.vcb $(L2).$(L2)-$(L1).pos_$(L1).$(L2)-$(L1).pos.snt  "$(GIZADIR)" &&\
	mkdir -p LR &&\
	mv GIZA++.*  mkcls1.log mkcls2.log LR
	@echo ""

$(GIZA_ALIGNMENTS_L2_L1_RL): $(TRAINGIZA_L2_L1)/$(L1).$(L2)-$(L1).pos_$(L2).$(L2)-$(L1).pos.snt
	@echo "Performing GIZA++ training ("$(L2)"-"$(L1)") from right to left ..."
	cd $(TRAINGIZA_L2_L1) &&  bash ../trainGIZA++.sh $(L1).$(L2)-$(L1).pos.vcb  $(L2).$(L2)-$(L1).pos.vcb $(L1).$(L2)-$(L1).pos_$(L2).$(L2)-$(L1).pos.snt "$(GIZADIR)"  &&\
	mkdir -p RL &&\
	mv GIZA++.* mkcls1.log mkcls2.log RL
	@echo ""

alignments.$(L1)-$(L2).LR.gz: $(GIZA_ALIGNMENTS_L1_L2_LR) 
	@echo "Converting GIZA++ LR alignments into apertium format ("$(L1)"-"$(L2)") ..."
	cat $(GIZA_ALIGNMENTS_L1_L2_LR) | giza++A3-to-apertium | gzip > $@
	@echo ""

alignments.$(L1)-$(L2).RL.gz: $(GIZA_ALIGNMENTS_L1_L2_RL) 
	@echo "Converting GIZA++ RL alignments into apertium format ("$(L1)"-"$(L2)") ..."
	cat $(GIZA_ALIGNMENTS_L1_L2_RL) | giza++A3-to-apertium | apertium-switch-alignments | gzip > $@
	@echo ""

alignments.$(L2)-$(L1).LR.gz: $(GIZA_ALIGNMENTS_L2_L1_LR) 
	@echo "Converting GIZA++ LR alignments into apertium format ("$(L2)"-"$(L1)") ..."
	cat $(GIZA_ALIGNMENTS_L2_L1_LR) | giza++A3-to-apertium | gzip > $@
	@echo ""

alignments.$(L2)-$(L1).RL.gz: $(GIZA_ALIGNMENTS_L2_L1_RL) 
	@echo "Converting GIZA++ RL alignments into apertium format ("$(L2)"-"$(L1)") ..."
	cat $(GIZA_ALIGNMENTS_L2_L1_RL) | giza++A3-to-apertium | apertium-switch-alignments | gzip > $@
	@echo ""

ifdef PAR_PROVIDED_ALIGNMENTS
L12DEPENDENCIES:=
else
L12DEPENDENCIES:=alignments.$(L1)-$(L2).LR.gz alignments.$(L1)-$(L2).RL.gz
endif

alignments.$(L1)-$(L2).gz: $(L12DEPENDENCIES)
	@echo "Symmetrizing alignment ("$(L1)"-"$(L1)") by "$(SIMMETRIZATION_METHOD)
	apertium-symmetrize-alignments -i alignments.$(L1)-$(L2).LR.gz -j alignments.$(L1)-$(L2).RL.gz -o $@ -p $(SIMMETRIZATION_METHOD) -z 2> log.symm.$(L1)-$(L2)

ifdef PAR_PROVIDED_ALIGNMENTS
L21DEPENDENCIES:=
else
L21DEPENDENCIES:=alignments.$(L2)-$(L1).LR.gz alignments.$(L2)-$(L1).RL.gz
endif

alignments.$(L2)-$(L1).gz: $(L21DEPENDENCIES)
	@echo "Symmetrizing alignment ("$(L2)"-"$(L1)") by "$(SIMMETRIZATION_METHOD)
	apertium-symmetrize-alignments -i alignments.$(L2)-$(L1).LR.gz -j alignments.$(L2)-$(L1).RL.gz -o $@ -p $(SIMMETRIZATION_METHOD) -z 2> log.symm.$(L2)-$(L1)

ifdef PAR_PROVIDED_BILPHRASES
$(SHUFFLE_PREFIX)bilphrases.$(L1)-$(L2).gz: 
else
$(SHUFFLE_PREFIX)bilphrases.$(L1)-$(L2).gz: $(SHUFFLE_PREFIX)alignments.$(L1)-$(L2).gz
endif
	@echo "Extracting bilingual phrases from alignments."$(L1)"-"$(L2)".gz ..."
	apertium-xtract-bilingual-phrases -i $(SHUFFLE_PREFIX)alignments.$(L1)-$(L2).gz -o $@ -n $(MIN) -m $(MAX) $(MARKER_PARAMETER) -z 2> log.$(SHUFFLE_PREFIX)bilph.$(L1)-$(L2)
	gzip log.$(SHUFFLE_PREFIX)bilph.$(L1)-$(L2)
	@echo ""

ifdef PAR_PROVIDED_BILPHRASES
$(SHUFFLE_PREFIX)bilphrases.$(L2)-$(L1).gz: 
else
$(SHUFFLE_PREFIX)bilphrases.$(L2)-$(L1).gz: $(SHUFFLE_PREFIX)alignments.$(L2)-$(L1).gz
endif
	@echo "Extracting bilingual phrases from alignments."$(L2)"-"$(L1)".gz ..."
	apertium-xtract-bilingual-phrases -i $(SHUFFLE_PREFIX)alignments.$(L2)-$(L1).gz -o $@ -n $(MIN) -m $(MAX) $(MARKER_PARAMETER) -z 2> log.$(SHUFFLE_PREFIX)bilph.$(L2)-$(L1)
	gzip log.$(SHUFFLE_PREFIX)bilph.$(L2)-$(L1)
	@echo ""

$(SHUFFLE_PREFIX)alignment-templates.$(L1)-$(L2).gz: $(ATXFILE_L1_L2) $(SHUFFLE_PREFIX)bilphrases.$(L1)-$(L2).gz $(BIL_L1_L2)
	@echo "Extracing alignment templates from bilphrases."$(L1)"-"$(L2)".gz ..."
	apertium-validate-transfer-at $(ATXFILE_L1_L2)
	apertium-xtract-alignment-templates -x $(ATXFILE_L1_L2) -b $(BIL_L1_L2) -i $(SHUFFLE_PREFIX)bilphrases.$(L1)-$(L2).gz -o $@ -z -e 2> log.$(SHUFFLE_PREFIX)aligtem.$(L1)-$(L2)
	gzip log.$(SHUFFLE_PREFIX)aligtem.$(L1)-$(L2)
	@echo ""

$(SHUFFLE_PREFIX)alignment-templates.$(L2)-$(L1).gz: $(ATXFILE_L2_L1) $(SHUFFLE_PREFIX)bilphrases.$(L2)-$(L1).gz $(BIL_L2_L1)
	@echo "Extracing alignment templates from bilphrases."$(L2)"-"$(L1)".gz ..."
	apertium-validate-transfer-at $(ATXFILE_L2_L1)
	apertium-xtract-alignment-templates -x $(ATXFILE_L2_L1) -b $(BIL_L2_L1) -i $(SHUFFLE_PREFIX)bilphrases.$(L2)-$(L1).gz -o $@ -z -e 2> log.$(SHUFFLE_PREFIX)aligtem.$(L2)-$(L1)
	gzip log.$(SHUFFLE_PREFIX)aligtem.$(L2)-$(L1)
	@echo ""

$(SHUFFLE_PREFIX)alignment-templates.togeneralise.$(L1)-$(L2).gz: $(ATXFILE_L1_L2) $(SHUFFLE_PREFIX)bilphrases.$(L1)-$(L2).gz $(BIL_L1_L2)
	@echo "Extracing alignment templates from bilphrases."$(L1)"-"$(L2)".gz ..."
	apertium-validate-transfer-at $(ATXFILE_L1_L2)
	apertium-xtract-alignment-templates -k -x $(ATXFILE_L1_L2) -b $(BIL_L1_L2) -i $(SHUFFLE_PREFIX)bilphrases.$(L1)-$(L2).gz -o $@ -z -e 2> log.$(SHUFFLE_PREFIX)aligtem.togeneralise.$(L1)-$(L2)
	gzip log.$(SHUFFLE_PREFIX)aligtem.togeneralise.$(L1)-$(L2)
	@echo ""

$(SHUFFLE_PREFIX)alignment-templates.togeneralise.$(L2)-$(L1).gz: $(ATXFILE_L2_L1) $(SHUFFLE_PREFIX)bilphrases.$(L2)-$(L1).gz $(BIL_L2_L1)
	@echo "Extracing alignment templates from bilphrases."$(L2)"-"$(L1)".gz ..."
	apertium-validate-transfer-at $(ATXFILE_L2_L1)
	apertium-xtract-alignment-templates -k -x $(ATXFILE_L2_L1) -b $(BIL_L2_L1) -i $(SHUFFLE_PREFIX)bilphrases.$(L2)-$(L1).gz -o $@ -z -e 2> log.$(SHUFFLE_PREFIX)aligtem.togeneralise.$(L2)-$(L1)
	gzip log.$(SHUFFLE_PREFIX)aligtem.togeneralise.$(L2)-$(L1)
	@echo ""

#conclicts are removed later
$(SHUFFLE_PREFIX)bilphrases.noconflicts.$(L1)-$(L2).gz: $(SHUFFLE_PREFIX)bilphrases.$(L1)-$(L2).gz
	ln -s $(SHUFFLE_PREFIX)bilphrases.$(L1)-$(L2).gz $@

$(SHUFFLE_PREFIX)bilphrases.noconflicts.$(L2)-$(L1).gz: $(SHUFFLE_PREFIX)bilphrases.$(L2)-$(L1).gz
	ln -s $(SHUFFLE_PREFIX)bilphrases.$(L2)-$(L1).gz $@


$(SHUFFLE_PREFIX)alignment-templates.withalllemmas.togeneralise.$(L1)-$(L2).gz: $(ATXFILE_L1_L2) $(SHUFFLE_PREFIX)bilphrases.noconflicts.$(L1)-$(L2).gz $(BIL_L1_L2)
	@echo "Extracing alignment templates with all the lemmas from bilphrases."$(L1)"-"$(L2)".gz ..."
	apertium-validate-transfer-at $(ATXFILE_L1_L2)
	apertium-xtract-alignment-templates -k -l -u -x $(ATXFILE_L1_L2) -b $(BIL_L1_L2) -i $(SHUFFLE_PREFIX)bilphrases.noconflicts.$(L1)-$(L2).gz -o $@ -z -e 2> log.$(SHUFFLE_PREFIX)aligtem.withalllemmas.togeneralise.$(L1)-$(L2)
	gzip log.$(SHUFFLE_PREFIX)aligtem.withalllemmas.togeneralise.$(L1)-$(L2)
	@echo ""

$(SHUFFLE_PREFIX)alignment-templates.withalllemmas.togeneralise.$(L2)-$(L1).gz: $(ATXFILE_L2_L1) $(SHUFFLE_PREFIX)bilphrases.noconflicts.$(L2)-$(L1).gz $(BIL_L2_L1)
	@echo "Extracing alignment templates with all the lemmas from bilphrases."$(L2)"-"$(L1)".gz ..."
	apertium-validate-transfer-at $(ATXFILE_L2_L1)
	apertium-xtract-alignment-templates -k -l -u -x $(ATXFILE_L2_L1) -b $(BIL_L2_L1) -i $(SHUFFLE_PREFIX)bilphrases.noconflicts.$(L2)-$(L1).gz -o $@ -z -e 2> log.$(SHUFFLE_PREFIX)aligtem.withalllemmas.togeneralise.$(L2)-$(L1)
	gzip log.$(SHUFFLE_PREFIX)aligtem.withalllemmas.togeneralise.$(L2)-$(L1)
	@echo ""

$(SHUFFLE_PREFIX)alignment-templates.uniq.$(L1)-$(L2).gz: $(SHUFFLE_PREFIX)alignment-templates.$(L1)-$(L2).gz
	@echo "Unifying alignment templates from file alignment-templates."$(L1)"-"$(L2)".gz ..."
	zcat $(SHUFFLE_PREFIX)alignment-templates.$(L1)-$(L2).gz | ./filter_alignment_templates.awk |\
	cut -d' ' -f1 --complement | sort | uniq -c | sed -re "s/^[ ]+//g" | sort -t '|' -k 2,2 | gzip > $@
	@echo ""

$(SHUFFLE_PREFIX)alignment-templates.uniq.$(L2)-$(L1).gz: $(SHUFFLE_PREFIX)alignment-templates.$(L2)-$(L1).gz
	@echo "Unifying alignment templates from file alignment-templates."$(L2)"-"$(L1)".gz ..."
	zcat $(SHUFFLE_PREFIX)alignment-templates.$(L2)-$(L1).gz | ./filter_alignment_templates.awk |\
	cut -d' ' -f1 --complement | sort | uniq -c | sed -re "s/^[ ]+//g" | sort -t '|' -k 2,2 | gzip > $@
	@echo ""
	
$(SHUFFLE_PREFIX)alignment-templates.withalllemmas.togeneralise.uniq.$(L1)-$(L2).gz: $(SHUFFLE_PREFIX)alignment-templates.withalllemmas.togeneralise.$(L1)-$(L2).gz
	@echo "Unifying alignment templates from file alignment-templates.togeneralise."$(L1)"-"$(L2)".gz ..."
	zcat $(SHUFFLE_PREFIX)alignment-templates.withalllemmas.togeneralise.$(L1)-$(L2).gz | ./filter_alignment_templates.awk |\
	cut -d' ' -f1 --complement | sort | uniq -c | sed -re "s/^[ ]+//g" | sort -t '|' -k 2,2 | gzip > $@
	@echo ""
	
$(SHUFFLE_PREFIX)alignment-templates.withalllemmas.togeneralise.uniq.$(L2)-$(L1).gz: $(SHUFFLE_PREFIX)alignment-templates.withalllemmas.togeneralise.$(L2)-$(L1).gz
	@echo "Unifying alignment templates from file alignment-templates.togeneralise."$(L2)"-"$(L1)".gz ..."
	zcat $(SHUFFLE_PREFIX)alignment-templates.withalllemmas.togeneralise.$(L2)-$(L1).gz | ./filter_alignment_templates.awk |\
	cut -d' ' -f1 --complement | sort | uniq -c | sed -re "s/^[ ]+//g" | sort -t '|' -k 2,2 |  gzip > $@
	@echo ""

$(SHUFFLE_PREFIX)alignment-templates.withalllemmas.togeneralise.uniq.noconflicts.$(L1)-$(L2).gz: $(SHUFFLE_PREFIX)alignment-templates.withalllemmas.togeneralise.uniq.$(L1)-$(L2).gz
ifdef PAR_DONT_REMOVE_CONFLICTS
	ln -s $(SHUFFLE_PREFIX)alignment-templates.withalllemmas.togeneralise.uniq.$(L1)-$(L2).gz $@
else
	zcat $(SHUFFLE_PREFIX)alignment-templates.withalllemmas.togeneralise.uniq.$(L1)-$(L2).gz | LC_ALL=C sort -k 2 -t '|' | python $(RULELEARNINGLIBDIR)/removeBilphraseConflicts.py --first_field_is_actual_freq $(THRESHOLD_IN_BILPHRASE_CONTRADICTIONS) 2> discardedBilphrases.$(L1)-$(L2) | gzip > $@
endif

$(SHUFFLE_PREFIX)alignment-templates.withalllemmas.togeneralise.uniq.noconflicts.$(L2)-$(L1).gz: $(SHUFFLE_PREFIX)alignment-templates.withalllemmas.togeneralise.uniq.$(L2)-$(L1).gz
ifdef PAR_DONT_REMOVE_CONFLICTS
	ln -s $(SHUFFLE_PREFIX)alignment-templates.withalllemmas.togeneralise.uniq.$(L2)-$(L1).gz $@
else
	zcat$(SHUFFLE_PREFIX)alignment-templates.withalllemmas.togeneralise.uniq.$(L2)-$(L1).gz | LC_ALL=C sort -k 2 -t '|' | python $(RULELEARNINGLIBDIR)/removeBilphraseConflicts.py --first_field_is_actual_freq $(THRESHOLD_IN_BILPHRASE_CONTRADICTIONS) 2> discardedBilphrases.$(L2)-$(L1) | gzip > $@
endif

FILTERING_START:=$(SHUFFLE_PREFIX)alignment-templates.withalllemmas.togeneralise.uniq.noconflicts

$(FILTERING_START).$(FILTERING_OPTION).filtered$(FILTERING_REMOVE_CONFLICTS)-$(FILTERING_TH_COUNT)-$(FILTERING_CRITERION).$(L1)-$(L2).tllemmasfrombiling: $(FILTERING_START).$(L1)-$(L2).gz
	zcat $? | cut -f 2 -d '|'  | sed -r 's_^[ ]*__' | sed -r 's_[ ]*$$__' | sed -r 's_ _\t_g' | sed -r 's:_: :g' | apertium-apply-biling --biling $(BIL_L1_L2) --withqueue --explicitempty | sed -r 's_<[^>]*>__g' > $@

$(FILTERING_START).$(FILTERING_OPTION).filtered$(FILTERING_REMOVE_CONFLICTS)-$(FILTERING_TH_COUNT)-$(FILTERING_CRITERION).$(L2)-$(L1).tllemmasfrombiling: $(FILTERING_START).$(L2)-$(L1).gz
	zcat $? | cut -f 2 -d '|'  | sed -r 's_^[ ]*__' | sed -r 's_[ ]*$$__' | sed -r 's_ _\t_g' | sed -r 's:_: :g' | apertium-apply-biling --biling $(BIL_L2_L1) --withqueue --explicitempty | sed -r 's_<[^>]*>__g' > $@

ifdef PAR_MY_EXTRACTING
$(SHUFFLE_PREFIX)alignmentTemplatesPlusLemmas.withalllemmas.$(FILTERING_OPTION).filtered$(FILTERING_REMOVE_CONFLICTS)-$(FILTERING_TH_COUNT)-$(FILTERING_CRITERION).$(L1)-$(L2).gz: bilphrases.$(L1)-$(L2).gz
	bash processBilphrases.sh bilphrases.$(L1)-$(L2).gz $(BIL_L1_L2) $(RULELEARNINGLIBDIR) "$(PAR_ENDS_ALIGNED)" "$(PAR_SAME_STRUCTURE)" 2>  processBilphrases.$(L1)-$(L2).log > $@
else
$(SHUFFLE_PREFIX)alignmentTemplatesPlusLemmas.withalllemmas.$(FILTERING_OPTION).filtered$(FILTERING_REMOVE_CONFLICTS)-$(FILTERING_TH_COUNT)-$(FILTERING_CRITERION).$(L1)-$(L2).gz: $(FILTERING_START).$(L1)-$(L2).gz $(FILTERING_START).$(FILTERING_OPTION).filtered$(FILTERING_REMOVE_CONFLICTS)-$(FILTERING_TH_COUNT)-$(FILTERING_CRITERION).$(L1)-$(L2).tllemmasfrombiling
	zcat $(FILTERING_START).$(L1)-$(L2).gz | python $(RULELEARNINGLIBDIR)/generateATplusLemmasFromTransferTools.py | paste -d '|' - $(FILTERING_START).$(FILTERING_OPTION).filtered$(FILTERING_REMOVE_CONFLICTS)-$(FILTERING_TH_COUNT)-$(FILTERING_CRITERION).$(L1)-$(L2).tllemmasfrombiling | grep -v -F "__INCORRECT_ALIGNMENT_TEMPLATE__" | gzip > $@
endif

ifdef PAR_MY_EXTRACTING
$(SHUFFLE_PREFIX)alignmentTemplatesPlusLemmas.withalllemmas.$(FILTERING_OPTION).filtered$(FILTERING_REMOVE_CONFLICTS)-$(FILTERING_TH_COUNT)-$(FILTERING_CRITERION).$(L2)-$(L1).gz: bilphrases.$(L2)-$(L1).gz
	bash processBilphrases.sh bilphrases.$(L2)-$(L1).gz $(BIL_L2_L1) $(RULELEARNINGLIBDIR) "$(PAR_ENDS_ALIGNED)" "$(PAR_SAME_STRUCTURE)" 2>  processBilphrases.$(L2)-$(L1).log > $@
else
$(SHUFFLE_PREFIX)alignmentTemplatesPlusLemmas.withalllemmas.$(FILTERING_OPTION).filtered$(FILTERING_REMOVE_CONFLICTS)-$(FILTERING_TH_COUNT)-$(FILTERING_CRITERION).$(L2)-$(L1).gz: $(FILTERING_START).$(L2)-$(L1).gz $(FILTERING_START).$(FILTERING_OPTION).filtered$(FILTERING_REMOVE_CONFLICTS)-$(FILTERING_TH_COUNT)-$(FILTERING_CRITERION).$(L2)-$(L1).tllemmasfrombiling
	zcat $(FILTERING_START).$(L2)-$(L1).gz | python $(RULELEARNINGLIBDIR)/generateATplusLemmasFromTransferTools.py | paste -d '|' - $(FILTERING_START).$(FILTERING_OPTION).filtered$(FILTERING_REMOVE_CONFLICTS)-$(FILTERING_TH_COUNT)-$(FILTERING_CRITERION).$(L2)-$(L1).tllemmasfrombiling | grep -v -F "__INCORRECT_ALIGNMENT_TEMPLATE__" | gzip > $@
endif

	
#alignmentTemplatesPlusLemmas.withalllemmas.$(L1)-$(L2).txt
#	@echo "formatting them to be generalised"
#	python $(RULELEARNINGLIBDIR)/generateATplusLemmasFromTransferTools.py
#
#alignmentTemplatesPlusLemmas.withalllemmas.$(L2)-$(L1).txt
#	@echo "formatting them to be generalised"
#	python $(RULELEARNINGLIBDIR)/generateATplusLemmasFromTransferTools.py


./work-$(L1)-$(L2)/queries/1/experiment/alignmentTemplatesPlusLemmas.txt: alignmentTemplatesPlusLemmas.$(L1)-$(L2).txt
	@echo "Copying alignment templates plus lemmas $(L1)-$(L2)"
	mkdir -p "./work-$(L1)-$(L2)/queries/1/experiment/"
	cp alignmentTemplatesPlusLemmas.$(L1)-$(L2).txt $@
	@echo ""

./work-$(L2)-$(L1)/queries/1/experiment/alignmentTemplatesPlusLemmas.txt: alignmentTemplatesPlusLemmas.$(L2)-$(L1).txt
	@echo "Copying alignment templates plus lemmas $(L2)-$(L1)"
	mkdir -p "./work-$(L2)-$(L1)/queries/1/experiment/"
	cp alignmentTemplatesPlusLemmas.$(L2)-$(L1).txt $@
	@echo ""
	
trules.$(L1)-$(L2).xml: alignment-templates.uniq.$(L1)-$(L2).gz
	@echo "Converting alignment templates from file alignment-templates.uniq."$(L1)"-"$(L2)".gz into apertium transfer rules ... "
	apertium-gen-transfer-from-aligment-templates -i alignment-templates.uniq.$(L1)-$(L2).gz -m $(TH_COUNT) -c $(CRITERION) -z  $(DISCARD_RULE_OPTION)> $@ 2> log.trules.$(L1)-$(L2)
	@echo ""

trules.$(L1)-$(L2).debug.xml: trules.$(L1)-$(L2).xml
	cat $? | python $(RULELEARNINGLIBDIR)/addDebugInfoToTransferRules.py > $@

alignment-templates-included-in-rules.$(L1)-$(L2): trules.$(L1)-$(L2).xml
	cat trules.$(L1)-$(L2).xml | grep  '<when><!' | sed 's_ *<when><!--__' | sed 's_-->$$__' | sed 's_^[0-9]* | __' > $@

alignment-templates-included-in-rules.$(L1)-$(L2).patterns: alignment-templates-included-in-rules.$(L1)-$(L2)
	cut -f 1 -d'|' alignment-templates-included-in-rules.$(L1)-$(L2) > $@

trules.$(L2)-$(L1).xml: alignment-templates.uniq.$(L2)-$(L1).gz
	@echo "Converting alignment templates from file alignment-templates.uniq."$(L2)"-"$(L1)".gz into apertium transfer rules ... "
	apertium-gen-transfer-from-aligment-templates -i alignment-templates.uniq.$(L2)-$(L1).gz -m $(TH_COUNT) -c $(CRITERION) -z $(DISCARD_RULE_OPTION) > $@ 2> log.trules.$(L2)-$(L1)
	@echo ""
	
trules.$(L2)-$(L1).debug.xml: trules.$(L2)-$(L1).xml
	cat $? | python $(RULELEARNINGLIBDIR)/addDebugInfoToTransferRules.py > $@
	
alignment-templates-included-in-rules.$(L2)-$(L1): trules.$(L2)-$(L1).xml
	cat trules.$(L2)-$(L1).xml | grep  '<when><!' | sed 's_ *<when><!--__' | sed 's_-->$$__' | sed 's_^[0-9]* | __' > $@

alignment-templates-included-in-rules.$(L2)-$(L1).patterns: alignment-templates-included-in-rules.$(L2)-$(L1)
	cut -f 1 -d'|' alignment-templates-included-in-rules.$(L2)-$(L1) > $@

#Evaluation of non-generalised rules

trules.$(L1)-$(L2).bin: trules.$(L1)-$(L2).xml
	apertium-validate-transfer trules.$(L1)-$(L2).xml
	apertium-preprocess-transfer trules.$(L1)-$(L2).xml trules.$(L1)-$(L2).bin
	
trules.$(L1)-$(L2).debug.bin: trules.$(L1)-$(L2).debug.xml
	apertium-validate-transfer trules.$(L1)-$(L2).debug.xml
	apertium-preprocess-transfer trules.$(L1)-$(L2).debug.xml trules.$(L1)-$(L2).debug.bin

./modes/$(L1)-$(L2)-debug.mode: trules.$(L1)-$(L2).debug.xml trules.$(L1)-$(L2).debug.bin  $(BIL_L1_L2)
	@echo "Creating apertium mode $(L1)-$(L2)"
	mkdir -p ./modes/
	bash $(RULELEARNINGLIBDIR)/createModeWithLearnedRules.sh $(ORIGINAL_APERTIUM_MODES_DIR)/$(L1)-$(L2).mode ""  trules.$(L1)-$(L2).debug  apertium-$(L1)-$(L2).posttransfer.ptx  $(BIL_L1_L2) "python $(RULELEARNINGLIBDIR)/removeDebugInfoFromTransfer.py" "" > $@
	@echo ""
	
./modes/$(L1)-$(L2).mode: trules.$(L1)-$(L2).xml trules.$(L1)-$(L2).bin  $(BIL_L1_L2)
	@echo "Creating apertium mode $(L1)-$(L2)"
	mkdir -p ./modes/
	bash $(RULELEARNINGLIBDIR)/createModeWithLearnedRules.sh $(ORIGINAL_APERTIUM_MODES_DIR)/$(L1)-$(L2).mode ""  trules.$(L1)-$(L2)  apertium-$(L1)-$(L2).posttransfer.ptx  $(BIL_L1_L2) "python $(RULELEARNINGLIBDIR)/removeDebugInfoFromTransfer.py" "" > $@
	@echo ""
	
./modes/$(L1)-$(L2)-norules.mode: empty-rules-for-translating.t1x  $(BIL_L1_L2)
	@echo "Creating apertium mode $(L1)-$(L2) word-for-word"
	mkdir -p ./modes/
	apertium-preprocess-transfer empty-rules-for-translating.t1x empty-rules-for-translating.bin
	bash $(RULELEARNINGLIBDIR)/createModeNoRules.sh $(ORIGINAL_APERTIUM_MODES_DIR)/$(L1)-$(L2).mode  "" empty-rules-for-translating.t1x empty-rules-for-translating.bin apertium-$(L1)-$(L2).posttransfer.ptx   $(BIL_L1_L2) > $@
	@echo ""
	
test.$(L1)-$(L2).source: $(TESTCORPUS).$(L1)
	cat $? > $@
	
test.$(L1)-$(L2).reference: $(TESTCORPUS).$(L2)
	cat $? > $@

test.$(L1)-$(L2).translation: test.$(L1)-$(L2).source ./modes/$(L1)-$(L2).mode
	@echo "Translating test corpus"
	cat test.$(L1)-$(L2).source | bash $(RULELEARNINGLIBDIR)/translate_apertium.sh "" $(L1)-$(L2) join "" "." > $@
	@echo ""
	
test.$(L1)-$(L2).translation-debug-debuginfo: test.$(L1)-$(L2).source ./modes/$(L1)-$(L2)-debug.mode
	@echo "Translating test corpus"
	cat test.$(L1)-$(L2).source | bash $(RULELEARNINGLIBDIR)/translate_apertium.sh "" $(L1)-$(L2)-debug join "" "." > test.$(L1)-$(L2).translation-debug 2>test.$(L1)-$(L2).translation-debug-debuginfo
	@echo ""
	
test.$(L1)-$(L2).translation-norules: test.$(L1)-$(L2).source ./modes/$(L1)-$(L2)-norules.mode
	@echo "Translating test corpus word-for-word"
	cat test.$(L1)-$(L2).source | bash $(RULELEARNINGLIBDIR)/translate_apertium.sh "" $(L1)-$(L2)-norules join "" "." > $@
	@echo ""
	
evaluation.$(L1)-$(L2): test.$(L1)-$(L2).source test.$(L1)-$(L2).reference test.$(L1)-$(L2).translation
	@echo "Evaluating learned rules witout generalisation"
	bash $(RULELEARNINGLIBDIR)/mteval-v11b-nosgm.sh test.$(L1)-$(L2).source test.$(L1)-$(L2).reference test.$(L1)-$(L2).translation > $@
	@echo ""

evaluation.$(L1)-$(L2)-norules: test.$(L1)-$(L2).source test.$(L1)-$(L2).reference test.$(L1)-$(L2).translation-norules
	@echo "Evaluating word-for-word"
	bash $(RULELEARNINGLIBDIR)/mteval-v11b-nosgm.sh test.$(L1)-$(L2).source test.$(L1)-$(L2).reference test.$(L1)-$(L2).translation-norules > $@
	@echo ""

report-rules-$(L1)-$(L2).1: test.$(L1)-$(L2).translation-debug-debuginfo alignment-templates-included-in-rules.$(L1)-$(L2)
	cat test.$(L1)-$(L2).translation-debug-debuginfo | grep -v "LOCALE:" | grep -v '^0$$' | grep -v '^ww' | LC_ALL=C sort | uniq -c | sort -r -n -k 1,1 | sed 's_^ *__'   | while read line ; do FREQ=`echo "$$line" | cut -f 1 -d ' ' `; ATNUM=`echo "$$line" | cut -f 2 -d ' '`; AT=`head -n $$ATNUM alignment-templates-included-in-rules.$(L1)-$(L2) | tail -n 1 `;MODAT=`echo "$$AT" | sed 's_^[^|]*|__'`; ISNEWAT=0; echo "$$FREQ $$ISNEWAT $$AT"  ; done > $@

report-rules-$(L1)-$(L2).2: test.$(L1)-$(L2).translation-debug-debuginfo alignment-templates-included-in-rules.$(L1)-$(L2).patterns 
	cat test.$(L1)-$(L2).translation-debug-debuginfo | grep -v "LOCALE:" | grep '^ww' | sed 's_^ww__' | LC_ALL=C sort | uniq -c | sort -r -n -k 1,1 | sed 's_^ *__'   | while read line ; do FREQ=`echo "$$line" | cut -f 1 -d ' ' `; ATNUM=`echo "$$line" | cut -f 2 -d ' '`; AT=`head -n $$ATNUM alignment-templates-included-in-rules.$(L1)-$(L2).patterns  | tail -n 1 `;MODAT=`echo "$$AT" | sed 's_^[^|]*|__'`; ISNEWAT=0; echo "$$FREQ $$ISNEWAT $$AT"  ; done > $@
	
report-rules-$(L1)-$(L2).3: test.$(L1)-$(L2).translation-debug-debuginfo
	cat test.$(L1)-$(L2).translation-debug-debuginfo | grep -v "LOCALE:" | grep '^0$$' | wc -l > $@
	
report-rules-$(L1)-$(L2)-words: report-rules-$(L1)-$(L2).1 report-rules-$(L1)-$(L2).2 report-rules-$(L1)-$(L2).3
	cat report-rules-$(L1)-$(L2).1 report-rules-$(L1)-$(L2).2 report-rules-$(L1)-$(L2).3 | python $(RULELEARNINGLIBDIR)/addWordInforToReport.py  > $@

report-rules-$(L1)-$(L2)-words-sum: report-rules-$(L1)-$(L2)-words
	cat report-rules-$(L1)-$(L2)-words | python $(RULELEARNINGLIBDIR)/summarizeReport.py > $@
  

trules.$(L2)-$(L1).bin: trules.$(L2)-$(L1).xml
	apertium-validate-transfer trules.$(L2)-$(L1).xml
	apertium-preprocess-transfer trules.$(L2)-$(L1).xml trules.$(L2)-$(L1).bin
	
trules.$(L2)-$(L1).debug.bin: trules.$(L2)-$(L1).debug.xml
	apertium-validate-transfer trules.$(L2)-$(L1).debug.xml
	apertium-preprocess-transfer trules.$(L2)-$(L1).debug.xml trules.$(L2)-$(L1).debug.bin

./modes/$(L2)-$(L1)-debug.mode: trules.$(L2)-$(L1).debug.xml trules.$(L2)-$(L1).debug.bin  $(BIL_L2_L1)
	@echo "Creating apertium debug mode $(L2)-$(L1)"
	mkdir -p ./modes/
	bash $(RULELEARNINGLIBDIR)/createModeWithLearnedRules.sh $(ORIGINAL_APERTIUM_MODES_DIR)/$(L2)-$(L1).mode ""  trules.$(L2)-$(L1).debug  apertium-$(L2)-$(L1).posttransfer.ptx  $(BIL_L2_L1) "python $(RULELEARNINGLIBDIR)/removeDebugInfoFromTransfer.py" "" > $@
	@echo ""


./modes/$(L2)-$(L1).mode: trules.$(L2)-$(L1).xml trules.$(L2)-$(L1).bin $(BIL_L2_L1)
	@echo "Creating apertium mode $(L2)-$(L1)"
	mkdir -p ./modes/
	bash $(RULELEARNINGLIBDIR)/createModeWithLearnedRules.sh $(ORIGINAL_APERTIUM_MODES_DIR)/$(L2)-$(L1).mode ""  trules.$(L2)-$(L1)  apertium-$(L2)-$(L1).posttransfer.ptx  $(BIL_L2_L1) "python $(RULELEARNINGLIBDIR)/removeDebugInfoFromTransfer.py" "" > $@
	@echo ""

./modes/$(L2)-$(L1)-norules.mode: empty-rules-for-translating.t1x $(BIL_L2_L1)
	@echo "Creating apertium mode $(L2)-$(L1) word-for-word"
	mkdir -p ./modes/
	apertium-preprocess-transfer empty-rules-for-translating.t1x empty-rules-for-translating.bin
	bash $(RULELEARNINGLIBDIR)/createModeNoRules.sh $(ORIGINAL_APERTIUM_MODES_DIR)/$(L2)-$(L1).mode  "" empty-rules-for-translating.t1x empty-rules-for-translating.bin apertium-$(L2)-$(L1).posttransfer.ptx  $(BIL_L2_L1) > $@
	@echo ""

test.$(L2)-$(L1).source: $(TESTCORPUS).$(L2)
	cat $? > $@
	
test.$(L2)-$(L1).reference: $(TESTCORPUS).$(L1)
	cat $? > $@

test.$(L2)-$(L1).translation: test.$(L2)-$(L1).source ./modes/$(L2)-$(L1).mode
	@echo "Translating test corpus"
	cat test.$(L2)-$(L1).source | bash $(RULELEARNINGLIBDIR)/translate_apertium.sh "" $(L2)-$(L1) join "" "." > $@
	@echo ""
	
test.$(L2)-$(L1).translation-debug-debuginfo: test.$(L2)-$(L1).source ./modes/$(L2)-$(L1)-debug.mode
	@echo "Translating test corpus"
	cat test.$(L2)-$(L1).source | bash $(RULELEARNINGLIBDIR)/translate_apertium.sh "" $(L2)-$(L1)-debug join "" "." > test.$(L2)-$(L1).translation-debug 2>test.$(L2)-$(L1).translation-debug-debuginfo
	@echo ""

test.$(L2)-$(L1).translation-norules: test.$(L2)-$(L1).source ./modes/$(L2)-$(L1)-norules.mode
	@echo "Translating test corpus word-for-word"
	cat test.$(L2)-$(L1).source | bash $(RULELEARNINGLIBDIR)/translate_apertium.sh "" $(L2)-$(L1)-norules join "" "." > $@
	@echo ""	

evaluation.$(L2)-$(L1): test.$(L2)-$(L1).source test.$(L2)-$(L1).reference test.$(L2)-$(L1).translation
	@echo "Evaluating learned rules witout generalisation"
	bash $(RULELEARNINGLIBDIR)/mteval-v11b-nosgm.sh test.$(L2)-$(L1).source test.$(L2)-$(L1).reference test.$(L2)-$(L1).translation > $@
	@echo ""
	
evaluation.$(L2)-$(L1)-norules: test.$(L2)-$(L1).source test.$(L2)-$(L1).reference test.$(L2)-$(L1).translation-norules
	@echo "Evaluating word-for-word"
	bash $(RULELEARNINGLIBDIR)/mteval-v11b-nosgm.sh test.$(L2)-$(L1).source test.$(L2)-$(L1).reference test.$(L2)-$(L1).translation-norules > $@
	@echo ""

report-rules-$(L2)-$(L1).1: test.$(L2)-$(L1).translation-debug-debuginfo alignment-templates-included-in-rules.$(L2)-$(L1)
	cat test.$(L2)-$(L1).translation-debug-debuginfo | grep -v "LOCALE:" | grep -v '^0$$' | grep -v '^ww' | LC_ALL=C sort | uniq -c | sort -r -n -k 1,1 | sed 's_^ *__'   | while read line ; do FREQ=`echo "$$line" | cut -f 1 -d ' ' `; ATNUM=`echo "$$line" | cut -f 2 -d ' '`; AT=`head -n $$ATNUM alignment-templates-included-in-rules.$(L2)-$(L1) | tail -n 1 `;MODAT=`echo "$$AT" | sed 's_^[^|]*|__'`; ISNEWAT=0; echo "$$FREQ $$ISNEWAT $$AT"  ; done > $@

report-rules-$(L2)-$(L1).2: test.$(L2)-$(L1).translation-debug-debuginfo alignment-templates-included-in-rules.$(L2)-$(L1).patterns 
	cat test.$(L2)-$(L1).translation-debug-debuginfo | grep -v "LOCALE:" | grep '^ww' | sed 's_^ww__' | LC_ALL=C sort | uniq -c | sort -r -n -k 1,1 | sed 's_^ *__'   | while read line ; do FREQ=`echo "$$line" | cut -f 1 -d ' ' `; ATNUM=`echo "$$line" | cut -f 2 -d ' '`; AT=`head -n $$ATNUM alignment-templates-included-in-rules.$(L2)-$(L1).patterns  | tail -n 1 `;MODAT=`echo "$$AT" | sed 's_^[^|]*|__'`; ISNEWAT=`cat /tmp/prevpatterns | grep "$MODAT\$$" | wc -l`; echo "$$FREQ $$ISNEWAT $$AT"  ; done > $@
	
report-rules-$(L2)-$(L1).3: test.$(L2)-$(L1).translation-debug-debuginfo
	cat test.$(L2)-$(L1).translation-debug-debuginfo | grep -v "LOCALE:" | grep '^0$$' | wc -l > $@
	
report-rules-$(L2)-$(L1)-words: report-rules-$(L2)-$(L1).1 report-rules-$(L2)-$(L1).2 report-rules-$(L2)-$(L1).3
	cat report-rules-$(L2)-$(L1).1 report-rules-$(L2)-$(L1).2 report-rules-$(L2)-$(L1).3 | python $(RULELEARNINGLIBDIR)/addWordInforToReport.py  > $@

report-rules-$(L2)-$(L1)-words-sum: report-rules-$(L2)-$(L1)-words
	cat report-rules-$(L2)-$(L1)-words | python $(RULELEARNINGLIBDIR)/summarizeReport.py > $@
  

clean: 
	rm -f $(CLEANFILES) *~
