#!/bin/sh 
BIN="/usr/local/bin"
EUEN="/usr/local/share/apertium/apertium-eu-en"
$BIN/apertium-transfer $EUEN/apertium-eu-en.eu-en.t1x  $EUEN/eu-en.t1x.bin  $EUEN/eu-en.autobil.bin |$BIN/apertium-interchunk $EUEN/apertium-eu-en.eu-en.t2x  $EUEN/eu-en.t2x.bin |$BIN/apertium-postchunk $EUEN/apertium-eu-en.eu-en.t3x  $EUEN/eu-en.t3x.bin 
  
