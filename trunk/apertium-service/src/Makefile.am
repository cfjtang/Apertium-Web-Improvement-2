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

bin_PROGRAMS = apertium-service

apertium_service_CPPFLAGS = @XMLPP_CFLAGS@ @LTTOOLBOX_CFLAGS@ @APERTIUM_CFLAGS@ @XMLRPC_CFLAGS@ @LIBTEXTCAT_CFLAGS@ @COMBINE_CFLAGS@
apertium_service_LDADD = @XMLPP_LIBS@ @LTTOOLBOX_LIBS@ @APERTIUM_LIBS@ @XMLRPC_LIBS@ @XMLRPC_LDFLAGS@ @BOOST_THREAD_LIB@ @BOOST_FILESYSTEM_LIB@ @BOOST_SYSTEM_LIB@ @BOOST_PROGRAM_OPTIONS_LIB@ @BOOST_REGEX_LIB@ @BOOST_DATE_TIME_LIB@ @LIBTEXTCAT_LIBS@ @COMBINE_LIBS@

apertium_service_SOURCES = ./utils/ConfigurationManager.cpp ./utils/Statistics.cpp ./utils/Logger.cpp ./core/vislcg3/Reading.cpp ./core/vislcg3/Strings.cpp ./core/vislcg3/FormatConverter.cpp ./core/vislcg3/SingleWindow.cpp ./core/vislcg3/GrammarWriter.cpp ./core/vislcg3/Grammar.cpp ./core/vislcg3/GrammarApplicator_runGrammar.cpp ./core/vislcg3/Rule.cpp ./core/vislcg3/GrammarApplicator_matchSet.cpp ./core/vislcg3/TextualParser.cpp ./core/vislcg3/GrammarApplicator.cpp ./core/vislcg3/icu_uoptions.cpp ./core/vislcg3/ApertiumApplicator.cpp ./core/vislcg3/uextras.cpp ./core/vislcg3/Cohort.cpp ./core/vislcg3/GrammarApplicator_runRules.cpp ./core/vislcg3/Window.cpp ./core/vislcg3/CompositeTag.cpp ./core/vislcg3/Anchor.cpp ./core/vislcg3/BinaryGrammar.cpp ./core/vislcg3/GrammarApplicator_reflow.cpp ./core/vislcg3/MatxinApplicator.cpp ./core/vislcg3/Tag.cpp ./core/vislcg3/BinaryGrammar_read.cpp ./core/vislcg3/Set.cpp ./core/vislcg3/GrammarApplicator_runContextualTest.cpp ./core/vislcg3/CohortIterator.cpp ./core/vislcg3/ContextualTest.cpp ./core/vislcg3/BinaryGrammar_write.cpp ./core/FunctionMapper.cpp ./core/Translator.cpp ./core/ResourceBroker.cpp ./core/Synthesiser.cpp ./core/Process.cpp ./core/PreTransfer.cpp ./core/ApertiumRuntimeException.cpp ./core/TextClassifier.cpp ./core/ModesManager.cpp ./interfaces/xmlrpc/ApertiumXMLRPCService.cpp ./main.cpp ./format/Encoding.cpp
