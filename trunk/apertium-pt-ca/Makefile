VERSION=0.8.1
# LANG1=oc
# LANG2=ca
# PREFIX1=pt-ca
# PREFIX2=ca-pt

# BASENAME=apertium-pt-ca

TARGETS_COMMON = pt-ca.automorf.bin  pt-ca.autobil.bin pt-ca.autogen.bin pt-ca.autopgen.bin\
      ca-pt.automorf.bin  ca-pt.autobil.bin ca-pt.autogen.bin ca-pt.autopgen.bin
     

TARGETS = $(TARGETS_COMMON) trules-pt-ca.bin trules-ca-pt.bin \
          trules-pt-ca.xml trules-ca-pt.xml

all: $(TARGETS)

# diccionaris pt-ca i pt-ca crossed

pt-ca.automorf.bin: apertium-pt-ca.pt.dix
	apertium-validate-dictionary apertium-pt-ca.pt.dix
	lt-comp lr apertium-pt-ca.pt.dix $@

ca-pt.automorf.bin: apertium-pt-ca.ca.dix
	apertium-validate-dictionary apertium-pt-ca.ca.dix
	lt-comp lr apertium-pt-ca.ca.dix $@

pt-ca.autobil.bin: apertium-pt-ca.pt-ca.dix
	apertium-validate-dictionary apertium-pt-ca.pt-ca.dix
	lt-comp lr apertium-pt-ca.pt-ca.dix $@

ca-pt.autobil.bin: apertium-pt-ca.pt-ca.dix
	apertium-validate-dictionary apertium-pt-ca.pt-ca.dix
	lt-comp rl apertium-pt-ca.pt-ca.dix $@

pt-ca.autogen.bin: apertium-pt-ca.ca.dix
	apertium-validate-dictionary apertium-pt-ca.ca.dix
	lt-comp rl apertium-pt-ca.ca.dix $@

ca-pt.autogen.bin: apertium-pt-ca.pt.dix
	apertium-validate-dictionary apertium-pt-ca.pt.dix
	lt-comp rl apertium-pt-ca.pt.dix $@

pt-ca.autopgen.bin: apertium-pt-ca.post-ca.dix
	apertium-validate-dictionary apertium-pt-ca.post-ca.dix
	lt-comp lr apertium-pt-ca.post-ca.dix $@

ca-pt.autopgen.bin: apertium-pt-ca.post-pt.dix
	apertium-validate-dictionary apertium-pt-ca.post-pt.dix
	lt-comp lr apertium-pt-ca.post-pt.dix $@


# transfers pt-ca
trules-pt-ca.bin: apertium-pt-ca.trules-pt-ca.xml
	apertium-validate-transfer apertium-pt-ca.trules-pt-ca.xml
	apertium-preprocess-transfer apertium-pt-ca.trules-pt-ca.xml \
	                             trules-pt-ca.bin
	
trules-ca-pt.bin: apertium-pt-ca.trules-ca-pt.xml
	apertium-validate-transfer apertium-pt-ca.trules-ca-pt.xml
	apertium-preprocess-transfer apertium-pt-ca.trules-ca-pt.xml \
	                             trules-ca-pt.bin

trules-pt-ca.xml: apertium-pt-ca.trules-pt-ca.xml
	ln -s apertium-pt-ca.trules-pt-ca.xml trules-pt-ca.xml

trules-ca-pt.xml: apertium-pt-ca.trules-ca-pt.xml
	ln -s apertium-pt-ca.trules-ca-pt.xml trules-ca-pt.xml

CLEANFILES = $(TARGETS)
