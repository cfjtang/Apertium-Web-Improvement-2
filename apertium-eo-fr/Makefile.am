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
	$(PREFIX2).post_t2x.bin $(PREFIX2).t3x.bin \
	$(PREFIX2).t1x.class


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

$(PREFIX2).t1x.bin $(PREFIX2).t1x.class: $(BASENAME).$(PREFIX2).t1x
	apertium-validate-transfer $(BASENAME).$(PREFIX2).t1x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t1x $(PREFIX2).t1x.bin
	@if command -V apertium-preprocess-transfer-bytecode-j >/dev/null; then \
		apertium-preprocess-transfer-bytecode-j $(BASENAME).$(PREFIX2).t1x $(PREFIX2).t1x.class; \
	else \
		echo "NOTE: lttoolbox-java (used for bytecode accelerated transfer) is missing"; \
		echo "      but that's OK"; \
		echo; \
		touch $(BASENAME).$(PREFIX2).t1x $(PREFIX2).t1x.class; \
	fi


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

apertium_eo_fr_DATA= $(TARGETS_COMMON) \
	$(PREFIX2).prob \
	$(LANG1)-pn.dat \
	$(LANG2)-pn.dat \
	$(BASENAME).$(PREFIX2).t1x \
	$(BASENAME).$(PREFIX2).antaux1_t2x $(BASENAME).$(PREFIX2).antaux2_t2x $(BASENAME).$(PREFIX2).antaux3_t2x $(BASENAME).$(PREFIX2).antaux4_t2x \
	$(BASENAME).$(PREFIX2).t2x $(BASENAME).$(PREFIX2).post_t2x \
	$(BASENAME).$(PREFIX2).t3x

###############################################################################
## Installation
###############################################################################

apertium_eo_frdir=$(prefix)/share/apertium/apertium-$(PREFIX1)/
apertium_modesdir=$(prefix)/share/apertium/modes/

modes/$(PREFIX2).mode: modes.xml
	apertium-gen-modes modes.xml
	mv *.mode modes/

install-data-local:
	mv modes modes.bak
	apertium-gen-modes modes.xml apertium-$(PREFIX1)
	rm -rf modes
	mv modes.bak modes
	test -d $(DESTDIR)$(apertium_modesdir) || mkdir $(DESTDIR)$(apertium_modesdir)
	$(INSTALL_DATA) $(PREFIX2).mode $(DESTDIR)$(apertium_modesdir)


# All modes are created by the same goal, listing several will just give problems with make -j2
noinst_DATA=modes/$(PREFIX2).mode


###############################################################################
## Cleanup
###############################################################################

CLEANFILES = $(TARGETS_COMMON)
clean-local: 
	-rm -rf .deps modes
