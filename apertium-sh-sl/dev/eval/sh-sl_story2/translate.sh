# Generate the translation
cat ../story2_hr.txt | apertium -d ../../../ sh-sl > story2.sh-sl.txt

# Generate the analyses, chunker, interchunk and postchunk
cat ../story2_hr.txt | apertium -d ../../../ sh-sl-morph > story2.sh-sl-morph.txt
cat ../story2_hr.txt | apertium -d ../../../ sh-sl-chunker > story2.sh-sl-chunker.txt
cat ../story2_hr.txt | apertium -d ../../../ sh-sl-interchunk > story2.sh-sl-interchunk.txt
cat ../story2_hr.txt | apertium -d ../../../ sh-sl-postchunk > story2.sh-sl-postchunk.txt

# Generate the CG output
cat ../story2_hr.txt | apertium -d ../../../ sh-sl-constraints > story2.sh-sl-constraints.txt
