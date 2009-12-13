#
# @file
# @author  Pasquale Minervini <p.minervini@gmail.com>
# @version 1.0
#
# @section LICENSE
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

INCLUDES = @XMLPP_CFLAGS@ @LTTOOLBOX_CFLAGS@ @APERTIUM_CFLAGS@ @XMLRPC_CFLAGS@ @LIBTEXTCAT_CFLAGS@
LDADD = @XMLPP_LIBS@ @LTTOOLBOX_LIBS@ @APERTIUM_LIBS@ @XMLRPC_LIBS@ @XMLRPC_LDFLAGS@ @BOOST_THREAD_LIB@ @BOOST_FILESYSTEM_LIB@ @BOOST_PROGRAM_OPTIONS_LIB@ @BOOST_REGEX_LIB@ @BOOST_DATE_TIME_LIB@ @LIBTEXTCAT_LIBS@

# cpp_sources = ./format/Encoding.cpp ./interfaces/xmlrpc/ApertiumXMLRPCService.cpp ./core/PreTransfer.cpp ./core/FunctionMapper.cpp ./core/ModesManager.cpp ./core/TextClassifier.cpp ./core/ResourceBroker.cpp ./core/Translator.cpp ./core/ApertiumRuntimeException.cpp ./core/cg/Rule.cpp ./core/cg/ApertiumApplicator.cpp ./core/cg/Window.cpp ./core/cg/GrammarApplicator_runContextualTest.cpp ./core/cg/uextras.cpp ./core/cg/Cohort.cpp ./core/cg/GrammarApplicator_reflow.cpp ./core/cg/CompositeTag.cpp ./core/cg/Strings.cpp ./core/cg/GrammarApplicator_matchSet.cpp ./core/cg/TextualParser.cpp ./core/cg/BinaryGrammar_write.cpp ./core/cg/GrammarApplicator_runGrammar.cpp ./core/cg/Reading.cpp ./core/cg/Grammar.cpp ./core/cg/BinaryGrammar.cpp ./core/cg/Set.cpp ./core/cg/ContextualTest.cpp ./core/cg/Tag.cpp ./core/cg/icu_uoptions.cpp ./core/cg/GrammarWriter.cpp ./core/cg/Anchor.cpp ./core/cg/GrammarApplicator_runRules.cpp ./core/cg/GrammarApplicator.cpp ./core/cg/SingleWindow.cpp ./core/cg/BinaryGrammar_read.cpp ./utils/Statistics.cpp ./utils/ConfigurationManager.cpp ./utils/Logger.cpp ./main.cpp
# cpp_sources = ./format/Encoding.cpp ./interfaces/xmlrpc/ApertiumXMLRPCService.cpp ./core/PreTransfer.cpp ./core/FunctionMapper.cpp ./core/ModesManager.cpp ./core/TextClassifier.cpp ./core/ResourceBroker.cpp ./core/Translator.cpp ./core/ApertiumRuntimeException.cpp ./core/cg/Rule.cpp ./core/cg/ApertiumApplicator.cpp ./core/cg/Window.cpp ./core/cg/GrammarApplicator_runContextualTest.cpp ./core/cg/uextras.cpp ./core/cg/Cohort.cpp ./core/cg/GrammarApplicator_reflow.cpp ./core/cg/CompositeTag.cpp ./core/cg/Strings.cpp ./core/cg/GrammarApplicator_matchSet.cpp ./core/cg/TextualParser.cpp ./core/cg/BinaryGrammar_write.cpp ./core/cg/GrammarApplicator_runGrammar.cpp ./core/cg/Reading.cpp ./core/cg/Grammar.cpp ./core/cg/BinaryGrammar.cpp ./core/cg/Set.cpp ./core/cg/ContextualTest.cpp ./core/cg/Tag.cpp ./core/cg/GrammarWriter.cpp ./core/cg/FormatConverter.cpp ./core/cg/Anchor.cpp ./core/cg/GrammarApplicator_runRules.cpp ./core/cg/GrammarApplicator.cpp ./core/cg/SingleWindow.cpp ./core/cg/BinaryGrammar_read.cpp ./utils/Statistics.cpp ./utils/ConfigurationManager.cpp ./utils/Logger.cpp ./main.cpp

cpp_sources = ./format/Encoding.cpp ./interfaces/xmlrpc/ApertiumXMLRPCService.cpp ./core/PreTransfer.cpp ./core/FunctionMapper.cpp ./core/ModesManager.cpp ./core/TextClassifier.cpp ./core/ResourceBroker.cpp ./core/Translator.cpp ./core/ApertiumRuntimeException.cpp ./core/cg/Rule.cpp ./core/cg/ApertiumApplicator.cpp ./core/cg/Window.cpp ./core/cg/GrammarApplicator_runContextualTest.cpp ./core/cg/uextras.cpp ./core/cg/Cohort.cpp ./core/cg/GrammarApplicator_reflow.cpp ./core/cg/CompositeTag.cpp ./core/cg/Strings.cpp ./core/cg/GrammarApplicator_matchSet.cpp ./core/cg/TextualParser.cpp ./core/cg/BinaryGrammar_write.cpp ./core/cg/GrammarApplicator_runGrammar.cpp ./core/cg/Reading.cpp ./core/cg/Grammar.cpp ./core/cg/BinaryGrammar.cpp ./core/cg/Set.cpp ./core/cg/ContextualTest.cpp ./core/cg/Tag.cpp ./core/cg/GrammarWriter.cpp ./core/cg/FormatConverter.cpp ./core/cg/Anchor.cpp ./core/cg/GrammarApplicator_runRules.cpp ./core/cg/GrammarApplicator.cpp ./core/cg/SingleWindow.cpp ./core/cg/BinaryGrammar_read.cpp ./core/Synthesiser.cpp ./utils/Statistics.cpp ./utils/ConfigurationManager.cpp ./utils/Logger.cpp ./main.cpp

bin_PROGRAMS = apertium-service

apertium_service_SOURCES = $(cpp_sources)
