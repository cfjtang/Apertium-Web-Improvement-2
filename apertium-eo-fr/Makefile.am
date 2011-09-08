VERSION=0.9.0
LANG1=eo
LANG2=fr
PREFIX1=$(LANG2)-$(LANG1)

BASENAME=apertium-eo-fr
BILNAME=apertium-eo-fr.eo-fr.dix

TARGETS_COMMON = $(PREFIX1).automorf.bin $(PREFIX1).autobil.bin $(PREFIX1).autogen.bin \
     $(PREFIX1).t1x.bin $(PREFIX1).antaux1_t2x.bin $(PREFIX1).antaux2_t2x.bin $(PREFIX1).antaux3_t2x.bin $(PREFIX1).antaux4_t2x.bin $(PREFIX1).t2x.bin $(PREFIX1).post_t2x.bin $(PREFIX1).t3x.bin \
     modes $(PREFIX1).mode

TARGETS = $(TARGETS_COMMON)

$(PREFIX1).antaux1_t2x.bin: $(BASENAME).$(PREFIX1).antaux1_t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX1).antaux1_t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).antaux1_t2x $@

$(PREFIX1).antaux2_t2x.bin: $(BASENAME).$(PREFIX1).antaux2_t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX1).antaux2_t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).antaux2_t2x $@

$(PREFIX1).antaux3_t2x.bin: $(BASENAME).$(PREFIX1).antaux3_t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX1).antaux3_t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).antaux3_t2x $@

$(PREFIX1).antaux4_t2x.bin: $(BASENAME).$(PREFIX1).antaux4_t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX1).antaux4_t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).antaux4_t2x $@

$(PREFIX1).post_t2x.bin: $(BASENAME).$(PREFIX1).post_t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX1).post_t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).post_t2x $@

$(PREFIX1).automorf.bin: $(BASENAME).$(LANG2).dix
	apertium-validate-dictionary $(BASENAME).$(LANG2).dix
	lt-comp lr $(BASENAME).$(LANG2).dix $@ $(BASENAME).$(LANG2).acx

$(PREFIX1).autobil.bin: $(BILNAME)
	apertium-validate-dictionary $(BILNAME)
	lt-comp rl $(BILNAME) $@

$(PREFIX1).autogen.bin: $(BASENAME).$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).$(LANG1).dix
	lt-comp rl $(BASENAME).$(LANG1).dix $@

$(PREFIX1).t1x.bin: $(BASENAME).$(PREFIX1).t1x
	apertium-validate-transfer $(BASENAME).$(PREFIX1).t1x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t1x $@
	@if [ "`which apertium-preprocess-transfer-bytecode-j`" == "" ]; then echo && echo "NOTE: lttoolbox-java (used for bytecode accelerated transfer) is missing" && echo "      Therefore the following will fail (but it's OK)" && echo; fi
	-apertium-preprocess-transfer-bytecode-j $(BASENAME).$(PREFIX1).t1x $(PREFIX1).t1x.class

$(PREFIX1).t2x.bin: $(BASENAME).$(PREFIX1).t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX1).t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t2x $@

$(PREFIX1).t3x.bin: $(BASENAME).$(PREFIX1).t3x
	apertium-validate-postchunk $(BASENAME).$(PREFIX1).t3x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t3x $@

EXTRA_DIST=$(BASENAME).$(LANG1).dix $(BILNAME) \
	   $(BASENAME).post-$(LANG1).dix \
	   $(BASENAME).$(LANG2).dix \
	   $(PREFIX1).prob \
	   $(BASENAME).$(PREFIX1).t1x $(BASENAME).$(PREFIX1).antaux1_t2x $(BASENAME).$(PREFIX1).antaux2_t2x $(BASENAME).$(PREFIX1).antaux3_t2x $(BASENAME).$(PREFIX1).antaux4_t2x $(BASENAME).$(PREFIX1).t2x $(BASENAME).$(PREFIX1).post_t2x \
	   $(BASENAME).$(PREFIX1).t3x \
	   $(LANG1)-pn.dat \
	   $(LANG2)-pn.dat \
	   modes.xml

apertium_eo_ca_DATA=$(PREFIX1).automorf.bin \
		    $(PREFIX1).autobil.bin  \
		    $(PREFIX1).autogen.bin  \
		    $(PREFIX1).prob \
		    $(PREFIX1).t1x.bin $(PREFIX1).antaux1_t2x.bin $(PREFIX1).antaux2_t2x.bin $(PREFIX1).antaux3_t2x.bin $(PREFIX1).antaux4_t2x.bin $(PREFIX1).t2x.bin $(PREFIX1).post_t2x.bin $(PREFIX1).t3x.bin \
		    $(BASENAME).$(PREFIX1).t1x $(BASENAME).$(PREFIX1).t2x $(BASENAME).$(PREFIX1).t3x \
		    $(LANG1)-pn.dat \
		    $(LANG2)-pn.dat \
		    $(PREFIX1).mode

apertium_eo_cadir=$(prefix)/share/apertium/apertium-$(PREFIX1)/
apertium_eo_camodesdir=$(prefix)/share/apertium/modes/

$(PREFIX1).mode: modes.xml
	apertium-gen-modes modes.xml
	cp *.mode modes/

modes: modes.xml
	apertium-gen-modes modes.xml

install-data-local:
	apertium-gen-modes modes.xml apertium-$(PREFIX1)
	$(INSTALL_DATA) $(PREFIX1).mode $(apertium_eo_camodesdir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX1).t1x $(BASENAME).$(PREFIX1).antaux1_t2x  $(BASENAME).$(PREFIX1).antaux2_t2x $(BASENAME).$(PREFIX1).antaux3_t2x $(BASENAME).$(PREFIX1).antaux4_t2x $(BASENAME).$(PREFIX1).t2x $(BASENAME).$(PREFIX1).post_t2x $(apertium_eo_cadir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX1).t3x $(apertium_eo_cadir)


#	$(INSTALL_DATA) $(PREFIX2)-pn.mode $(apertium_es_modesdir)

CLEANFILES = -rf $(TARGETS) modes 

