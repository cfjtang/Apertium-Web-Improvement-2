VERSION=0.1
LANG1=sl
LANG2=mk
PREFIX1=$(LANG1)-$(LANG2)
PREFIX2=$(LANG2)-$(LANG1)
TAGGER1=$(LANG1)-tagger-data
TAGGER2=$(LANG2)-tagger-data

TAGGER_UNSUPERVISED_ITERATIONS=8

#Do not modify this, if you do that make sure *.crp.txt files exist
TAGGER_SUPERVISED_ITERATIONS=0

BASENAME=apertium-$(PREFIX1)

TARGETS_COMMON = $(PREFIX1).automorf.bin $(PREFIX1).autobil.bin $(PREFIX1).autobil.empty_for_postprocessrules.bin $(PREFIX1).autobil.empty_for_postprocessrules_n.bin $(PREFIX1).autobil.empty_for_postprocessrules_adj.bin $(PREFIX1).autobil.empty_for_postprocessrules_adv.bin $(PREFIX1).autobil.empty_for_postprocessrules_vbser.bin $(PREFIX1).autobil.empty_for_postprocessrules_vblex.bin $(PREFIX1).autogen.bin \
     $(PREFIX1).autopgen.bin $(PREFIX2).automorf.bin $(PREFIX2).autobil.bin $(PREFIX2).autobil.empty_for_postprocessrules.bin \
$(PREFIX2).autobil.empty_for_postprocessrules_n.bin \
$(PREFIX2).autobil.empty_for_postprocessrules_adj.bin \
$(PREFIX2).autobil.empty_for_postprocessrules_adv.bin \
$(PREFIX2).autobil.empty_for_postprocessrules_vbser.bin \
$(PREFIX2).autobil.empty_for_postprocessrules_vblex.bin \
     $(PREFIX2).autogen.bin $(PREFIX2).autopgen.bin

TARGETS = $(TARGETS_COMMON) trules-$(PREFIX1).bin trules-$(PREFIX2).bin \
          trules-$(PREFIX1).xml trules-$(PREFIX2).xml

TARGETS_OLD = $(TARGETS_COMMON) $(PREFIX1).transfer $(PREFIX1).tagger \
              $(PREFIX2).transfer $(PREFIX2).tagger 

TARGETS_TAGGER_UNSUP=$(TAGGER1)/$(LANG1).dic \
                     $(TAGGER2)/$(LANG2).dic \
                     $(TAGGER1)/$(LANG1).crp $(TAGGER2)/$(LANG2).crp

TARGETS_TAGGER_SUP= $(TAGGER1)/$(LANG1).dic \
                   $(TAGGER1)/$(LANG1).tagged \
                   $(TAGGER1)/$(LANG1).untagged \
                   $(TAGGER2)/$(LANG2).dic \
                   $(TAGGER2)/$(LANG2).tagged \
                   $(TAGGER2)/$(LANG2).untagged

TARGETS_TAGGER_COMMON = $(PREFIX1).prob $(PREFIX2).prob 

DISTFILES = $(BASENAME).$(LANG1).dix $(BASENAME).$(LANG2).dix \
	    $(BASENAME).$(PREFIX1).dix $(BASENAME).post-$(LANG1).dix \
	    $(BASENAME).post-$(LANG2).dix $(BASENAME).$(LANG1).tsx \
	    $(BASENAME).$(LANG2).tsx $(BASENAME).trules-$(PREFIX1).xml \
	    $(BASENAME).trules-$(PREFIX2).xml Makefile $(TAGGER1)/$(LANG1).tagged \
	    $(TAGGER2)/$(LANG2).tagged  $(TAGGER1)/$(LANG1).tagged.txt \
	    $(TAGGER2)/$(LANG2).tagged.txt README COPYING \
	    $(PREFIX1).prob $(PREFIX2).prob
	    
DISTCOMP = $(PREFIX1).automorf.bin $(PREFIX2).automorf.bin \
           $(PREFIX1).autobil.bin $(PREFIX2).autobil.bin \
	   $(PREFIX1).autobil.empty_for_postprocessrules.bin \
	   $(PREFIX2).autobil.empty_for_postprocessrules.bin \
	   $(PREFIX1).autobil.empty_for_postprocessrules_n.bin \
	   $(PREFIX2).autobil.empty_for_postprocessrules_n.bin \
	   $(PREFIX1).autobil.empty_for_postprocessrules_adj.bin \
	   $(PREFIX2).autobil.empty_for_postprocessrules_adj.bin \
	   $(PREFIX1).autobil.empty_for_postprocessrules_adv.bin \
	   $(PREFIX2).autobil.empty_for_postprocessrules_adv.bin \
	   $(PREFIX1).autobil.empty_for_postprocessrules_vbser.bin \
	   $(PREFIX2).autobil.empty_for_postprocessrules_vbser.bin \
	   $(PREFIX1).autobil.empty_for_postprocessrules_vblex.bin \
	   $(PREFIX2).autobil.empty_for_postprocessrules_vblex.bin \
           $(PREFIX1).autogen.bin $(PREFIX2).autogen.bin \
           $(PREFIX1).autopgen.bin $(PREFIX2).autopgen.bin \
           trules-$(PREFIX1).xml trules-$(PREFIX2).xml \
           trules-$(PREFIX1).bin trules-$(PREFIX2).bin \
           $(PREFIX1).prob $(PREFIX2).prob

all: $(TARGETS)
	@rm -f .old 

old: $(TARGETS_OLD)
	@touch .old

tagger_train: tagger_supervised

tagger_supervised: .tagger_supervised $(TARGETS_TAGGER_SUP) $(TARGETS_TAGGER_COMMON)

.tagger_supervised:
	@if [[ -e .tagger_unsupervised ]]; \
	then rm -f $(TARGETS_TAGGER_COMMON) .tagger_unsupervised; \
	rm -f $(TAGGER1)/$(LANG1).crp $(TAGGER2)/$(LANG2).crp; \
	fi;
	@touch .tagger_supervised

tagger_unsupervised: .tagger_unsupervised $(TARGETS_TAGGER_UNSUP) $(TARGETS_TAGGER_COMMON)

.tagger_unsupervised:
	@if [[ -e .tagger_supervised ]];\
	then rm -f $(TARGETS_TAGGER_COMMON) .tagger_supervised; \
	rm -f $(TAGGER1)/$(LANG1).crp $(TAGGER2)/$(LANG2).crp; \
	fi;
	@touch .tagger_unsupervised

clean: 
	rm -f $(TARGETS) $(PREFIX1).tagger $(PREFIX1).transfer \
	                 $(PREFIX2).tagger $(PREFIX2).transfer
	rm postprocessrules*.xml

tagger_clean:
	rm -f $(PREFIX1).amb $(PREFIX1).prob \
	      $(PREFIX2).amb $(PREFIX2).prob \
	      $(TAGGER1)/$(LANG1).crp $(TAGGER2)/$(LANG2).crp \
	      $(TAGGER1)/$(LANG1).dic $(TAGGER2)/$(LANG2).dic \
	      $(TAGGER1)/$(LANG1).untagged $(TAGGER2)/$(LANG2).untagged 

$(PREFIX1).automorf.bin: $(BASENAME).$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).$(LANG1).dix
	lt-comp lr $(BASENAME).$(LANG1).dix $@

$(PREFIX1).autobil.bin: $(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary $(BASENAME).$(PREFIX1).dix
	lt-comp lr $(BASENAME).$(PREFIX1).dix $@

$(PREFIX1).autobil.empty_for_postprocessrules.bin: $(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix
	lt-comp lr $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix $@

$(PREFIX1).autobil.empty_for_postprocessrules_n.bin: $(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix
	lt-comp lr $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix $@

$(PREFIX1).autobil.empty_for_postprocessrules_adj.bin: $(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix
	lt-comp lr $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix $@

$(PREFIX1).autobil.empty_for_postprocessrules_adv.bin: $(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix
	lt-comp lr $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix $@

$(PREFIX1).autobil.empty_for_postprocessrules_vbser.bin: $(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix
	lt-comp lr $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix $@

$(PREFIX1).autobil.empty_for_postprocessrules_vblex.bin: $(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix
	lt-comp lr $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix $@

$(PREFIX1).autogen.bin: $(BASENAME).$(LANG2).dix
	apertium-validate-dictionary $(BASENAME).$(LANG2).dix
	lt-comp rl $(BASENAME).$(LANG2).dix $@

$(PREFIX1).autopgen.bin: $(BASENAME).post-$(LANG2).dix
	apertium-validate-dictionary $(BASENAME).post-$(LANG2).dix
	lt-comp lr $(BASENAME).post-$(LANG2).dix $@

$(PREFIX2).automorf.bin: $(BASENAME).$(LANG2).dix
	apertium-validate-dictionary $(BASENAME).$(LANG2).dix
	lt-comp lr $(BASENAME).$(LANG2).dix $@

$(PREFIX2).autobil.empty_for_postprocessrules.bin: $(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix
	lt-comp rl $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix $@

$(PREFIX2).autobil.empty_for_postprocessrules_n.bin: $(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix
	lt-comp rl $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix $@

$(PREFIX2).autobil.empty_for_postprocessrules_adj.bin: $(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix
	lt-comp rl $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix $@

$(PREFIX2).autobil.empty_for_postprocessrules_adv.bin: $(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix
	lt-comp rl $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix $@

$(PREFIX2).autobil.empty_for_postprocessrules_vbser.bin: $(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix
	lt-comp rl $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix $@

$(PREFIX2).autobil.empty_for_postprocessrules_vblex.bin: $(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix
	lt-comp rl $(BASENAME).$(PREFIX1).empty_for_postprocessrules.dix $@

$(PREFIX2).autobil.bin: $(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary $(BASENAME).$(PREFIX1).dix
	lt-comp rl $(BASENAME).$(PREFIX1).dix $@

$(PREFIX2).autogen.bin: $(BASENAME).$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).$(LANG1).dix
	lt-comp rl $(BASENAME).$(LANG1).dix $@

$(PREFIX2).autopgen.bin: $(BASENAME).post-$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).post-$(LANG1).dix
	lt-comp lr $(BASENAME).post-$(LANG1).dix $@

trules-$(PREFIX1).bin: $(BASENAME).trules-$(PREFIX1).xml
	apertium-validate-transfer $(BASENAME).trules-$(PREFIX1).xml
	apertium-preprocess-transfer $(BASENAME).trules-$(PREFIX1).xml \
	                             trules-$(PREFIX1).bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules-$(PREFIX1).xml \
				postprocessrules-$(PREFIX1).bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules_n_-$(PREFIX1).xml \
				postprocessrules-$(PREFIX1)_n.bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules_adj_-$(PREFIX1).xml \
				postprocessrules-$(PREFIX1)_adj.bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules_adv_-$(PREFIX1).xml \
				postprocessrules-$(PREFIX1)_adv.bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules_vbser_-$(PREFIX1).xml \
				postprocessrules-$(PREFIX1)_vbser.bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules_vblex_-$(PREFIX1).xml \
				postprocessrules-$(PREFIX1)_vblex.bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules_n_-$(PREFIX1)_2.xml \
				postprocessrules-$(PREFIX1)_n_2.bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules_adj_-$(PREFIX1)_2.xml \
				postprocessrules-$(PREFIX1)_adj_2.bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules_adv_-$(PREFIX1)_2.xml \
				postprocessrules-$(PREFIX1)_adv_2.bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules_vbser_-$(PREFIX1)_2.xml \
				postprocessrules-$(PREFIX1)_vbser_2.bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules_vblex_-$(PREFIX1)_2.xml \
				postprocessrules-$(PREFIX1)_vblex_2.bin
	                             
trules-$(PREFIX2).bin: $(BASENAME).trules-$(PREFIX2).xml
	apertium-validate-transfer $(BASENAME).trules-$(PREFIX2).xml
	apertium-preprocess-transfer $(BASENAME).trules-$(PREFIX2).xml \
	                             trules-$(PREFIX2).bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules-$(PREFIX2).xml \
				postprocessrules-$(PREFIX2).bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules_n_-$(PREFIX2).xml \
				postprocessrules-$(PREFIX2)_n.bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules_adj_-$(PREFIX2).xml \
				postprocessrules-$(PREFIX2)_adj.bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules_adv_-$(PREFIX2).xml \
				postprocessrules-$(PREFIX2)_adv.bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules_vbser_-$(PREFIX2).xml \
				postprocessrules-$(PREFIX2)_vbser.bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules_vblex_-$(PREFIX2).xml \
				postprocessrules-$(PREFIX2)_vblex.bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules_n_-$(PREFIX2)_2.xml \
				postprocessrules-$(PREFIX2)_n_2.bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules_adj_-$(PREFIX2)_2.xml \
				postprocessrules-$(PREFIX2)_adj_2.bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules_adv_-$(PREFIX2)_2.xml \
				postprocessrules-$(PREFIX2)_adv_2.bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules_vbser_-$(PREFIX2)_2.xml \
				postprocessrules-$(PREFIX2)_vbser_2.bin
	apertium-preprocess-transfer $(BASENAME).postprocessrules_vblex_-$(PREFIX2)_2.xml \
				postprocessrules-$(PREFIX2)_vblex_2.bin

trules-$(PREFIX1).xml: $(BASENAME).trules-$(PREFIX1).xml
	ln -s $(BASENAME).trules-$(PREFIX1).xml trules-$(PREFIX1).xml
	ln -s $(BASENAME).postprocessrules-$(PREFIX1).xml postprocessrules-$(PREFIX1).xml
	ln -s $(BASENAME).postprocessrules_n_-$(PREFIX1).xml postprocessrules-$(PREFIX1)_n.xml
	ln -s $(BASENAME).postprocessrules_adj_-$(PREFIX1).xml postprocessrules-$(PREFIX1)_adj.xml
	ln -s $(BASENAME).postprocessrules_adv_-$(PREFIX1).xml postprocessrules-$(PREFIX1)_adv.xml
	ln -s $(BASENAME).postprocessrules_vbser_-$(PREFIX1).xml postprocessrules-$(PREFIX1)_vbser.xml
	ln -s $(BASENAME).postprocessrules_vblex_-$(PREFIX1).xml postprocessrules-$(PREFIX1)_vblex.xml
	ln -s $(BASENAME).postprocessrules_n_-$(PREFIX1)_2.xml postprocessrules-$(PREFIX1)_n_2.xml
	ln -s $(BASENAME).postprocessrules_adj_-$(PREFIX1)_2.xml postprocessrules-$(PREFIX1)_adj_2.xml
	ln -s $(BASENAME).postprocessrules_adv_-$(PREFIX1)_2.xml postprocessrules-$(PREFIX1)_adv_2.xml
	ln -s $(BASENAME).postprocessrules_vbser_-$(PREFIX1)_2.xml postprocessrules-$(PREFIX1)_vbser_2.xml
	ln -s $(BASENAME).postprocessrules_vblex_-$(PREFIX1)_2.xml postprocessrules-$(PREFIX1)_vblex_2.xml

trules-$(PREFIX2).xml: $(BASENAME).trules-$(PREFIX2).xml
	ln -s $(BASENAME).trules-$(PREFIX2).xml trules-$(PREFIX2).xml
	ln -s $(BASENAME).postprocessrules-$(PREFIX2).xml postprocessrules-$(PREFIX2).xml
	ln -s $(BASENAME).postprocessrules_n_-$(PREFIX2).xml postprocessrules-$(PREFIX2)_n.xml
	ln -s $(BASENAME).postprocessrules_adj_-$(PREFIX2).xml postprocessrules-$(PREFIX2)_adj.xml
	ln -s $(BASENAME).postprocessrules_adv_-$(PREFIX2).xml postprocessrules-$(PREFIX2)_adv.xml
	ln -s $(BASENAME).postprocessrules_vbser_-$(PREFIX2).xml postprocessrules-$(PREFIX2)_vbser.xml
	ln -s $(BASENAME).postprocessrules_vblex_-$(PREFIX2).xml postprocessrules-$(PREFIX2)_vblex.xml
	ln -s $(BASENAME).postprocessrules_n_-$(PREFIX2)_2.xml postprocessrules-$(PREFIX2)_n_2.xml
	ln -s $(BASENAME).postprocessrules_adj_-$(PREFIX2)_2.xml postprocessrules-$(PREFIX2)_adj_2.xml
	ln -s $(BASENAME).postprocessrules_adv_-$(PREFIX2)_2.xml postprocessrules-$(PREFIX2)_adv_2.xml
	ln -s $(BASENAME).postprocessrules_vbser_-$(PREFIX2)_2.xml postprocessrules-$(PREFIX2)_vbser_2.xml
	ln -s $(BASENAME).postprocessrules_vblex_-$(PREFIX2)_2.xml postprocessrules-$(PREFIX2)_vblex_2.xml

$(PREFIX1).tagger: $(BASENAME).$(LANG1).tsx
	apertium-gen-tagger -O $(BASENAME).$(LANG1).tsx $@

$(PREFIX2).tagger: $(BASENAME).$(LANG2).tsx
	apertium-gen-tagger -O $(BASENAME).$(LANG2).tsx $@

$(PREFIX1).transfer: $(BASENAME).trules-$(PREFIX1).xml
	apertium-gen-transfer -O $(BASENAME).trules-$(PREFIX1).xml $@

$(PREFIX2).transfer: $(BASENAME).trules-$(PREFIX2).xml
	apertium-gen-transfer -O $(BASENAME).trules-$(PREFIX2).xml $@

$(TAGGER1)/$(LANG1).dic: $(BASENAME).$(LANG1).dix $(PREFIX1).automorf.bin
	@echo "Generating $@";
	@echo "This may take some time. Please, take a cup of coffee and come back later.";
	apertium-validate-dictionary $(BASENAME).$(LANG1).dix
	apertium-validate-tagger $(BASENAME).$(LANG1).tsx
	lt-expand $(BASENAME).$(LANG1).dix | grep -v "__REGEXP__" | grep -v ":<:" |\
	gawk 'BEGIN{FS=":>:|:"}{print $$1;}' | apertium-destxt >$(LANG1).dic.expanded
	@echo "." >>$(LANG1).dic.expanded
	@echo "?" >>$(LANG1).dic.expanded
	@echo ";" >>$(LANG1).dic.expanded
	@echo ":" >>$(LANG1).dic.expanded
	@echo "!" >>$(LANG1).dic.expanded
	@echo "42" >>$(LANG1).dic.expanded
	@echo "," >>$(LANG1).dic.expanded
	@echo "(" >>$(LANG1).dic.expanded
	@echo "\\[" >>$(LANG1).dic.expanded
	@echo ")" >>$(LANG1).dic.expanded
	@echo "\\]" >>$(LANG1).dic.expanded
	@echo "�" >>$(LANG1).dic.expanded
	@echo "�" >>$(LANG1).dic.expanded
	lt-proc -a $(PREFIX1).automorf.bin <$(LANG1).dic.expanded | \
	apertium-filter-ambiguity $(BASENAME).$(LANG1).tsx > $@
	rm $(LANG1).dic.expanded;

$(TAGGER2)/$(LANG2).dic: $(BASENAME).$(LANG2).dix $(PREFIX2).automorf.bin
	@echo "Generating $@";
	@echo "This may take some time. Please, take a cup of coffee and come back later.";
	apertium-validate-dictionary $(BASENAME).$(LANG2).dix
	apertium-validate-tagger $(BASENAME).$(LANG2).tsx
	lt-expand $(BASENAME).$(LANG2).dix | grep -v "__REGEXP__" | grep -v ":<:" |\
	gawk 'BEGIN{FS=":>:|:"}{print $$1;}' | apertium-destxt >$(LANG2).dic.expanded
	@echo "." >>$(LANG2).dic.expanded
	@echo "?" >>$(LANG2).dic.expanded
	@echo ";" >>$(LANG2).dic.expanded
	@echo ":" >>$(LANG2).dic.expanded
	@echo "!" >>$(LANG2).dic.expanded
	@echo "42" >>$(LANG2).dic.expanded
	@echo "," >>$(LANG2).dic.expanded
	@echo "(" >>$(LANG2).dic.expanded
	@echo "\\[" >>$(LANG2).dic.expanded
	@echo ")" >>$(LANG2).dic.expanded
	@echo "\\]" >>$(LANG2).dic.expanded
	@echo "�" >>$(LANG2).dic.expanded
	@echo "�" >>$(LANG2).dic.expanded
	lt-proc -a $(PREFIX2).automorf.bin <$(LANG2).dic.expanded | \
	apertium-filter-ambiguity $(BASENAME).$(LANG2).tsx > $@
	rm $(LANG2).dic.expanded;
	
$(TAGGER1)/$(LANG1).crp.txt:
	@if [ -e .tagger_unsupervised ]; \
	then echo "Error: File '"$(TAGGER1)/$(LANG1).crp"' is needed to perform an unsupervised tagger training" 1>&2; \
	echo "In order to generate the file '"$(TAGGER1)/$(LANG1).crp"' the file '"$(TAGGER1)/$(LANG1).crp.txt"' must exist" 1>&2; \
	echo "Please provide a large raw txt corpus with at least 200000 words." 1>&2; \
	exit 1; \
	else touch $@; \
	fi
	

$(TAGGER2)/$(LANG2).crp.txt:
	@if [ -e .tagger_unsupervised ]; \
	then echo "Error: File '"$(TAGGER2)/$(LANG2).crp"' is needed to perform an unsupervised tagger training" 1>&2; \
	echo "In order to generate the file '"$(TAGGER2)/$(LANG2).crp"' the file '"$(TAGGER2)/$(LANG2).crp.txt"' must exist" 1>&2; \
	echo "Please provide a large raw txt corpus with at least 200000 words." 1>&2; \
	exit 1; \
	else touch $@; \
	fi

$(TAGGER1)/$(LANG1).tagged:
	@if [ -e .tagger_supervised ]; \
	then echo "Error: File '"$@"' is needed to perform a supervised tagger training" 1>&2; \
	echo "This file should exist. It is the result of solving the ambiguity from the '"$(TAGGER1)/$(LANG1).tagged.txt"' file" 1>&2; \
	exit 1; \
	else touch $@; \
	fi;

$(TAGGER2)/$(LANG2).tagged:
	@if [ -e .tagger_supervised ]; \
	then echo "Error: File '"$@"' is needed to perform a supervised tagger training" 2>&2; \
	echo "This file should exist. It is the result of solving the ambiguity from the '"$(TAGGER2)/$(LANG2).tagged.txt"' file" 1>&2; \
	exit 1; \
	else touch $@; \
	fi;

$(TAGGER1)/$(LANG1).tagged.txt:
	@if [ -e .tagger_supervised ]; \
	then echo "Error: File '"$@"' is needed to perform a supervised tagger training" 1>&2; \
	echo "In order to generate the file '"$(TAGGER1)/$(LANG1).untagged"' the file '"$@"' must exist" 1>&2; \
	exit 1; \
	else touch $@; \
	fi;

$(TAGGER2)/$(LANG2).tagged.txt:
	@if [ -e .tagger_supervised ]; \
	then echo "Error: File '"$@"' is needed to perform a supervised tagger training" 2>&2; \
	echo "In order to generate the file '"$(TAGGER2)/$(LANG2).untagged"' the file '"$@"' must exist" 2>&2; \
	exit 1; \
	else touch $@; \
	fi;
	
$(TAGGER1)/$(LANG1).untagged: $(TAGGER1)/$(LANG1).tagged.txt $(PREFIX1).automorf.bin
	cat $(TAGGER1)/$(LANG1).tagged.txt | apertium-destxt | lt-proc $(PREFIX1).automorf.bin  > $@; 

$(TAGGER2)/$(LANG2).untagged: $(TAGGER2)/$(LANG2).tagged.txt $(PREFIX2).automorf.bin
	cat $(TAGGER2)/$(LANG2).tagged.txt | apertium-destxt | lt-proc $(PREFIX2).automorf.bin  > $@

$(PREFIX1).prob: $(BASENAME).$(LANG1).tsx $(TAGGER1)/$(LANG1).dic $(TAGGER1)/$(LANG1).untagged $(TAGGER1)/$(LANG1).tagged $(TAGGER1)/$(LANG1).crp
	@echo "Generating $@";
	@echo "This may take some time...";
	apertium-validate-tagger $(BASENAME).$(LANG1).tsx;
	if [ -e .old ]; \
	then make $(PREFIX1).tagger; \
	     if [ -e .tagger_unsupervised ]; \
	     then ./$(PREFIX1).tagger --train $(TAGGER_UNSUPERVISED_ITERATIONS)  $(TAGGER1)/$(LANG1); \
	     else ./$(PREFIX1).tagger --train $(TAGGER_SUPERVISED_ITERATIONS) --supervised $(TAGGER1)/$(LANG1);\
	     fi; \
	     mv $(TAGGER1)/$(LANG1).prob ./$(PREFIX1).prob; \
    	     mv $(TAGGER1)/$(LANG1).amb ./$(PREFIX1).amb; \
    	fi; \
        if [ ! -e .old ]; \
          then if [ -e .tagger_unsupervised ]; \
             then apertium-tagger -t $(TAGGER_UNSUPERVISED_ITERATIONS) \
                                     $(TAGGER1)/$(LANG1).dic \
                                     $(TAGGER1)/$(LANG1).crp \
                                     $(BASENAME).$(LANG1).tsx \
                                     $(PREFIX1).prob; \
             else apertium-tagger -s $(TAGGER_SUPERVISED_ITERATIONS) $(TAGGER1)/$(LANG1).dic \
                                  $(TAGGER1)/$(LANG1).dic \
                                  $(BASENAME).$(LANG1).tsx \
                                  $(PREFIX1).prob \
                                  $(TAGGER1)/$(LANG1).tagged \
                                  $(TAGGER1)/$(LANG1).untagged; \
             fi; \
         fi;

$(PREFIX2).prob: $(BASENAME).$(LANG2).tsx $(TAGGER2)/$(LANG2).dic  $(TAGGER2)/$(LANG2).untagged $(TAGGER2)/$(LANG2).tagged $(TAGGER2)/$(LANG2).crp
	@echo "Generating $@";
	@echo "This may take some time...";
	apertium-validate-tagger $(BASENAME).$(LANG2).tsx
	if [ -e .old ]; \
	then make $(PREFIX2).tagger; \
	     if [ -e .tagger_unsupervised ]; \
	     then ./$(PREFIX2).tagger --train $(TAGGER_UNSUPERVISED_ITERATIONS)  $(TAGGER2)/$(LANG2); \
	     else ./$(PREFIX2).tagger --train $(TAGGER_SUPERVISED_ITERATIONS) --supervised $(TAGGER2)/$(LANG2);\
	     fi; \
	     mv $(TAGGER2)/$(LANG2).prob ./$(PREFIX2).prob; \
    	     mv $(TAGGER2)/$(LANG2).amb ./$(PREFIX2).amb; \
    	 fi;\
    	 if [ ! -e .old ]; \
         then if [ -e .tagger_unsupervised ]; \
             then apertium-tagger --train $(TAGGER_UNSUPERVISED_ITERATIONS) \
                                  $(TAGGER2)/$(LANG2).dic \
                                  $(TAGGER2)/$(LANG2).crp \
                                  $(BASENAME).$(LANG2).tsx \
                                  $(PREFIX2).prob; \
             else apertium-tagger --supervised $(TAGGER_SUPERVISED_ITERATIONS) \
                                  $(TAGGER2)/$(LANG2).dic \
                                  $(TAGGER2)/$(LANG2).dic \
                                  $(BASENAME).$(LANG2).tsx \
                                  $(PREFIX2).prob \
                                  $(TAGGER2)/$(LANG2).tagged \
                                  $(TAGGER2)/$(LANG2).untagged; \
             fi; \
        fi;

$(TAGGER1)/$(LANG1).crp: $(PREFIX1).automorf.bin $(TAGGER1)/$(LANG1).crp.txt
	apertium-destxt < $(TAGGER1)/$(LANG1).crp.txt | lt-proc $(PREFIX1).automorf.bin > $(TAGGER1)/$(LANG1).crp

$(TAGGER2)/$(LANG2).crp: $(PREFIX2).automorf.bin $(TAGGER2)/$(LANG2).crp.txt
	apertium-destxt < $(TAGGER2)/$(LANG2).crp.txt | lt-proc $(PREFIX2).automorf.bin > $(TAGGER2)/$(LANG2).crp
     
dist: $(DISTFILES)
	tar cvf $(BASENAME)-tmp-$(VERSION).tar $(DISTFILES)
	rm -R -f $(BASENAME)-$(VERSION)
	mkdir $(BASENAME)-$(VERSION)
	mv $(BASENAME)-tmp-$(VERSION).tar $(BASENAME)-$(VERSION)
	tar xvf $(BASENAME)-$(VERSION)/$(BASENAME)-tmp-$(VERSION).tar -C $(BASENAME)-$(VERSION)
	rm $(BASENAME)-$(VERSION)/$(BASENAME)-tmp-$(VERSION).tar
	tar cvf $(BASENAME)-$(VERSION).tar $(BASENAME)-$(VERSION)
	rm -R -f $(BASENAME)-$(VERSION)
	gzip -f -9 $(BASENAME)-$(VERSION).tar

bzdist: $(DISTFILES)
	tar cvf $(BASENAME)-tmp-$(VERSION).tar $(DISTFILES)
	rm -R -f $(BASENAME)-$(VERSION)
	mkdir $(BASENAME)-$(VERSION)
	mv $(BASENAME)-tmp-$(VERSION).tar $(BASENAME)-$(VERSION)
	tar xvf $(BASENAME)-$(VERSION)/$(BASENAME)-tmp-$(VERSION).tar -C $(BASENAME)-$(VERSION)
	rm $(BASENAME)-$(VERSION)/$(BASENAME)-tmp-$(VERSION).tar
	tar cvf $(BASENAME)-$(VERSION).tar $(BASENAME)-$(VERSION)
	rm -R -f $(BASENAME)-$(VERSION)
	bzip2 -f $(BASENAME)-$(VERSION).tar

distcomp: $(DISTCOMP)
	if [ -e .old ]; \
	then echo "ERROR: Can't make distcomp of an old-style compilation"; \
	     exit 1; \
	fi; \
	rm -R -f $(BASENAME)-comp
	mkdir $(BASENAME)-comp
	cp $(DISTCOMP) $(BASENAME)-comp
	tar cvf $(BASENAME)-comp.tar $(BASENAME)-comp
	gzip -f -9 $(BASENAME)-comp.tar
	rm -R $(BASENAME)-comp

bzdistcomp: $(DISTCOMP)
	if [ -e .old ]; \
	then echo "ERROR: Can't make distcomp of an old-style compilation"; \
	     exit 1; \
	fi; \
	rm -R -f $(BASENAME)-comp
	mkdir $(BASENAME)-comp
	cp $(DISTCOMP) $(BASENAME)-comp
	tar cvf $(BASENAME)-comp.tar $(BASENAME)-comp
	bzip2 -f $(BASENAME)-comp.tar
	rm -R $(BASENAME)-comp
translateevaldata:
	./translate . $(LANG1)-$(LANG2) txt tmp1 > tmp2
