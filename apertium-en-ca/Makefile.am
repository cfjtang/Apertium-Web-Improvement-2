VERSION=0.8.99
LANG1=en
LANG1a=en_GB
LANG1b=en_US
LANG2=ca
PREFIX1=$(LANG1)-$(LANG2)
PREFIX2=$(LANG2)-$(LANG1)
PREFIX2b=$(LANG2)-$(LANG1b)
ALT=alt.xsl
PREFVAR1=#dejar en blanco
PREFVAR2=val-

BASENAME=apertium-$(PREFIX1)

TARGETS_COMMON = $(PREFIX1).automorf.bin $(PREFIX1).autobil.bin $(PREFIX1).autogen.bin \
     $(PREFIX1).autopgen.bin $(PREFIX2).automorf.bin $(PREFIX2).autobil.bin \
     $(PREFIX2).autogen.bin $(PREFIX2).autopgen.bin $(PREFIX1).lextormono.bin $(PREFIX2).lextormono.bin \
     $(PREFIX1).lextorbil.bin $(PREFIX2).lextorbil.bin $(PREFIX1).genitive.bin \
     $(PREFIX1).t1x.bin $(PREFIX1).t2x.bin $(PREFIX1).t3x.bin \
     $(PREFIX1)-poly.autobil.bin $(PREFIX2)-poly.autobil.bin empty.bin \
     $(PREFIX2).t1x.bin $(PREFIX2).t2x.bin $(PREFIX2).t3x.bin modes \
     $(PREFIX2b).autogen.bin $(PREFVAR2)$(PREFIX1).autobil.bin

TARGETS = $(TARGETS_COMMON) \
	  $(BASENAME).$(LANG1)-lextor.dix $(BASENAME).$(LANG2)-lextor.dix \
	  $(BASENAME).$(PREFIX1)-lextor.dix $(BASENAME).$(PREFIX2)-lextor.dix 

.deps/$(LANG1).metadix: $(BASENAME).$(LANG1).metadix
	if [ ! -d .deps ]; then mkdir .deps; fi
	xsltproc --stringparam lang en_GB --stringparam side left filter.xsl $(BASENAME).$(LANG1).metadix >$@

.deps/$(LANG1).dix: .deps/$(LANG1).metadix
	xsltproc buscaPar.xsl .deps/$(LANG1).metadix | uniq >$$$$tmp1 && \
	xsltproc $$$$tmp1 .deps/$(LANG1).metadix > $@ && rm $$$$tmp1

.deps/$(LANG1b).metadix: $(BASENAME).$(LANG1).metadix
	if [ ! -d .deps ]; then mkdir .deps; fi
	xsltproc --stringparam lang en_US --stringparam side left filter.xsl $(BASENAME).$(LANG1).metadix >$@

.deps/$(LANG1b).dix: .deps/$(LANG1b).metadix
	xsltproc buscaPar.xsl .deps/$(LANG1b).metadix | uniq >$$$$tmp1 && \
	xsltproc $$$$tmp1 .deps/$(LANG1b).metadix > $@ && rm $$$$tmp1

.deps/pgen-ca.dix: $(BASENAME).post-ca.dix
	xsltproc --stringparam alt cat alt.xsl $< >$@ 

.deps/pgen-ca_valencia.dix: $(BASENAME).post-ca.dix
	xsltproc --stringparam alt val alt.xsl $< >$@ 

.deps/$(PREFVAR1)$(LANG2).dix: $(BASENAME).$(LANG2).dix
	xsltproc translate-to-default-equivalent.xsl $< | xsltproc --stringparam lang cat --stringparam side left filter.xsl - | xsltproc --stringparam alt std alt.xsl - >$@

.deps/$(PREFVAR2)$(LANG2).dix: $(BASENAME).$(LANG2).dix
	xsltproc translate-to-default-equivalent.xsl $< | xsltproc --stringparam lang val --stringparam side left filter.xsl - | xsltproc --stringparam alt std alt.xsl - >$@

.deps/$(PREFIX1).bil.dix: $(BASENAME).$(PREFIX1).dix
	xsltproc --stringparam left en_GB --stringparam right cat --stringparam side left filterbil.xsl $< | xsltproc --stringparam alt std alt.xsl - >$@

.deps/$(PREFIX2).bil.dix: $(BASENAME).$(PREFIX1).dix
	xsltproc --stringparam left en_GB --stringparam right cat --stringparam side right filterbil.xsl $< | xsltproc --stringparam alt std alt.xsl - >$@

.deps/$(PREFIX2)-US.bil.dix: $(BASENAME).$(PREFIX1).dix
	xsltproc --stringparam left en_US --stringparam right cat --stringparam side left filterbil.xsl $< | xsltproc --stringparam alt std alt.xsl - >$@

.deps/$(PREFVAR2)$(PREFIX1).bil.dix: $(BASENAME).$(PREFIX1).dix
	xsltproc --stringparam left en_GB --stringparam right val --stringparam side right filterbil.xsl $< | xsltproc --stringparam alt std alt.xsl - >$@

$(PREFIX1).automorf.bin: .deps/$(LANG1).dix $(BASENAME).$(LANG1).acx
	apertium-validate-dictionary .deps/$(LANG1).dix
	apertium-validate-acx $(BASENAME).$(LANG1).acx
	lt-comp lr .deps/$(LANG1).dix $@ $(BASENAME).$(LANG1).acx

#$(PREFIX1).automorf.bin: $(BASENAME).$(LANG1).metadix $(BASENAME).$(LANG1).acx
#	@xsltproc buscaPar.xsl apertium-en-ca.en.metadix | uniq > tmp1gen.xsl 	 
#	@xsltproc tmp1gen.xsl apertium-en-ca.en.metadix >$(BASENAME).$(LANG1).dixtmp1 
#	@rm tmp1gen.xsl
#	apertium-validate-acx $(BASENAME).$(LANG1).acx
#	apertium-validate-dictionary $(BASENAME).$(LANG1).dixtmp1
#	lt-comp lr $(BASENAME).$(LANG1).dixtmp1 $@ $(BASENAME).$(LANG1).acx
#	@rm $(BASENAME).$(LANG1).dixtmp1

$(PREFIX1).autobil.bin: .deps/$(PREFIX1).bil.dix
	apertium-validate-dictionary $<
	xsltproc translate-to-default-equivalent.xsl $< >tmp$(PREFIX1).autobil.dix
	lt-comp lr tmp$(PREFIX1).autobil.dix $@
#	rm tmp$(PREFIX1).autobil.dix

$(PREFVAR2)$(PREFIX1).autobil.bin: .deps/$(PREFVAR2)$(PREFIX1).bil.dix
	apertium-validate-dictionary $<
	xsltproc translate-to-default-equivalent.xsl $< >tmp$(PREFVAR2)$(PREFIX1).autobil.dix
	lt-comp lr tmp$(PREFVAR2)$(PREFIX1).autobil.dix $@
#	rm tmp$(PREFVAR2)$(PREFIX1).autobil.dix

$(PREFVAR1)$(PREFIX1).autogen.bin: .deps/$(PREFVAR1)$(LANG2).dix 
	apertium-validate-dictionary .deps/$(PREFVAR1)$(LANG2).dix 
	lt-comp rl .deps/$(PREFVAR1)$(LANG2).dix  $@

$(PREFVAR2)$(PREFIX1).autogen.bin: .deps/$(PREFVAR2)$(LANG2).dix 
	apertium-validate-dictionary .deps/$(PREFVAR2)$(LANG2).dix 
	lt-comp rl .deps/$(PREFVAR2)$(LANG2).dix  $@

$(PREFIX1).autopgen.bin: .deps/pgen-ca.dix
	apertium-validate-dictionary .deps/pgen-ca.dix
	lt-comp lr .deps/pgen-ca.dix $@

$(PREFVAR2)$(PREFIX1).autopgen.bin: .deps/pgen-ca_valencia.dix
	apertium-validate-dictionary .deps/pgen-ca_valencia.dix
	lt-comp lr .deps/pgen-ca_valencia.dix $@

$(PREFVAR1)$(PREFIX2).automorf.bin: .deps/$(PREFVAR1)$(LANG2).dix $(BASENAME).$(LANG1).acx
	apertium-validate-acx $(BASENAME).$(LANG2).acx
	apertium-validate-dictionary	 .deps/$(PREFVAR1)$(LANG2).dix
	lt-comp lr .deps/$(PREFVAR1)$(LANG2).dix $@ $(BASENAME).$(LANG2).acx

$(PREFVAR2)$(PREFIX2).automorf.bin: .deps/$(PREFVAR2)$(LANG2).dix $(BASENAME).$(LANG1).acx
	apertium-validate-acx $(BASENAME).$(LANG2).acx
	apertium-validate-dictionary .deps/$(PREFVAR2)$(LANG2).dix
	lt-comp lr .deps/$(PREFVAR2)$(LANG2).dix $@ $(BASENAME).$(LANG2).acx

$(PREFIX2).autobil.bin: .deps/$(PREFIX2).bil.dix
	apertium-validate-dictionary .deps/$(PREFIX2).bil.dix
	xsltproc translate-to-default-equivalent.xsl .deps/$(PREFIX2).bil.dix >tmp$(PREFIX2).autobil.dix
	lt-comp rl tmp$(PREFIX2).autobil.dix $@
	rm tmp$(PREFIX2).autobil.dix

$(PREFIX2b).autobil.bin: .deps/$(PREFIX2)-US.bil.dix
	apertium-validate-dictionary .deps/$(PREFIX2)-US.bil.dix
	xsltproc translate-to-default-equivalent.xsl .deps/$(PREFIX2)-US.bil.dix >tmp$(PREFIX2)-US.autobil.dix
	lt-comp rl tmp$(PREFIX2)-US.autobil.dix $@
	rm tmp$(PREFIX2)-US.autobil.dix

$(PREFIX2).autogen.bin: .deps/$(LANG1).dix
	apertium-validate-dictionary .deps/$(LANG1).dix
	lt-comp rl .deps/$(LANG1).dix $@

$(PREFIX2b).autogen.bin: .deps/$(LANG1b).dix
	apertium-validate-dictionary .deps/$(LANG1b).dix
	lt-comp rl .deps/$(LANG1b).dix $@

#$(PREFIX2).autogen.bin: $(BASENAME).$(LANG1).metadix
#	@xsltproc buscaPar.xsl apertium-en-ca.en.metadix | uniq > tmp2gen.xsl 	 
#	@xsltproc tmp2gen.xsl apertium-en-ca.en.metadix >$(BASENAME).$(LANG1).dixtmp2
#	@rm tmp2gen.xsl
#	apertium-validate-dictionary $(BASENAME).$(LANG1).dixtmp2
#	lt-comp rl $(BASENAME).$(LANG1).dixtmp2 $@
#	@rm $(BASENAME).$(LANG1).dixtmp2

$(PREFIX2).autopgen.bin: $(BASENAME).post-$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).post-$(LANG1).dix
	lt-comp lr $(BASENAME).post-$(LANG1).dix $@

$(BASENAME).$(LANG1)-lextor.dix: .deps/$(PREFIX1).bil.dix
	apertium-validate-dictionary .deps/$(PREFIX1).bil.dix
	apertium-gen-lextormono rl .deps/$(PREFIX1).bil.dix $@

$(BASENAME).$(LANG2)-lextor.dix: .deps/$(PREFIX2).bil.dix
	apertium-validate-dictionary .deps/$(PREFIX2).bil.dix
	apertium-gen-lextormono lr .deps/$(PREFIX2).bil.dix $@

$(BASENAME).$(PREFVAR2)$(LANG1)-lextor.dix: .deps/$(PREFVAR2)$(PREFIX1).bil.dix
	apertium-validate-dictionary .deps/$(PREFIX1).bil.dix
	apertium-gen-lextormono rl .deps/$(PREFIX1).bil.dix $@

$(BASENAME).$(LANG2)-US-lextor.dix: .deps/$(PREFIX2)-US.bil.dix
	apertium-validate-dictionary .deps/$(PREFIX2).bil.dix
	apertium-gen-lextormono lr .deps/$(PREFIX2).bil.dix $@

$(PREFIX1).lextormono.bin: $(BASENAME).$(LANG1)-lextor.dix
	lt-comp lr $(BASENAME).$(LANG1)-lextor.dix $@

$(PREFIX2).lextormono.bin: $(BASENAME).$(LANG2)-lextor.dix
	lt-comp lr $(BASENAME).$(LANG2)-lextor.dix $@

$(PREFVAR2)$(PREFIX1).lextormono.bin: $(BASENAME).$(PREFVAR2)$(LANG1)-lextor.dix
	lt-comp lr $(BASENAME).$(LANG1)-lextor.dix $@

$(PREFIX2)-US.lextormono.bin: $(BASENAME).$(LANG2)-US-lextor.dix
	lt-comp lr $(BASENAME).$(LANG2)-lextor.dix $@


$(BASENAME).$(PREFIX1)-lextor.dix: .deps/$(PREFIX1).bil.dix
	apertium-validate-dictionary .deps/$(PREFIX1).bil.dix
	apertium-gen-lextorbil rl .deps/$(PREFIX1).bil.dix $@

$(BASENAME).$(PREFIX2)-lextor.dix: .deps/$(PREFIX2).bil.dix
	apertium-validate-dictionary .deps/$(PREFIX2).bil.dix
	apertium-gen-lextorbil lr .deps/$(PREFIX2).bil.dix $@

$(BASENAME).$(PREFVAR2)$(PREFIX1)-lextor.dix: .deps/$(PREFVAR2)$(PREFIX1).bil.dix
	apertium-validate-dictionary .deps/$(PREFVAR2)$(PREFIX1).bil.dix
	apertium-gen-lextorbil rl .deps/$(PREFVAR2)$(PREFIX1).bil.dix $@

$(BASENAME).$(PREFIX2)-US-lextor.dix: .deps/$(PREFIX2)-US.bil.dix
	apertium-validate-dictionary .deps/$(PREFIX2)-US.bil.dix
	apertium-gen-lextorbil lr .deps/$(PREFIX2)-US.bil.dix $@

$(PREFIX1).lextorbil.bin: $(BASENAME).$(PREFIX1)-lextor.dix
	lt-comp lr $(BASENAME).$(PREFIX1)-lextor.dix $@

$(PREFIX2).lextorbil.bin: $(BASENAME).$(PREFIX2)-lextor.dix
	lt-comp lr $(BASENAME).$(PREFIX2)-lextor.dix $@

$(PREFVAR2)$(PREFIX1).lextorbil.bin: $(BASENAME).$(PREFVAR2)$(PREFIX1)-lextor.dix
	lt-comp lr $(BASENAME).$(PREFVAR2)$(PREFIX1)-lextor.dix $@

$(PREFIX2)-US.lextorbil.bin: $(BASENAME).$(PREFIX2)-US-lextor.dix
	lt-comp lr $(BASENAME).$(PREFIX2)-US-lextor.dix $@

$(PREFIX1)-poly.autobil.bin: $(BASENAME).$(PREFIX1)-poly.dix
	lt-comp lr $(BASENAME).$(PREFIX1)-poly.dix $@

$(PREFIX2)-poly.autobil.bin: $(BASENAME).$(PREFIX2)-poly.dix
	lt-comp lr $(BASENAME).$(PREFIX2)-poly.dix $@

$(PREFIX1).genitive.bin: $(BASENAME).$(PREFIX1).genitive.t1x
	apertium-validate-transfer $(BASENAME).$(PREFIX1).genitive.t1x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).genitive.t1x $@

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

empty.bin: empty.t1x
	apertium-validate-transfer empty.t1x
	apertium-preprocess-transfer empty.t1x $@

EXTRA_DIST=$(BASENAME).$(LANG1).metadix  $(BASENAME).$(PREFIX1).dix \
	   $(BASENAME).post-$(LANG1).dix $(BASENAME).post-$(LANG2).dix \
	   $(BASENAME).$(LANG2).dix $(BASENAME).$(LANG1).tsx \
	   $(BASENAME).$(LANG2).tsx empty.t1x \
	   $(BASENAME).$(PREFIX1)-poly.dix $(BASENAME).$(PREFIX2)-poly.dix \
	   $(PREFIX1).prob $(PREFIX2).prob \
	   buscaPar.xsl principal.xsl apertium-en-ca.en.metadix \
	   $(PREFIX1).lextor $(PREFIX2).lextor \
	   $(PREFIX1)-supervised.make $(PREFIX1)-unsupervised.make \
	   $(PREFIX2)-supervised.make $(PREFIX2)-unsupervised.make \
	   $(LANG1)-tagger-data/README $(LANG2)-tagger-data/README \
	   lextor-data/README lextor.make translate-to-default-equivalent.xsl \
	   $(BASENAME).$(PREFIX1).genitive.t1x alt.xsl \
	   $(BASENAME).$(PREFIX1).t1x $(BASENAME).$(PREFIX1).t2x \
	   $(BASENAME).$(PREFIX1).t3x $(BASENAME).$(PREFIX2).t1x \
	   $(BASENAME).$(PREFIX2).t2x $(BASENAME).$(PREFIX2).t3x \
           $(BASENAME).$(LANG1).acx $(BASENAME).$(LANG2).acx \
           filter.xsl filterbil.xsl filter.sh \
	   modes.xml

apertium_en_ca_DATA=$(PREFIX1).automorf.bin $(PREFIX2).automorf.bin \
		    $(PREFIX1).autobil.bin $(PREFIX2).autobil.bin \
		    $(PREFIX2b).autobil.bin $(PREFVAR2)$(PREFIX1).autobil.bin\
		    $(PREFIX1).autogen.bin $(PREFIX2).autogen.bin \
		    $(PREFIX1).autopgen.bin $(PREFIX2).autopgen.bin \
		    $(PREFVAR2)$(PREFIX1).autogen.bin $(PREFVAR2)$(PREFIX1).autopgen.bin\
		    $(PREFIX1)-poly.autobil.bin $(PREFIX2)-poly.autobil.bin \
		    $(PREFIX1).prob $(PREFIX2).prob \
		    $(PREFIX1).lextormono.bin $(PREFIX2).lextormono.bin \
		    $(PREFIX1).lextorbil.bin $(PREFIX2).lextorbil.bin \
	            $(BASENAME).$(PREFIX1).genitive.t1x \
		    $(PREFIX1).genitive.bin  \
		    $(PREFIX1).t1x.bin $(PREFIX1).t2x.bin $(PREFIX1).t3x.bin \
		    $(PREFIX2).t1x.bin $(PREFIX2).t2x.bin $(PREFIX2).t3x.bin \
		    $(PREFIX1).mode $(PREFIX2).mode $(PREFVAR2)$(PREFIX1).mode $(PREFIX2b).mode\
		    $(PREFIX1)-multi.mode $(PREFIX2)-multi.mode empty.bin

apertium_en_cadir=$(prefix)/share/apertium/apertium-$(PREFIX1)/
apertium_en_modesdir=$(prefix)/share/apertium/modes/

$(PREFIX1).mode: modes.xml
	apertium-gen-modes modes.xml

$(PREFIX2).mode: modes.xml
	apertium-gen-modes modes.xml

$(PREFIX2b).mode: modes.xml
	apertium-gen-modes modes.xml

$(PREFVAR2)$(PREFIX1).mode: modes.xml
	apertium-gen-modes modes.xml

$(PREFIX1)-multi.mode: modes.xml
	apertium-gen-modes modes.xml

$(PREFIX2)-multi.mode: modes.xml
	apertium-gen-modes modes.xml

modes: modes.xml
	apertium-gen-modes modes.xml
	cp $(PREFIX1).mode modes/
	cp $(PREFIX2).mode modes/

install-data-local:
	apertium-gen-modes modes.xml apertium-$(PREFIX1)
	$(INSTALL_DATA) $(PREFIX1).mode $(apertium_en_modesdir)
	$(INSTALL_DATA) $(PREFIX2).mode $(apertium_en_modesdir)
	$(INSTALL_DATA) $(PREFIX2b).mode $(apertium_en_modesdir)
	$(INSTALL_DATA) $(PREFVAR2)$(PREFIX1).mode $(apertium_en_modesdir)
	$(INSTALL_DATA) $(PREFIX1)-multi.mode $(apertium_en_modesdir)
	$(INSTALL_DATA) $(PREFIX2)-multi.mode $(apertium_en_modesdir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX1).genitive.t1x $(apertium_en_cadir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX1).t1x $(BASENAME).$(PREFIX1).t2x $(apertium_en_cadir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX1).t3x $(BASENAME).$(PREFIX2).t1x $(apertium_en_cadir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX2).t2x $(BASENAME).$(PREFIX2).t3x $(apertium_en_cadir)


CLEANFILES = -rf $(TARGETS) modes

