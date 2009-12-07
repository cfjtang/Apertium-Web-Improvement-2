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
 * The class Translate is used to execute the sequence of step required by
 * a translation task by using the informations contained in Modes files.
 */

#include "config.h"

#if defined(HAVE_IRSTLM)

#include <iostream>
#include <vector>
#include <string>

#include "Synthesiser.h"
#include "format/Encoding.h"

#include "memt/alignment.hh"
#include "memt/hypotheses.hh"
#include "memt/irstlm_ranker.hh"
#include "memt/max_conseq_aligner.hh"
#include "memt/parallel_scan_generator.hh"
#include "memt/case_insensitive_morph_matcher.hh"

std::string Synthesiser::synthesise(ResourceBroker &rb, ConfigurationManager &cm, std::vector<std::string> &translations, std::string srcLang, std::string destLang) {

	std::vector<std::wstring> input_lines(translations.size());

	for (unsigned int i = 0; i < translations.size(); ++i) {
		input_lines[i] = Encoding::utf8ToWstring(translations[i]);
	}

	std::vector<std::string> lm(1);
	lm[0] = cm.getLanguageModels()[srcLang];

	std::vector<std::string> mm(1);
	mm[0] = cm.getMonolingualDictionaries()[std::pair<std::string, std::string>(srcLang, destLang)];

	Program plm("irstlm");
	plm.setFileNames(lm);

	Program pmm("matcher");
	pmm.setFileNames(mm);

	IRSTLMRankerWrapper *i = rb.IRSTLMRankerPool.acquire(plm);

    Case_Insensitive_Morph_Matcher *matcher = rb.Case_Insensitive_Morph_MatcherPool.acquire(pmm);

    IRSTLMRanker *r = i->getRanker();

	Alignment alignment = Alignment(input_lines);

    alignment.match(*matcher);

    Max_Conseq_Aligner aligner;
    alignment.align(aligner, 0);

    Parallel_Scan_Generator generator;
    Hypotheses hypotheses = Hypotheses(alignment, generator);

    hypotheses.rank(r);

    std::wstring wret = hypotheses.best();

    rb.Case_Insensitive_Morph_MatcherPool.release(matcher, pmm);

	rb.IRSTLMRankerPool.release(i, plm);

	return Encoding::wstringToUtf8(wret);
}

#endif
