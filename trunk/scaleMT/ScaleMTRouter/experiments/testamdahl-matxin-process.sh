echo "---Testing time needed to translate a text depending of text length---"

echo "Source text: GPL license in spanish. Language pair: en-eu"

TEXT_FILE=gpl_es_4matxin.txt
NUM_LINES=`wc -l $TEXT_FILE | cut -f 1 -d" "`
TMP_TEXT_FILE=/tmp/test$$
RESULTS_FILE=resultsamdahl-matxin-process
TMP_TIME=/tmp/time$$

echo "Number of lines: $NUM_LINES"

rm -Rf $RESULTS_FILE
rm -Rf jmeter.log
touch $RESULTS_FILE
echo "Testing matxin"
for i in `seq 0 $NUM_LINES`;
do

head -n $i $TEXT_FILE > $TMP_TEXT_FILE

NUMCHARS=`wc -c $TMP_TEXT_FILE | cut -f 1 -d" "`
echo "Launching matxin process with $NUMCHARS characters"
(time -p cat $TMP_TEXT_FILE | Matxin_translator -c /usr/local/share/matxin/config/es-eu.cfg) 2>$TMP_TIME >/dev/null
RESPONSETIME=`cat $TMP_TIME | grep user | cut -f 2 -d " " `



echo "$NUMCHARS $RESPONSETIME" >> $RESULTS_FILE


done

