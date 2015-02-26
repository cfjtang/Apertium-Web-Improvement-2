#!/bin/bash

C=2
GREP='.'
if [ $# -eq 1 ]; then
    C=$1
    GREP='^	-'
fi

bash wiki-tests.sh Regression mt ar update | grep -C $C "$GREP"
