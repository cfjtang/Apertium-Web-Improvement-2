#/bin/bash

RULES=$1
REDIR=$2

if [ $# -lt 2 ]; then
	echo 'show-rule-ranking.sh <rule file> <result dir>';
	exit;
fi

if [[ ! -e $RULES ]]; then
	echo 'File '$RULES' not found';
	exit;
fi
if [[ ! -d $REDIR ]]; then
	echo 'Directory '$REDIR' not found';
	exit;
fi

len=`cat $RULES | wc -l`; 
for i in `seq 1 $len`; do 
	if [ -e $REDIR/r$i.ranked ]; then
		rule=`cat $RULES | head -n $i | tail -1`;
		echo -e `python calc_baseline_diff.py $REDIR/r0.ranked $REDIR/r$i.ranked`"\t"$rule;
	fi
done | sed 's/0\./0/g' | sort -gr | sed 's/0/0./1'
