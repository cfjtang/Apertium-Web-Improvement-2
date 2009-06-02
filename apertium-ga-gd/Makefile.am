VERSION=0.1.0
LANG1=ga
LANG2=gd
PREFIX1=$(LANG1)-$(LANG2)
PREFIX2=$(LANG2)-$(LANG1)

BASENAME=apertium-$(PREFIX1)

TARGETS_COMMON = $(PREFIX1).automorf.bin $(PREFIX1).autobil.bin $(PREFIX1).autogen.bin \
     $(PREFIX1).autopgen.bin $(PREFIX2).automorf.bin $(PREFIX2).autobil.bin \
     $(PREFIX2).autogen.bin \
     $(PREFIX2).autopgen.bin \
     pre-$(LANG1).t1x.bin \
     pre-$(LANG2).t1x.bin \
     $(PREFIX1).t1x.bin \
     $(PREFIX2).t1x.bin \
     $(PREFIX1).rlx.bin \
     $(PREFIX1).mode $(PREFIX2).mode \
     $(PREFIX1).automut.bin \
     $(PREFIX2).automut.bin

$(PREFIX1).rlx.bin: $(BASENAME).$(PREFIX1).rlx
	cg-comp $(BASENAME).$(PREFIX1).rlx $@


$(PREFIX1).automorf.bin: $(BASENAME).$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).$(LANG1).dix 
	lt-comp lr $(BASENAME).$(LANG1).dix $@ $(BASENAME).$(LANG1).acx

$(PREFIX1).autobil.bin: $(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary $(BASENAME).$(PREFIX1).dix
	lt-comp lr $(BASENAME).$(PREFIX1).dix $@

$(PREFIX1).autogen.bin: $(BASENAME).$(LANG2).dix
	apertium-validate-dictionary $(BASENAME).$(LANG2).dix
	lt-comp rl $(BASENAME).$(LANG2).dix $@

$(PREFIX1).automut.bin: $(BASENAME).muta-$(LANG2).dix
	apertium-validate-dictionary $(BASENAME).muta-$(LANG2).dix
	lt-comp lr $(BASENAME).muta-$(LANG2).dix $@

$(PREFIX1).autopgen.bin: $(BASENAME).post-$(LANG2).dix
	apertium-validate-dictionary $(BASENAME).post-$(LANG2).dix
	lt-comp lr $(BASENAME).post-$(LANG2).dix $@

$(PREFIX2).automorf.bin: $(BASENAME).$(LANG2).dix
	apertium-validate-dictionary $(BASENAME).$(LANG2).dix
	lt-comp lr $(BASENAME).$(LANG2).dix $@ 

$(PREFIX2).autobil.bin: $(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary $(BASENAME).$(PREFIX1).dix
	lt-comp rl $(BASENAME).$(PREFIX1).dix $@

$(PREFIX2).autogen.bin: $(BASENAME).$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).$(LANG1).dix
	lt-comp rl $(BASENAME).$(LANG1).dix $@

$(PREFIX2).automut.bin: $(BASENAME).muta-$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).muta-$(LANG1).dix
	lt-comp lr $(BASENAME).muta-$(LANG1).dix $@

$(PREFIX2).autopgen.bin: $(BASENAME).post-$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).post-$(LANG1).dix
	lt-comp lr $(BASENAME).post-$(LANG1).dix $@

pre-$(LANG1).t1x.bin: $(BASENAME).pre-$(LANG1).t1x
	apertium-validate-transfer $(BASENAME).pre-$(LANG1).t1x
	apertium-preprocess-transfer $(BASENAME).pre-$(LANG1).t1x $@

pre-$(LANG2).t1x.bin: $(BASENAME).pre-$(LANG2).t1x
	apertium-validate-transfer $(BASENAME).pre-$(LANG2).t1x
	apertium-preprocess-transfer $(BASENAME).pre-$(LANG2).t1x $@

$(PREFIX1).t1x.bin: $(BASENAME).$(PREFIX1).t1x
	apertium-validate-transfer $(BASENAME).$(PREFIX1).t1x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t1x $@

$(PREFIX2).t1x.bin: $(BASENAME).$(PREFIX2).t1x
	apertium-validate-transfer $(BASENAME).$(PREFIX2).t1x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t1x $@

EXTRA_DIST=$(BASENAME).$(LANG1).dix $(BASENAME).$(PREFIX1).dix \
           $(BASENAME).post-$(LANG1).dix $(BASENAME).post-$(LANG2).dix \
	   $(BASENAME).$(LANG2).dix \
	   $(BASENAME).$(LANG1).dix.xml \
	   $(BASENAME).$(LANG2).dix.xml \
	   $(BASENAME).$(PREFIX1).dix.xml \
           $(PREFIX1).prob $(PREFIX2).prob \
	   $(BASENAME).$(PREFIX1).t1x \
	   $(BASENAME).$(PREFIX2).t1x \
	   $(BASENAME).pre-$(LANG1).t1x \
	   $(BASENAME).pre-$(LANG2).t1x \
	   $(BASENAME).$(LANG1).tsx \
	   $(BASENAME).$(LANG1).acx \
	   $(BASENAME).$(LANG2).tsx modes.xml

#           $(LANG1)-tagger-data/README $(LANG2)-tagger-data/README 

clean-dicts:
	touch $(BASENAME).$(LANG1).dix $(BASENAME).$(LANG2).dix $(BASENAME).$(PREFIX1).dix
	rm $(BASENAME).$(LANG1).dix
	rm $(BASENAME).$(LANG2).dix
	rm $(BASENAME).$(PREFIX1).dix
	
apertium_ga_gddir=$(prefix)/share/apertium/apertium-$(PREFIX1)/
apertium_ga_modesdir=$(prefix)/share/apertium/modes/

$(PREFIX1).mode: modes.xml 
	apertium-gen-modes modes.xml
	cp *.mode modes/

$(PREFIX2).mode: modes.xml 
	apertium-gen-modes modes.xml
	cp *.mode modes/

apertium_ga_gd_DATA=$(PREFIX1).automorf.bin $(PREFIX2).automorf.bin \
                    $(PREFIX1).autobil.bin $(PREFIX2).autobil.bin \
                    $(PREFIX1).autogen.bin $(PREFIX2).autogen.bin \
                    $(PREFIX1).autopgen.bin \
                    $(PREFIX2).autopgen.bin \
                    $(PREFIX1).prob $(PREFIX2).prob \
     		    pre-$(LANG1).t1x.bin \
     		    pre-$(LANG2).t1x.bin \
		    $(PREFIX1).t1x.bin \
		    $(PREFIX2).t1x.bin \
     		    $(PREFIX1).rlx.bin \
     		    $(PREFIX1).automut.bin \
     		    $(PREFIX2).automut.bin \
	            $(PREFIX1).mode $(PREFIX2).mode modes.xml
		  
install-data-local:
	apertium-gen-modes modes.xml apertium-$(PREFIX1)
	$(INSTALL_DATA) $(PREFIX1).mode $(apertium_ga_modesdir)
	$(INSTALL_DATA) $(PREFIX2).mode $(apertium_ga_modesdir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX1).t1x $(apertium_ga_gddir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX2).t1x $(apertium_ga_gddir)
	$(INSTALL_DATA) $(BASENAME).pre-$(LANG1).t1x $(apertium_ga_gddir)
	$(INSTALL_DATA) $(BASENAME).pre-$(LANG2).t1x $(apertium_ga_gddir)
	cp *.mode modes/

CLEANFILES = -rf $(TARGETS_COMMON) modes
