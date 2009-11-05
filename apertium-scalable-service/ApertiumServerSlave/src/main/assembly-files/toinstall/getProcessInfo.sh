#! /bin/bash
 
function get_child_processes
{
	LOCAL_CHILDREN=`cat /tmp/psinfo-$$.txt | grep -E "[ ]*[0-9]+[ ]+$1" | sed -e "s/^ //" | tr -s " " | cut -d " " -f 1`
	CHILDREN="$CHILDREN $LOCAL_CHILDREN"
	for child in $LOCAL_CHILDREN; do
		get_child_processes $child
	done

}

function get_cpu_mem_usage
{
	top -b -n 1 >/tmp/resulttop-$$.txt
	MEM_USAGE=0
	CPU_USAGE=0
	for process in $1; do
		LOCAL_CPU_USAGE=`cat /tmp/resulttop-$$.txt | grep -E "^[ ]*$process " | tr -s " " | sed -e "s/^ //" | cut -f 9 -d " "`
		LOCAL_MEM_USAGE=`cat /tmp/resulttop-$$.txt | grep -E "^[ ]*$process " | tr -s " " | sed -e "s/^ //" | cut -f 10 -d " "`
		CPU_USAGE=`echo "$CPU_USAGE + $LOCAL_CPU_USAGE" | bc -l`
		MEM_USAGE=`echo "$MEM_USAGE + $LOCAL_MEM_USAGE" | bc -l`
	done
	rm /tmp/resulttop-$$.txt
}

CHILDREN="$1"
ps -o pid,ppid ax >/tmp/psinfo-$$.txt
get_child_processes $1
rm /tmp/psinfo-$$.txt
get_cpu_mem_usage "$CHILDREN"

echo "$CPU_USAGE $MEM_USAGE"
