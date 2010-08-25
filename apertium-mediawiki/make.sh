#!/bin/bash

#removing old binaries first
if [ -f demediawiki ]; then
  rm demediawiki
fi
if [ -f remediawiki ]; then
  rm remediawiki
fi

#compiling
g++ -o demediawiki parser.cpp tokenizer.cpp
g++ -w -o remediawiki remediawiki.cpp

#testing
./test.sh
