VERSION=0.3.1
LANG1=eu
LANG2=en
PREFIX1=$(LANG1)-$(LANG2)
PREFIX2=$(LANG2)-$(LANG1)

BASENAME=apertium-$(PREFIX1)


TARGETS_COMMON = $(PREFIX1).automorf.bin \
                    $(PREFIX1).autobil.bin \
                    $(PREFIX1).autogen.bin \
                    $(PREFIX1).autopgen.bin \
                    $(PREFIX1).ordinals.t1x.bin \
                    $(PREFIX1).colloc.t1x.bin \
		    $(PREFIX1).t1x.bin $(PREFIX1).t2x.bin $(PREFIX1).t3x.bin

TARGETS = $(TARGETS_COMMON) trules-$(PREFIX1).bin trules-$(PREFIX2).bin \
          trules-$(PREFIX1).xml trules-$(PREFIX2).xml
          # $(BASENAME).$(LANG1)-lextor.dix $(BASENAME).$(LANG2)-lextor.dix \
	  # $(BASENAME).$(PREFIX1)-lextor.dix $(BASENAME).$(PREFIX2)-lextor.dix


$(PREFIX2).automorf.bin: $(BASENAME).$(LANG2).dix
	apertium-validate-dictionary $(BASENAME).$(LANG2).dix
	lt-comp lr $(BASENAME).$(LANG2).dix $@


$(PREFIX1).automorf.bin: $(BASENAME).$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).$(LANG1).dix
	lt-comp lr $(BASENAME).$(LANG1).dix $@

$(PREFIX1).autobil.bin: $(BASENAME).$(PREFIX1).dix
	apertium-validate-dictionary $(BASENAME).$(PREFIX1).dix
	xsltproc --stringparam r2l yes lexchoicebil.xsl $(BASENAME).$(PREFIX1).dix >tmp$(PREFIX1).autobil.dix
	lt-comp lr tmp$(PREFIX1).autobil.dix $@
	rm tmp$(PREFIX1).autobil.dix

$(BASENAME).$(LANG2).dix: $(BASENAME).$(LANG2).metadix
	@xsltproc buscaPar.xsl apertium-$(PREFIX1).$(LANG2).metadix | uniq > tmp2gen.xsl
	@xsltproc tmp2gen.xsl apertium-$(PREFIX1).$(LANG2).metadix >$ $@
	@rm tmp2gen.xsl

$(PREFIX1).autogen.bin: $(BASENAME).$(LANG2).dix
	apertium-validate-dictionary $(BASENAME).$(LANG2).dix
	lt-comp rl $(BASENAME).$(LANG2).dix $@

$(PREFIX1).autopgen.bin: $(BASENAME).post-$(LANG2).dix
	apertium-validate-dictionary $(BASENAME).post-$(LANG2).dix
	lt-comp lr $(BASENAME).post-$(LANG2).dix $@

##$(PREFIX2).automorf.bin: $(BASENAME).$(LANG2).dix
##	apertium-validate-dictionary $(BASENAME).$(LANG2).dix
##	lt-comp lr $(BASENAME).$(LANG2).dix $@

##$(PREFIX2).autobil.bin: $(BASENAME).$(PREFIX1).dix
##	apertium-validate-dictionary $(BASENAME).$(PREFIX1).dix
##NO TOCAR	apertium-gen-oldbil $(BASENAME).$(PREFIX1).dix tmp$(PREFIX2).autobil.dix
##	xsltproc translate-to-default-equivalent.xsl $(BASENAME).$(PREFIX1).dix >tmp$(PREFIX2).autobil.dix
##	lt-comp rl tmp$(PREFIX2).autobil.dix $@
##	rm tmp$(PREFIX2).autobil.dix

##$(PREFIX2).autogen.bin: $(BASENAME).$(LANG1).metadix
##	@xsltproc buscaPar.xsl apertium-en-ca.en.metadix | uniq > tmp2gen.xsl
##	@xsltproc tmp2gen.xsl apertium-en-ca.en.metadix >$(BASENAME).$(LANG1).dixtmp2
##	@rm tmp2gen.xsl
##	apertium-validate-dictionary $(BASENAME).$(LANG1).dixtmp2
##	lt-comp rl $(BASENAME).$(LANG1).dixtmp2 $@
##	@rm $(BASENAME).$(LANG1).dixtmp2

##$(PREFIX2).autopgen.bin: $(BASENAME).post-$(LANG1).dix
##	apertium-validate-dictionary $(BASENAME).post-$(LANG1).dix
##	lt-comp lr $(BASENAME).post-$(LANG1).dix $@

##trules-$(PREFIX1).bin: $(BASENAME).trules-$(PREFIX1).xml
##	apertium-validate-transfer $(BASENAME).trules-$(PREFIX1).xml
##	apertium-preprocess-transfer $(BASENAME).trules-$(PREFIX1).xml \
##	                             trules-$(PREFIX1).bin

##trules-$(PREFIX2).bin: $(BASENAME).trules-$(PREFIX2).xml
##	apertium-validate-transfer $(BASENAME).trules-$(PREFIX2).xml
##	apertium-preprocess-transfer $(BASENAME).trules-$(PREFIX2).xml \
##	                             trules-$(PREFIX2).bin

##trules-$(PREFIX1).xml: $(BASENAME).trules-$(PREFIX1).xml
##	ln -s $(BASENAME).trules-$(PREFIX1).xml trules-$(PREFIX1).xml

##trules-$(PREFIX2).xml: $(BASENAME).trules-$(PREFIX2).xml
##	ln -s $(BASENAME).trules-$(PREFIX2).xml trules-$(PREFIX2).xml


##$(BASENAME).$(LANG1)-lextor.dix: $(BASENAME).$(PREFIX1).dix
##	apertium-validate-dictionary $(BASENAME).$(PREFIX1).dix
##	apertium-gen-lextormono rl $(BASENAME).$(PREFIX1).dix $@

##$(BASENAME).$(LANG2)-lextor.dix: $(BASENAME).$(PREFIX1).dix
##	apertium-validate-dictionary $(BASENAME).$(PREFIX1).dix
##	apertium-gen-lextormono lr $(BASENAME).$(PREFIX1).dix $@

##$(PREFIX1).lextormono.bin: $(BASENAME).$(LANG1)-lextor.dix
##	lt-comp lr $(BASENAME).$(LANG1)-lextor.dix $@

##$(PREFIX2).lextormono.bin: $(BASENAME).$(LANG2)-lextor.dix
##	lt-comp lr $(BASENAME).$(LANG2)-lextor.dix $@


##$(BASENAME).$(PREFIX1)-lextor.dix: $(BASENAME).$(PREFIX1).dix
##	apertium-validate-dictionary $(BASENAME).$(PREFIX1).dix
##	apertium-gen-lextorbil rl $(BASENAME).$(PREFIX1).dix $@

##$(BASENAME).$(PREFIX2)-lextor.dix: $(BASENAME).$(PREFIX1).dix
##	apertium-validate-dictionary $(BASENAME).$(PREFIX1).dix
##	apertium-gen-lextorbil lr $(BASENAME).$(PREFIX1).dix $@

##$(PREFIX1).lextorbil.bin: $(BASENAME).$(PREFIX1)-lextor.dix
##	lt-comp lr $(BASENAME).$(PREFIX1)-lextor.dix $@

##$(PREFIX2).lextorbil.bin: $(BASENAME).$(PREFIX2)-lextor.dix
##	lt-comp lr $(BASENAME).$(PREFIX2)-lextor.dix $@

$(PREFIX1).colloc.t1x.bin: $(BASENAME).colloc.t1x
	apertium-validate-transfer $(BASENAME).colloc.t1x
	apertium-preprocess-transfer $(BASENAME).colloc.t1x $@

$(PREFIX1).ordinals.t1x.bin: $(BASENAME).ordinals.t1x
	apertium-validate-transfer $(BASENAME).ordinals.t1x
	apertium-preprocess-transfer $(BASENAME).ordinals.t1x $@

$(PREFIX1).t1x.bin: $(BASENAME).$(PREFIX1).t1x
	apertium-validate-transfer $(BASENAME).$(PREFIX1).t1x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t1x $@

$(PREFIX1).t2x.bin: $(BASENAME).$(PREFIX1).t2x
	apertium-validate-interchunk $(BASENAME).$(PREFIX1).t2x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t2x $@

$(PREFIX1).t3x.bin: $(BASENAME).$(PREFIX1).t3x
	apertium-validate-postchunk $(BASENAME).$(PREFIX1).t3x
	apertium-preprocess-transfer $(BASENAME).$(PREFIX1).t3x $@

##$(PREFIX2).t1x.bin: $(BASENAME).$(PREFIX2).t1x
##	apertium-validate-transfer $(BASENAME).$(PREFIX2).t1x
##	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t1x $@

##$(PREFIX2).t2x.bin: $(BASENAME).$(PREFIX2).t2x
##	apertium-validate-interchunk $(BASENAME).$(PREFIX2).t2x
##	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t2x $@

##$(PREFIX2).t3x.bin: $(BASENAME).$(PREFIX2).t3x
##	apertium-validate-postchunk $(BASENAME).$(PREFIX2).t3x
##	apertium-preprocess-transfer $(BASENAME).$(PREFIX2).t3x $@


EXTRA_DIST=$(BASENAME).$(LANG1).dix $(BASENAME).$(PREFIX1).dix \
	   $(BASENAME).post-$(LANG2).dix \
           $(BASENAME).$(LANG2).metadix \
           $(BASENAME).$(LANG1).tsx \
           $(PREFIX1).prob \
           lexchoicebil.xsl \
           $(BASENAME).$(PREFIX1).t1x $(BASENAME).$(PREFIX1).t2x \
           $(BASENAME).$(PREFIX1).t3x \
           apertium-eu-en.colloc.t1x apertium-eu-en.ordinals.t1x \
           buscaPar.xsl principal.xsl \
           modes.xml

#           $(BASENAME).post-$(LANG1).dix
#	    $(BASENAME).$(PREFIX2).t1x
#	    $(BASENAME).$(PREFIX2).t2x $(BASENAME).$(PREFIX2).t3x
#	    $(BASENAME).$(LANG2).tsx

apertium_eu_esdir=$(prefix)/share/apertium/apertium-$(PREFIX1)/
apertium_modesdir=$(prefix)/share/apertium/modes/

modes/$(PREFIX1).mode: modes.xml
	apertium-validate-modes modes.xml
	apertium-gen-modes modes.xml
	cp *.mode modes/

apertium_eu_es_DATA=$(TARGETS_COMMON) \
                    $(PREFIX1).prob \
		    modes.xml

# All modes are created by the same goal, listing several will just give problems with make -j2
noinst_DATA=modes/$(PREFIX1).mode

install-data-local:
	mv modes modes.bak
	apertium-gen-modes modes.xml $(BASENAME)
	rm -rf modes
	mv modes.bak modes
	test -d $(DESTDIR)$(apertium_modesdir) || mkdir $(DESTDIR)$(apertium_modesdir)
	modes=`xmllint --xpath '//mode[@install="yes"]/@name' modes.xml | sed 's/ *name="\([^"]*\)"/\1.mode /g'`; \
		$(INSTALL_DATA) $$modes $(DESTDIR)$(apertium_modesdir); \
		rm $$modes
	$(INSTALL_DATA) $(BASENAME).$(PREFIX1).t1x $(BASENAME).$(PREFIX1).t2x $(DESTDIR)$(apertium_eu_esdir)
	$(INSTALL_DATA) $(BASENAME).$(PREFIX1).t3x $(DESTDIR)$(apertium_eu_esdir)
	$(INSTALL_DATA) $(BASENAME).ordinals.t1x $(DESTDIR)$(apertium_eu_esdir)
	$(INSTALL_DATA) $(BASENAME).colloc.t1x $(DESTDIR)$(apertium_eu_esdir)


CLEANFILES = $(TARGETS_COMMON)
clean-local:
	-rm -rf modes
