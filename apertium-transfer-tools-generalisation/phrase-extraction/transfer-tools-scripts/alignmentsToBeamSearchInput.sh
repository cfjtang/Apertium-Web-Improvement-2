#! /bin/bash

INPUTFILE=$1
BILDICTIONARY=$2
WDIR=`dirname $1`

zcat $INPUTFILE | sed 's:\^\$:^*unknown$:g' | sed 's:^[^|]*| ::'  | sed 's:\^:| ^:'   |  sed 's:| *\^:| ':g | sed 's:\$ *|: |:g' | sed 's:\$ *\^: :g' | sed 's:^| ::' | gzip > $INPUTFILE.toBeam.step1.gz

#the last sed removes multiple entries
zcat $INPUTFILE.toBeam.step1.gz | cut -f 1 -d '|'  | sed  's:^ *::' | sed  's_ *$__' | sed -r 's_ _\t_g' | sed -r 's:_: :g'  | apertium-apply-biling --biling $BILDICTIONARY | sed 's:/[^\t]*::g' | gzip > $INPUTFILE.toBeam.bildic.gz

zcat $INPUTFILE.toBeam.bildic.gz  | sed 's:<[^\t]*::g' | sed 's: :_:g' | sed 's:\t: :g' > $INPUTFILE.toBeam.tllemmas

zcat $INPUTFILE.toBeam.bildic.gz  | sed 's:^[^<\t]*:LEMMAPLACEHOLDER:' | sed 's:\t[^<\t]*:\tLEMMAPLACEHOLDER:g' | sed 's:LEMMAPLACEHOLDER<:<:g' | sed 's:LEMMAPLACEHOLDER:__EMPTYRESTRICTION__:g' | sed 's:\t: :g' > $INPUTFILE.toBeam.restrictions

zcat $INPUTFILE.toBeam.step1.gz | paste -d '|' - $INPUTFILE.toBeam.restrictions $INPUTFILE.toBeam.tllemmas | gzip > $INPUTFILE.toBeam.gz