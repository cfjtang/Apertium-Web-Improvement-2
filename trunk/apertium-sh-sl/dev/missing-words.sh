# The script extracts words which aren't in the bidix and sorts them by frequency (however small the story)

# The missing words
cat sl.translated.txt | grep "@\w* " -o | tr '[:upper:]' '[:lower:]'| sort | uniq -c | sort -r > missing.sh-sl.tmp
cat sh_HR.translated.txt | grep "@\w* " -o | tr '[:upper:]' '[:lower:]'| sort | uniq -c | sort -r > missing.sl-sh.tmp


# The tag mismatching words
cat sl.translated.txt | grep "#\w* " -o | tr '[:upper:]' '[:lower:]'| sort | uniq -c | sort -r > mismatch.sh-sl.tmp
cat sh_HR.translated.txt | grep "#\w* " -o | tr '[:upper:]' '[:lower:]'| sort | uniq -c | sort -r > mismatch.sl-sh.tmp

# The midterm story
cat story_Pavao1_HR_original.txt | lt-proc ../sh-sl.automorf.bin | grep '*\w*\$' -o | sed 's|\$||g' | sort | uniq -c | sort -r > missing.pavao1.tmp
cat story_Pavao2_HR_original.txt | lt-proc ../sh-sl.automorf.bin | grep '*\w*\$' -o | sed 's|\$||g' | sort | uniq -c | sort -r > missing.pavao2.tmp