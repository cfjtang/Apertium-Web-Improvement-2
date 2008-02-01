VERSION=1.0
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


EXTRA_DIST=$(BASENAME).$(LANG1).dix $(BASENAME).$(PREFIX1).dix 
           
apertium_oldca_XXdir=$(prefix)/share/apertium/$(PREFIX1)/
apertium_oldca_XX_DATA=$(PREFIX1).automorf.bin 

CLEANFILES = $(TARGETS)
