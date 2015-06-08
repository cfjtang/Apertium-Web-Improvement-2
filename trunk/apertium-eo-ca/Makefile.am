VERSION=0.9.1
LANG1=eo
LANG2=ca
PREFIX1=$(LANG2)-$(LANG1)
PREFIX2=$(LANG2)-$(LANG1)-translit

BASENAME=apertium-eo-ca
BILNAME=apertium-eo-ca.eo-ca.dix

TARGETS_COMMON = $(PREFIX1).automorf.bin $(PREFIX1).autobil.bin $(PREFIX1).autogen.bin \
     $(PREFIX2).autobil.bin $(PREFIX2).autogen.bin $(PREFIX1).autopgen.bin $(PREFIX2).autopgen.bin \
     $(PREFIX1).t1x.bin $(PREFIX1).antaux_t2x.bin $(PREFIX1).antaux2_t2x.bin $(PREFIX1).t2x.bin $(PREFIX1).t3x.bin \
     $(PREFIX2).t1x.bin $(PREFIX2).t2x.bin $(PREFIX2).t3x.bin

TARGETS = $(TARGETS_COMMON)

$(PREFIX1).antaux_t2x.bin: $(BASENAME).$(PREFIX1).antaux_t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX1).antaux_t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).antaux_t2x $@

$(PREFIX1).antaux2_t2x.bin: $(BASENAME).$(PREFIX1).antaux2_t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX1).antaux2_t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).antaux2_t2x $@

$(PREFIX1).automorf.bin: $(BASENAME).$(LANG2).dix
	apertium-validate-dictionary $(BASENAME).$(LANG2).dix
	lt-comp lr $(BASENAME).$(LANG2).dix $@ $(BASENAME).$(LANG2).acx

$(PREFIX1).autobil.bin: $(BILNAME)
	apertium-validate-dictionary $(BILNAME)
	lt-comp rl $(BILNAME) $@

$(BILNAME).translit: $(BILNAME)
	./transliterate.sh $(BILNAME) >$@

$(PREFIX2).autobil.bin: $(BILNAME).translit
	apertium-validate-dictionary $(BILNAME).translit
	lt-comp rl $(BILNAME).translit $@

$(PREFIX1).autogen.bin: $(BASENAME).$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).$(LANG1).dix
	lt-comp rl $(BASENAME).$(LANG1).dix $@

$(BASENAME).$(LANG1).dix.translit: $(BASENAME).$(LANG1).dix
	./transliterate.sh $(BASENAME).$(LANG1).dix >$@

$(PREFIX2).autogen.bin: $(BASENAME).$(LANG1).dix.translit
	apertium-validate-dictionary $(BASENAME).$(LANG1).dix.translit
	lt-comp rl $(BASENAME).$(LANG1).dix.translit $@

$(PREFIX1).autopgen.bin: $(BASENAME).post-$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).post-$(LANG1).dix
	lt-comp lr $(BASENAME).post-$(LANG1).dix $@

$(BASENAME).post-$(LANG1).dix.translit: $(BASENAME).post-$(LANG1).dix
	./transliterate.sh $(BASENAME).post-$(LANG1).dix >$@

$(PREFIX2).autopgen.bin: $(BASENAME).post-$(LANG1).dix.translit
	apertium-validate-dictionary $(BASENAME).post-$(LANG1).dix.translit
	lt-comp lr $(BASENAME).post-$(LANG1).dix.translit $@

$(PREFIX1).t1x.bin: $(BASENAME).$(PREFIX1).t1x
	apertium-validate-transfer $(BASENAME).$(PREFIX1).t1x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t1x $@

$(BASENAME).$(PREFIX1).t1x.translit: $(BASENAME).$(PREFIX1).t1x
	./transliterate.sh $(BASENAME).$(PREFIX1).t1x >$@

$(PREFIX2).t1x.bin: $(BASENAME).$(PREFIX1).t1x.translit
	apertium-validate-transfer $(BASENAME).$(PREFIX1).t1x.translit
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t1x.translit  $@


$(PREFIX1).t2x.bin: $(BASENAME).$(PREFIX1).t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX1).t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t2x $@

$(BASENAME).$(PREFIX1).t2x.translit: $(BASENAME).$(PREFIX1).t2x
	./transliterate.sh $(BASENAME).$(PREFIX1).t2x >$@

$(PREFIX2).t2x.bin: $(BASENAME).$(PREFIX1).t2x.translit
	apertium-validate-interchunk $(BASENAME).$(PREFIX1).t2x.translit
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t2x.translit $@


$(PREFIX1).t3x.bin: $(BASENAME).$(PREFIX1).t3x
	apertium-validate-postchunk $(BASENAME).$(PREFIX1).t3x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t3x $@

$(BASENAME).$(PREFIX1).t3x.translit: $(BASENAME).$(PREFIX1).t3x
	./transliterate.sh $(BASENAME).$(PREFIX1).t3x >$@

$(PREFIX2).t3x.bin: $(BASENAME).$(PREFIX1).t3x.translit
	apertium-validate-postchunk $(BASENAME).$(PREFIX1).t3x.translit
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t3x.translit $@

EXTRA_DIST=$(BASENAME).$(LANG1).dix $(BILNAME) \
	   $(BASENAME).post-$(LANG1).dix \
	   $(BASENAME).$(LANG2).dix \
	   $(PREFIX1).prob \
	   $(BASENAME).$(PREFIX1).t1x $(BASENAME).$(PREFIX1).antaux_t2x $(BASENAME).$(PREFIX1).antaux2_t2x $(BASENAME).$(PREFIX1).t2x \
	   $(BASENAME).$(PREFIX1).t3x \
	   transliterate.sh modes.xml

apertium_eo_ca_DATA=$(PREFIX1).automorf.bin \
		    $(PREFIX1).autobil.bin $(PREFIX2).autobil.bin \
		    $(PREFIX1).autogen.bin $(PREFIX2).autogen.bin \
		    $(PREFIX1).autopgen.bin $(PREFIX2).autopgen.bin \
		    $(PREFIX1).prob \
		    $(PREFIX1).t1x.bin $(PREFIX1).antaux_t2x.bin $(PREFIX1).antaux2_t2x.bin $(PREFIX1).t2x.bin $(PREFIX1).t3x.bin \
		    $(PREFIX2).t1x.bin $(PREFIX2).t2x.bin $(PREFIX2).t3x.bin \
		    $(BASENAME).$(PREFIX1).t1x $(BASENAME).$(PREFIX1).t2x $(BASENAME).$(PREFIX1).t3x \
		    $(BASENAME).$(PREFIX1).t1x.translit $(BASENAME).$(PREFIX1).t2x.translit $(BASENAME).$(PREFIX1).t3x.translit \
		    $(BASENAME).$(PREFIX1).antaux_t2x  $(BASENAME).$(PREFIX1).antaux2_t2x 

apertium_eo_cadir=$(prefix)/share/apertium/apertium-$(PREFIX1)/
apertium_modesdir=$(prefix)/share/apertium/modes/

modes/$(PREFIX1).mode: modes.xml
	apertium-validate-modes modes.xml
	apertium-gen-modes modes.xml
	cp *.mode modes/

# All modes are created by the same goal, listing several will just give problems with make -j2
noinst_DATA=modes/$(PREFIX1).mode

install-data-local:
	mv modes modes.bak
	apertium-gen-modes modes.xml apertium-$(PREFIX1)
	rm -rf modes
	mv modes.bak modes
	test -d $(DESTDIR)$(apertium_modesdir) || mkdir $(DESTDIR)$(apertium_modesdir)
	$(INSTALL_DATA) $(PREFIX1).mode $(DESTDIR)$(apertium_modesdir)
	$(INSTALL_DATA) $(PREFIX2).mode $(DESTDIR)$(apertium_modesdir)


CLEANFILES = -rf $(TARGETS) *.translit modes 
