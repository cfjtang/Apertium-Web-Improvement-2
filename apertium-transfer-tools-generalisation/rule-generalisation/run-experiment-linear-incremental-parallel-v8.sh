INPUTFILE=. 	
WORKDIR=../work-es-ca-transfertools-2M/linear-experiments
ID="shuf10"
CURDIR=`dirname $0`


#types of generalisation:
# -c : classic method. TL special attributes follow alignments. Do not use
# -c -n : new method. TL special attributes contain references to SL tags. Subproblems are split according to category and restrictions
# (nothing) : new method.TL special attributes contain references to SL tags and blingual dictionary Subproblems are split according to category
# -r : new method.TL special attributes contain references to SL tags and blingual dictionary. All the combinations of restrictions are explored. Subproblems are split according to category
# -g : new method.TL special attributes contain references to SL tags and blingual dictionary. Combinations of restrictions GD;ND, etc. are explored. Subproblems are split according to category
# -l : new method.TL special attributes contain references to SL tags and blingual dictionary. Combinations of restrictions combinations of restrictions GD;ND;mf;,etc are explored. Subproblems are split according to category

NUM_PARTS=1
CHOSEN_PART=1

COPY_EMPTY_TAGS=="no"

TAGSEQUENCESANDGROUPSSUFFIX=""
TAGGROUPSGENEMPTYSL=""
TAGGROUPSGENEMPTYTL=""

PYTHONHOME=""

STEP=""

PARALLELONLYONE=""

RICHATS="yes"
RICHATSFLAG="--rich_ats --ref_to_biling"
POWERSETFEATURESFLAG="--generalise_from_right_to_left"
RICHATSFLAGONLYGEN=""
DIFFERENTRESTRICTIONSFLAG=""

FIRSTMINIMISELEMMAS=""

usage()
{
cat << EOF
Bla bla bla	
	
EOF
}

while getopts “f:d:i:p:x:e:h:t:m:yqcanrglvsubj” OPTION
do
     case $OPTION in
        f)
             INPUTFILE=$OPTARG
             ;;
        d)
             WORKDIR=$OPTARG
             ;;
        i)
	     ID=$OPTARG
             ;;
        x)
                TAGSEQUENCESANDGROUPSSUFFIX=$OPTARG
                ;;
        e)
                TAGGROUPSGENEMPTYSL=$OPTARG
                ;;
        h)
                PYTHONHOME=$OPTARG
                ;;
        t)
                STEP=$OPTARG
                ;;
        m)
                NUM_PARTS=`echo "$OPTARG" | cut -f 2 -d '-'` 
                CHOSEN_PART=`echo "$OPTARG" | cut -f 1 -d '-'` 
                ;;
        y)
                COPY_EMPTY_TAGS="yes"
                ;;
        q)
                PARALLELONLYONE="-j 1"
                ;;
        c)
               RICHATS="no"
               RICHATSFLAG=""
               ;;
        a)
                POWERSETFEATURESFLAG="" #generate all combinations of features to generalise
                ;;
        n)
		RICHATSFLAGONLYGEN="--rich_ats"
		;;
	r)
	        DIFFERENTRESTRICTIONSFLAG="--different_restriction_options"
	        ;;
	g)
	        DIFFERENTRESTRICTIONSFLAG="--different_restriction_options --only_to_be_determined_in_restriction"
	        ;;
	l)
	        DIFFERENTRESTRICTIONSFLAG="--different_restriction_options --only_to_be_determined_and_mf_in_restriction"
	        ;;
	s)
		DIFFERENTRESTRICTIONSFLAG="--different_restriction_options --only_to_be_determined_and_change_in_restriction"
	        ;;
	b)
		DIFFERENTRESTRICTIONSFLAG="--different_restriction_options --only_tags_triggering_diference_in_restriction"
		;;
	j)
		DIFFERENTRESTRICTIONSFLAG="--different_restriction_options --only_tags_triggering_diference_in_restriction --triggering_limited_length"
		;;
	v)
	       DIFFERENTRESTRICTIONSFLAG="--add_restrictions_to_every_tag $DIFFERENTRESTRICTIONSFLAG"
	       ;;
	u)
	      FIRSTMINIMISELEMMAS="yes"
	      ;;
        ?)
             usage
             exit
             ;;
     esac
done

if [ "COPY_EMPTY_TAGS" == "yes" ]; then
	TAGGROUPSGENEMPTYSL=`cat $CURDIR/taggroups$TAGSEQUENCESANDGROUPSSUFFIX | cut -f 1 -d ':' | tr '\n' ','`
	TAGGROUPSGENEMPTYTL=$TAGGROUPSGENEMPTYSL
fi

CHOSEN_PART_MINUS_ONE=`expr $CHOSEN_PART - 1`

TRUEWORKDIR="$WORKDIR/$ID"
mkdir -p $TRUEWORKDIR

mkdir -p $TRUEWORKDIR/generalisation/newbilphrases

#if [ ! -f $TRUEWORKDIR/alignmentTemplatesPLusLemmas.gz ]; then

BOXESINDEX="$TRUEWORKDIR/generalisation/boxesindex"

if [ "$STEP" == "1" -o  "$STEP" == "" ]; then

rm -f $BOXESINDEX

CURRENTID=1

echo "Sorting input file" >&2

RICHATSFLAGFORADD=""
if [ "$RICHATS" == "yes" ]; then
	RICHATSFLAGFORADD="--rich_ats"
fi

#sort input by sl tags and restrictions
zcat $INPUTFILE  | ${PYTHONHOME}python $CURDIR/addPosAndRestrictionsStr.py --tag_groups_file_name $CURDIR/taggroups$TAGSEQUENCESANDGROUPSSUFFIX --tag_sequences_file_name $CURDIR/tagsequences$TAGSEQUENCESANDGROUPSSUFFIX $RICHATSFLAGFORADD | sed 's_ |_|_' | LC_ALL=C sort | ${PYTHONHOME}python $CURDIR/spreadBilphrases.py  --dir $TRUEWORKDIR/generalisation > $BOXESINDEX

fi

BOXESINDEXVAR=`cat $BOXESINDEX`


if [ "$STEP" == "2" -o  "$STEP" == "" ]; then

echo "Computing generalisations" >&2

#compute all generalisations
mkdir -p $TRUEWORKDIR/generalisation/ats
mkdir -p $TRUEWORKDIR/generalisation/debug

#rm -f $TRUEWORKDIR/generalisation/boxesofnewdata
#for file in `ls $TRUEWORKDIR/generalisation/newbilphrases`; do
#	BOXNAME=${file%.bilphrases.gz}
#	echo "$BOXNAME" >> $TRUEWORKDIR/generalisation/boxesofnewdata
#done

cat $BOXESINDEX | cut -f 2 | sed 's/__CLOSEWORD__/_a_CLOSEWORD_a_/g' |  awk -F"__" '{print NF"|"$0}' | LC_ALL=C sort -r -n -k1,1 -t '|' | sed 's_^[^|]*|__' | sed 's/_a_CLOSEWORD_a_/__CLOSEWORD__/g' > $TRUEWORKDIR/generalisation/boxesofnewdata.sorted.bylength.tmp
rm -f $TRUEWORKDIR/generalisation/boxesofnewdata.sorted.bylength
for line in `cat $TRUEWORKDIR/generalisation/boxesofnewdata.sorted.bylength.tmp`; do
	INDEXLINE=`echo "$BOXESINDEXVAR" | grep "	$line\$" `
	echo "$INDEXLINE" | cut -f 1 >> $TRUEWORKDIR/generalisation/boxesofnewdata.sorted.bylength
done

split -l 10000 $TRUEWORKDIR/generalisation/boxesofnewdata.sorted.bylength $TRUEWORKDIR/generalisation/boxesofnewdata.sorted.bylength.split
cat $BOXESINDEX > $TRUEWORKDIR/generalisation/finalboxesindex

if [ "$FIRSTMINIMISELEMMAS" == "" ]; then

  #[ \"\`expr \$INDEX \% $NUM_PARTS\`\" == \"$CHOSEN_PART_MINUS_ONE\" ]
  for file in $TRUEWORKDIR/generalisation/boxesofnewdata.sorted.bylength.split* ; do 
	  parallel $PARALLELONLYONE -i  bash -c "INDEX=\`echo \"{}\"\`;  if [ \"\`expr \$INDEX % $NUM_PARTS\`\" == \"$CHOSEN_PART_MINUS_ONE\" ] ; then  zcat \"$TRUEWORKDIR/generalisation/newbilphrases/\$INDEX.bilphrases.gz\" | ${PYTHONHOME}python $CURDIR/generateMultipleATsFromBilphrases.py --tag_groups_file_name $CURDIR/taggroups$TAGSEQUENCESANDGROUPSSUFFIX --tag_sequences_file_name $CURDIR/tagsequences$TAGSEQUENCESANDGROUPSSUFFIX $RICHATSFLAG $RICHATSFLAGONLYGEN $POWERSETFEATURESFLAG $DIFFERENTRESTRICTIONSFLAG 2> \"$TRUEWORKDIR/generalisation/debug/debug-generalisation-\$INDEX\" | gzip > $TRUEWORKDIR/generalisation/ats/\$INDEX.ats.gz; gzip \"$TRUEWORKDIR/generalisation/debug/debug-generalisation-\$INDEX\"; fi;" -- `cat $file`
  done

else

  mkdir -p $TRUEWORKDIR/generalisation/onlylexicalats
  #Generate ATs changing lemmas
  for file in $TRUEWORKDIR/generalisation/boxesofnewdata.sorted.bylength.split* ; do 
	  parallel $PARALLELONLYONE -i  bash -c "INDEX=\`echo \"{}\"\`;  if [ \"\`expr \$INDEX % $NUM_PARTS\`\" == \"$CHOSEN_PART_MINUS_ONE\" ] ; then rm \"$TRUEWORKDIR/generalisation/debug/debug-onlylexical-\$INDEX.gz\" ; zcat \"$TRUEWORKDIR/generalisation/newbilphrases/\$INDEX.bilphrases.gz\" | ${PYTHONHOME}python $CURDIR/generateMultipleATsFromBilphrases.py --tag_groups_file_name $CURDIR/taggroups$TAGSEQUENCESANDGROUPSSUFFIX --tag_sequences_file_name $CURDIR/tagsequences$TAGSEQUENCESANDGROUPSSUFFIX --rich_ats --ref_to_biling --add_restrictions_to_every_tag --only_lexical 2> \"$TRUEWORKDIR/generalisation/debug/debug-onlylexical-\$INDEX\" | gzip > $TRUEWORKDIR/generalisation/onlylexicalats/\$INDEX.ats.gz; gzip \"$TRUEWORKDIR/generalisation/debug/debug-onlylexical-\$INDEX\"; fi;" -- `cat $file`
  done

  #Minimise
  for file in $TRUEWORKDIR/generalisation/boxesofnewdata.sorted.bylength.split* ; do 
	  parallel $PARALLELONLYONE -i  bash -c "INDEX=\`echo \"{}\"\`;  if [ \"\`expr \$INDEX % $NUM_PARTS\`\" == \"$CHOSEN_PART_MINUS_ONE\" ] ; then rm \"$TRUEWORKDIR/generalisation/debug/debug-onlylexical-\$INDEX.result.gz\" ; ${PYTHONHOME}python $CURDIR/chooseATs.py --tag_groups_file $CURDIR/taggroups$TAGSEQUENCESANDGROUPSSUFFIX --tag_sequences_file $CURDIR/tagsequences$TAGSEQUENCESANDGROUPSSUFFIX --gzip $CONTRADICTORYORRELAX --read_generalised_bilphrases_from_dir \"$TRUEWORKDIR/generalisation/newbilphrases\"  --read_generalised_ats_from_file \"$TRUEWORKDIR/generalisation/onlylexicalats/\$INDEX\" --remove_third_restriction --relax_restrictions 2> \"$TRUEWORKDIR/generalisation/debug/debug-onlylexical-\$INDEX.result\" | gzip > \"$TRUEWORKDIR/generalisation/onlylexicalats/\$INDEX.ats.result.gz\"; gzip \"$TRUEWORKDIR/generalisation/debug/debug-onlylexical-\$INDEX.result\" ; fi;" -- `cat $file`
  done

  #Generate ATs with structural changes
  for file in $TRUEWORKDIR/generalisation/boxesofnewdata.sorted.bylength.split* ; do 
	  parallel $PARALLELONLYONE -i  bash -c "INDEX=\`echo \"{}\"\`;  if [ \"\`expr \$INDEX % $NUM_PARTS\`\" == \"$CHOSEN_PART_MINUS_ONE\" ] ; then  rm \"$TRUEWORKDIR/generalisation/debug/debug-generalisation-\$INDEX.gz\"; zcat \"$TRUEWORKDIR/generalisation/newbilphrases/\$INDEX.bilphrases.gz\" | ${PYTHONHOME}python $CURDIR/generateMultipleATsFromBilphrases.py --tag_groups_file_name $CURDIR/taggroups$TAGSEQUENCESANDGROUPSSUFFIX --tag_sequences_file_name $CURDIR/tagsequences$TAGSEQUENCESANDGROUPSSUFFIX $RICHATSFLAG $RICHATSFLAGONLYGEN $POWERSETFEATURESFLAG $DIFFERENTRESTRICTIONSFLAG --ats_with_allowed_lemmas_file \"$TRUEWORKDIR/generalisation/onlylexicalats/\$INDEX.ats.result.gz\"  2> \"$TRUEWORKDIR/generalisation/debug/debug-generalisation-\$INDEX\" | gzip > $TRUEWORKDIR/generalisation/ats/\$INDEX.ats.gz; gzip \"$TRUEWORKDIR/generalisation/debug/debug-generalisation-\$INDEX\"; fi;" -- `cat $file`
  done

fi

fi
