#!/bin/bash

C=2
GREP='.'
if [ $# -eq 1 ]
then
    C=$1
    GREP='WORKS'
fi

bash wiki-tests.sh Regression tr ky update | grep -C $C "$GREP"

#bash wiki-tests.sh Regression ky tr update | grep -C $C "$GREP"

