# Copyright (C) 2009 Universitat d'Alacant / Universidad de Alicante
# 
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License as
# published by the Free Software Foundation; either version 2 of the
# License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
# 02111-1307, USA.

if [ $# -lt 6 ]
then
  echo "Wrong number of parameters."
  echo "Sintax: $0 apertium-data-dir chunks-file[.gz] translation-dir defscore report-file langmodel [-pspts] [-debug]"
  exit 1
fi

APERTIUM_DIR=$1
CHUNKS_FILE=$2
DIRECTION=$3
DEFSCORE=$4
REPORTFILE=$5
LANGMODEL=$6

FORMAT="txt"
APERTREX_DIR=`dirname $0`

AUX=`echo $CHUNKS_FILE | sed -re "s/[.gz]$//g"`
if [ $CHUNKS_FILE != $AUX ]
then
  GZIP=--gzip
fi

if [ $# -gt 6  -a  "$7" = "-pspts" ]
then
  PSPTS=--pspts
fi

if [ $# -gt 6  -a  "$8" = "-debug" ]
then
  DEBUG=--debug
fi

if [ $# -gt 7  -a  "$8" = "-debug" ]
then
  DEBUG=--debug
fi

apertium-des$FORMAT |\
sed -re "s/([^]])\[$/\1 [/g" | sed -re "s/^\]/] /g" | sed -re "s/[.]\[\]\[$/ .[][/" | gawk '{print}' |\
$APERTREX_DIR/translate-detect-chunks -c $CHUNKS_FILE -s $DEFSCORE $PSPTS $GZIP $DEBUG > $REPORTFILE

cat $REPORTFILE |\
sed -re "s/ \[$/[/g"| sed -re "s/ [.]\[\]\[$/.[][/" | sed -re "s/^\] /]/g" |\
gawk 'BEGIN{p=""; f=1}{if (f==1) f=0; else print p; p=$0}END{printf "%s", p}' |\
lt-proc $APERTIUM_DIR/$DIRECTION.automorf.bin |\
apertium-tagger -g  $APERTIUM_DIR/$DIRECTION.prob |\
apertium-pretransfer |\
apertium-transfer $APERTIUM_DIR/$DIRECTION.t1x  $APERTIUM_DIR/$DIRECTION.t1x.bin  $APERTIUM_DIR/$DIRECTION.autobil.bin |\
apertium-interchunk $APERTIUM_DIR/$DIRECTION.t2x  $APERTIUM_DIR/$DIRECTION.t2x.bin |\
apertium-postchunk $APERTIUM_DIR/$DIRECTION.t3x  $APERTIUM_DIR/$DIRECTION.t3x.bin |\
lt-proc -n $APERTIUM_DIR/$DIRECTION.autogen.bin |\
lt-proc -p $APERTIUM_DIR/$DIRECTION.autopgen.bin |\
apertium-re$FORMAT |\
$APERTREX_DIR/restore_chunk_marks -noinsert > $REPORTFILE-translation

cat $REPORTFILE-translation | $APERTREX_DIR/../external-tools/tokenizer.perl |\
sed -re "s/\s+/ /g" | sed -re "s/^\s+//g" | sed -re "s/\s+$//g" |\
$APERTREX_DIR/translate-score-replace-chunks -c $CHUNKS_FILE -l $LANGMODEL $GZIP
