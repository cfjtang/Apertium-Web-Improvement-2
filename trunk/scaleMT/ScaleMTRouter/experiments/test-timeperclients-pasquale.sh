#! /bin/bash

echo "---Testing time needed to translate a text depending of number of concurrent clients---"

echo "Source text: preamble of GPL license in english. Language pair: en-es"

TMP_TEXT_FILE=/tmp/test$$
TMP_JMX=/tmp/testjmx$$
RESULTS_FILE=results-timeperclients-pasquale
MAX_CLIENTS=100

rm -Rf $RESULTS_FILE
rm -Rf jmeter.log
touch $RESULTS_FILE
echo "Testing pasquale"
cp input-xmlrpc-conc.xml /tmp
for i in `seq 1 $MAX_CLIENTS`;
do

echo "<stringProp name=\"ThreadGroup.num_threads\">$i</stringProp>" > $TMP_TEXT_FILE
cat XML-RPC-conc.jmx.1 $TMP_TEXT_FILE XML-RPC-conc.jmx.2 > $TMP_JMX

echo "Launching JMeter with $i clients"
RESPONSETIME=`jmeter -n -t $TMP_JMX 2>>jmeter.log  | grep '^summary =' | sed -r "s|summary[ ]+=[ ]+[0-9]+[ ]+in[ ]+[0-9,\.]+s[ ]+=[ ]+([0-9,\.]+)/s[ ]+Avg:[ ]+([0-9]+)[ ]+Min:[ ]+[0-9]+[ ]+Max:[ ]+[0-9]+[ ]+Err:[ ]+0[ ]+\([0-9,\.]+%\)[ ]*|\2|"`

echo "$i $RESPONSETIME" >> $RESULTS_FILE


done
