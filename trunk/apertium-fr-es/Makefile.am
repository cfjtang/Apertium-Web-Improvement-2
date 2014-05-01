VERSION=0.9.2
LANG1=fr
LANG2=es
PREFIX1=$(LANG1)-$(LANG2)
PREFIX2=$(LANG2)-$(LANG1)
BASENAME=apertium-$(PREFIX1)

VAR1=std
VAR2=eco
PREFVAR1=#dejar en blanco
PREFVAR2=eco-
PREFVAR2x=eco
TARGETS_COMMON = $(PREFIX1).automorf.bin $(PREFIX1).autobil.bin $(PREFIX1).autogen.bin \
     $(PREFIX1).autopgen.bin $(PREFIX2).automorf.bin $(PREFIX2).autobil.bin \
     $(PREFIX2).autogen.bin $(PREFIX2).autopgen.bin \
     $(PREFIX2).autolex.bin \
     $(PREFIX1).t1x.bin $(PREFIX1).t2x.bin $(PREFIX1).t3x.bin \
     $(PREFIX2).t1x.bin $(PREFIX2).t2x.bin $(PREFIX2).t3x.bin

$(PREFVAR1)$(LANG1).dix: $(BASENAME).$(LANG1).dix
	xsltproc --stringparam alt $(VAR1) alt.xsl $< | gawk '{gsub("&", "&amp;"); print;}' >$@

$(PREFVAR2)$(LANG1).dix: $(BASENAME).$(LANG1).dix
	xsltproc --stringparam alt $(VAR2) alt.xsl $< | gawk '{gsub("&", "&amp;"); print;}' >$@

$(PREFVAR1)$(LANG2).dix: $(BASENAME).$(LANG2).dix
	xsltproc --stringparam alt $(VAR1) alt.xsl $< | gawk '{gsub("&", "&amp;"); print;}' >$@

$(PREFVAR2)$(LANG2).dix: $(BASENAME).$(LANG2).dix
	xsltproc --stringparam alt $(VAR2) alt.xsl $< | gawk '{gsub("&", "&amp;"); print;}'>$@

common-$(PREFIX1).dix: $(BASENAME).$(PREFIX1).dix
	xsltproc translate-to-default-equivalent.xsl $<  | gawk '{gsub("&", "&amp;"); print;}' >$@

$(PREFVAR1)$(PREFIX1).dix: common-$(PREFIX1).dix
	xsltproc --stringparam alt $(VAR1) alt.xsl $< | gawk '{gsub("&", "&amp;"); print;}' >$@

$(PREFVAR2)$(PREFIX1).dix: common-$(PREFIX1).dix
	xsltproc --stringparam alt $(VAR2) alt.xsl $< | gawk '{gsub("&", "&amp;"); print;}'>$@

$(PREFVAR1)$(PREFIX1).automorf.bin:$(PREFVAR1)$(LANG1).dix
	apertium-validate-dictionary $(PREFVAR1)$(LANG1).dix
	lt-comp lr $(PREFVAR1)$(LANG1).dix $@ $(BASENAME).$(LANG1).acx

$(PREFVAR1)$(PREFIX1).autobil.bin: $(PREFVAR1)$(PREFIX1).dix
	apertium-validate-dictionary $(PREFVAR1)$(PREFIX1).dix
	lt-comp lr $(PREFVAR1)$(PREFIX1).dix $@

$(PREFIX1).autolex.bin: $(BASENAME).$(PREFIX1).lrx
	lrx-comp $< $@

$(PREFVAR1)$(PREFIX1).autogen.bin: $(PREFVAR1)$(LANG2).dix
	apertium-validate-dictionary $(PREFVAR1)$(LANG2).dix 
	lt-comp rl $(PREFVAR1)$(LANG2).dix $@  $(BASENAME).$(LANG2).acx

$(PREFVAR1)$(PREFIX1).autopgen.bin: $(BASENAME).post-$(LANG2).dix
	apertium-validate-dictionary $(BASENAME).post-$(LANG2).dix
	lt-comp lr $(BASENAME).post-$(LANG2).dix $@

$(PREFVAR2)$(PREFIX1).automorf.bin: $(PREFVAR2)$(LANG1).dix
	apertium-validate-dictionary $(PREFVAR2)$(LANG1).dix
	lt-comp lr $(PREFVAR2)$(LANG1).dix $@  $(BASENAME).$(LANG1).acx

$(PREFVAR2)$(PREFIX1).autobil.bin: $(PREFVAR2)$(PREFIX1).dix
	apertium-validate-dictionary $(PREFVAR2)$(PREFIX1).dix
	lt-comp lr $(PREFVAR2)$(PREFIX1).dix $@

$(PREFIX1).$(PREFVAR2x).autolex.bin: $(BASENAME).$(PREFIX1).$(PREFVAR2x).lrx
	lrx-comp $< $@

$(PREFVAR2)$(PREFIX1).autogen.bin: $(PREFVAR2)$(LANG2).dix
	apertium-validate-dictionary $(PREFVAR2)$(LANG2).dix
	lt-comp rl $(PREFVAR2)$(LANG2).dix $@ $(BASENAME).$(LANG1).acx

$(PREFVAR2)$(PREFIX1).autopgen.bin: $(BASENAME).post-$(LANG2).dix
	apertium-validate-dictionary $(BASENAME).post-$(LANG2).dix
	lt-comp lr $(BASENAME).post-$(LANG2).dix $@

$(PREFVAR1)$(PREFIX2).automorf.bin: $(PREFVAR1)$(LANG2).dix
	apertium-validate-dictionary $(PREFVAR1)$(LANG2).dix
	lt-comp lr $(PREFVAR1)$(LANG2).dix $@ $(BASENAME).$(LANG2).acx

$(PREFVAR1)$(PREFIX2).autobil.bin: $(PREFVAR1)$(PREFIX1).dix
	apertium-validate-dictionary $(PREFVAR1)$(PREFIX1).dix
	lt-comp rl $(PREFVAR1)$(PREFIX1).dix $@

$(PREFIX2).autolex.bin: $(BASENAME).$(PREFIX2).lrx
	lrx-comp $< $@

$(PREFVAR1)$(PREFIX2).autogen.bin: $(PREFVAR1)$(LANG1).dix
	apertium-validate-dictionary $(PREFVAR1)$(LANG1).dix
	lt-comp rl $(PREFVAR1)$(LANG1).dix $@ $(BASENAME).$(LANG2).acx

$(PREFVAR1)$(PREFIX2).autopgen.bin: $(BASENAME).post-$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).post-$(LANG1).dix
	lt-comp lr $(BASENAME).post-$(LANG1).dix $@

$(PREFVAR2)$(PREFIX2).automorf.bin: $(PREFVAR2)$(LANG2).dix
	apertium-validate-dictionary $(PREFVAR2)$(LANG2).dix
	lt-comp lr $(PREFVAR2)$(LANG2).dix $@

$(PREFVAR2)$(PREFIX2).autobil.bin: $(PREFVAR2)$(PREFIX1).dix
	apertium-validate-dictionary $(PREFVAR2)$(PREFIX1).dix
	lt-comp rl $(PREFVAR2)$(PREFIX1).dix $@

$(PREFIX2).$(PREFVAR2x).autolex.bin: $(BASENAME).$(PREFIX2).$(PREFVAR2x).lrx
	lrx-comp $< $@

$(PREFVAR2)$(PREFIX2).autogen.bin: $(PREFVAR2)$(LANG1).dix
	apertium-validate-dictionary $(PREFVAR2)$(LANG1).dix
	lt-comp rl $(PREFVAR2)$(LANG1).dix $@

$(PREFVAR2)$(PREFIX2).autopgen.bin: $(BASENAME).post-$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).post-$(LANG1).dix
	lt-comp lr $(BASENAME).post-$(LANG1).dix $@

$(PREFVAR1)$(PREFIX1).t1x.bin: $(BASENAME).$(PREFIX1).t1x
	apertium-validate-transfer $(BASENAME).$(PREFIX1).t1x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t1x $@

$(PREFVAR1)$(PREFIX1).t2x.bin: $(BASENAME).$(PREFIX1).t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX1).t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t2x $@

$(PREFVAR1)$(PREFIX1).t3x.bin: $(BASENAME).$(PREFIX1).t3x
	apertium-validate-postchunk $(BASENAME).$(PREFIX1).t3x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t3x $@

$(PREFVAR2)$(PREFIX1).t1x.bin: $(BASENAME).$(PREFIX1).t1x
	apertium-validate-transfer $(BASENAME).$(PREFIX1).t1x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t1x $@

$(PREFVAR2)$(PREFIX1).t2x.bin: $(BASENAME).$(PREFIX1).t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX1).t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t2x $@

$(PREFVAR2)$(PREFIX1).t3x.bin: $(BASENAME).$(PREFIX1).t3x
	apertium-validate-postchunk $(BASENAME).$(PREFIX1).t3x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t3x $@

$(PREFVAR1)$(PREFIX2).t1x.bin: $(BASENAME).$(PREFIX2).t1x
	apertium-validate-transfer $(BASENAME).$(PREFIX2).t1x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t1x $@

$(PREFVAR1)$(PREFIX2).t2x.bin: $(BASENAME).$(PREFIX2).t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX2).t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t2x $@

$(PREFVAR1)$(PREFIX2).t3x.bin: $(BASENAME).$(PREFIX2).t3x
	apertium-validate-postchunk $(BASENAME).$(PREFIX2).t3x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t3x $@

$(PREFVAR2)$(PREFIX2).t1x.bin: $(BASENAME).$(PREFIX2).t1x
	apertium-validate-transfer $(BASENAME).$(PREFIX2).t1x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t1x $@

$(PREFVAR2)$(PREFIX2).t2x.bin: $(BASENAME).$(PREFIX2).t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX2).t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t2x $@

$(PREFVAR2)$(PREFIX2).t3x.bin: $(BASENAME).$(PREFIX2).t3x
	apertium-validate-postchunk $(BASENAME).$(PREFIX2).t3x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t3x $@



EXTRA_DIST=$(BASENAME).$(LANG1).dix $(BASENAME).$(PREFIX1).dix \
           $(BASENAME).post-$(LANG1).dix $(BASENAME).post-$(LANG2).dix \
           $(BASENAME).$(LANG2).dix $(BASENAME).$(LANG1).tsx \
           $(BASENAME).$(LANG2).tsx  \
           $(PREFIX1).prob $(PREFIX2).prob \
           buscaPar.xsl principal.xsl apertium-fr-es.fr.metadix \
           $(PREFIX1)-supervised.make $(PREFIX1)-unsupervised.make \
           $(PREFIX2)-supervised.make $(PREFIX2)-unsupervised.make \
           $(LANG1)-tagger-data/README $(LANG2)-tagger-data/README \
           $(BASENAME).$(PREFIX1).t1x $(BASENAME).$(PREFIX1).t2x \
           $(BASENAME).$(PREFIX1).t3x $(BASENAME).$(PREFIX2).t1x \
           $(BASENAME).$(PREFIX2).t2x $(BASENAME).$(PREFIX2).t3x \
           modes.xml  $(BASENAME).$(LANG1).acx alt.xsl translate-to-default-equivalent.xsl \
	   $(BASENAME).$(LANG2).acx

apertium-fr-es.fr.dix: apertium-fr-es.fr.metadix
	@xsltproc buscaPar.xsl apertium-fr-es.fr.metadix |gawk '{gsub("&", "&amp;"); print;}' | uniq > $$gen.xsl
	@xsltproc $$gen.xsl apertium-fr-es.fr.metadix | gawk '{gsub("&", "&amp;"); print;}' >$@
	@rm $$gen.xsl

apertium_fr_esdir=$(prefix)/share/apertium/apertium-$(PREFIX1)/
apertium_modesdir=$(prefix)/share/apertium/modes/

modes/$(PREFIX1).mode: modes.xml
	apertium-validate-modes modes.xml
	apertium-gen-modes modes.xml
	cp *.mode modes/

# All modes are created by the same goal, listing several will just give problems with make -j2
noinst_DATA=modes/$(PREFIX1).mode


apertium_fr_es_DATA=$(PREFVAR1)$(PREFIX1).automorf.bin $(PREFVAR1)$(PREFIX2).automorf.bin \
	$(PREFVAR1)$(PREFIX1).autobil.bin $(PREFVAR1)$(PREFIX2).autobil.bin \
	$(PREFVAR1)$(PREFIX1).autolex.bin $(PREFVAR1)$(PREFIX2).autolex.bin \
	$(PREFVAR1)$(PREFIX1).autogen.bin $(PREFVAR1)$(PREFIX2).autogen.bin \
	$(PREFVAR2)$(PREFIX1).automorf.bin $(PREFVAR2)$(PREFIX2).automorf.bin \
	$(PREFVAR2)$(PREFIX1).autobil.bin $(PREFVAR2)$(PREFIX2).autobil.bin \
	$(PREFIX1).$(PREFVAR2x).autolex.bin $(PREFIX2).$(PREFVAR2x).autolex.bin \
	$(PREFVAR2)$(PREFIX1).autogen.bin $(PREFVAR2)$(PREFIX2).autogen.bin \
	$(PREFIX1).autopgen.bin $(PREFIX2).autopgen.bin \
	$(PREFIX1).prob $(PREFIX2).prob \
	$(PREFIX1).t1x.bin $(PREFIX1).t2x.bin $(PREFIX1).t3x.bin \
	$(PREFIX2).t1x.bin $(PREFIX2).t2x.bin $(PREFIX2).t3x.bin

install-data-local:
	mv modes modes.bak
	apertium-gen-modes modes.xml apertium-$(PREFIX1)
	rm -rf modes
	mv modes.bak modes
	test -d $(DESTDIR)$(apertium_modesdir) || mkdir $(DESTDIR)$(apertium_modesdir)
	$(INSTALL_DATA) $(PREFVAR1)$(PREFIX1).mode $(DESTDIR)$(apertium_modesdir)
	$(INSTALL_DATA) $(PREFVAR1)$(PREFIX2).mode $(DESTDIR)$(apertium_modesdir)
	$(INSTALL_DATA) $(PREFVAR2)$(PREFIX1).mode $(DESTDIR)$(apertium_modesdir)
	$(INSTALL_DATA) $(PREFVAR2)$(PREFIX2).mode $(DESTDIR)$(apertium_modesdir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX1).t1x $(BASENAME).$(PREFIX1).t2x $(DESTDIR)$(apertium_fr_esdir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX1).t3x $(BASENAME).$(PREFIX2).t1x $(DESTDIR)$(apertium_fr_esdir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX2).t2x $(BASENAME).$(PREFIX2).t3x $(DESTDIR)$(apertium_fr_esdir)

CLEANFILES = $(TARGETS) $(BASENAME).$(LANG1).dix *.mode  $(PREFVAR2)$(LANG1).dix \
		$(PREFVAR2)$(PREFIX1).automorf.bin $(PREFVAR2)$(PREFIX1).autobil.bin $(PREFVAR2)$(PREFIX1).autogen.bin \
		$(PREFVAR2)$(PREFIX2).automorf.bin $(PREFVAR2)$(PREFIX2).autobil.bin $(PREFVAR2)$(PREFIX2).autogen.bin \
		$(LANG1).dix $(LANG2).dix $(PREFIX1).dix $(PREFVAR2)-$(LANG1).dix $(PREFVAR2)-$(LANG2).dix \
		$(PREFVAR2)-$(PREFIX1).dix

clean-local: 
	-rm -rf modes
