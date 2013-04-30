#
# The script translates the coding challenge stories and pipes out the debug info
#
if [ ! -d dev/debug-sl-sh ]; then
mkdir -p dev/debug-sl-sh
fi
if [ ! -d dev/debug-sh-sl ]; then	
mkdir -p dev/debug-sh-sl
fi
# Do the sl-sh translation (currently to sh_HR)
cat dev/sl_original.txt | apertium -d . sl-sh_HR > dev/sh_HR.translated.txt
cat dev/sl_original.txt | apertium -d . sl-sh-morph > dev/debug-sl-sh/sl-sh.morph.txt
cat dev/sl_original.txt | apertium -d . sl-sh-chunker > dev/debug-sl-sh/sl-sh.chunker.txt
cat dev/sl_original.txt | apertium -d . sl-sh-postchunk > dev/debug-sl-sh/sl-sh.postchunk.txt
cat dev/sl_original.txt | apertium -d . sl-sh-interchunk > dev/debug-sl-sh/sl-sh.interchunk.txt
cat dev/sl_original.txt | apertium -d . sl-sh-tagger > dev/debug-sl-sh/sl-sh.tagger.txt

# Do the sh-sl translation
cat dev/sh_original.txt | apertium -d . sh-sl > dev/sl.translated.txt
cat dev/sh_original.txt | apertium -d . sh-sl-morph > dev/debug-sh-sl/sh-sl.morph.txt
cat dev/sh_original.txt | apertium -d . sh-sl-constraints > dev/debug-sh-sl/sh-sl.constraints.txt
cat dev/sh_original.txt | apertium -d . sh-sl-chunker > dev/debug-sh-sl/sh-sl.chunker.txt
cat dev/sh_original.txt | apertium -d . sh-sl-interchunk > dev/debug-sh-sl/sh-sl.interchunk.txt
cat dev/sh_original.txt | apertium -d . sh-sl-postchunk > dev/debug-sh-sl/sh-sl.postchunk.txt
cat dev/sh_original.txt | apertium -d . sh-sl-debug > dev/debug-sh-sl/sh-sl.debug.txt
cat dev/sh_original.txt | apertium -d . sh-sl-lex > dev/debug-sh-sl/sh-sl.lex.txt
#cat dev/sh_original.txt | apertium -d . sl-sh-tagger > dev/debug-sh-sl/sh-sl.tagger.txt
