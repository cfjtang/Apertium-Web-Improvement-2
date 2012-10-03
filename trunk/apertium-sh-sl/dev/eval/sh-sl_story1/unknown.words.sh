cat story1.sh-sl-morph.txt | sed 's|\^[^\*\^\$]*\$||g' | sed 's|\^|\n|g' | sed 's|/.*||' | sort -u
