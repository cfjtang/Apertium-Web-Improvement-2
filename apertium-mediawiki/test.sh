#!/bin/bash

#test if demediawiki and remediawiki actually exists, otherwise return error code 1
if [ ! -f demediawiki ];then
  echo "demediawiki binary not found"
  exit 1
fi
if [ ! -f remediawiki ];then
  echo "remediawiki binary not found"
  exit 1
fi

#testing
echo 'The following lines show the process of parsing, the first and final should be syntactically equivalent, note this does not mean equal'
TEXT="[[test]][[test]]ing [[test|testing]] <nowiki>[[test]]ing [[boo!]] hehehe</nowiki>   ![[test|testing|more_testing|hehe]]?! <>''[[test|testing]]ohhh 
<<hehehe>>"
echo
echo $TEXT
echo
echo $TEXT | ./demediawiki
echo
echo $TEXT | ./demediawiki | ./remediawiki
