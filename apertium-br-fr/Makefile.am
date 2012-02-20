VERSION=0.5.0
LANG1=br
LANG2=fr
PREFIX1=$(LANG1)-$(LANG2)
PREFIX2=$(LANG2)-$(LANG1)

BASENAME=apertium-$(PREFIX1)

TARGETS_COMMON = $(BASENAME).$(LANG2).dix  \
     $(PREFIX1).automorf.bin $(PREFIX1).autobil.bin $(PREFIX1).autogen.bin \
     $(PREFIX1).autopgen.bin $(PREFIX2).automorf.bin $(PREFIX2).autobil.bin \
     $(PREFIX1).rlx.bin $(PREFIX1).pre-xfer.t1x.bin \
     $(PREFIX1).t1x.bin $(PREFIX1).t2x.bin $(PREFIX1).t3x.bin \
     $(PREFIX2).t1x.bin $(PREFIX2).t2x.bin $(PREFIX2).t3x.bin

# pre-$(LANG1).t1x.bin 

$(BASENAME).$(LANG2).dix: $(BASENAME).$(LANG2).metadix
	@xsltproc buscaPar.xsl $(BASENAME).$(LANG2).metadix | uniq > $$gen.xsl
	@xsltproc $$gen.xsl $(BASENAME).$(LANG2).metadix >$@
	@rm $$gen.xsl

$(PREFIX1).automorf.bin: $(BASENAME).$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).$(LANG1).dix
	lt-comp lr $(BASENAME).$(LANG1).dix $@ $(BASENAME).$(LANG1).acx

.deps/$(BASENAME).$(PREFIX1).dix: $(BASENAME).$(PREFIX1).dix lexchoicebil.xsl
	if [ ! -d .deps ]; then mkdir .deps; fi
	xsltproc lexchoicebil.xsl $(BASENAME).$(PREFIX1).dix > $@

$(PREFIX1).autobil.bin: .deps/$(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary .deps/$(BASENAME).$(PREFIX1).dix
	lt-comp lr .deps/$(BASENAME).$(PREFIX1).dix $@

$(PREFIX1).autogen.bin: $(BASENAME).$(LANG2).dix
	apertium-validate-dictionary $(BASENAME).$(LANG2).dix
	lt-comp rl $(BASENAME).$(LANG2).dix $@

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

#$(PREFIX2).autopgen.bin: $(BASENAME).post-$(LANG1).dix
#	apertium-validate-dictionary $(BASENAME).post-$(LANG1).dix
#	lt-comp lr $(BASENAME).post-$(LANG1).dix $@

#$(PREFIX2).autoorth.bin: $(BASENAME).orth-$(LANG1).dix
#	apertium-validate-dictionary $(BASENAME).orth-$(LANG1).dix
#	lt-comp lr $(BASENAME).orth-$(LANG1).dix $@

#pre-$(LANG1).t1x.bin: $(BASENAME).pre-$(LANG1).t1x
#	apertium-validate-transfer $(BASENAME).pre-$(LANG1).t1x
#	apertium-preprocess-transfer $(BASENAME).pre-$(LANG1).t1x $@


$(PREFIX1).rlx.bin: $(BASENAME).$(PREFIX1).rlx
	cg-comp $(BASENAME).$(PREFIX1).rlx $@

$(PREFIX1).pre-xfer.t1x.bin: $(BASENAME).pre-xfer-br.t1x
	apertium-validate-transfer $(BASENAME).pre-xfer-br.t1x
	apertium-preprocess-transfer $(BASENAME).pre-xfer-br.t1x $@

$(PREFIX1).t1x.bin: $(BASENAME).$(PREFIX1).t1x
	apertium-validate-transfer $(BASENAME).$(PREFIX1).t1x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t1x $@

$(PREFIX1).t2x.bin: $(BASENAME).$(PREFIX1).t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX1).t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t2x $@

$(PREFIX1).t3x.bin: $(BASENAME).$(PREFIX1).t3x
	apertium-validate-postchunk $(BASENAME).$(PREFIX1).t3x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t3x $@

#$(PREFIX2).t1x.bin: $(BASENAME).$(PREFIX2).t1x
#	apertium-validate-transfer $(BASENAME).$(PREFIX2).t1x
#	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t1x $@

#$(PREFIX2).t2x.bin: $(BASENAME).$(PREFIX2).t2x
#	apertium-validate-interchunk $(BASENAME).$(PREFIX2).t2x
#	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t2x $@

#$(PREFIX2).t3x.bin: $(BASENAME).$(PREFIX2).t3x
#	apertium-validate-postchunk $(BASENAME).$(PREFIX2).t3x
#	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t3x $@


EXTRA_DIST=$(BASENAME).$(LANG1).dix $(BASENAME).$(PREFIX1).dix \
           $(BASENAME).post-$(LANG2).dix \
	   $(BASENAME).$(LANG2).metadix \
	   $(BASENAME).$(LANG1).dix \
	   $(BASENAME).$(PREFIX1).dix \
	   $(BASENAME).$(PREFIX1).rlx \
           $(PREFIX1).prob $(PREFIX2).prob \
	   lexchoicebil.xsl \
           $(BASENAME).$(PREFIX1).t1x $(BASENAME).$(PREFIX1).t2x \
           $(BASENAME).$(PREFIX1).t3x \
           $(BASENAME).pre-xfer-br.t1x buscaPar.xsl principal.xsl \
           modes.xml $(BASENAME).$(LANG1).acx

#           $(LANG1)-tagger-data/README $(LANG2)-tagger-data/README 
#           $(BASENAME).$(LANG1).tsx 
#           $(BASENAME).$(LANG2).tsx  
#           $(BASENAME).post-$(LANG1).dix 
#	   $(BASENAME).orth-$(LANG1).dix 
#	   $(BASENAME).pre-$(LANG1).t1x 

apertium_br_frdir=$(prefix)/share/apertium/apertium-$(PREFIX1)/
apertium_br_modesdir=$(prefix)/share/apertium/modes/

modes/$(PREFIX1).mode: modes.xml
	apertium-validate-modes modes.xml
	apertium-gen-modes modes.xml
	cp *.mode modes/

modes/$(PREFIX2).mode: modes.xml 
	apertium-validate-modes modes.xml
	apertium-gen-modes modes.xml
	cp *.mode modes/

apertium_br_fr_DATA=$(PREFIX1).automorf.bin $(PREFIX2).automorf.bin \
                    $(PREFIX1).autobil.bin $(PREFIX2).autobil.bin \
                    $(PREFIX1).autogen.bin $(PREFIX2).autogen.bin \
                    $(PREFIX1).autopgen.bin \
                    $(PREFIX1).prob $(PREFIX2).prob \
		    $(PREFIX1).rlx.bin \
		    $(PREFIX1).t1x.bin $(PREFIX1).t2x.bin $(PREFIX1).t3x.bin \
		    $(PREFIX1).pre-xfer.t1x.bin

noinst_DATA=modes/$(PREFIX1).mode
#           modes/$(PREFIX2).mode

#       $(INSTALL_DATA) $(PREFIX2).mode $(DESTDIR)$(apertium_br_modesdir)

install-data-local:
	mv modes modes.bak
	apertium-gen-modes modes.xml apertium-$(PREFIX1)
	rm -rf modes
	mv modes.bak modes
	test -d $(DESTDIR)$(apertium_br_modesdir) || mkdir $(DESTDIR)$(apertium_br_modesdir)
	$(INSTALL_DATA) $(PREFIX1).mode $(DESTDIR)$(apertium_br_modesdir)
	rm $(PREFIX1).mode
	$(INSTALL_DATA) $(BASENAME).$(PREFIX1).t1x $(BASENAME).$(PREFIX1).t2x $(DESTDIR)$(apertium_br_frdir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX1).t3x $(DESTDIR)$(apertium_br_frdir)
	$(INSTALL_DATA) $(BASENAME).pre-xfer-br.t1x $(DESTDIR)$(apertium_br_frdir)

CLEANFILES = -rf $(TARGETS_COMMON) modes
