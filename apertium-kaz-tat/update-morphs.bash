#!/bin/bash

# Assuming that you have the whole apertium tree in your source dir and you are in kaz-tat directory.

# You have to compile apertium-kaz and apertium-tat first.

cp ../../languages/apertium-kaz/kaz.automorf.att.gz apertium-kaz-tat.kaz-tat.LR.att.gz
cp ../../languages/apertium-kaz/kaz.autogen.att.gz apertium-kaz-tat.tat-kaz.RL.att.gz
cp ../../languages/apertium-tat/tat.automorf.att.gz apertium-kaz-tat.tat-kaz.LR.att.gz
cp ../../languages/apertium-tat/tat.autogen.att.gz apertium-kaz-tat.kaz-tat.RL.att.gz

DIFF=$(diff ../../languages/apertium-kaz/apertium-kaz.kaz.rlx apertium-kaz-tat.kaz-tat.rlx)
if [ "$DIFF" != "" ]; then
        cp ../../languages/apertium-kaz/apertium-kaz.kaz.rlx apertium-kaz-tat.kaz-tat.rlx
fi;

DIFF=$(diff ../../languages/apertium-tat/apertium-tat.tat.rlx apertium-kaz-tat.tat-kaz.rlx)
if [ "$DIFF" != "" ]; then
        cp ../../languages/apertium-tat/apertium-tat.tat.rlx apertium-kaz-tat.tat-kaz.rlx
fi;

exit 0


