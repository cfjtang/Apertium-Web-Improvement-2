GENERATEDSCRIPTS = apertium-pn-recogniser-final 

bin_SCRIPTS =  $(GENERATEDSCRIPTS)

CLEANFILES = *~ $(GENERATEDSCRIPTS)

EXTRA_DIST = final.sh

apertium-pn-recogniser-final: Makefile.am final.sh
	@echo "Creating apertium-pn-recogniser-final script"
	@echo "#!$(BASH)" >$@
	@cat final.sh >>$@
	@chmod a+x $@

