#! /bin/bash

DIR=""
SL="es"
TL="ca"
GENFILE=""
QUERY_DIR=""
INVERSE_PAIR=""
EVALUATION_CORPUS="/work/vmsanchez/rules/corpora/consumer-eroski.test"
USE_GZIP="no"

TAGSEQUENCESANDGROUPSSUFFIX=""

PYTHONHOME=""

APERTIUM_PREFIX="$HOME/local"
APERTIUM_SOURCES="$HOME/sources"

RICHATSFLAG=""

CURDIR=`dirname $0`
FULLCURDIR=`readlink -f $CURDIR`

usage()
{
cat << EOF
Bla bla bla	
	
EOF
}

USE_SHORT_RESTRICTIONS_INFIX=".shortrestrictions"

while getopts “s:t:d:f:q:e:izx:h:p:u:cl” OPTION
do
     case $OPTION in
         s)
             SL=$OPTARG
             ;;
         t)
             TL=$OPTARG
             ;;
         d)
             DIR=$OPTARG
             ;;
         f)
             GENFILE=$OPTARG
             ;;
         q)
             QUERY_DIR=$OPTARG
             ;;
         e)
             EVALUATION_CORPUS=$OPTARG
             ;;
         i)
             INVERSE_PAIR="yes"
             ;;
         z)
                USE_GZIP="yes"
                ;;
        x)
                TAGSEQUENCESANDGROUPSSUFFIX=$OPTARG
                ;;
        l)
		USE_SHORT_RESTRICTIONS_INFIX=""
		;;
        h)
                PYTHONHOME=$OPTARG
                ;;
        p)
                APERTIUM_PREFIX=$OPTARG
                ;;
        u)
               APERTIUM_SOURCES=$OPTARG
               ;;
        c)
		RICHATSFLAG="--emptyrestrictionsmatcheverything"
		;;
         ?)
             usage
             exit
             ;;
     esac
done

TMPFILE1=`mktemp`
TMPFILE2=`mktemp`


PAIR="$SL-$TL"
if [ "$INVERSE_PAIR" ]; then
	PAIR="$TL-$SL"
fi

if [ "$QUERY_DIR" == "" ]; then
	QUERY_DIR=`basename $GENFILE`
fi


if [ "$USE_GZIP" == "yes" ]; then
	zcat $GENFILE > /tmp/evalexpv2temp$$
	GENFILE=/tmp/evalexpv2temp$$
fi

mkdir -p $DIR/queries/$QUERY_DIR/experiment

NUMATS=`cat $GENFILE | wc -l `
CURAT=$NUMATS

while read line
do 
	echo "$CURAT | $line"
	CURAT=`expr $CURAT - 1`
done < $GENFILE  > $DIR/queries/$QUERY_DIR/experiment/alignmentTemplatesGeneralisedResult.txt


EXPQUERIESDIR=$DIR/queries/$QUERY_DIR/experiment/
TAGGROUPS=$CURDIR/taggroups$TAGSEQUENCESANDGROUPSSUFFIX 

if [ "$APERTIUM_PREFIX" != ""]; then
  TRANSFERTOOLSPATH="LD_LIBRARY_PATH=$APERTIUM_PREFIX/lib $APERTIUM_PREFIX/bin/"
  APERTIUMPATH="$APERTIUM_PREFIX/bin/"
else
  TRANSFERTOOLSPATH=""
  APERTIUMPATH=""
fi

ORIGINALAPERTIUMMODE=$APERTIUM_SOURCES/modes/${SL}-$TL.mode
POSTTRANSFERRULES=$FULLCURDIR/../phrase-extraction/transfer-tools-scripts/apertium-${SL}-$TL.posttransfer.ptx

BINBIDICTIONARY=$APERTIUM_SOURCES/apertium-$PAIR/${SL}-$TL.autobil${USE_SHORT_RESTRICTIONS_INFIX}.bin

cp $EXPQUERIESDIR/alignmentTemplatesGeneralisedResult.txt $EXPQUERIESDIR/alignmentTemplatesGeneralised.txt
mkdir -p $EXPQUERIESDIR/evaluation
mkdir -p $EXPQUERIESDIR/evaluation/paired_bootstrap
mkdir -p $EXPQUERIESDIR/rules
mkdir -p $EXPQUERIESDIR/modes

cp $EVALUATION_CORPUS.$SL $EXPQUERIESDIR/evaluation/source
cp $EVALUATION_CORPUS.$TL $EXPQUERIESDIR/evaluation/reference

#sort ATs to be processed by the module which encodes them as Apertium rules
cat $EXPQUERIESDIR/alignmentTemplatesGeneralised.txt | ${PYTHONHOME}python $CURDIR/addGeneralisedLeftSide.py | LC_ALL=C sort -r | ${PYTHONHOME}python $CURDIR/uniqSum.py | awk -F"|" '{print $2"|"$1"|"$3"|"$4"|"$5"|"$6}' | sed 's_^ __' | sed 's_|\([0-9]\)_| \1_' |  LC_ALL=C sort -r | ${PYTHONHOME}python $CURDIR/removeExplicitEmptuTagsFromPatternTLandRest.py $RICHATSFLAG | awk -F"|" '{print $2"|"$3"|"$4"|"$5"|"$6}' | sed 's_^ __' > $EXPQUERIESDIR/rules/alignmentTemplates.txt
cat $EXPQUERIESDIR/rules/alignmentTemplates.txt | cut -f 1 -d '|' | uniq > $EXPQUERIESDIR/rules/alignmentTemplates.txt.patterns

#create Apertium rules
echo "${TRANSFERTOOLSPATH}apertium-gen-transfer-from-aligment-templates --input $EXPQUERIESDIR/rules/alignmentTemplates.txt --attributes $TAGGROUPS --generalise --nodoublecheckrestrictions --usediscardrule $RICHATSFLAG | ${PYTHONHOME}python $CURDIR/addDebugInfoToTransferRules.py > $EXPQUERIESDIR/rules/rules.xml" > $TMPFILE1
bash $TMPFILE1

#compile rules
${APERTIUMPATH}apertium-preprocess-transfer $EXPQUERIESDIR/rules/rules.xml $EXPQUERIESDIR/rules/rules.bin

#evaluate word for word
${APERTIUMPATH}apertium-preprocess-transfer $CURDIR/empty-rules-for-translating.t1x $EXPQUERIESDIR/rules/empty-rules-for-translating.t1x.bin
bash $CURDIR/createModeNoRules.sh "$ORIGINALAPERTIUMMODE" "$TRANSFERTOOLSPATH" $FULLCURDIR/empty-rules-for-translating.t1x $EXPQUERIESDIR/rules/empty-rules-for-translating.t1x.bin $POSTTRANSFERRULES $BINBIDICTIONARY > $EXPQUERIESDIR/modes/${SL}-${TL}_norules.mode
cat $EXPQUERIESDIR/evaluation/source | bash $CURDIR/translate_apertium.sh "$APERTIUMPATH" ${SL}-${TL}_norules join "" $EXPQUERIESDIR > $EXPQUERIESDIR/evaluation/translation_norules
bash $CURDIR/mteval-v11b-nosgm.sh $EXPQUERIESDIR/evaluation/source $EXPQUERIESDIR/evaluation/reference $EXPQUERIESDIR/evaluation/translation_norules > $EXPQUERIESDIR/evaluation/evaluation_norules
bash $CURDIR/calculateStatsForPairedBootstrapResampling.sh $EXPQUERIESDIR/evaluation/source $EXPQUERIESDIR/evaluation/translation_norules $EXPQUERIESDIR/evaluation/reference $EXPQUERIESDIR/evaluation/paired_bootstrap/stats_norules

#evaluate provided rules
bash $CURDIR/createModeWithLearnedRules.sh $ORIGINALAPERTIUMMODE "$TRANSFERTOOLSPATH" $EXPQUERIESDIR/rules/rules $POSTTRANSFERRULES $BINBIDICTIONARY "${PYTHONHOME}python $FULLCURDIR/removeDebugInfoFromTransfer.py"  > $EXPQUERIESDIR/modes/${SL}-${TL}_learned.mode 

cat $EXPQUERIESDIR/evaluation/source | bash $CURDIR/translate_apertium.sh "$APERTIUMPATH" ${SL}-${TL}_learned join "" $EXPQUERIESDIR > $EXPQUERIESDIR/evaluation/translation_learnedrules 2> $EXPQUERIESDIR/evaluation/used_rules

#translate and not remove unknown mark
cat $EXPQUERIESDIR/evaluation/source | bash $CURDIR/translate_apertium.sh "$APERTIUMPATH" ${SL}-${TL}_learned join "" $EXPQUERIESDIR --show_unknown > $EXPQUERIESDIR/evaluation/translation_learnedrules_withunknown 2> /dev/null

bash $CURDIR/mteval-v11b-nosgm.sh $EXPQUERIESDIR/evaluation/source $EXPQUERIESDIR/evaluation/reference $EXPQUERIESDIR/evaluation/translation_learnedrules | grep "^NIST" | cut -f 9 -d ' ' > $EXPQUERIESDIR/evaluation/evaluation_learnedrules

#compute segment level bleu
bash $CURDIR/mteval-v13-nosgm-segments.sh $EXPQUERIESDIR/evaluation/source $EXPQUERIESDIR/evaluation/reference $EXPQUERIESDIR/evaluation/translation_learnedrules |  grep -F " on segment " | cut -f 8 -d ' ' > $EXPQUERIESDIR/evaluation/evaluation_learnedrules_forsentences

paste $EXPQUERIESDIR/evaluation/evaluation_learnedrules_forsentences $EXPQUERIESDIR/evaluation/translation_learnedrules >$EXPQUERIESDIR/evaluation/translation_learnedrules_withbleu
 
cat $EXPQUERIESDIR/evaluation/used_rules | grep -v "LOCALE:" | grep -v '^0$' | grep -v '^ww' | LC_ALL=C sort | uniq -c | sort -r -n -k 1,1 | sed 's_^ *__'   | while read line ; do FREQ=`echo "$line" | cut -f 1 -d ' ' `; ATNUM=`echo "$line" | cut -f 2 -d ' '`; AT=`head -n $ATNUM $EXPQUERIESDIR/rules/alignmentTemplates.txt | tail -n 1 `;MODAT=`echo "$AT" | sed 's_^[^|]*|__'`; ISNEWAT=0; echo "$FREQ $ISNEWAT $AT"  ; done > $EXPQUERIESDIR/evaluation/report_rules
 
cat $EXPQUERIESDIR/evaluation/used_rules | grep -v "LOCALE:" | grep '^ww' | sed 's_^ww__' | LC_ALL=C sort | uniq -c | sort -r -n -k 1,1 | sed 's_^ *__'   | while read line ; do FREQ=`echo "$line" | cut -f 1 -d ' ' `; ATNUM=`echo "$line" | cut -f 2 -d ' '`; AT=`head -n $ATNUM $EXPQUERIESDIR/rules/alignmentTemplates.txt.patterns | tail -n 1 `;MODAT=`echo "$AT" | sed 's_^[^|]*|__'`; ISNEWAT=0; echo "$FREQ $ISNEWAT $AT"  ; done >> $EXPQUERIESDIR/evaluation/report_rules
 
cat $EXPQUERIESDIR/evaluation/used_rules | grep -v "LOCALE:" | grep '^0$' | wc -l  >> $EXPQUERIESDIR/evaluation/report_rules

cat $EXPQUERIESDIR/evaluation/report_rules | ${PYTHONHOME}python $CURDIR/addWordInforToReport.py  > $EXPQUERIESDIR/evaluation/report_rules_words
 
cat $EXPQUERIESDIR/evaluation/report_rules_words | ${PYTHONHOME}python $CURDIR/summarizeReport.py  > $EXPQUERIESDIR/evaluation/report_rules_words_summarized

bash $CURDIR/calculateStatsForPairedBootstrapResampling.sh $EXPQUERIESDIR/evaluation/source $EXPQUERIESDIR/evaluation/translation_learnedrules $EXPQUERIESDIR/evaluation/reference $EXPQUERIESDIR/evaluation/paired_bootstrap/stats_learnedrules

java -jar $CURDIR/tercom.7.25.jar  -r $EXPQUERIESDIR/evaluation/reference.xml -h $EXPQUERIESDIR/evaluation/translation_learnedrules.xml | grep -F "Total TER:" | cut -f 3 -d ' ' > $EXPQUERIESDIR/evaluation/ter_learnedrules 


#table with most frequent rules and examples of application. Similar steps

echo "${TRANSFERTOOLSPATH}apertium-gen-transfer-from-aligment-templates --input $EXPQUERIESDIR/rules/alignmentTemplates.txt --attributes $TAGGROUPS --generalise --nodoublecheckrestrictions --usediscardrule $RICHATSFLAG | ${PYTHONHOME}python $CURDIR/addDebugInfoToTransferRules-debug.py > $EXPQUERIESDIR/rules/rules-extradebug.xml" > $TMPFILE2
bash $TMPFILE2

${APERTIUMPATH}apertium-preprocess-transfer $EXPQUERIESDIR/rules/rules-extradebug.xml $EXPQUERIESDIR/rules/rules-extradebug.bin
bash $CURDIR/createModeWithLearnedRules.sh $ORIGINALAPERTIUMMODE "$TRANSFERTOOLSPATH" $EXPQUERIESDIR/rules/rules-extradebug $POSTTRANSFERRULES $BINBIDICTIONARY "${PYTHONHOME}python $FULLCURDIR/removeDebugInfoFromTransfer.py moredebug"  > $EXPQUERIESDIR/modes/${SL}-${TL}_extradebug.mode 
cat $EXPQUERIESDIR/evaluation/source | bash $CURDIR/translate_apertium.sh $APERTIUMPATH ${SL}-${TL}_extradebug join "" $EXPQUERIESDIR > $EXPQUERIESDIR/evaluation/output-extradebug 2> $EXPQUERIESDIR/evaluation/debug-extradebug

cat $EXPQUERIESDIR/evaluation/debug-extradebug | grep -v "^0" | grep -v "^LOCALE:" | grep -v "^SINGLESOURCE:" | grep -v "^SINGLETARGET:" > $EXPQUERIESDIR/evaluation/debug-extradebug-clean

AUTOGEN=`cat $EXPQUERIESDIR/modes/${SL}-${TL}_norules.mode | tr '|' '\n' | grep -F "autogen.bin" | awk -F '$' '{ print $2;}'|  sed 's_1 __'`; 
#check sed
cat $EXPQUERIESDIR/evaluation/debug-extradebug-clean |  grep -F "TARGET:" | sed 's_^TARGET: __' | sed 's_^$_EMPTY_' | ${APERTIUMPATH}lt-proc -g $AUTOGEN | sed 's_\(.*\)_\n\1\n_' > $EXPQUERIESDIR/evaluation/debug-extradebug-clean-tlsurface

paste $EXPQUERIESDIR/evaluation/debug-extradebug-clean $EXPQUERIESDIR/evaluation/debug-extradebug-clean-tlsurface | python $CURDIR/collapseSourcetarget.py | LC_ALL=C sort | uniq -c > $EXPQUERIESDIR/evaluation/debug-extradebug-clean-withtlsurface

cat $EXPQUERIESDIR/evaluation/report_rules_words | bash $CURDIR/createHTMLTableFromFrequentRules.sh $EXPQUERIESDIR/evaluation/debug-extradebug-clean-withtlsurface $EXPQUERIESDIR/rules/alignmentTemplates.txt > $EXPQUERIESDIR/evaluation/table.html

#check seds
cat $EXPQUERIESDIR/evaluation/debug-extradebug | grep -v "^LOCALE"  | tr  '\n' '|' | tr -d ']' | tr -d ']' | sed 's_TARGET:\([^|]*\)|SOURCE:\([^|]*\)_[\2|\1]_g' | sed 's_SINGLETARGET:\([^|]*\)|SINGLESOURCE:\([^|]*\)_[\2|\1]_g'  | sed 's_\^.<sent>\$\]_^.<sent>$]\n_g' | sed -r 's_(^|\|)([0-9]+)\|\[_[\2|_g' > $EXPQUERIESDIR/evaluation/debug-segmented


rm -f $TMPFILE1 $TMPFILE2
