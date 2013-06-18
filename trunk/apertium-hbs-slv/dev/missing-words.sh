# The script extracts words which aren't in the bidix and sorts them by frequency (however small the story)

# The missing words
cat slv.translated.txt | grep "@\w* " -o | tr '[:upper:]' '[:lower:]'| sort | uniq -c | sort -r > missing.hbs-slv.tmp
cat hbs_HR.translated.txt | grep "@\w* " -o | tr '[:upper:]' '[:lower:]'| sort | uniq -c | sort -r > missing.slv-hbs.tmp


# The tag mismatching words
cat slv.translated.txt | grep "#\w* " -o | tr '[:upper:]' '[:lower:]'| sort | uniq -c | sort -r > mismatch.hbs-slv.tmp
cat hbs_HR.translated.txt | grep "#\w* " -o | tr '[:upper:]' '[:lower:]'| sort | uniq -c | sort -r > mismatch.slv-hbs.tmp

# The midterm story
cat story_Pavao1_HR_original.txt | lt-proc ../hbs-slv.automorf.bin | grep '*\w*\$' -o | sed 's|\$||g' | sort | uniq -c | sort -r > missing.pavao1.tmp
cat story_Pavao2_HR_original.txt | lt-proc ../hbs-slv.automorf.bin | grep '*\w*\$' -o | sed 's|\$||g' | sort | uniq -c | sort -r > missing.pavao2.tmp
