#! /bin/bash

echo "---Testing time needed to translate a text depending of text length---"

echo "Source text: GPL license in english. Language pair: en-es"

TEXT_FILE=gplen4.txt
NUM_LINES=`wc -l $TEXT_FILE | cut -f 1 -d" "`
TMP_TEXT_FILE=/tmp/test$$
TMP_JMX=/tmp/input-xmlrpc.xml
JMX=XML-RPC-seq.jmx
RESULTS_FILE=results-timeperlength-pasquale

echo "Number of lines: $NUM_LINES"

rm -Rf $RESULTS_FILE
rm -Rf jmeter.log
touch $RESULTS_FILE
echo "Testing Pasquale"
for i in `seq 1 4  $NUM_LINES`;
do

head -n $i $TEXT_FILE > $TMP_TEXT_FILE
cat input-xmlrpc.xml.1 $TMP_TEXT_FILE input-xmlrpc.xml.2 > $TMP_JMX

NUMCHARS=`wc -c $TMP_TEXT_FILE | cut -f 1 -d" "`
echo "Launching JMeter with $NUMCHARS characters"
RESPONSETIME=`jmeter -n -t $JMX 2>>jmeter.log  | grep '^summary =' | sed -r "s|summary[ ]+=[ ]+[0-9]+[ ]+in[ ]+[0-9,\.]+s[ ]+=[ ]+([0-9,\.]+)/s[ ]+Avg:[ ]+([0-9]+)[ ]+Min:[ ]+[0-9]+[ ]+Max:[ ]+[0-9]+[ ]+Err:[ ]+0[ ]+\([0-9,\.]+%\)[ ]*|\2|"`

echo "$NUMCHARS $RESPONSETIME" >> $RESULTS_FILE


done
