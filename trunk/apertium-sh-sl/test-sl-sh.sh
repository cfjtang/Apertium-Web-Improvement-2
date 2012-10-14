#! /bin/sh
USAGE="\n
A script for testing generation of individual tag groups (direction sl->sh).\n
The script works like a LHS testvoc, grepping *pattern* in the lt-expand output\n

Usage: `basename $0` [-hge] pattern \n
\n
\t-h\tthis help message\n
\t-g\tgrep out generation errors (default)\n
\t-f\tfull generation output\n
\n
Example:\n
\t `basename $0` \"<adv>\"\n
"

DOWEGREP=1

# Parse command line options.
while getopts egh OPT; do
  case "$OPT" in
    h)
      echo $USAGE
      exit 0
      ;;
    g) DOWEGREP=1
	  ;;
    e) DOWEGREP=0
	  ;;
    \?)
      # getopts issues an error message
      echo $USAGE >&2
      exit 1
      ;;
  esac
done

# Remove the switches we parsed above.
shift `expr $OPTIND - 1`

# We want at least one non-option argument. 
# Remove this block if you don't need it.
if [ $# -eq 0 ]; then
  echo $USAGE >&2
  exit 1
fi

if [ $DOWEGREP -eq 0 ]; then
lt-expand apertium-sh-sl.sl.dix | grep $1 |
	sed 's|.*:||' |
	sed -e 's|^|\^|' -e 's|$|\$|' |
	apertium-pretransfer |
	lt-proc -b sl-sh.autobil.bin | 
	apertium-transfer -b apertium-sh-sl.sl-sh.t1x sl-sh.t1x.bin |
	apertium-interchunk apertium-sh-sl.sl-sh.t2x sl-sh.t2x.bin |
	apertium-postchunk apertium-sh-sl.sl-sh.t3x sl-sh.t3x.bin |
	lt-proc -d sl-sh.autogen.bin 
else 
lt-expand apertium-sh-sl.sl.dix | grep $1 |
	sed 's|.*:||' |
	sed -e 's|^|\^|' -e 's|$|\$|' |
	apertium-pretransfer |
	lt-proc -b sl-sh.autobil.bin | 
	apertium-transfer -b apertium-sh-sl.sl-sh.t1x sl-sh.t1x.bin |
	apertium-interchunk apertium-sh-sl.sl-sh.t2x sl-sh.t2x.bin |
	apertium-postchunk apertium-sh-sl.sl-sh.t3x sl-sh.t3x.bin |
	lt-proc -d sl-sh.autogen.bin | grep '\\'
fi
