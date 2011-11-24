#!/bin/sh 
BIN="/usr/local/bin"
EUES="/usr/local/share/apertium/apertium-eu-es"
ESEN="/usr/local/share/apertium/apertium-en-es"
$BIN/apertium-transfer $EUES/apertium-eu-es.eu-es.t1x  $EUES/eu-es.t1x.bin  $EUES/eu-es.autobil.bin |$BIN/apertium-interchunk $EUES/apertium-eu-es.eu-es.t2x  $EUES/eu-es.t2x.bin |$BIN/apertium-postchunk $EUES/apertium-eu-es.eu-es.t3x  $EUES/eu-es.t3x.bin |$BIN/apertium-transfer $ESEN/apertium-en-es.es-en.t1x  $ESEN/es-en.t1x.bin  $ESEN/es-en.autobil.bin |$BIN/apertium-interchunk $ESEN/apertium-en-es.es-en.t2x  $ESEN/es-en.t2x.bin |$BIN/apertium-postchunk $ESEN/apertium-en-es.es-en.t3x  $ESEN/es-en.t3x.bin 
  
