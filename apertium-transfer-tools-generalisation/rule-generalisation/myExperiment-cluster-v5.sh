#!/bin/bash

#

# myexperiment-es-ca.sh

#

#$ -N myexperiment-es-ca

#$ -cwd

#$ -o $HOME/logs/myexperiment.$JOB_ID.out

#$ -e $HOME/logs/myexperiment.$JOB_ID.err


#Paremeter description
#1: Corpus size
#2: Step: 0=pre-generalisation (split bilingual phrases in boxes) 1=AT generation 2=filtering and linprog 3=tuning ""= all
#3: Part: 
#4: Filtering method: if it is empty, all the defined filtering methods are applied in step 2
#5: SL (empty for es)
#6: TL (empty for ca)
#7: the name of the language pair in Apertium is TL-SL? "inverse" for yes, empty for no
#8: dev corpus. Empty for consumer-eroski.dev
#9: test corpus. Empty for consumer-eroski.test

#The part parameter permits splitting the problem in smaller subproblems
# step 0: part is ignored
# step 1: PART is the part of the problem and the total amount of parts: PART = CURPART-TOTALPARTS. If it is empty, the whole problem is contained in a single file
# step 2: PART is the part of the problem and the total amount of parts: PART = CURPART-TOTALPARTS. If it is empty, the whole problem is contained in a single file
# step 3:  
#  X-box= tunes the defined filtering method and deletes boxes below X bilphrase
#  X-subrules= tunes the defined filtering method and removes subrules
#  X-optwer= tunes the defined filtering method and removes redundant rules. Only with the threshold in position X of the list
#  empty: tunes box0,subchorizo and optwer with every possible threshold
#	X = 1-21 if X is empty, the whole range of thresholds is used
#
# step 8: PART is the part of the problem and the total amount of parts: PART = CURPART-TOTALPARTS. If it is empty, the whole problem is contained in a single file


#shflags
. ./shflags
DEFINE_string 'size' '' 'Size of bilingual corpus' 'v'
DEFINE_string 'step' '' 'pre-gen, generalisation, filtering or tuning' 'e'
DEFINE_string 'part' '' 'for parallel processing' 'p'
DEFINE_string 'filtering_method' '' 'Filtering method' 'f'
DEFINE_string 'source_language' 'es' 'Source language' 's'
DEFINE_string 'target_language' 'ca' 'Target language' 't'
DEFINE_string 'inverse_pair' '' 'Is TL-SL the name of the pair in Apertium?' 'i'
DEFINE_string 'dev_corpus' 'consumer-eroski.dev' 'Development corpus' 'd'
DEFINE_string 'test_corpus' 'consumer-eroski.test' 'Test corpus' 'r'
DEFINE_string 'bilingual_phrases_id' '' 'Identifier of the starting bilingual phrases' 'b'
DEFINE_string 'touch_when_finished' '' 'File to be created when job finised' 'u'
DEFINE_string 'alg_version' 'NORMAL' '' 'o'
DEFINE_string 'only_one_threshold' '' 'Threshold for beam search' 'n'

BEAMSEARCH_ALLOWINCOMP="allowincomp"

FLAGS "$@" || exit $?
eval set -- "${FLAGS_ARGV}"

VERSION=${FLAGS_alg_version}
NEWGENERATIONALGORITHMFLAG="-c"
TRANSFERMODEFLAG="-y"

if [  "$VERSION" == "NEWGENERATION" ]; then
	NEWGENERATIONALGORITHMFLAG="-c -n"
elif [  "$VERSION" == "REFTOBILING" ]; then
	NEWGENERATIONALGORITHMFLAG=""
	TRANSFERMODEFLAG=""
elif [  "$VERSION" == "REFTOBILINGMULTIPLERES" ]; then
	NEWGENERATIONALGORITHMFLAG="-r"
	TRANSFERMODEFLAG=""
elif [  "$VERSION" == "REFTOBILINGMULTIPLERESDET" ]; then
	NEWGENERATIONALGORITHMFLAG="-g"
	TRANSFERMODEFLAG=""
elif [  "$VERSION" == "REFTOBILINGEXPLICITMULTIPLERESDET" ]; then
	NEWGENERATIONALGORITHMFLAG="-g -v"
	TRANSFERMODEFLAG=""
elif [  "$VERSION" == "REFTOBILINGEXPLICITMULTIPLERESDETCHANGE" ]; then
	NEWGENERATIONALGORITHMFLAG="-s -v"
	TRANSFERMODEFLAG=""
elif [  "$VERSION" == "REFTOBILINGMULTIPLERESDETMF" ]; then
	NEWGENERATIONALGORITHMFLAG="-l"
	TRANSFERMODEFLAG=""
elif [  "$VERSION" == "REFTOBILINGEXPLICITMULTIPLERESDETMF" ]; then
	NEWGENERATIONALGORITHMFLAG="-l -v"
	TRANSFERMODEFLAG=""
elif [  "$VERSION" == "REFTOBILINGEXPLICITMULTIPLERESTRIGGER" ]; then
	NEWGENERATIONALGORITHMFLAG="-b -v"
	TRANSFERMODEFLAG=""
elif [  "$VERSION" == "REFTOBILINGEXPLICITMULTIPLERESTRIGGERFAST" ]; then
	NEWGENERATIONALGORITHMFLAG="-j -v"
	TRANSFERMODEFLAG=""
elif [  "$VERSION" == "REFTOBILINGEXPLICITMULTIPLERESTRIGGERFASTNOGOODR" ]; then
	NEWGENERATIONALGORITHMFLAG="-k -v"
	TRANSFERMODEFLAG=""
elif [  "$VERSION" == "REFTOBILINGFIRSTLEMMASEXPLICITMULTIPLERESDETMF" ]; then
	NEWGENERATIONALGORITHMFLAG="-l -v -u"
	TRANSFERMODEFLAG=""
elif [  "$VERSION" == "REFTOBILINGPOWERSETFIRSTLEMMASEXPLICITMULTIPLERESDETMF" ]; then
	NEWGENERATIONALGORITHMFLAG="-l -v -u -a"
	TRANSFERMODEFLAG=""
elif [  "$VERSION" == "REFTOBILINGEXPLICIT" ]; then
	NEWGENERATIONALGORITHMFLAG="-v"
	TRANSFERMODEFLAG=""
fi






if [ ${FLAGS_size} == "" ] ; then
    echo 'error: size missing' >&2
    flags_help
fi
SIZE=${FLAGS_size}
STEP=${FLAGS_step}
PART=${FLAGS_part}
FILTERING_METHOD=${FLAGS_filtering_method}
SL=${FLAGS_source_language}
TL=${FLAGS_target_language}

INVERSE_PAIR_TUNING_FLAG=""
PAIR="${SL}-${TL}"
if [ "${FLAGS_inverse_pair}" == "INVERSE" ] ; then
	INVERSE_PAIR_TUNING_FLAG="-i"
	APERTIUM_PAIR="${TL}-${SL}"
else
	APERTIUM_PAIR="$PAIR"
fi
DEV_CORPUS=${FLAGS_dev_corpus}
TEST_CORPUS=${FLAGS_test_corpus}
BILPHRASES_ID=${FLAGS_bilingual_phrases_id}

echo "The dev corpus is $DEV_CORPUS and the test corpus is $TEST_CORPUS"

PART_SUFFIX=""
PART_GENERALISATION_ARGUMENT=""
if [ "$PART" != "" ]; then
	PART_SUFFIX="-$PART"
	PART_GENERALISATION_ARGUMENT="-m $PART"
fi

JOB_DIR=/scratch/vmsanchez/$JOB_ID

TARGET_SOURCES=$JOB_DIR/sources
TARGET_LOCAL=$JOB_DIR/local
OLD_EXPERIMENT_DIR=/scratch/vmsanchez/experiments-linear-l5-es-ca

EXPERIMENT_DIR=$JOB_DIR/experiments-linear-l5-${SL}-${TL}-v2


rm -Rf $OLD_EXPERIMENT_DIR

#copy sources from HOME

echo "Copying sources to node $SIZE"

mkdir -p $TARGET_SOURCES
mkdir -p $TARGET_LOCAL

cp  $HOME/sources/apertium-transfer-tools-generalisation.tar.gz $TARGET_SOURCES
cd $TARGET_SOURCES
tar xzf apertium-transfer-tools-generalisation.tar.gz

cp  $HOME/local.tar.gz $TARGET_LOCAL/..
cd $TARGET_LOCAL/..
tar xzf local.tar.gz
mv $TARGET_LOCAL/share/apertium/modes/${SL}-${TL}.mode $TARGET_LOCAL/share/apertium/modes/${SL}-${TL}.mode.orig 
cat $TARGET_LOCAL/share/apertium/modes/${SL}-${TL}.mode.orig | sed "s:/home/vmsanchez/local/:$TARGET_LOCAL/:g" > $TARGET_LOCAL/share/apertium/modes/${SL}-${TL}.mode

cp  $HOME/.pythonbrew/pythons/Python-2.6.7.tar.gz $TARGET_SOURCES
cd $TARGET_SOURCES
tar xzf Python-2.6.7.tar.gz

#mkdir -p $TARGET_SOURCES/apertium-transfer-tools-v2/transfer-tools-scripts
#cp $HOME/sources/apertium-transfer-tools-v2/transfer-tools-scripts/apertium-${PAIR}.posttransfer.ptx  $TARGET_SOURCES/apertium-transfer-tools-v2/transfer-tools-scripts

#create mode for beam search
bash $TARGET_SOURCES/apertium-transfer-tools-generalisation/rule-generalisation/createModeForBeamSearchEvaluation.sh $TARGET_LOCAL/share/apertium/modes/${SL}-${TL}.mode "" $TARGET_SOURCES/apertium-transfer-tools-generalisation/phrase-extraction/transfer-tools-scripts/apertium-${PAIR}.posttransfer.ptx > $TARGET_LOCAL/share/apertium/modes/${TL}_lex_from_beam-${TL}.mode

ln -s $TARGET_LOCAL/share/apertium/modes $TARGET_SOURCES/modes 
mkdir -p $TARGET_SOURCES/apertium-$APERTIUM_PAIR
cp $HOME/sources/apertium-${APERTIUM_PAIR}/${PAIR}.autobil.shortrestrictions.bin $TARGET_SOURCES/apertium-$APERTIUM_PAIR

cp  $HOME/data/input/evaluation-corpora.tar.gz $TARGET_SOURCES
cd $TARGET_SOURCES
tar xzf evaluation-corpora.tar.gz

cp $HOME/data/input/$PAIR/$BILPHRASES_ID/shuf${SIZE}l5.alignmentTemplatesPlusLemmas.withalllemmas.onlyslpos.filtered-1-count.$PAIR.gz $TARGET_SOURCES

cp $HOME/data/input/$PAIR/$BILPHRASES_ID/shuf${SIZE}l5.sentences.$PAIR.gz $TARGET_SOURCES

SENTENCES_FILE=$TARGET_SOURCES/shuf${SIZE}l5.sentences.$PAIR.gz



#cp $HOME/data/input/$PAIR/shuf${SIZE}corpus-$PAIR.$SL.gz $HOME/data/input/$PAIR/$BILPHRASES_ID/shuf${SIZE}corpus-$PAIR.$TL.gz $TARGET_SOURCES

PATH=$TARGET_LOCAL/bin:$PATH
export LD_LIBRARY_PATH=$TARGET_LOCAL/lib


mkdir -p $EXPERIMENT_DIR

#perform experiment

APERTIUM_PREFIX=$TARGET_LOCAL
APERTIUM_SOURCES=$TARGET_SOURCES
CORPORA_DIR=$TARGET_SOURCES/evaluation-corpora

#test

if [ "$STEP" == "test" ]; then
	mkdir -p $EXPERIMENT_DIR/beamevaluationtest
	echo '^el<det><def><m><sg>$ ^ordenador<n><m><sg>$ | ^el<det><def><m><sg>$ ^ordinador<n><m><sg>$ | ^el<det><def><m><sg>$ ^ordinador<n><m><sg>$' > $EXPERIMENT_DIR/beamevaluationtest/hypothesis
	bash $TARGET_SOURCES/apertium-transfer-tools-generalisation/rule-generalisation/evaluateBeamSearchHypothesis.sh --input_file $EXPERIMENT_DIR/beamevaluationtest/hypothesis --target_language ca --use_tmp_dir $EXPERIMENT_DIR/beamevaluationtest --apertium_data_dir $TARGET_LOCAL/share/apertium > $EXPERIMENT_DIR/beamevaluationtest/testresult
	cp -r $EXPERIMENT_DIR/beamevaluationtest $HOME
fi




#generalisation

if [ "$STEP" == "0" -o  "$STEP" == "" ]; then

echo "Pre-Generalising $SIZE"
cd $TARGET_SOURCES/apertium-transfer-tools-generalisation/rule-generalisation
bash $TARGET_SOURCES/apertium-transfer-tools-generalisation/rule-generalisation/run-experiment-linear-incremental-parallel-v8.sh $NEWGENERATIONALGORITHMFLAG -q -d $EXPERIMENT_DIR  -f $TARGET_SOURCES/shuf${SIZE}l5.alignmentTemplatesPlusLemmas.withalllemmas.onlyslpos.filtered-1-count.${PAIR}.gz -i shuf$SIZE -h $TARGET_SOURCES/Python-2.6.7/bin/ -t 1 -x "_$PAIR"

#copy results to HOME
mkdir -p $HOME/results/experiments-linear-l5-$PAIR/$BILPHRASES_ID/shuf$SIZE
cd $EXPERIMENT_DIR/shuf$SIZE
tar czf generalisation-pre.tar.gz generalisation
cp generalisation-pre.tar.gz $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE

if [ "$STEP" == "0" ]; then
	rm -Rf generalisation
	rm generalisation-pre.tar.gz
fi

fi

if [ "$STEP" == "1" -o  "$STEP" == "" -o "$STEP" == "1+" -o "$STEP" == "1-2" ]; then

if [ "$STEP" == "1" -o  "$STEP" == "1+" -o "$STEP" == "1-2" ]; then
	mkdir -p $EXPERIMENT_DIR/shuf$SIZE
	cd $EXPERIMENT_DIR/shuf$SIZE
	cp $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/generalisation-pre.tar.gz ./
	tar xzf generalisation-pre.tar.gz
fi

echo "Generalising $SIZE $PART"

bash $TARGET_SOURCES/apertium-transfer-tools-generalisation/rule-generalisation/run-experiment-linear-incremental-parallel-v8.sh $NEWGENERATIONALGORITHMFLAG -q -d $EXPERIMENT_DIR  -i shuf$SIZE -h $TARGET_SOURCES/Python-2.6.7/bin/ -y -t 2 $PART_GENERALISATION_ARGUMENT -x "_$PAIR"

#copy results to HOME
mkdir -p $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/generalisation
cd $EXPERIMENT_DIR/shuf$SIZE

tar czf generalisation$PART_SUFFIX.tar.gz generalisation
cp generalisation$PART_SUFFIX.tar.gz $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE
cp generalisation/finalboxesindex $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/generalisation

if [ "$STEP" == "1" ]; then
	rm -Rf generalisation
	rm generalisation$PART_SUFFIX.tar.gz
fi

fi

# Default threshold sequence to be used 
SEQ_PROPTOTAL_START=0.4
SEQ_PROPTOTAL_STEP=-0.02
SEQ_PROPTOTAL_END=0

SEQ_COUNT_START=15
SEQ_COUNT_STEP=-1
SEQ_COUNT_END=2

SEQ_RELAXWEIGHT_START=0
SEQ_RELAXWEIGHT_STEP=-0.25
SEQ_RELAXWEIGHT_END=-5

SEQ_PROPCORRECT_EXTENDED_START=0.15
SEQ_PROPCORRECT_EXTENDED_STEP=0.05
SEQ_PROPCORRECT_EXTENDED_END=1

SEQ_PROPCORRECT_START=0.45
SEQ_PROPCORRECT_STEP=0.05
SEQ_PROPCORRECT_END=1


ALL_FILTERING_METHODS="proptotalboxposrestabove2:$SEQ_PROPTOTAL_START;$SEQ_PROPTOTAL_STEP;$SEQ_PROPTOTAL_END: proptotalboxposrestrelaxabove2:$SEQ_PROPTOTAL_START;$SEQ_PROPTOTAL_STEP;$SEQ_PROPTOTAL_END: proptotalboxposrestrelaxnocontraabove2:$SEQ_PROPTOTAL_START;$SEQ_PROPTOTAL_STEP;$SEQ_PROPTOTAL_END: count:$SEQ_COUNT_START;$SEQ_COUNT_STEP;$SEQ_COUNT_END: relax_weightabove2:$SEQ_RELAXWEIGHT_START;$SEQ_RELAXWEIGHT_STEP;$SEQ_RELAXWEIGHT_END:-n proportion_correct_bilphrases_threshold:$SEQ_PROPCORRECT_START;$SEQ_PROPCORRECT_STEP;$SEQ_PROPCORRECT_END:-n proportion_correct_bilphrases_thresholdabove2:$SEQ_PROPCORRECT_START;$SEQ_PROPCORRECT_STEP;$SEQ_PROPCORRECT_END:-n proportion_correct_bilphrases_thresholdrelaxabove2:$SEQ_PROPCORRECT_START;$SEQ_PROPCORRECT_STEP;$SEQ_PROPCORRECT_END:-n proportion_correct_bilphrases_thresholdextendedrangerelaxabove2:$SEQ_PROPCORRECT_EXTENDED_START;$SEQ_PROPCORRECT_EXTENDED_STEP;$SEQ_PROPCORRECT_EXTENDED_END:-n proportion_correct_bilphrases_thresholdrelaxsymmdiffabove2:$SEQ_PROPCORRECT_START;$SEQ_PROPCORRECT_STEP;$SEQ_PROPCORRECT_END:-n proportion_correct_bilphrases_thresholdrelaxabove3:$SEQ_PROPCORRECT_START;$SEQ_PROPCORRECT_STEP;$SEQ_PROPCORRECT_END:-n proportion_correct_bilphrases_thresholdrelaxabove4:$SEQ_PROPCORRECT_START;$SEQ_PROPCORRECT_STEP;$SEQ_PROPCORRECT_END:-n countrelax:$SEQ_COUNT_START;$SEQ_COUNT_STEP;$SEQ_COUNT_END:"

#filtering
if [ "$STEP" == "2" -o  "$STEP" == "" -o  "$STEP" == "1+" -o "$STEP" == "1-2" ]; then

	#copy prev step from $HOME
	if [ "$STEP" == "2" ]; then
		mkdir -p $EXPERIMENT_DIR/shuf$SIZE
		cd $EXPERIMENT_DIR/shuf$SIZE
		cp  $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/generalisation$PART_SUFFIX.tar.gz ./
		tar xzf generalisation$PART_SUFFIX.tar.gz
	fi

	for FILTERING_DATA in $ALL_FILTERING_METHODS ; do

	FILTERING_NAME=`echo $FILTERING_DATA | cut -f 1 -d ':'`
	FILTERING_THRESHOLDS=`echo $FILTERING_DATA | cut -f 2 -d ':'`
	FILTERING_ADDPARAMETER=`echo $FILTERING_DATA | cut -f 3 -d ':'`
	SEQ_START=`echo $FILTERING_THRESHOLDS | cut -f 1 -d ';'`
	SEQ_STEP=`echo $FILTERING_THRESHOLDS | cut -f 2 -d ';'`
	SEQ_END=`echo $FILTERING_THRESHOLDS | cut -f 3 -d ';'`

	if [ "$FILTERING_METHOD" == "" -o "$FILTERING_METHOD" == "$FILTERING_NAME" ]; then
		echo "Filtering $FILTERING_NAME $SIZE $PART"
		cd $TARGET_SOURCES/apertium-transfer-tools-generalisation/rule-generalisation
		bash $TARGET_SOURCES/apertium-transfer-tools-generalisation/rule-generalisation/filter-and-linear-prog-incremental-parallel-v6.sh -q -d $EXPERIMENT_DIR -i shuf$SIZE  -h $TARGET_SOURCES/Python-2.6.7/bin/ -s $SEQ_START -a $SEQ_STEP -b $SEQ_END -o $FILTERING_NAME -x "_$PAIR" $FILTERING_ADDPARAMETER

		#copy results to HOME
		cd $EXPERIMENT_DIR/shuf$SIZE
		tar czf filtering-$FILTERING_NAME$PART_SUFFIX.tar.gz filtering-$FILTERING_NAME
		cp filtering-$FILTERING_NAME$PART_SUFFIX.tar.gz $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE
	fi
	done

fi


#beam search
if [ "$STEP" == "8" ]; then
	echo "Beam search $FILTERING_NAME $SIZE $PART"
	
	mkdir -p $EXPERIMENT_DIR/shuf$SIZE
	
	#copy data
	cd $EXPERIMENT_DIR/shuf$SIZE
	for FILTERING_DATA in $ALL_FILTERING_METHODS ; do
		FILTERING_NAME=`echo $FILTERING_DATA | cut -f 1 -d ':'`
		if [ "$FILTERING_METHOD" == "" -o "$FILTERING_METHOD" == "$FILTERING_NAME" ]; then
			if [ -e "$HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/filtering-$FILTERING_NAME.tar.gz" ] ; then
				cp  $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/filtering-$FILTERING_NAME.tar.gz ./
				tar xzf filtering-$FILTERING_NAME.tar.gz
			else
				for file in `ls $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/filtering-${FILTERING_NAME}-[0-9]*-[0-9]*.tar.gz` ; do
					cp "$file" ./
					tar xzf `basename $file`
				done
			fi
		fi
	done
	mkdir -p generalisation
	cp $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/generalisation/finalboxesindex ./generalisation
	
	#do beam search
	for FILTERING_DATA in $ALL_FILTERING_METHODS ; do

		FILTERING_NAME=`echo $FILTERING_DATA | cut -f 1 -d ':'`
		FILTERING_THRESHOLDS=`echo $FILTERING_DATA | cut -f 2 -d ':'`
		FILTERING_ADDPARAMETER=`echo $FILTERING_DATA | cut -f 3 -d ':'`
		SEQ_START=`echo $FILTERING_THRESHOLDS | cut -f 1 -d ';'`
		SEQ_STEP=`echo $FILTERING_THRESHOLDS | cut -f 2 -d ';'`
		SEQ_END=`echo $FILTERING_THRESHOLDS | cut -f 3 -d ';'`

		if [ "$FILTERING_METHOD" == "" -o "$FILTERING_METHOD" == "$FILTERING_NAME" ]; then
			
			mkdir -p $EXPERIMENT_DIR/shuf$SIZE/beamsearch-$FILTERING_NAME
			
			#take part of the sentence
			SENTENCES_ARGUMENT=$SENTENCES_FILE
			PART_ARGUMENT=""
			if [ "$PART" != "" ]; then
				PART_NUM=`echo "$PART" | cut -f 1 -d '-'`
				PART_TOTAL=`echo "$PART" | cut -f 2 -d '-'`
				PART_ARGUMENT="--result_infix -$PART"
				LOCAL_SENTENCES_FILE=$EXPERIMENT_DIR/shuf$SIZE/beamsearch-$FILTERING_NAME/sentences.$PART.gz
				NUM_SENTENCES=`zcat $SENTENCES_FILE | wc -l`
				SENTENCES_PER_SLOT=`expr $NUM_SENTENCES / $PART_TOTAL`
				zcat $SENTENCES_FILE | head -n `expr $SENTENCES_PER_SLOT '*' $PART_NUM` | tail -n $SENTENCES_PER_SLOT | gzip > $LOCAL_SENTENCES_FILE
				SENTENCES_ARGUMENT=$LOCAL_SENTENCES_FILE
			fi
			
			#do beam search
			for THRESHOLD in `LC_ALL=C seq $SEQ_START $SEQ_STEP $SEQ_END`; do
				if [ "${FLAGS_only_one_threshold}" == "" -o  "${FLAGS_only_one_threshold}" == "$THRESHOLD" ]; then
					bash $TARGET_SOURCES/apertium-transfer-tools-generalisation/rule-generalisation/beamSearch.sh --target_language $TL --tag_groups_seqs_suffix "_$PAIR" --ats_filtering_dir $EXPERIMENT_DIR/shuf$SIZE/filtering-$FILTERING_NAME/ats/  --dir $EXPERIMENT_DIR/shuf$SIZE/beamsearch-$FILTERING_NAME/ --sentences $SENTENCES_ARGUMENT  --ats_suffix "-f-$THRESHOLD.result.gz" $PART_ARGUMENT --python_home $TARGET_SOURCES/Python-2.6.7/bin/ --apertium_data_dir $TARGET_LOCAL/share/apertium --final_boxes_index  $EXPERIMENT_DIR/shuf$SIZE/generalisation/finalboxesindex
				fi
			done
			
			#copy result to home
			cd $EXPERIMENT_DIR/shuf$SIZE
			tar czf beamsearch${BEAMSEARCH_ALLOWINCOMP}-$FILTERING_NAME$PART_SUFFIX.tar.gz beamsearch-$FILTERING_NAME
			cp beamsearch${BEAMSEARCH_ALLOWINCOMP}-$FILTERING_NAME$PART_SUFFIX.tar.gz $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE
		fi
	done
	
fi

#maximisation of score
if [ "$STEP" == "9" ]; then
	echo "Maximising score $FILTERING_NAME after beam search"
	
	mkdir -p $EXPERIMENT_DIR/shuf$SIZE/generalisation
	
	cp $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/generalisation/finalboxesindex $EXPERIMENT_DIR/shuf$SIZE/generalisation
	for FILTERING_DATA in $ALL_FILTERING_METHODS ; do
		FILTERING_NAME=`echo $FILTERING_DATA | cut -f 1 -d ':'`
		if [ "$FILTERING_METHOD" == "" -o "$FILTERING_METHOD" == "$FILTERING_NAME" ]; then
			if [ -e "$HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/filtering-$FILTERING_NAME.tar.gz" ] ; then
				cp  $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/filtering-$FILTERING_NAME.tar.gz ./
				tar xzf filtering-$FILTERING_NAME.tar.gz
			else
				for file in `ls $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/filtering-${FILTERING_NAME}-[0-9]*-[0-9]*.tar.gz` ; do
					cp "$file" ./
					tar xzf `basename $file`
				done
			fi
		fi
	done
	
	for FILTERING_DATA in $ALL_FILTERING_METHODS ; do

		FILTERING_NAME=`echo $FILTERING_DATA | cut -f 1 -d ':'`
		FILTERING_THRESHOLDS=`echo $FILTERING_DATA | cut -f 2 -d ':'`
		FILTERING_ADDPARAMETER=`echo $FILTERING_DATA | cut -f 3 -d ':'`
		SEQ_START=`echo $FILTERING_THRESHOLDS | cut -f 1 -d ';'`
		SEQ_STEP=`echo $FILTERING_THRESHOLDS | cut -f 2 -d ';'`
		SEQ_END=`echo $FILTERING_THRESHOLDS | cut -f 3 -d ';'`

		if [ "$FILTERING_METHOD" == "" -o "$FILTERING_METHOD" == "$FILTERING_NAME" ]; then
			
			#get beam search results
			cd $EXPERIMENT_DIR/shuf$SIZE
			for file in `ls $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/beamsearch${BEAMSEARCH_ALLOWINCOMP}-${FILTERING_NAME}-[0-9]*-[0-9]*.tar.gz` ; do
				cp "$file" ./
				tar xzf `basename $file`
			done
			
			
			#maximise score
			for THRESHOLD in `LC_ALL=C seq $SEQ_START $SEQ_STEP $SEQ_END`; do
				mkdir -p $EXPERIMENT_DIR/shuf$SIZE/maximise-score-$FILTERING_NAME
			        SEL_THRESHOLD="ALL"
				if [  "$PART" != "" ]; then
				    SEL_THRESHOLD=`LC_ALL=C seq $SEQ_START $SEQ_STEP $SEQ_END | sed -n ${PART}p`
				fi
				
				
				if [ "${SEL_THRESHOLD}" == "ALL" -o "${SEL_THRESHOLD}" == "$THRESHOLD" ]; then
				   
				  
				
			           bash $TARGET_SOURCES/apertium-transfer-tools-generalisation/rule-generalisation/selectRulesMaximiseScore.sh  --beam_search_dir $EXPERIMENT_DIR/shuf$SIZE/beamsearch-$FILTERING_NAME  --dir $EXPERIMENT_DIR/shuf$SIZE/maximise-score-$FILTERING_NAME --ats_suffix "-f-$THRESHOLD.result.gz" --python_home $TARGET_SOURCES/Python-2.6.7/bin/ --beam "yes" --final_boxes_index $EXPERIMENT_DIR/shuf$SIZE/generalisation/finalboxesindex
					
				   #copy results to home
				   cd $EXPERIMENT_DIR/shuf$SIZE
				   tar czf maximise-score${BEAMSEARCH_ALLOWINCOMP}-${FILTERING_NAME}-$THRESHOLD.tar.gz maximise-score-$FILTERING_NAME
				   cp maximise-score${BEAMSEARCH_ALLOWINCOMP}-${FILTERING_NAME}-$THRESHOLD.tar.gz $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE
				   rm -R $EXPERIMENT_DIR/shuf$SIZE/maximise-score-$FILTERING_NAME
				fi
				
			done
			
		fi
	done
fi

if [ "$STEP" == "3" -o  "$STEP" == "" -o  "$STEP" == "1+" ]; then

	PART_NUM=""
	PART_MODE=""

	#By default, tune only with box 0
	SEQ_BOX_START=0
	#SEQ_BOX_START=1
	#SEQ_BOX_STEP=10
	SEQ_BOX_STEP=1
	#SEQ_BOX_END=200
	SEQ_BOX_END=0


	if [ "$PART" != "" ]; then
		PART_NUM=`echo "$PART" | cut -f 1 -d '-'`
		PART_MODE=`echo "$PART" | cut -f 2 -d '-'`
		
		if [ "$PART_MODE" == "box" ]; then
			if [ "$PART_NUM" != "0" ]; then
				SEQ_BOX_START=$PART_NUM
				SEQ_BOX_STEP=$PART_NUM
				SEQ_BOX_END=$PART_NUM
			fi
		fi
		
		#if special ppostprocessing, do not tune for any box threshold
		if [ "$PART_MODE" == "subrules" -o  "$PART_MODE" == "optwer" ]; then
			#don't tune box
			SEQ_BOX_START=1
			SEQ_BOX_STEP=1
			SEQ_BOX_END=0
			
			if [ "$PART_NUM" != "" ]; then
			
			PARAMETER_PROPTOTAL=`LC_ALL=C seq $SEQ_PROPTOTAL_START $SEQ_PROPTOTAL_STEP $SEQ_PROPTOTAL_END | sed -n ${PART_NUM}p`
			PARAMETER_COUNT=`LC_ALL=C seq $SEQ_COUNT_START $SEQ_COUNT_STEP $SEQ_COUNT_END | sed -n ${PART_NUM}p`
			PARAMETER_RELAXWEIGHT=`LC_ALL=C seq $SEQ_RELAXWEIGHT_START $SEQ_RELAXWEIGHT_STEP $SEQ_RELAXWEIGHT_END | sed -n ${PART_NUM}p`
			PARAMETER_PROPCORRECT=`LC_ALL=C seq $SEQ_PROPCORRECT_START $SEQ_PROPCORRECT_STEP $SEQ_PROPCORRECT_END | sed -n ${PART_NUM}p`
			PARAMETER_PROPCORRECT_EXTENDED=`LC_ALL=C seq $SEQ_PROPCORRECT_EXTENDED_START $SEQ_PROPCORRECT_EXTENDED_STEP $SEQ_PROPCORRECT_EXTENDED_END | sed -n ${PART_NUM}p`
			
			SEQ_PROPTOTAL_START=$PARAMETER_PROPTOTAL
			SEQ_PROPTOTAL_STEP=$PARAMETER_PROPTOTAL
			SEQ_PROPTOTAL_END=$PARAMETER_PROPTOTAL

			SEQ_COUNT_START=$PARAMETER_COUNT
			SEQ_COUNT_STEP=$PARAMETER_COUNT
			SEQ_COUNT_END=$PARAMETER_COUNT
			
			SEQ_RELAXWEIGHT_START=$PARAMETER_RELAXWEIGHT
			SEQ_RELAXWEIGHT_STEP=$PARAMETER_RELAXWEIGHT
			SEQ_RELAXWEIGHT_END=$PARAMETER_RELAXWEIGHT
			
			SEQ_PROPCORRECT_START=$PARAMETER_PROPCORRECT
			SEQ_PROPCORRECT_STEP=$PARAMETER_PROPCORRECT
			SEQ_PROPCORRECT_END=$PARAMETER_PROPCORRECT
			
			SEQ_PROPCORRECT_EXTENDED_START=$PARAMETER_PROPCORRECT_EXTENDED
			SEQ_PROPCORRECT_EXTENDED_STEP=$PARAMETER_PROPCORRECT_EXTENDED
			SEQ_PROPCORRECT_EXTENDED_END=$PARAMETER_PROPCORRECT_EXTENDED
			
			
			
			fi
			
		fi
	fi

	ALL_FILTERING_METHODS="proptotalboxposrestabove2:$SEQ_PROPTOTAL_START;$SEQ_PROPTOTAL_STEP;$SEQ_PROPTOTAL_END proptotalboxposrestrelaxabove2:$SEQ_PROPTOTAL_START;$SEQ_PROPTOTAL_STEP;$SEQ_PROPTOTAL_END proptotalboxposrestrelaxnocontraabove2:$SEQ_PROPTOTAL_START;$SEQ_PROPTOTAL_STEP;$SEQ_PROPTOTAL_END count:$SEQ_COUNT_START;$SEQ_COUNT_STEP;$SEQ_COUNT_END relax_weightabove2:$SEQ_RELAXWEIGHT_START;$SEQ_RELAXWEIGHT_STEP;$SEQ_RELAXWEIGHT_END proportion_correct_bilphrases_threshold:$SEQ_PROPCORRECT_START;$SEQ_PROPCORRECT_STEP;$SEQ_PROPCORRECT_END proportion_correct_bilphrases_thresholdabove2:$SEQ_PROPCORRECT_START;$SEQ_PROPCORRECT_STEP;$SEQ_PROPCORRECT_END proportion_correct_bilphrases_thresholdrelaxabove2:$SEQ_PROPCORRECT_START;$SEQ_PROPCORRECT_STEP;$SEQ_PROPCORRECT_END proportion_correct_bilphrases_thresholdextendedrangerelaxabove2:$SEQ_PROPCORRECT_EXTENDED_START;$SEQ_PROPCORRECT_EXTENDED_STEP;$SEQ_PROPCORRECT_EXTENDED_END proportion_correct_bilphrases_thresholdrelaxsymmdiffabove2:$SEQ_PROPCORRECT_START;$SEQ_PROPCORRECT_STEP;$SEQ_PROPCORRECT_END proportion_correct_bilphrases_thresholdrelaxabove3:$SEQ_PROPCORRECT_START;$SEQ_PROPCORRECT_STEP;$SEQ_PROPCORRECT_END proportion_correct_bilphrases_thresholdrelaxabove4:$SEQ_PROPCORRECT_START;$SEQ_PROPCORRECT_STEP;$SEQ_PROPCORRECT_END countrelax:$SEQ_COUNT_START;$SEQ_COUNT_STEP;$SEQ_COUNT_END"

	if [ "$STEP" == "3" ]; then
		
		mkdir -p $EXPERIMENT_DIR/shuf$SIZE
		cd $EXPERIMENT_DIR/shuf$SIZE
		for FILTERING_DATA in $ALL_FILTERING_METHODS ; do
			FILTERING_NAME=`echo $FILTERING_DATA | cut -f 1 -d ':'`
			if [ "$FILTERING_METHOD" == "" -o "$FILTERING_METHOD" == "$FILTERING_NAME" ]; then
				
				MAXIMISE_SCORE_FILES=`find $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/ -maxdepth 1 -name "maximise-score${BEAMSEARCH_ALLOWINCOMP}-${FILTERING_NAME}-*.tar.gz"`
				NUM_MAXIMISE_SCORE_FILES=`echo "$MAXIMISE_SCORE_FILES" | wc -l`
				
				if [ "$NUM_MAXIMISE_SCORE_FILES" != "0" ] ; then
					for msfile in $MAXIMISE_SCORE_FILES ; do
					  cp $msfile ./
					  tar xzf `basename $msfile`
					done
				fi
				
				if [ -e "$HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/filtering-$FILTERING_NAME.tar.gz" ] ; then
					cp  $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/filtering-$FILTERING_NAME.tar.gz ./
					tar xzf filtering-$FILTERING_NAME.tar.gz
				else
					for file in `ls $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/filtering-${FILTERING_NAME}-[0-9]*-[0-9]*.tar.gz` ; do
						cp "$file" ./
						tar xzf `basename $file`
					done
				fi
			fi
		done
		
		mkdir -p generalisation
		cp $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/generalisation/finalboxesindex ./generalisation
		#cp $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/generalisation-fortuning.tar.gz  ./
		#tar xzf generalisation-fortuning.tar.gz
	fi

	#tuning

	for FILTERING_DATA in $ALL_FILTERING_METHODS ; do
		FILTERING_NAME=`echo $FILTERING_DATA | cut -f 1 -d ':'`
		FILTERING_THRESHOLDS=`echo $FILTERING_DATA | cut -f 2 -d ':'`
		SEQ_START=`echo $FILTERING_THRESHOLDS | cut -f 1 -d ';'`
		SEQ_STEP=`echo $FILTERING_THRESHOLDS | cut -f 2 -d ';'`
		SEQ_END=`echo $FILTERING_THRESHOLDS | cut -f 3 -d ';'`
		
		if [ "${FLAGS_only_one_threshold}" != "" ]; then
			SEQ_START=${FLAGS_only_one_threshold}
			SEQ_STEP=1
			SEQ_END=${FLAGS_only_one_threshold}
		fi
		
		if [ "$FILTERING_METHOD" == "" -o "$FILTERING_METHOD" == "$FILTERING_NAME" ]; then
			
			#Tuning with different box thresholds (usuallly only 0) = std tuning
			
			TUNING_FROM_BEAMSEARCH_FLAG=""
			#if [ -e "$EXPERIMENT_DIR/shuf$SIZE/maximise-score-$FILTERING_NAME.tar.gz" ]; then
			NUM_LOC_MAXIMISE_SCORE_FILES=`find $EXPERIMENT_DIR/shuf$SIZE -maxdepth 1 -name "maximise-score${BEAMSEARCH_ALLOWINCOMP}-${FILTERING_NAME}-*.tar.gz" | wc -l`
			if [ "$NUM_LOC_MAXIMISE_SCORE_FILES" != "0" ]; then
					TUNING_FROM_BEAMSEARCH_FLAG="-v"
			fi
			
			
			echo "Tuning $FILTERING_NAME $SIZE $PART with different box thresholds"
			cd $TARGET_SOURCES/apertium-transfer-tools-generalisation/rule-generalisation
			parallel -j 1 -i bash -c "bash $TARGET_SOURCES/apertium-transfer-tools-generalisation/rule-generalisation/tune-experiment-linear-incremental-parallel-v8.sh $INVERSE_PAIR_TUNING_FLAG $TRANSFERMODEFLAG $TUNING_FROM_BEAMSEARCH_FLAG -q -j -p $APERTIUM_PREFIX -f $APERTIUM_SOURCES -d $EXPERIMENT_DIR/shuf$SIZE/ -c $CORPORA_DIR/$DEV_CORPUS -e $CORPORA_DIR/$TEST_CORPUS  -s $SL -t $TL -r $SEQ_START -a $SEQ_STEP -b $SEQ_END -o $FILTERING_NAME -x "_$PAIR" -h $TARGET_SOURCES/Python-2.6.7/bin/ -m {} > $EXPERIMENT_DIR/shuf$SIZE/debug-tuning-$FILTERING_NAME-box{}" -- `LC_ALL=C seq $SEQ_BOX_START $SEQ_BOX_STEP $SEQ_BOX_END`
			
			cd $EXPERIMENT_DIR/shuf$SIZE
			for boxthres in `LC_ALL=C seq $SEQ_BOX_START $SEQ_BOX_STEP $SEQ_BOX_END`; do
				if [ "$boxthres" == "0" ]; then
					tar czf tuning-$FILTERING_NAME-${SEQ_START}-${SEQ_STEP}-${SEQ_END}.tar.gz tuning-$FILTERING_NAME-${SEQ_START}-${SEQ_STEP}-${SEQ_END}
					
					if [ "$TUNING_FROM_BEAMSEARCH_FLAG" != "" ]; then
						mkdir -p $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/beamresult
						cp tuning-$FILTERING_NAME-${SEQ_START}-${SEQ_STEP}-${SEQ_END}.tar.gz $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/beamresult
						cp $EXPERIMENT_DIR/shuf$SIZE/debug-tuning-$FILTERING_NAME-box$boxthres $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/beamresult
					else
						cp tuning-$FILTERING_NAME-${SEQ_START}-${SEQ_STEP}-${SEQ_END}.tar.gz $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE
						cp $EXPERIMENT_DIR/shuf$SIZE/debug-tuning-$FILTERING_NAME-box$boxthres $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE
					fi

				else
					tar czf tuning-$FILTERING_NAME-${SEQ_START}-${SEQ_STEP}-${SEQ_END}-box$boxthres.tar.gz tuning-$FILTERING_NAME-${SEQ_START}-${SEQ_STEP}-${SEQ_END}-box$boxthres
					cp tuning-$FILTERING_NAME-${SEQ_START}-${SEQ_STEP}-${SEQ_END}-box$boxthres.tar.gz $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE
					cp $EXPERIMENT_DIR/shuf$SIZE/debug-tuning-$FILTERING_NAME-box$boxthres $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE
				fi
			done
			
			# tuning subrules
			if [ "$PART_MODE" != "box" -a "$PART_MODE" != "optwer" ]; then
			
				echo "Tuning $FILTERING_NAME $SIZE $PART subrules"
				echo "$TARGET_SOURCES/apertium-transfer-tools-generalisation/rule-generalisation/tune-experiment-linear-incremental-parallel-v8.sh $TRANSFERMODEFLAG $TUNING_FROM_BEAMSEARCH_FLAG -q  -p $APERTIUM_PREFIX -f $APERTIUM_SOURCES -s $SL -t $TL  -d $EXPERIMENT_DIR/shuf$SIZE/ -c $CORPORA_DIR/$DEV_CORPUS -e $CORPORA_DIR/$TEST_CORPUS -r $SEQ_START -a $SEQ_STEP -b $SEQ_END -o $FILTERING_NAME -x "_$PAIR" -h $TARGET_SOURCES/Python-2.6.7/bin/ -z $INVERSE_PAIR_TUNING_FLAG"
				LENGTH=`zcat $EXPERIMENT_DIR/shuf$SIZE/maximise-score-${FILTERING_NAME}/rulesid-f-${SEQ_START}.gz | wc -l`
				echo "Length of maximisation result: $LENGTH"
				
				cd $TARGET_SOURCES/apertium-transfer-tools-generalisation/rule-generalisation
				bash $TARGET_SOURCES/apertium-transfer-tools-generalisation/rule-generalisation/tune-experiment-linear-incremental-parallel-v8.sh $TRANSFERMODEFLAG $TUNING_FROM_BEAMSEARCH_FLAG -q  -p $APERTIUM_PREFIX -f $APERTIUM_SOURCES -s $SL -t $TL  -d $EXPERIMENT_DIR/shuf$SIZE/ -c $CORPORA_DIR/$DEV_CORPUS -e $CORPORA_DIR/$TEST_CORPUS -r $SEQ_START -a $SEQ_STEP -b $SEQ_END -o $FILTERING_NAME -x "_$PAIR" -h $TARGET_SOURCES/Python-2.6.7/bin/ -z $INVERSE_PAIR_TUNING_FLAG 2>&1 > $EXPERIMENT_DIR/shuf$SIZE/debug-tuning-$FILTERING_NAME-${SEQ_START}-${SEQ_STEP}-${SEQ_END}-subrules
				cd $EXPERIMENT_DIR/shuf$SIZE
				
				tar czf tuning-$FILTERING_NAME-${SEQ_START}-${SEQ_STEP}-${SEQ_END}-subrules.tar.gz tuning-$FILTERING_NAME-${SEQ_START}-${SEQ_STEP}-${SEQ_END}-subrules
				if [ "$TUNING_FROM_BEAMSEARCH_FLAG" != "" ]; then
					mkdir -p $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/beamresult
					cp tuning-$FILTERING_NAME-${SEQ_START}-${SEQ_STEP}-${SEQ_END}-subrules.tar.gz $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/beamresult
					cp $EXPERIMENT_DIR/shuf$SIZE/debug-tuning-$FILTERING_NAME-${SEQ_START}-${SEQ_STEP}-${SEQ_END}-subrules $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE/beamresult
				else
				  cp tuning-$FILTERING_NAME-${SEQ_START}-${SEQ_STEP}-${SEQ_END}-subrules.tar.gz $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE
				  cp $EXPERIMENT_DIR/shuf$SIZE/debug-tuning-$FILTERING_NAME-${SEQ_START}-${SEQ_STEP}-${SEQ_END}-subrules $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE
				fi
			fi
			
			# tuning optwer
			if [ "$PART_MODE" != "box" -a "$PART_MODE" != "subrules" ]; then
			
				echo "Tuning $FILTERING_NAME $SIZE $PART optwer"
				
				cd $TARGET_SOURCES/apertium-transfer-tools-generalisation/rule-generalisation
				bash $TARGET_SOURCES/apertium-transfer-tools-generalisation/rule-generalisation/tune-experiment-linear-incremental-parallel-v8.sh $TRANSFERMODEFLAG -q  -p $APERTIUM_PREFIX -f $APERTIUM_SOURCES -s $SL -t $TL  -d $EXPERIMENT_DIR/shuf$SIZE/ -c $CORPORA_DIR/$DEV_CORPUS -e $CORPORA_DIR/$TEST_CORPUS -r $SEQ_START -a $SEQ_STEP -b $SEQ_END -o $FILTERING_NAME -x "_$PAIR" -h $TARGET_SOURCES/Python-2.6.7/bin/ -k $TARGET_SOURCES/shuf${SIZE}corpus-$PAIR $INVERSE_PAIR_TUNING_FLAG > $EXPERIMENT_DIR/shuf$SIZE/debug-tuning-$FILTERING_NAME-${SEQ_START}-${SEQ_STEP}-${SEQ_END}-optwer
				cd $EXPERIMENT_DIR/shuf$SIZE
				
				tar czf tuning-$FILTERING_NAME-${SEQ_START}-${SEQ_STEP}-${SEQ_END}-optwer.tar.gz tuning-$FILTERING_NAME-${SEQ_START}-${SEQ_STEP}-${SEQ_END}-optwer
				
				cp tuning-$FILTERING_NAME-${SEQ_START}-${SEQ_STEP}-${SEQ_END}-optwer.tar.gz $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE
				
				cp $EXPERIMENT_DIR/shuf$SIZE/debug-tuning-$FILTERING_NAME-${SEQ_START}-${SEQ_STEP}-${SEQ_END}-optwer $HOME/results/experiments-linear-l5-${PAIR}/$BILPHRASES_ID/shuf$SIZE				
			fi			
		fi
	done
fi

rm -Rf $JOB_DIR

if [ "${FLAGS_touch_when_finished}" != "" ] ; then
	touch "${FLAGS_touch_when_finished}"
fi
