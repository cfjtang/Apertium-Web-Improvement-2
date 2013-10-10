#! /bin/bash

CURDIR=`dirname $0`

#shflags
. $CURDIR/shflags
DEFINE_string 'size' '500' 'Size of bilingual corpus' 'v'
DEFINE_string 'filtering_method' 'proportion_correct_bilphrases_thresholdrelaxabove2' 'Filtering method' 'f'
DEFINE_string 'source_language' 'es' 'Source language' 's'
DEFINE_string 'target_language' 'ca' 'Target language' 't'
DEFINE_string 'inverse_pair' '' 'INVERSE means that TL-SL is the name of the pair in Apertium' 'i'
DEFINE_string 'dev_corpus' 'consumer-eroski.dev' 'Development corpus' 'd'
DEFINE_string 'test_corpus' 'consumer-eroski.test' 'Test corpus' 'r'
DEFINE_string 'bilingual_phrases_id' 'marker-withcontradictions' 'Identifier of the starting bilingual phrases' 'b'
DEFINE_string 'num_parts' '20' 'Number of partitions of data' 'p'
DEFINE_string 'step' '' 'Step: 1= pregeneralisation, 2=generalisation 3=filtering, 4=tuning, empty=all'
DEFINE_string 'alg_version' 'NORMAL' '' 'o'
DEFINE_string 'beam_search' '' '' 'm'

FLAGS "$@" || exit $?
eval set -- "${FLAGS_ARGV}"

echo "Test corpus is ${FLAGS_test_corpus} and dev corpus is ${FLAGS_dev_corpus}"

PARTS=${FLAGS_num_parts}

RESULTS_DIR=$HOME/results/experiments-linear-l5-${FLAGS_source_language}-${FLAGS_target_language}/${FLAGS_bilingual_phrases_id}/shuf${FLAGS_size}/
mkdir -p $RESULTS_DIR

if [ "${FLAGS_step}" == "" -o "${FLAGS_step}" == "1" ]; then
#first step: pre-generalisation
qsub $CURDIR/myExperiment-cluster-v5.sh --size "${FLAGS_size}" --source_language "${FLAGS_source_language}" --target_language "${FLAGS_target_language}" --inverse_pair ${FLAGS_inverse_pair} --dev_corpus "${FLAGS_dev_corpus}" --test_corpus "${FLAGS_test_corpus}" --bilingual_phrases_id "${FLAGS_bilingual_phrases_id}" --step 0 --touch_when_finished "$RESULTS_DIR/pregeneralisation.finished" --alg_version ${FLAGS_alg_version}

while [ "`find $RESULTS_DIR -name 'pregeneralisation.finished' | wc -l`" == "0" ] ; do 
	sleep 30; 
done
fi

if [ "${FLAGS_step}" == "" -o "${FLAGS_step}" == "2" ]; then
#second step: generalisation
for p in `seq $PARTS` ; do
	qsub $CURDIR/myExperiment-cluster-v5.sh --size "${FLAGS_size}" --filtering_method "${FLAGS_filtering_method}" --source_language "${FLAGS_source_language}" --target_language "${FLAGS_target_language}" --inverse_pair "${FLAGS_inverse_pair}" --dev_corpus "${FLAGS_dev_corpus}" --test_corpus "${FLAGS_test_corpus}" --bilingual_phrases_id "${FLAGS_bilingual_phrases_id}" --step "1" --touch_when_finished "$RESULTS_DIR/$p.gen.${FLAGS_filtering_method}.finished" --part "${p}-${PARTS}" --alg_version ${FLAGS_alg_version}
done


while [ "`find $RESULTS_DIR -name "*.gen.${FLAGS_filtering_method}.finished" | wc -l`" != "$PARTS" ] ; do 
	sleep 30; 
done
fi


if [ "${FLAGS_step}" == "" -o "${FLAGS_step}" == "3"  -o "${FLAGS_step}" == "3-4" -o "${FLAGS_step}" == "3-8-9-4" ]; then
#third step: filtering
for p in `seq $PARTS` ; do
	qsub $CURDIR/myExperiment-cluster-v5.sh --size "${FLAGS_size}" --filtering_method "${FLAGS_filtering_method}" --source_language "${FLAGS_source_language}" --target_language "${FLAGS_target_language}" --inverse_pair "${FLAGS_inverse_pair}" --dev_corpus "${FLAGS_dev_corpus}" --test_corpus "${FLAGS_test_corpus}" --bilingual_phrases_id "${FLAGS_bilingual_phrases_id}" --step "2" --touch_when_finished "$RESULTS_DIR/$p.filter.${FLAGS_filtering_method}.finished" --part "${p}-${PARTS}" --alg_version ${FLAGS_alg_version}
done


while [ "`find $RESULTS_DIR -name "*.filter.${FLAGS_filtering_method}.finished" | wc -l`" != "$PARTS" ] ; do 
	sleep 30; 
done
fi


if [ "${FLAGS_step}" == "8" -o "${FLAGS_step}" == "" -o "${FLAGS_step}" == "8-9-4" -o "${FLAGS_step}" == "3-8-9-4" ]; then

ONLYONETHRESHOLDFLAG=""
if [ "${FLAGS_beam_search}" != "" ]; then
  ONLYONETHRESHOLDFLAG="--only_one_threshold ${FLAGS_beam_search}"
fi

if [ "${FLAGS_step}" != "8" ]; then
  BEAMSEARCHPARTS=`expr $PARTS \* 3`
else
  BEAMSEARCHPARTS="$PARTS"
fi
#optional step: beam search
for p in `seq $BEAMSEARCHPARTS` ; do
	qsub $CURDIR/myExperiment-cluster-v5.sh --size "${FLAGS_size}" --filtering_method "${FLAGS_filtering_method}" --source_language "${FLAGS_source_language}" --target_language "${FLAGS_target_language}" --inverse_pair "${FLAGS_inverse_pair}" --dev_corpus "${FLAGS_dev_corpus}" --test_corpus "${FLAGS_test_corpus}" --bilingual_phrases_id "${FLAGS_bilingual_phrases_id}" --step "8" --touch_when_finished "$RESULTS_DIR/$p.beamsearch.${FLAGS_filtering_method}.finished" --part "${p}-${BEAMSEARCHPARTS}" --alg_version ${FLAGS_alg_version} $ONLYONETHRESHOLDFLAG
done


while [ "`find $RESULTS_DIR -name "*.beamsearch.${FLAGS_filtering_method}.finished" | wc -l`" != "$BEAMSEARCHPARTS" ] ; do 
	sleep 30; 
done
fi



PARTS_TUNING=18

if [ "${FLAGS_step}" == "9" -o "${FLAGS_step}" == "9-4" -o "${FLAGS_step}" == "" -o "${FLAGS_step}" == "8-9-4" -o "${FLAGS_step}" == "3-8-9-4" ]; then
#optional step: maximisation after beam search
for p in `seq $PARTS_TUNING` ; do
	qsub $CURDIR/myExperiment-cluster-v5.sh --size "${FLAGS_size}" --filtering_method "${FLAGS_filtering_method}" --source_language "${FLAGS_source_language}" --target_language "${FLAGS_target_language}" --inverse_pair "${FLAGS_inverse_pair}" --dev_corpus "${FLAGS_dev_corpus}" --test_corpus "${FLAGS_test_corpus}" --bilingual_phrases_id "${FLAGS_bilingual_phrases_id}" --step "9" --touch_when_finished "$RESULTS_DIR/$p.maximisation.${FLAGS_filtering_method}.finished" --alg_version ${FLAGS_alg_version}  --part "${p}"
done

while [ "`find $RESULTS_DIR -name "*.maximisation.${FLAGS_filtering_method}.finished" | wc -l`" != "$PARTS_TUNING" ] ; do 
	sleep 30; 
done
fi

NODESWITHCG="cn01.iuii.ua.local|cn02.iuii.ua.local|cn04.iuii.ua.local|cn05.iuii.ua.local|cn06.iuii.ua.local|cn07.iuii.ua.local|cn08.iuii.ua.local|cn09.iuii.ua.local|cn10.iuii.ua.local|cn11.iuii.ua.local|cn12.iuii.ua.local|cn13.iuii.ua.local|cn14.iuii.ua.local|cn15.iuii.ua.local|cn16.iuii.ua.local|cn17.iuii.ua.local|cn18.iuii.ua.local|cn19.iuii.ua.local|cn20.iuii.ua.local|cn21.iuii.ua.local|cn23.iuii.ua.local|cn24.iuii.ua.local|cn25.iuii.ua.local|cn26.iuii.ua.local"

#create generalisation-fortuning gz!!
#Only needed for box threshold. safe to ignore

if [ "${FLAGS_step}" == "" -o "${FLAGS_step}" == "4" -o "${FLAGS_step}" == "3-4" -o "${FLAGS_step}" == "9-4" -o "${FLAGS_step}" == "8-9-4" -o "${FLAGS_step}" == "3-8-9-4" ]; then

if [ "${FLAGS_beam_search}" != "" ]; then
	qsub $CURDIR/myExperiment-cluster-v5.sh --size "${FLAGS_size}" --filtering_method "${FLAGS_filtering_method}" --source_language "${FLAGS_source_language}" --target_language "${FLAGS_target_language}" --inverse_pair "${FLAGS_inverse_pair}" --dev_corpus "${FLAGS_dev_corpus}" --test_corpus "${FLAGS_test_corpus}" --bilingual_phrases_id "${FLAGS_bilingual_phrases_id}" --step "3" --touch_when_finished $RESULTS_DIR/1.tuningfrombeam.${FLAGS_filtering_method}.finished --part "0-box" --only_one_threshold ${FLAGS_beam_search} --alg_version ${FLAGS_alg_version}
	
	while [ "`find $RESULTS_DIR -name "1.tuningfrombeam.${FLAGS_filtering_method}.finished" | wc -l`" != "1" ] ; do 
		sleep 30; 
	done
else

	#third step: tuning with subrules
	for p in `seq $PARTS_TUNING` ; do
		qsub -l h="$NODESWITHCG" $CURDIR/myExperiment-cluster-v5.sh --size "${FLAGS_size}" --filtering_method "${FLAGS_filtering_method}" --source_language "${FLAGS_source_language}" --target_language "${FLAGS_target_language}" --inverse_pair "${FLAGS_inverse_pair}" --dev_corpus "${FLAGS_dev_corpus}" --test_corpus "${FLAGS_test_corpus}" --bilingual_phrases_id "${FLAGS_bilingual_phrases_id}" --step "3" --touch_when_finished $RESULTS_DIR/$p.tuning.${FLAGS_filtering_method}.finished --part "${p}-subrules" --alg_version ${FLAGS_alg_version}
	done

	while [ "`find $RESULTS_DIR -name "*.tuning.${FLAGS_filtering_method}.finished" | wc -l`" != "$PARTS_TUNING" ] ; do 
		sleep 30; 
	done

fi
fi