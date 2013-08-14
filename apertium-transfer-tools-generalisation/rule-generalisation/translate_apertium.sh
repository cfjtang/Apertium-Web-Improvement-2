#! /bin/bash

APERTIUM_PATH=$1
MODE=$2

JOIN_LINES_OPTION=$3

FORMAT=$4
DATA_DIR=$5

UNKNOWN_FLAG="-u"

if [ "$6" == "--show_unknown" ]; then
  UNKNOWN_FLAG=""
fi


if [ "$FORMAT" == "" ]; then
	FORMAT_STR=""
else
	FORMAT_STR="-f $FORMAT"
fi

if [ "$DATA_DIR" == "" ]; then
	DATA_STR=""
else
	DATA_STR="-d $DATA_DIR"
fi

SLASH_APERTIUM="/"
if [ "" == "$APERTIUM_PATH" ]; then
	SLASH_APERTIUM=""
fi


if [ "join" == "$JOIN_LINES_OPTION" ]; then

	sed -r 's_$_ ._' | $APERTIUM_PATH${SLASH_APERTIUM}apertium $UNKNOWN_FLAG $DATA_STR $FORMAT_STR $MODE | sed -e '${/^$/d}' | sed -r 's_[ ]?.$__'
	#$APERTIUM_PATH${SLASH_APERTIUM}apertium -u $DATA_STR $FORMAT_STR $MODE | sed -e '${/^$/d}' 

else

while read line
do

output=`echo "$line" | $APERTIUM_PATH${SLASH_APERTIUM}apertium $UNKNOWN_FLAG $DATA_STR $FORMAT_STR $MODE`
echo "$output"

done

fi
