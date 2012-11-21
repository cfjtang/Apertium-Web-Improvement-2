VERSION=0.5.0
LANG1=sv
LANG2=da
PREFIX1=$(LANG1)-$(LANG2)
PREFIX2=$(LANG2)-$(LANG1)

BASENAME=apertium-$(PREFIX1)

TARGETS_COMMON = $(PREFIX1).automorf.bin $(PREFIX1).autobil.bin $(PREFIX1).autogen.bin \
     $(PREFIX1).autopgen.bin $(PREFIX2).automorf.bin $(PREFIX2).autobil.bin \
     $(PREFIX2).autogen.bin $(PREFIX2).autopgen.bin

TARGETS = $(TARGETS_COMMON) $(PREFIX1).t1x.bin $(PREFIX2).t1x.bin 

$(PREFIX1).automorf.bin: $(BASENAME).$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).$(LANG1).dix
	lt-comp lr $(BASENAME).$(LANG1).dix $@

$(PREFIX1).autobil.bin: $(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary $(BASENAME).$(PREFIX1).dix
	lt-comp lr $(BASENAME).$(PREFIX1).dix $@

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

$(PREFIX2).autopgen.bin: $(BASENAME).post-$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).post-$(LANG1).dix
	lt-comp lr $(BASENAME).post-$(LANG1).dix $@

$(PREFIX1).t1x.bin: $(BASENAME).$(PREFIX1).t1x
	apertium-validate-transfer $(BASENAME).$(PREFIX1).t1x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t1x $(PREFIX1).t1x.bin
#	@if [ "`which apertium-preprocess-transfer-bytecode-j`" == "" ]; then echo && echo "NOTE: lttoolbox-java (used for bytecode accelerated transfer) is missing" && echo "      Therefore the following will fail (but it's OK)" && echo; fi
#	-apertium-preprocess-transfer-bytecode-j $(BASENAME).$(PREFIX1).t1x $(PREFIX1).t1x.class

$(PREFIX2).t1x.bin: $(BASENAME).$(PREFIX2).t1x
	apertium-validate-transfer $(BASENAME).$(PREFIX2).t1x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t1x $(PREFIX2).t1x.bin
#	@if [ "`which apertium-preprocess-transfer-bytecode-j`" == "" ]; then echo && echo "NOTE: lttoolbox-java (used for bytecode accelerated transfer) is missing" && echo "      Therefore the following will fail (but it's OK)" && echo; fi
#	-apertium-preprocess-transfer-bytecode-j $(BASENAME).$(PREFIX2).t1x $(PREFIX2).t1x.class

$(PREFIX1).mode: modes.xml 
	apertium-gen-modes modes.xml
	cp *.mode modes/

$(PREFIX2).mode: modes.xml 
	apertium-gen-modes modes.xml
	cp *.mode modes/

EXTRA_DIST=$(BASENAME).$(LANG1).dix $(BASENAME).$(PREFIX1).dix \
           $(BASENAME).post-$(LANG1).dix $(BASENAME).post-$(LANG2).dix \
           $(BASENAME).$(LANG2).dix $(BASENAME).$(LANG1).tsx \
           $(BASENAME).$(LANG2).tsx $(BASENAME).$(PREFIX1).t1x \
           $(BASENAME).$(PREFIX2).t1x $(PREFIX1).prob $(PREFIX2).prob modes.xml

apertium_sv_modesdir=$(prefix)/share/apertium/modes/
apertium_sv_dadir=$(prefix)/share/apertium/apertium-$(PREFIX1)/
apertium_sv_da_DATA=$(PREFIX1).automorf.bin $(PREFIX2).automorf.bin \
                    $(PREFIX1).autobil.bin $(PREFIX2).autobil.bin \
                    $(PREFIX1).autogen.bin $(PREFIX2).autogen.bin \
                    $(PREFIX1).autopgen.bin $(PREFIX2).autopgen.bin \
                    $(PREFIX1).prob $(PREFIX2).prob \
                    $(BASENAME).$(PREFIX1).t1x $(PREFIX1).t1x.bin \
                    $(BASENAME).$(PREFIX2).t1x $(PREFIX2).t1x.bin \
		    $(PREFIX1).mode 

#$(PREFIX2).mode

install-data-local:
	mv modes modes.bak
	apertium-gen-modes modes.xml apertium-$(PREFIX1)
	rm -rf modes
	mv modes.bak modes
	test -d $(DESTDIR)$(apertium_sv_modesdir) || mkdir $(DESTDIR)$(apertium_sv_modesdir)
	$(INSTALL_DATA) $(PREFIX1).mode $(DESTDIR)$(apertium_sv_modesdir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX1).t1x $(DESTDIR)$(apertium_sv_dadir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX2).t1x $(DESTDIR)$(apertium_sv_dadir)
	cp *.mode modes/

#	$(INSTALL_DATA) $(PREFIX2).mode $(DESTDIR)$(apertium_sv_modesdir)


CLEANFILES = $(TARGETS)
