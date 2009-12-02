echo "---Testing time needed to translate a text depending of text length---"

echo "Source text: GPL license in spanish. Language pair: es-en"

TEXT_FILE=gpl_es_4matxin.txt
NUM_LINES=`wc -l $TEXT_FILE | cut -f 1 -d" "`
TMP_TEXT_FILE=/tmp/test$$
RESULTS_FILE=resultsamdahl-apertium-ws
TMP_JMX=/tmp/testjmx$$
TMP_TIME=/tmp/time$$

echo "Number of lines: $NUM_LINES"

rm -Rf $RESULTS_FILE
rm -Rf jmeter.log
touch $RESULTS_FILE
echo "Testing apertium"
for i in `seq 0 $NUM_LINES`;
do

head -n $i $TEXT_FILE > $TMP_TEXT_FILE

cat scaleMT-amdahl-apertium.jmx.1 $TMP_TEXT_FILE scaleMT-amdahl-apertium.jmx.2 > $TMP_JMX

NUMCHARS=`wc -c $TMP_TEXT_FILE | cut -f 1 -d" "`

echo "Launching JMeter with $NUMCHARS characters"
RESPONSETIME=`jmeter -n -t $TMP_JMX 2>>jmeter.log  | grep '^summary =' | sed -r "s|summary[ ]+=[ ]+[0-9]+[ ]+in[ ]+[0-9\.,]+s[ ]+=[ ]+([0-9\.,]+)/s[ ]+Avg:[ ]+([0-9]+)[ ]+Min:[ ]+([0-9]+)[ ]+Max:[ ]+[0-9]+[ ]+Err:[ ]+0[ ]+\([0-9\.,]+%\)[ ]*|\3|"`



echo "$NUMCHARS $RESPONSETIME" >> $RESULTS_FILE


done
