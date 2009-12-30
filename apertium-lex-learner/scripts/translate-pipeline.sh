#!/bin/bash


/home/fran/local/bin/apertium-tagger -g /home/fran/local/share/apertium/apertium-en-ca/ca-en.prob |\
/home/fran/local/bin/apertium-pretransfer|\
/home/fran/local/bin/apertium-transfer /home/fran/local/share/apertium/apertium-en-ca/apertium-en-ca.ca-en.t1x  /home/fran/local/share/apertium/apertium-en-ca/ca-en.t1x.bin  /home/fran/local/share/apertium/apertium-en-ca/ca-en.autobil.bin |\
/home/fran/local/bin/apertium-interchunk /home/fran/local/share/apertium/apertium-en-ca/apertium-en-ca.ca-en.t2x  /home/fran/local/share/apertium/apertium-en-ca/ca-en.t2x.bin |\
/home/fran/local/bin/apertium-postchunk /home/fran/local/share/apertium/apertium-en-ca/apertium-en-ca.ca-en.t3x  /home/fran/local/share/apertium/apertium-en-ca/ca-en.t3x.bin |\
/home/fran/local/bin/lt-proc -g /home/fran/local/share/apertium/apertium-en-ca/ca-en.autogen.bin |\
/home/fran/local/bin/lt-proc -p /home/fran/local/share/apertium/apertium-en-ca/ca-en.autopgen.bin 
