#! /bin/bash

CURDIR=`dirname $0`


#shflags
. $CURDIR/shflags

DEFINE_string 'beam_search_dir' '' 'directory where the ats are' 'a'
DEFINE_string 'ats_suffix' '' 'at file suffix' 'x'
DEFINE_string 'dir' '' 'directory where the new files and dirs will be created' 'd'
DEFINE_string 'python_home' '' 'dir of python interpreter' 'p'
DEFINE_string 'beam' '' 'do beam search instead of lin. prog' 'b'

FLAGS "$@" || exit $?
eval set -- "${FLAGS_ARGV}"

echo "ATS suffix: ${FLAGS_ats_suffix}" 1>&2

BEAM_FLAG=""
if [ "${FLAGS_beam}" != "" ]; then
	BEAM_FLAG="--beam --only_hyps_with_maximum_local --discard_sentences_all_maximum"
fi

mkdir -p ${FLAGS_dir}
RESULT_SUFFIX=`echo "${FLAGS_ats_suffix}" | sed 's:.result::'`
RULESID_FILE=${FLAGS_dir}/rulesid$RESULT_SUFFIX
RESULT_FILE=${FLAGS_dir}/result$RESULT_SUFFIX
SCORES_FILE=${FLAGS_dir}/allscores$RESULT_SUFFIX
ALL_ATS_FILE=${FLAGS_beam_search_dir}/ats${FLAGS_ats_suffix}

rm -f $SCORES_FILE
NUMBERS=`ls  ${FLAGS_beam_search_dir} | grep "^scores-" | awk -F"-" '{print $2}' | LC_ALL=C sort | uniq`
TOTALNUMBERS=`echo "$NUMBERS" | wc -l`
for number in $NUMBERS; do
	zcat "${FLAGS_beam_search_dir}/scores-${number}-${TOTALNUMBERS}${FLAGS_ats_suffix}" >> ${SCORES_FILE%.gz}
done
gzip ${SCORES_FILE%.gz}

zcat ${SCORES_FILE%.gz} | ${FLAGS_python_home}python $CURDIR/maximiseScore.py --only_n_first 1000 $BEAM_FLAG  2> ${RESULT_FILE}-debug | gzip > $RULESID_FILE
#SED_EXPR=`zcat $RULESID_FILE | sed 's:$:p:' | tr '\n' ';' | sed 's:;$::'`

#zcat $ALL_ATS_FILE | sed -n "$SED_EXPR" | gzip > $RESULT_FILE

