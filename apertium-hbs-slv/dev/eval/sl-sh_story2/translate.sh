# Generate the translation
cat ../story2_sl.txt | apertium -d ../../../ sl-sh > story2.sl-sh.txt

# Generate the analyses, chunker, interchunk and postchunk
cat ../story2_sl.txt | apertium -d ../../../ sl-sh-morph > story2.sl-sh-morph.txt
cat ../story2_sl.txt | apertium -d ../../../ sl-sh-chunker > story2.sl-sh-chunker.txt
cat ../story2_sl.txt | apertium -d ../../../ sl-sh-interchunk > story2.sl-sh-interchunk.txt
cat ../story2_sl.txt | apertium -d ../../../ sl-sh-postchunk > story2.sl-sh-postchunk.txt

# Generate the CG output
cat ../story2_sl.txt | apertium -d ../../../ sl-sh-constraints > story2.sl-sh-constraints.txt
