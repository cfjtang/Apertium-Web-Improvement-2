#!/bin/sh 
BIN="/usr/local/bin"
EUES="/usr/local/share/apertium/apertium-eu-es"
$BIN/apertium-transfer $EUES/apertium-eu-es.eu-es.t1x  $EUES/eu-es.t1x.bin  $EUES/eu-es.autobil.bin |$BIN/apertium-interchunk $EUES/apertium-eu-es.eu-es.t2x  $EUES/eu-es.t2x.bin |$BIN/apertium-postchunk $EUES/apertium-eu-es.eu-es.t3x  $EUES/eu-es.t3x.bin 
  
