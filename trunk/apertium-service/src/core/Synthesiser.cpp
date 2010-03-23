/**
 * @file
 * @author  Pasquale Minervini <p.minervini@gmail.com>
 * @version 1.0
 *
 * @section LICENSE
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @section DESCRIPTION
 *
 * The class Synthesiser is used to execute the Multi-Engine Translation Synthesiser.
 */

#include "config.h"

#if defined(HAVE_COMBINE)

#include <iostream>
#include <vector>
#include <string>

#include "Synthesiser.h"
#include "format/Encoding.h"

#include <apertium-combine/alignment.hh>
#include <apertium-combine/hypotheses.hh>
#include <apertium-combine/irstlm_ranker.hh>
#include <apertium-combine/max_conseq_aligner.hh>
#include <apertium-combine/parallel_scan_generator.hh>
#include <apertium-combine/case_insensitive_morph_matcher.hh>

std::string Synthesiser::synthesise(ResourceBroker &rb, std::vector<std::string> &translations, std::string srcLang, std::string destLang) {
	std::string lm, mm;

	ConfigurationManager::LanguageModelsType languageModels = configurationManager->getLanguageModels();
	ConfigurationManager::LanguageModelsType::iterator itlm = languageModels.find(destLang);

	if (itlm != languageModels.end()) {
		lm = itlm->second;
	} else {
		throw ApertiumRuntimeException("Invalid parameter: no language models for the language \"" + destLang + "\"");
	}

	ConfigurationManager::MonolingualDictionariesType monolingualDictionaries =	configurationManager->getMonolingualDictionaries();

	std::pair<std::string, std::string> monodixpair = std::pair<std::string, std::string>(srcLang, destLang);
	ConfigurationManager::MonolingualDictionariesType::iterator itmm = monolingualDictionaries.find(monodixpair);

	if (itmm != monolingualDictionaries.end()) {
		mm = itmm->second;
	} else {
		throw ApertiumRuntimeException("Invalid parameter: no monolingual dictionaries for the language pair \"" + srcLang + "-" + destLang + "\"");
	}

	std::vector<std::wstring> input_lines(translations.size());

	for (unsigned int i = 0; i < translations.size(); ++i) {
		input_lines[i] = Encoding::utf8ToWstring(translations[i]);
	}

	std::vector<std::string> vlm(1);
	vlm[0] = lm;

	std::vector<std::string> vmm(1);
	vmm[0] = mm;

	Program plm("irstlm");
	plm.setFileNames(vlm);

	Program pmm("matcher");
	pmm.setFileNames(vmm);

	IRSTLMRanker *r = rb.IRSTLMRankerPool.acquire(plm);

	Case_Insensitive_Morph_Matcher *matcher = rb.Case_Insensitive_Morph_MatcherPool.acquire(pmm);

	Alignment alignment = Alignment(input_lines);

	alignment.match(*matcher);

	Max_Conseq_Aligner aligner;
	alignment.align(aligner, 0);

	Parallel_Scan_Generator generator;
	Hypotheses hypotheses = Hypotheses(alignment, generator);

	hypotheses.rank(r);

	std::wstring wret = hypotheses.best();

	rb.Case_Insensitive_Morph_MatcherPool.release(matcher, pmm);

	rb.IRSTLMRankerPool.release(r, plm);

	return Encoding::wstringToUtf8(wret);
}

void Synthesiser::eagerlyLoadMonodix(ResourceBroker &rb, std::string srcLang, std::string destLang, unsigned int qty) {
	std::string mm;

	ConfigurationManager::MonolingualDictionariesType monolingualDictionaries =	configurationManager->getMonolingualDictionaries();

	std::pair<std::string, std::string> monodixpair = std::pair<std::string, std::string>(srcLang, destLang);
	ConfigurationManager::MonolingualDictionariesType::iterator itmm = monolingualDictionaries.find(monodixpair);

	if (itmm != monolingualDictionaries.end()) {
		mm = itmm->second;
	} else {
		throw ApertiumRuntimeException("Invalid parameter: no monolingual dictionaries for the language pair \"" + srcLang + "-" + destLang + "\"");
	}

	std::vector<std::string> vmm(1);
	vmm[0] = mm;

	Program pmm("matcher");
	pmm.setFileNames(vmm);

	std::stack<ICase_Insensitive_Morph_Matcher *> mem;

	for (unsigned int i = 0; i < qty; ++i) {
		Case_Insensitive_Morph_Matcher *i = rb.Case_Insensitive_Morph_MatcherPool.acquire(p);
		mem.push(i);
	}

	while (!mem.empty()) {
		Case_Insensitive_Morph_Matcher *i = mem.top();
		rb.Case_Insensitive_Morph_MatcherPool.release(i, p);
		mem.pop();
	}
}

void Synthesiser::eagerlyLoadLanguageModel(ResourceBroker &rb, std::string lang, unsigned int qty) {
	std::string lm;

	ConfigurationManager::LanguageModelsType languageModels = configurationManager->getLanguageModels();
	ConfigurationManager::LanguageModelsType::iterator itlm = languageModels.find(lang);

	if (itlm != languageModels.end()) {
		lm = itlm->second;
	} else {
		throw ApertiumRuntimeException("Invalid parameter: no language models for the language \"" + lang + "\"");
	}

	std::vector<std::string> vlm(1);
	vlm[0] = lm;

	Program plm("irstlm");
	plm.setFileNames(vlm);

	std::stack<IRSTLMRanker *> mem;

	for (unsigned int i = 0; i < qty; ++i) {
		IRSTLMRanker *i = rb.IRSTLMRankerPool.acquire(p);
		mem.push(i);
	}

	while (!mem.empty()) {
		IRSTLMRanker *i = mem.top();
		rb.IRSTLMRankerPool.release(i, p);
		mem.pop();
	}
}

#endif /* defined(HAVE_COMBINE) */
