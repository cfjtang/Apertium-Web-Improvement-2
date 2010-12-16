#!/bin/bash

C=2
GREP='.'
if [ $# -eq 1 ]
then
    C=$1
    GREP='[*#/]'
fi

bash wiki-tests.sh Regression it ca update | grep -C $C "$GREP"

bash wiki-tests.sh Regression ca it update | grep -C $C "$GREP"
