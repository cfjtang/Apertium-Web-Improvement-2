VERSION=0.1
LANG1=sh
LANG1a=sh_BS
LANG1b=sh_HR
LANG1c=sh_SR
LANG2=mk
PREFIX1=$(LANG1)-$(LANG2)
PREFIX2=$(LANG2)-$(LANG1)
PREFIX2a=$(LANG2)-$(LANG1a)
PREFIX2b=$(LANG2)-$(LANG1b)
PREFIX2c=$(LANG2)-$(LANG1c)

BASENAME=apertium-$(PREFIX1)

TARGETS_COMMON = $(BASENAME).$(LANG1a).dix  $(BASENAME).$(LANG1b).dix $(BASENAME).$(LANG1c).dix \
	$(PREFIX2a).autogen.bin $(PREFIX2b).autogen.bin $(PREFIX2c).autogen.bin \
	$(BASENAME).$(LANG1).dix $(BASENAME).$(LANG2).dix $(BASENAME).$(LANG1)-$(LANG2).dix \
	$(PREFIX1).automorf.bin $(PREFIX1).autobil.bin $(PREFIX1).autogen.bin \
	$(PREFIX2).automorf.bin $(PREFIX2).autobil.bin \
	$(PREFIX1).t1x.bin $(PREFIX1).t2x.bin $(PREFIX1).t3x.bin \
	$(PREFIX2).t1x.bin $(PREFIX2).t2x.bin $(PREFIX2).t3x.bin modes 

#    $(PREFIX1).autopgen.bin $(PREFIX2).autopgen.bin 

$(BASENAME).$(LANG1a).dix:
	xsltproc --stringparam alt $(LANG1a) alt.xsl $(BASENAME).$(LANG1).metadix >$@

$(PREFIX2a).autogen.bin: $(BASENAME).$(LANG1a).dix
	apertium-validate-dictionary $(BASENAME).$(LANG1a).dix
	lt-comp rl $(BASENAME).$(LANG1a).dix $@

$(BASENAME).$(LANG1b).dix:
	xsltproc --stringparam alt $(LANG1b) alt.xsl $(BASENAME).$(LANG1).metadix >$@

$(PREFIX2b).autogen.bin: $(BASENAME).$(LANG1b).dix
	apertium-validate-dictionary $(BASENAME).$(LANG1b).dix
	lt-comp rl $(BASENAME).$(LANG1b).dix $@

$(BASENAME).$(LANG1c).dix:
	xsltproc --stringparam alt $(LANG1c) alt.xsl $(BASENAME).$(LANG1).metadix >$@

$(PREFIX2c).autogen.bin: $(BASENAME).$(LANG1c).dix
	apertium-validate-dictionary $(BASENAME).$(LANG1c).dix
	lt-comp rl $(BASENAME).$(LANG1c).dix $@

$(BASENAME).$(LANG1).dix:
	xsltproc --stringparam alt $(LANG1) alt.xsl $(BASENAME).$(LANG1).metadix > dix.tmp 
	mv dix.tmp $@

$(PREFIX1).automorf.bin: $(BASENAME).$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).$(LANG1).dix
	lt-comp lr $(BASENAME).$(LANG1).dix $@

$(PREFIX1).autobil.bin: $(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary $(BASENAME).$(PREFIX1).dix
	lt-comp lr $(BASENAME).$(PREFIX1).dix $@

$(PREFIX1).autogen.bin: $(BASENAME).$(LANG2).dix
	apertium-validate-dictionary $(BASENAME).$(LANG2).dix
	lt-comp rl $(BASENAME).$(LANG2).dix $@

#$(PREFIX1).autopgen.bin: $(BASENAME).post-$(LANG2).dix
#	apertium-validate-dictionary $(BASENAME).post-$(LANG2).dix
#	lt-comp lr $(BASENAME).post-$(LANG2).dix $@

$(PREFIX1).translit.bin: $(BASENAME).translit.dix
#	apertium-validate-dictionary $(BASENAME).translit.dix
	lt-comp lr $(BASENAME).translit.dix $@

$(PREFIX2).automorf.bin: $(BASENAME).$(LANG2).dix
     
$(PREFIX2).translit.bin: $(BASENAME).translit.dix
#	apertium-validate-dictionary $(BASENAME).translit.dix
	lt-comp rl $(BASENAME).translit.dix $@

$(PREFIX2).automorf.bin: $(BASENAME).$(LANG2).dix
	apertium-validate-dictionary $(BASENAME).$(LANG2).dix
	lt-comp lr $(BASENAME).$(LANG2).dix $@

$(PREFIX2).autobil.bin: $(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary $(BASENAME).$(PREFIX1).dix
	lt-comp rl $(BASENAME).$(PREFIX1).dix $@

#$(PREFIX2).autogen.bin: $(BASENAME).$(LANG1).dix
#	apertium-validate-dictionary $(BASENAME).$(LANG1).dix
#	lt-comp rl $(BASENAME).$(LANG1).dix $@

$(PREFIX2).autopgen.bin: $(BASENAME).post-$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).post-$(LANG1).dix
	lt-comp lr $(BASENAME).post-$(LANG1).dix $@

#
#	TRANSFER
#

$(PREFIX1).t1x.bin: $(BASENAME).$(PREFIX1).t1x
	apertium-validate-transfer $(BASENAME).$(PREFIX1).t1x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t1x $@

$(PREFIX1).t2x.bin: $(BASENAME).$(PREFIX1).t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX1).t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t2x $@

$(PREFIX1).t3x.bin: $(BASENAME).$(PREFIX1).t3x
	apertium-validate-postchunk $(BASENAME).$(PREFIX1).t3x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t3x $@

$(PREFIX2).t1x.bin: $(BASENAME).$(PREFIX2).t1x
	apertium-validate-transfer $(BASENAME).$(PREFIX2).t1x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t1x $@

$(PREFIX2).t2x.bin: $(BASENAME).$(PREFIX2).t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX2).t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t2x $@

$(PREFIX2).t3x.bin: $(BASENAME).$(PREFIX2).t3x
	apertium-validate-postchunk $(BASENAME).$(PREFIX2).t3x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t3x $@


EXTRA_DIST=$(BASENAME).$(LANG1).dix $(BASENAME).$(PREFIX1).dix \
           $(BASENAME).$(LANG2).dix $(BASENAME).$(LANG1).tsx \
           $(BASENAME).$(LANG2).tsx  \
           $(PREFIX1).prob $(PREFIX2).prob \
           $(BASENAME).$(PREFIX1).t1x $(BASENAME).$(PREFIX1).t2x \
           $(BASENAME).$(PREFIX1).t3x $(BASENAME).$(PREFIX2).t1x \
           $(BASENAME).$(PREFIX2).t2x $(BASENAME).$(PREFIX2).t3x \
           modes.xml

#           $(LANG1)-tagger-data/README $(LANG2)-tagger-data/README 
#           $(BASENAME).post-$(LANG1).dix $(BASENAME).post-$(LANG2).dix 

$(PREFIX1).mode: modes.xml
	apertium-gen-modes modes.xml

$(PREFIX2a).mode: modes.xml 
	apertium-gen-modes modes.xml

$(PREFIX2b).mode: modes.xml 
	apertium-gen-modes modes.xml

$(PREFIX2c).mode: modes.xml clean-dicts
	apertium-gen-modes modes.xml

clean-dicts:
	touch $(BASENAME).$(LANG1).dix $(BASENAME).$(LANG2).dix $(BASENAME).$(PREFIX1).dix
	touch $(BASENAME).$(LANG1a).dix $(BASENAME).$(LANG1b).dix $(BASENAME).$(LANG1c).dix
	rm $(BASENAME).$(LANG1).dix
	rm $(BASENAME).$(LANG1a).dix
	rm $(BASENAME).$(LANG1b).dix
	rm $(BASENAME).$(LANG1c).dix
	
apertium_sh_mkmodesdir=$(prefix)/share/apertium/modes/
apertium_sh_mkdir=$(prefix)/share/apertium/apertium-$(PREFIX1)/
apertium_sh_mk_DATA=$(PREFIX1).automorf.bin $(PREFIX2).automorf.bin \
		    $(PREFIX2a).autogen.bin $(PREFIX2b).autogen.bin $(PREFIX2c).autogen.bin \
                    $(PREFIX1).autobil.bin $(PREFIX2).autobil.bin \
                    $(PREFIX1).autogen.bin \
                    $(PREFIX1).prob $(PREFIX2).prob \
		    $(PREFIX1).t1x.bin $(PREFIX1).t2x.bin $(PREFIX1).t3x.bin \
		    $(PREFIX2).t1x.bin $(PREFIX2).t2x.bin $(PREFIX2).t3x.bin \
		    $(BASENAME).$(PREFIX1).t1x $(BASENAME).$(PREFIX1).t2x $(BASENAME).$(PREFIX1).t3x\
		    $(BASENAME).$(PREFIX2).t1x $(BASENAME).$(PREFIX2).t2x $(BASENAME).$(PREFIX2).t3x\
		    $(PREFIX2).autopgen.bin \
		    $(PREFIX1).translit.bin $(PREFIX2).translit.bin \
		    $(PREFIX1).mode $(PREFIX2a).mode $(PREFIX2b).mode $(PREFIX2c).mode
		  
#                    $(PREFIX1).autopgen.bin 
 #                   $(PREFIX2).autopgen.bin 
# 		     $(PREFIX2).autogen.bin 

install-data-local:
	$(prefix)/bin/apertium-gen-modes modes.xml apertium-$(PREFIX1)
	$(INSTALL_DATA) $(PREFIX1).mode $(apertium_sh_mkmodesdir)
	$(INSTALL_DATA) $(PREFIX2a).mode $(apertium_sh_mkmodesdir)
	$(INSTALL_DATA) $(PREFIX2b).mode $(apertium_sh_mkmodesdir)
	$(INSTALL_DATA) $(PREFIX2c).mode $(apertium_sh_mkmodesdir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX1).t1x $(BASENAME).$(PREFIX2).t1x $(apertium_sh_mkdir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX1).t2x $(BASENAME).$(PREFIX2).t2x $(apertium_sh_mkdir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX1).t3x $(BASENAME).$(PREFIX2).t3x $(apertium_sh_mkdir)



CLEANFILES = -r $(TARGETS_COMMON) 
