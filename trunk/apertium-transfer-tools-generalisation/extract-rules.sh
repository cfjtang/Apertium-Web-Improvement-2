#! /bin/bash

CURDIR=`dirname $0`

#shflags
. $CURDIR/rule-generalisation/shflags

DEFINE_string 'source_language' 'es' 'source language language' 's'
DEFINE_string 'target_language' 'ca' 'target language' 't'
DEFINE_string 'corpus' '' 'prefix of files containing parallel corpus (suffixes are .SL and .TL)' 'c'
DEFINE_string 'giza_dir' '~/giza-pp/GIZA++-v2' 'Giza++ directory' 'g'
DEFINE_string 'data_dir' '' 'Directory where the compiled Apertium dictionaries can be found (default: /usr/local/share/apertium/apertium-SL-TL/)' 'd'
######################
# (optional)
# Temporary directory where all the the files will be stored
# If not set, the script will create a new temporary directory under /tmp
######################
DEFINE_string 'tmp_dir' '' 'temporary directory' 'm'
DEFINE_boolean 'segment_by_marker' 'false' 'Segment SL corpus according to marker hypothesis' 'y'
DEFINE_string 'filtering_thresholds' '0.45 0.05 1' 'Thresholds for filtering alignment templates. Format is start step end, as in the seq command. A single threshold can also be defined' 'f'
DEFINE_string 'test_corpus' '' 'evaluation corpus prefix (suffixes are .SL and .TL)'
DEFINE_string 'dev_corpus' '' 'development corpus prefix (suffixes are .SL and .TL). It is used to choose the most appropriate threshold'

#process parameters
FLAGS "$@" || exit $?
eval set -- "${FLAGS_ARGV}"

if [ "${FLAGS_tmp_dir}" == "" ]; then
	TMPDIR=`mktemp -d`
else
	TMPDIR=${FLAGS_tmp_dir}
	mkdir -p $TMPDIR
fi
SL=${FLAGS_source_language}
TL=${FLAGS_target_language}
PAIR="${SL}-${TL}"

CORPUS=${FLAGS_corpus}
GIZADIR=${FLAGS_giza_dir}
FULLRULELEARNINGDIR=`readlink -f $CURDIR/rule-generalisation`

if [ "${FLAGS_data_dir}" == "" ]; then
  DATADIR="/usr/local/share/apertium/apertium-$SL-$TL/"
else
  DATADIR="${FLAGS_data_dir}"
fi

MARKERFLAG=""
if [ ${FLAGS_segment_by_marker} == ${FLAGS_TRUE} ]; then
MARKERFLAG="PAR_BILPHRASES_MARKER_SOFT=yes"
fi

NUMPARTS=`echo "${FLAGS_filtering_thresholds}" | wc -w`

if [ "$NUMPARTS" == "1" ]; then
  THRESHOLD_START=${FLAGS_filtering_thresholds}
  THRESHOLD_STEP=1
  THRESHOLD_END=${FLAGS_filtering_thresholds}
else
  THRESHOLD_START=`echo "${FLAGS_filtering_thresholds}" | cut -f 1 -d ' '`
  THRESHOLD_STEP=`echo "${FLAGS_filtering_thresholds}" | cut -f 2 -d ' '`
  THRESHOLD_END=`echo "${FLAGS_filtering_thresholds}" | cut -f 3 -d ' '`
fi

TEST_CORPUS=${FLAGS_test_corpus}
DEV_CORPUS=${FLAGS_dev_corpus}

echo "Temporary directory: $TMPDIR" 1>&2
echo "Checking whether 'lt-proc' is in the PATH: " 1>&2
which lt-proc
if [ "$?" != "0" ]; then
  echo "ERROR: not found" 1>&2
else
  echo "OK" 1>&2
fi

echo "Checking whether 'apertium-symmetrize-alignments' is in the PATH: " 1>&2
which apertium-symmetrize-alignments
if [ "$?" != "0" ]; then
  echo "ERROR: not found" 1>&2
else
  echo "OK" 1>&2
fi

echo "Checking whether needed GIZA++ executables are found in GIZA++ dir: " 1>&2
for myfile in plain2snt.out snt2plain.out "../mkcls-v2/mkcls" GIZA++ ; do
  if [ -e "$GIZADIR/$myfile" ]; then
    echo "$myfile OK" 1>&2
  else
    echo "ERROR: $myfile not found" 1>&2
  fi
done

BILEXTRACTIONDIR=$TMPDIR/bilingualphrases

if [ ! -e $BILEXTRACTIONDIR ]; then

mkdir -p $BILEXTRACTIONDIR
BILEXTRACTIONDIRFULL=`readlink -f $BILEXTRACTIONDIR`

pushd $CURDIR/phrase-extraction/transfer-tools-scripts
bash linkToWorkDir.sh $BILEXTRACTIONDIRFULL
popd

#get corpus
cat $CORPUS.$SL | gzip > $BILEXTRACTIONDIR/$SL.txt.gz
cat $CORPUS.$TL | gzip > $BILEXTRACTIONDIR/$TL.txt.gz

#run makefile to extract bilingual phrases
pushd $BILEXTRACTIONDIR
make PAR_DONT_REMOVE_CONFLICTS=yes PAR_MAX=5 PAR_L1=$SL PAR_L2=$TL PAR_NO_SHORT_RESTRICTIONS=yes PAR_MY_EXTRACTING=yes $MARKERFLAG PAR_DATADIR="$DATADIR" PAR_RULELEARNINGLIBDIR="$FULLRULELEARNINGDIR" PAR_GIZADIR="$GIZADIR" PAR_ENDS_ALIGNED="ENDSALIGNED"
popd

else

echo "Bilingual phrase extraction directory already exists. Omitting bilingual phrase extraction." 1>&2

fi

#extract transfer rules from bilingual phrases

#1. generate multiple alignment templates from each bilingual phrases
if [ ! -e $TMPDIR/generalisation ]; then
bash $CURDIR/rule-generalisation/run-experiment-linear-incremental-parallel-v8.sh -v -d $TMPDIR  -f $BILEXTRACTIONDIR/alignmentTemplatesPlusLemmas.withalllemmas.onlyslpos.filtered-1-count.${SL}-${TL}.gz -i "." -x "_$PAIR"
else
echo "AT generalisation direrctory already exists. Omitting AT generalisation." 1>&2
fi

#2. minimise alignment templates
if [ ! -e $TMPDIR/filtering-proportion_correct_bilphrases_thresholdrelaxabove2 ]; then
bash $CURDIR/rule-generalisation/filter-and-linear-prog-incremental-parallel-v6.sh  -d $TMPDIR -i . -s $THRESHOLD_START -a $THRESHOLD_STEP -b $THRESHOLD_END -o proportion_correct_bilphrases_thresholdrelaxabove2 -x "_$PAIR" -n
else
echo "AT minimisation directory already exists. Omitting AT minimisation." 1>&2
fi

#3. convert alignment templates to Apertium rules and test them
SOURCESDIR=`echo "$DATADIR" | sed 's,/*[^/]\+/*$,,'`
PAIROFDATADIR=`basename $DATADIR | sed 's:^apertium-::'`

if [ "$PAIROFDATADIR" == "$PAIR" ]; then
INVERSE_PAIR_TUNING_FLAG=""
else
INVERSE_PAIR_TUNING_FLAG="-i"
fi

if [ ! -e $TMPDIR/tuning-proportion_correct_bilphrases_thresholdrelaxabove2-${THRESHOLD_START}-${THRESHOLD_STEP}-${THRESHOLD_END}-subrules/summary ]; then
bash $CURDIR/rule-generalisation/tune-experiment-linear-incremental-parallel-v8.sh -f $SOURCESDIR -s $SL -t $TL  -d $TMPDIR -c "$DEV_CORPUS" -e "$TEST_CORPUS" -r $THRESHOLD_START -a $THRESHOLD_STEP -b $THRESHOLD_END -o proportion_correct_bilphrases_thresholdrelaxabove2 -x "_$PAIR" -z $INVERSE_PAIR_TUNING_FLAG -l
else
echo "Rule redundancy removal, tuning and testing directory already exists. Omitting rule redundancy removal, tuning and testing." 1>&2
fi
