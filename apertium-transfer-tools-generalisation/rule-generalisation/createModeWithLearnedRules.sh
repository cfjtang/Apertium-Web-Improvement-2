#! /bin/bash

ORIGINAL_MODE=$1
TRANSFER_TOOLS_PATH=$2
LEARNED_RULES=$3
POSTRANSFER_FILE=$4
BIN_BIDICTIONARY=$5
ADDITIONAL_TRANSFER_OPERATIONS=$6
ADDITIONAL_TRANSFER_OPERATIONS_BEFORE=$7


if [ "" == "$ADDITIONAL_TRANSFER_OPERATIONS"  ]; then

ADDITIONAL_TRANSFER_OPERATIONS="cat -"

fi

if [ "" == "$ADDITIONAL_TRANSFER_OPERATIONS_BEFORE"  ]; then

ADDITIONAL_TRANSFER_OPERATIONS_BEFORE="cat -"

fi

SLASHTRANSFERTOOLS="/"
if [ "" == "$TRANSFER_TOOLS_PATH"  ]; then 
	SLASHTRANSFERTOOLS=""
fi

if [ "" == "$BIN_BIDICTIONARY" ]; then
	BIN_BIDICTIONARY=`cat $ORIGINAL_MODE | awk -F"apertium-transfer" '{print $NF}' | awk -F"|" '{print $1}' | awk -F" " '{print $3}' | tr -d '\n'`
fi

TOPRETRANSFER=`cat $ORIGINAL_MODE | awk -F"apertium-pretransfer" '{ print $1 }' | tr -d '\n' | sed 's_ *$__'`
FROMPRETRANSFER=`cat $ORIGINAL_MODE | awk -F"apertium-pretransfer" '{ print $2 }' | tr -d '\n'`

FROMSECONDLTPROC=`echo $FROMPRETRANSFER |  grep -o 'lt-proc.*$'  | tr -d '\n'` #awk -F"lt-proc" '{ print $2 }' | tr -d '\n'
APERTIUM_PATH=`echo $TOPRETRANSFER | awk -F"lt-proc" '{ print $1 }' | tr -d '\n'`

echo "${TOPRETRANSFER}apertium-pretransfer | $ADDITIONAL_TRANSFER_OPERATIONS_BEFORE | ${APERTIUM_PATH}apertium-transfer ${LEARNED_RULES}.xml ${LEARNED_RULES}.bin $BIN_BIDICTIONARY | sed 's_\^\([^#$]*\)#\([^<$]*\)\(<[^\$]*\)\\\$_^\\1\\3#\\2\$_g' | $ADDITIONAL_TRANSFER_OPERATIONS | ${TRANSFER_TOOLS_PATH}${SLASHTRANSFERTOOLS}apertium-posttransfer -x  ${POSTRANSFER_FILE} | ${APERTIUM_PATH}${FROMSECONDLTPROC}"

