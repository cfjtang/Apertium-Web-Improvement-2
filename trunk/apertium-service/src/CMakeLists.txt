# ########## Project setup ##########
PROJECT(ApertiumService)
CMAKE_MINIMUM_REQUIRED(VERSION 2.4.5)

# ######### General setup ##########
INCLUDE_DIRECTORIES(${CMAKE_SOURCE_DIR})

TARGET_LINK_LIBRARIES(ApertiumServiceMain ITKCommon ITKIO ITKBasicFilters)

# ########## ApertiumService executable ##########
# Sources:
SET(ApertiumService_executable_SRCS
./format/Encoding.cpp
./interfaces/xmlrpc/ApertiumXMLRPCService.cpp
./core/PreTransfer.cpp
./core/FunctionMapper.cpp
./core/ModesManager.cpp
./core/TextClassifier.cpp
./core/ResourceBroker.cpp
./core/Translator.cpp
./core/ApertiumRuntimeException.cpp
./core/cg/Rule.cpp
./core/cg/ApertiumApplicator.cpp
./core/cg/Window.cpp
./core/cg/GrammarApplicator_runContextualTest.cpp
./core/cg/uextras.cpp
./core/cg/Cohort.cpp
./core/cg/GrammarApplicator_reflow.cpp
./core/cg/CompositeTag.cpp
./core/cg/Strings.cpp
./core/cg/GrammarApplicator_matchSet.cpp
./core/cg/TextualParser.cpp
./core/cg/BinaryGrammar_write.cpp
./core/cg/GrammarApplicator_runGrammar.cpp
./core/cg/Reading.cpp
./core/cg/Grammar.cpp
./core/cg/BinaryGrammar.cpp
./core/cg/Set.cpp
./core/cg/ContextualTest.cpp
./core/cg/Tag.cpp
./core/cg/GrammarWriter.cpp
./core/cg/FormatConverter.cpp
./core/cg/Anchor.cpp
./core/cg/GrammarApplicator_runRules.cpp
./core/cg/GrammarApplicator.cpp
./core/cg/SingleWindow.cpp
./core/cg/BinaryGrammar_read.cpp
./core/Synthesiser.cpp
./utils/Statistics.cpp
./utils/ConfigurationManager.cpp
./utils/Logger.cpp
./main.cpp
)

# Headers:
SET(ApertiumService_executable_HDRS
./format/HTMLDeformat.h
./format/HTMLReformat.h
./format/TXTDeformat.h
./format/Encoding.h
./format/TXTReformat.h
./format/Base64.h
./format/Format.h
./interfaces/xmlrpc/ApertiumXMLRPCService.h
./core/Synthesiser.h
./core/FunctionMapper.h
./core/ApertiumRuntimeException.h
./core/io.h
./core/ModesManager.h
./core/PreTransfer.h
./core/ResourceBroker.h
./core/ThreadSafeQueue.h
./core/Process.h
./core/Translator.h
./core/ThreadSafeObjectPool.h
./core/cg/version.h
./core/cg/Grammar.h
./core/cg/GrammarWriter.h
./core/cg/Window.h
./core/cg/inlines.h
./core/cg/Rule.h
./core/cg/TextualParser.h
./core/cg/Reading.h
./core/cg/cycle.h
./core/cg/ApertiumApplicator.h
./core/cg/Anchor.h
./core/cg/stdafx.h
./core/cg/FormatConverter.h
./core/cg/IGrammarParser.h
./core/cg/Strings.h
./core/cg/Set.h
./core/cg/options.h
./core/cg/MatxinApplicator.h
./core/cg/macros.h
./core/cg/BinaryGrammar.h
./core/cg/GrammarApplicator.h
./core/cg/CompositeTag.h
./core/cg/CohortIterator.h
./core/cg/icu_uoptions.h
./core/cg/uextras.h
./core/cg/Cohort.h
./core/cg/ContextualTest.h
./core/cg/Tag.h
./core/cg/SingleWindow.h
./core/TextClassifier.h
./utils/ConfigurationManager.h
./utils/Statistics.h
./utils/Logger.h
)

CONFIGURE_FILE(config.h.in ${CMAKE_BINARY_DIR}/config.h)
CONFIGURE_FILE(Makefile.in ${CMAKE_BINARY_DIR}/Makefile)


# actual target:
ADD_EXECUTABLE(apertium-service ${ApertiumService_executable_SRCS})

# add install target:
INSTALL(TARGETS apertium-service DESTINATION bin)
