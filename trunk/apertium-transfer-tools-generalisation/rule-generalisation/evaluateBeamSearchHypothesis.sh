#! /bin/bash
CURDIR=`dirname $0`

#shflags
. $CURDIR/shflags

DEFINE_string 'input_file' '' 'file containing source, translation and reference' 'f'
DEFINE_string 'source_language' '' 'source language' 's'
DEFINE_string 'target_language' '' 'target language' 't'
DEFINE_string 'apertium_data_dir' '' 'directory where the Apertium modes are stored' 'd'
DEFINE_string 'use_tmp_dir' '' 'temporary directory' 'm'

FLAGS "$@" || exit $?
eval set -- "${FLAGS_ARGV}"

INPUT=${FLAGS_input_file}
SL=${FLAGS_source_language}
TL=${FLAGS_target_language}

if  [ "${FLAGS_apertium_data_dir}" != "" ] ; then
	DATADIRFLAG="-d ${FLAGS_apertium_data_dir}"
else
	DATADIRFLAG=""
fi

if [ "${FLAGS_use_tmp_dir}" != "" ]; then
	TMPDIR=${FLAGS_use_tmp_dir}
	mkdir -p $TMPDIR
else
	TMPDIR=`mktemp -d`
fi
echo "Temporary directory: $TMPDIR" 1>&2

cat $INPUT | cut -f 1 -d '|' | tr '<' '.' | tr '>' '.' > $TMPDIR/source
cat $INPUT | cut -f 2 -d '|' | apertium $DATADIRFLAG -f none ${TL}_lex_from_beam-${TL}  > $TMPDIR/test
cat $INPUT | cut -f 3 -d '|' | apertium $DATADIRFLAG -f none ${TL}_lex_from_beam-${TL}  > $TMPDIR/reference

bash $CURDIR/mteval-v13-nosgm-segments.sh $TMPDIR/source $TMPDIR/reference $TMPDIR/test | grep "^  BLEU" | cut -f 8 -d ' '

if [ "${FLAGS_use_tmp_dir}" == "" ]; then
	rm -R $TMPDIR
fi

