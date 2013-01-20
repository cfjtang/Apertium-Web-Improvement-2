VERSION=0.9.1
LANG1=eo
LANG2=fr
PREFIX1=$(LANG1)-$(LANG2)
PREFIX2=$(LANG2)-$(LANG1)

BASENAME=apertium-eo-fr
BILNAME=apertium-eo-fr.eo-fr.dix

TARGETS_COMMON = $(PREFIX2).automorf.bin $(PREFIX2).autobil.bin $(PREFIX2).autogen.bin \
	$(PREFIX2).t1x.bin $(PREFIX2).antaux1_t2x.bin $(PREFIX2).antaux2_t2x.bin \
	$(PREFIX2).antaux3_t2x.bin $(PREFIX2).antaux4_t2x.bin $(PREFIX2).t2x.bin \
	$(PREFIX2).post_t2x.bin $(PREFIX2).t3x.bin modes $(PREFIX2).mode

TARGETS = $(TARGETS_COMMON)

$(PREFIX2).antaux1_t2x.bin: $(BASENAME).$(PREFIX2).antaux1_t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX2).antaux1_t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).antaux1_t2x $@

$(PREFIX2).antaux2_t2x.bin: $(BASENAME).$(PREFIX2).antaux2_t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX2).antaux2_t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).antaux2_t2x $@

$(PREFIX2).antaux3_t2x.bin: $(BASENAME).$(PREFIX2).antaux3_t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX2).antaux3_t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).antaux3_t2x $@

$(PREFIX2).antaux4_t2x.bin: $(BASENAME).$(PREFIX2).antaux4_t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX2).antaux4_t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).antaux4_t2x $@

$(PREFIX2).post_t2x.bin: $(BASENAME).$(PREFIX2).post_t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX2).post_t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).post_t2x $@

$(PREFIX2).automorf.bin: $(BASENAME).$(LANG2).dix
	apertium-validate-dictionary $(BASENAME).$(LANG2).dix
	lt-comp lr $(BASENAME).$(LANG2).dix $@ $(BASENAME).$(LANG2).acx

$(PREFIX2).autobil.bin: $(BILNAME)
	apertium-validate-dictionary $(BILNAME)
	lt-comp rl $(BILNAME) $@

$(PREFIX2).autogen.bin: $(BASENAME).$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).$(LANG1).dix
	lt-comp rl $(BASENAME).$(LANG1).dix $@

$(PREFIX2).t1x.bin: $(BASENAME).$(PREFIX2).t1x
	apertium-validate-transfer $(BASENAME).$(PREFIX2).t1x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t1x $@
	@if [ "`which apertium-preprocess-transfer-bytecode-j`" == "" ]; then echo && echo "NOTE: lttoolbox-java (used for bytecode accelerated transfer) is missing" && echo "      Therefore the following will fail (but it's OK)" && echo; fi
	apertium-preprocess-transfer-bytecode-j $(BASENAME).$(PREFIX2).t1x $(PREFIX2).t1x.class

$(PREFIX2).t2x.bin: $(BASENAME).$(PREFIX2).t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX2).t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t2x $@

$(PREFIX2).t3x.bin: $(BASENAME).$(PREFIX2).t3x
	apertium-validate-postchunk $(BASENAME).$(PREFIX2).t3x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t3x $@

EXTRA_DIST= \
	$(BASENAME).$(LANG1).dix $(BILNAME) \
	$(BASENAME).post-$(LANG1).dix \
	$(BASENAME).$(LANG2).dix \
	$(PREFIX2).prob \
	$(BASENAME).$(PREFIX2).t1x \
	$(BASENAME).$(PREFIX2).antaux1_t2x \
	$(BASENAME).$(PREFIX2).antaux2_t2x \
	$(BASENAME).$(PREFIX2).antaux3_t2x \
	$(BASENAME).$(PREFIX2).antaux4_t2x \
	$(BASENAME).$(PREFIX2).t2x \
	$(BASENAME).$(PREFIX2).post_t2x \
	$(BASENAME).$(PREFIX2).t3x \
	$(LANG1)-pn.dat \
	$(LANG2)-pn.dat \
	modes.xml

apertium_eo_fr_DATA= \
	$(PREFIX2).automorf.bin \
	$(PREFIX2).autobil.bin  \
	$(PREFIX2).autogen.bin  \
	$(PREFIX2).prob \
	$(PREFIX2).t1x.bin \
	$(PREFIX2).antaux1_t2x.bin \
	$(PREFIX2).antaux2_t2x.bin \
	$(PREFIX2).antaux3_t2x.bin \
	$(PREFIX2).antaux4_t2x.bin \
	$(PREFIX2).t2x.bin \
	$(PREFIX2).post_t2x.bin \
	$(PREFIX2).t3x.bin \
	$(BASENAME).$(PREFIX2).t1x \
	$(BASENAME).$(PREFIX2).t2x \
	$(BASENAME).$(PREFIX2).t3x \
	$(LANG1)-pn.dat \
	$(LANG2)-pn.dat \
	$(PREFIX2).mode

apertium_eo_frdir=$(prefix)/share/apertium/apertium-$(PREFIX1)/
apertium_eo_frmodesdir=$(prefix)/share/apertium/modes/

$(PREFIX2).mode: modes.xml
	apertium-gen-modes modes.xml
	cp *.mode modes/

modes: modes.xml
	apertium-gen-modes modes.xml

install-data-local:
	apertium-gen-modes modes.xml apertium-$(PREFIX1)
	test -d $(DESTDIR)$(apertium_eo_frmodesdir) || mkdir $(DESTDIR)$(apertium_eo_frmodesdir)
	$(INSTALL_DATA) $(PREFIX2).mode $(DESTDIR)$(apertium_eo_frmodesdir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX2).t1x $(PREFIX2).t1x.class $(DESTDIR)$(apertium_eo_frdir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX2).antaux1_t2x $(BASENAME).$(PREFIX2).antaux2_t2x $(BASENAME).$(PREFIX2).antaux3_t2x $(BASENAME).$(PREFIX2).antaux4_t2x $(BASENAME).$(PREFIX2).t2x $(BASENAME).$(PREFIX2).post_t2x $(DESTDIR)$(apertium_eo_frdir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX2).t3x $(DESTDIR)$(apertium_eo_frdir)

#	$(INSTALL_DATA) $(PREFIX1)-pn.mode $(DESTDIR)$(apertium_es_modesdir)

CLEANFILES = -rf $(TARGETS) modes 
