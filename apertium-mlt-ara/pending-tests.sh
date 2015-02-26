#!/bin/bash

C=2
GREP='.'
if [ $# -eq 1 ]; then
    C=$1
    GREP='WORKS'
fi

bash wiki-tests.sh Pending mt ar update | grep -C $C "$GREP"
