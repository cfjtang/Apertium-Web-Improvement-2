# author: Felipe Sánchez Martínez

bin_PROGRAMS = score-chunks filter-chunks translate-detect-chunks translate-score-replace-chunks

bin_SCRIPTS = sort-uniq-chunks total-count-chunks apertium-chunks-mixer-wrapper restore_chunk_marks

EXTRA_DIST = sort-uniq-chunks.sh apertium-chunks-mixer-wrapper.sh total-count-chunks.awk restore_chunk_marks.pl

score_chunks_SOURCES = score-chunks.cc alignment.h alignment.cc marker_word.h marker_word.cc \
                       zfstream.h zfstream.cc utils.h utils.cc utf_converter.h utf_converter.cc

score_chunks_LDADD = $(Z_LIBS)

filter_chunks_SOURCES = filter-chunks.cc alignment.h alignment.cc marker_word.h marker_word.cc \
                        zfstream.h zfstream.cc utils.h utils.cc utf_converter.h utf_converter.cc

filter_chunks_LDADD = $(Z_LIBS)

translate_detect_chunks_SOURCES = translate-detect-chunks.cc alignment.h alignment.cc marker_word.h marker_word.cc \
                        sentence_translator.h sentence_translator.cc my_trie.h utf_converter.h utf_converter.cc \
                        zfstream.h zfstream.cc utils.h utils.cc language_model.h language_model.cc

translate_detect_chunks_LDADD = $(Z_LIBS)

translate_score_replace_chunks_SOURCES = translate-score-replace-chunks.cc alignment.h alignment.cc marker_word.h marker_word.cc \
                        sentence_translator.h sentence_translator.cc my_trie.h utf_converter.h utf_converter.cc\
                        zfstream.h zfstream.cc utils.h utils.cc language_model.h language_model.cc

translate_score_replace_chunks_LDADD = $(Z_LIBS)

INCLUDES = $(Z_CFLAGS)

CLEANFILES = *~ $(bin_SCRIPTS)

total-count-chunks: total-count-chunks.awk Makefile.am
	@echo "Creating $@ script"
	@echo "#!$(AWK) -f" > $@
	@cat total-count-chunks.awk >> $@
	@chmod a+x $@

sort-uniq-chunks: sort-uniq-chunks.sh Makefile.am
	@echo "Creating $@ script"
	@echo "#!$(BASH)" > $@  
	@cat sort-uniq-chunks.sh >> $@
	@chmod a+x $@

apertium-chunks-mixer-wrapper: apertium-chunks-mixer-wrapper.sh Makefile.am
	@echo "Creating $@ script"
	@echo "#!$(BASH)" > $@
	@cat apertium-chunks-mixer-wrapper.sh >> $@
	@chmod a+x $@

restore_chunk_marks: restore_chunk_marks.pl Makefile.am
	@echo "Creating $@ script"
	@echo "#!$(PERL) -w" > $@
	@cat restore_chunk_marks.pl >> $@
	@chmod a+x $@
