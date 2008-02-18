VERSION=0.7
LANG1=oldca
LANG2=XX
PREFIX1=$(LANG1)-$(LANG2)
PREFIX2=$(LANG2)-$(LANG1)

BASENAME=apertium-$(PREFIX1)

TARGETS_COMMON = $(PREFIX1).automorf.bin 

TARGETS = $(TARGETS_COMMON) 

$(PREFIX1).automorf.bin: $(BASENAME).$(LANG1).dix
	apertium-validate-dictionary $(BASENAME).$(LANG1).dix
	lt-comp lr $(BASENAME).$(LANG1).dix $@


EXTRA_DIST=$(BASENAME).$(LANG1).dix modes.xml
           
apertium_oldca_XXdir=$(prefix)/share/apertium/apertium-$(PREFIX1)/
apertium_oldca_XX_DATA=$(PREFIX1).automorf.bin 
apertium_oldca_XXmodesdir=$(prefix)/share/apertium/modes/

CLEANFILES = $(TARGETS)

install-data-local:
	$(prefix)/bin/apertium-gen-modes modes.xml apertium-$(PREFIX1)
	$(INSTALL_DATA) $(PREFIX1)-morph.mode $(apertium_oldca_XXmodesdir)
