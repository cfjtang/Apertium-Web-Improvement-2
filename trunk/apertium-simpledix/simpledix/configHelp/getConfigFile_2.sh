#! /bin/sh

./getConfigFile_2_aux.sh $1 $2 | xmllint -format - | sed s/\"%\"/\"\"/g  > autoconf.xml
