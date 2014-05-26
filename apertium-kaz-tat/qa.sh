# Takes the basename of the test scrpt in /tests as an argument
# and runs the test.

#!/bin/bash

if [ $# -eq 0 ]
then
    testToRun=all.test
else
    testToRun=$1.test
fi

bash "tests/$testToRun"
