#!/opt/local/bin/python2.5

# Copyright (C) 2009 
# Author: Gabriel Synnaeve 
# License: http://www.opensource.org/licenses/PythonSoftFoundation.php (MIT)

# Usage : ./segment.py < to_be_segmented > segemented_file
# Default, write srcset markups
# Options: 
#  -t --> tstset markups
#  -r --> refset markups

import sys

if sys.argv[1] == '-t':
    ### Don't use that one but wrap-xml.perl found in moses/tools/scripts/
    sys.stdout.write('<tstset setid="test-cyen" srclang="any">\n')
    sys.stdout.write('<doc docid="test-cyen">\n')
elif sys.argv[1] == '-r':
    sys.stdout.write('<refset setid="test-cyen" srclang="any" trglang="en">\n')
    sys.stdout.write('<DOC docid="test-cyen" sysid="ref">\n')
else:
    sys.stdout.write('<srcset setid="test-cyen" srclang="any">\n')
    sys.stdout.write('<doc docid="test-cyen">\n')

i = 1

for line in sys.stdin:
    l = line.replace('\n','')
    sys.stdout.write('<seg id='+str(i)+'> '+l+' </seg>\n')
    i += 1

sys.stdout.write('</DOC>\n')
if sys.argv[1] == '-t':
    ### Don't use that one but wrap-xml.perl found in moses/tools/scripts/
    print "tstset"
    sys.stdout.write('</tstset>\n')
elif sys.argv[1] == '-r':
    print "refset"
    sys.stdout.write('</refset>\n')
else: 
    print "srcset"
    sys.stdout.write('</srcset>\n')

    
