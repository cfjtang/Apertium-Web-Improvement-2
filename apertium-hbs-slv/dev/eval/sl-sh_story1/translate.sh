# Generate the translation
cat ../story1_sl.txt | apertium -d ../../../ sl-sh > story1.sl-sh.txt

# Generate the analyses, chunker, interchunk and postchunk
cat ../story1_sl.txt | apertium -d ../../../ sl-sh-morph > story1.sl-sh-morph.txt
cat ../story1_sl.txt | apertium -d ../../../ sl-sh-chunker > story1.sl-sh-chunker.txt
cat ../story1_sl.txt | apertium -d ../../../ sl-sh-interchunk > story1.sl-sh-interchunk.txt
cat ../story1_sl.txt | apertium -d ../../../ sl-sh-postchunk > story1.sl-sh-postchunk.txt

# Generate the CG output
cat ../story1_sl.txt | apertium -d ../../../ sl-sh-constraints > story1.sl-sh-constraints.txt