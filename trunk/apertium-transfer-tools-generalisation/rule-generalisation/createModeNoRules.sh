#! /bin/bash

ORIGINAL_MODE=$1
TRANSFER_TOOLS_PATH=$2
EMPTY_RULES_XML=$3
EMPTY_RULES_BIN=$4
POSTRANSFER_FILE=$5
BIN_BIDICTIONARY=$6

if [ "" == "$BIN_BIDICTIONARY" ]; then
	BIN_BIDICTIONARY=`cat $ORIGINAL_MODE | awk -F"apertium-transfer" '{print $NF}' | awk -F"|" '{print $1}' | awk -F" " '{print $3}' | tr -d '\n'`
fi

SLASHTRANSFERTOOLS="/"
if [ "" == "$TRANSFER_TOOLS_PATH"  ]; then 
	SLASHTRANSFERTOOLS=""
fi

TOPRETRANSFER=`cat $ORIGINAL_MODE | awk -F"apertium-pretransfer" '{ print $1 }' | tr -d '\n' | sed 's_ *$__'`
FROMPRETRANSFER=`cat $ORIGINAL_MODE | awk -F"apertium-pretransfer" '{ print $2 }' | tr -d '\n'`

FROMSECONDLTPROC=`echo $FROMPRETRANSFER | grep -o 'lt-proc.*$' | tr -d '\n'`
APERTIUM_PATH=`echo $TOPRETRANSFER | awk -F"lt-proc" '{ print $1 }' | tr -d '\n'`

echo "${TOPRETRANSFER}apertium-pretransfer | ${APERTIUM_PATH}apertium-transfer $EMPTY_RULES_XML $EMPTY_RULES_BIN $BIN_BIDICTIONARY |  sed 's_\^\([^#$]*\)#\([^<$]*\)\(<[^\$]*\)\\\$_^\\1\\3#\\2\$_g' | ${TRANSFER_TOOLS_PATH}${SLASHTRANSFERTOOLS}apertium-posttransfer -x  ${POSTRANSFER_FILE} | ${APERTIUM_PATH}${FROMSECONDLTPROC}"

