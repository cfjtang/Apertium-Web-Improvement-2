INCLUDES = @XMLPP_CFLAGS@ @LTTOOLBOX_CFLAGS@ @APERTIUM_CFLAGS@ @LIBIQXMLRPC_CFLAGS@ @LIBTEXTCAT_CFLAGS@
LDADD = @XMLPP_LIBS@ @LTTOOLBOX_LIBS@ @APERTIUM_LIBS@ @LIBIQXMLRPC_LIBS@ @BOOST_THREAD_LIB@ @BOOST_FILESYSTEM_LIB@ \
@BOOST_PROGRAM_OPTIONS_LIB@ @BOOST_REGEX_LIB@ @BOOST_DATE_TIME_LIB@ @LIBTEXTCAT_LIBS@

h_sources = ./utils/AuthenticationManager.h ./ApertiumRuntimeException.h ./ApertiumDetect.h ./format/Deformat.h ./format/Encoding.h \
./format/Base64.h ./format/Reformat.h ./ApertiumAuthPlugin.h ./ApertiumLanguagePairs.h ./core/FunctionMapper.h \
./core/PreTransfer.h ./core/ThreadSafeQueue.h ./core/ObjectBroker.h ./core/ThreadSafeObjectPool.h ./core/ModesManager.h \
./core/TextClassifier.h ./core/Translator.h ./ApertiumLogInterceptor.h ./ApertiumServer.h \
./ApertiumTranslate.h ./core/cg/version.h ./core/cg/Grammar.h ./core/cg/GrammarWriter.h ./core/cg/Window.h ./core/cg/inlines.h ./core/cg/Rule.h \
./core/cg/TextualParser.h ./core/cg/Reading.h ./core/cg/cycle.h ./core/cg/ApertiumApplicator.h ./core/cg/Anchor.h ./core/cg/stdafx.h ./core/cg/IGrammarParser.h \
./core/cg/Recycler.h ./core/cg/Strings.h ./core/cg/Set.h ./core/cg/options.h ./core/cg/macros.h ./core/cg/BinaryGrammar.h ./core/cg/GrammarApplicator.h \
./core/cg/CompositeTag.h ./core/cg/icu_uoptions.h ./core/cg/uextras.h ./core/cg/Cohort.h ./core/cg/ContextualTest.h ./core/cg/Tag.h ./core/cg/SingleWindow.h \
./utils/ConfigurationManager.h ./utils/Logger.h ./utils/Statistics.h

cpp_sources = ./ApertiumServer.cpp ./format/Encoding.cpp ./ApertiumLanguagePairs.cpp ./ApertiumDetect.cpp \
./core/PreTransfer.cpp ./core/ModesManager.cpp ./core/FunctionMapper.cpp ./core/ObjectBroker.cpp ./core/TextClassifier.cpp \
./core/Translator.cpp ./ApertiumLogInterceptor.cpp ./ApertiumAuthPlugin.cpp ./ApertiumTranslate.cpp \
./ApertiumRuntimeException.cpp ./core/cg/Rule.cpp ./core/cg/ApertiumApplicator.cpp ./core/cg/Recycler.cpp ./core/cg/Window.cpp \
./core/cg/GrammarApplicator_runContextualTest.cpp ./core/cg/uextras.cpp ./core/cg/Cohort.cpp ./core/cg/GrammarApplicator_reflow.cpp \
./core/cg/CompositeTag.cpp ./core/cg/Strings.cpp ./core/cg/GrammarApplicator_matchSet.cpp ./core/cg/TextualParser.cpp ./core/cg/BinaryGrammar_write.cpp \
./core/cg/GrammarApplicator_runGrammar.cpp ./core/cg/Reading.cpp ./core/cg/Grammar.cpp ./core/cg/BinaryGrammar.cpp ./core/cg/Set.cpp ./core/cg/ContextualTest.cpp \
./core/cg/Tag.cpp ./core/cg/icu_uoptions.cpp ./core/cg/GrammarWriter.cpp ./core/cg/Anchor.cpp ./core/cg/GrammarApplicator_runRules.cpp \
./core/cg/GrammarApplicator.cpp ./core/cg/SingleWindow.cpp ./core/cg/BinaryGrammar_read.cpp ./utils/AuthenticationManager.cpp \
./utils/ConfigurationManager.cpp ./utils/Logger.cpp ./utils/Statistics.cpp ./main.cpp

bin_PROGRAMS = apertium_service

apertium_service_SOURCES = $(cpp_sources)

EXTRA_DIST = $(h_sources)
