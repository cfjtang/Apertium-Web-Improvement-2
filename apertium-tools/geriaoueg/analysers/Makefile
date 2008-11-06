ROOT=/build/svnroot/apertium/trunk/
LTCOMP=/build/local/default/bin/lt-comp
BRDIX=$(ROOT)/incubator/apertium-br-fr.br.dix
ISDIX=$(ROOT)/incubator/apertium-fo-is.is.dix
CYDIX=$(ROOT)/apertium-cy-en/apertium-cy-en.cy.dix
ACX=$(ROOT)/incubator/apertium-br-cy.br.acx
TARGETS=br-fr.bin cy-en.bin br-nl.bin is-ca.bin

all: $(TARGETS)

br-nl.bin:
	$(LTCOMP) lr $(BRDIX) $@ $(ACX)

br-fr.bin:
	$(LTCOMP) lr $(BRDIX) $@ $(ACX)

cy-en.bin:
	$(LTCOMP) lr $(CYDIX) $@ $(ACX)

is-ca.bin:
	$(LTCOMP) lr $(ISDIX) $@ $(ACX)


clean: 
	rm -f $(TARGETS)
