<?//coding: utf-8
/* Apertium Web Post Editing Tool
 * Functions for Spell and Grammar Checking Module
 *
 * Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
 * Mentors : Luis Villarejo, Mireia Farrús
 *
 * Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
 * Mentors : Arnaud Vié, Luis Villarejo
 */


/*	In both proofing and spell checking, mistakes in the text are represented as an array
	$mistakes = array(
		0 => array('text' => original text, 'start' => mistake pos begin, 'end' => mistake pos end, 'desc' => mistake desc, 'sugg' => array of mistake suggestions),
		1 => array(...))
*/

include('config.php');
/* Create the object $spell */
include_once("spelling.php");
/* Create the object $grammar */
include_once("grammar.php");

/*--------------------------------------
General functions
--------------------------------------*/

function checkForMistakes($input_text, $language, $motherlanguage='')
{
	//generate the correction fields for a given text
	global $extern, $spell, $grammar;
	
	$text = $input_text;	

	/* Security issues */
	if (!ctype_alpha($language) OR !ctype_alpha($motherlanguage))
		return false;

	//run grammar proofing
	$correction_result = $grammar->getGrammarCorrection($language, 'html', $text, $motherlanguage, false, true);
	
	if(!empty($correction_result))
	{
		//$offset will remember the new position as replacements are done
		$offset = 0;
		foreach($correction_result as $mistake)
		{
			$replacement_text = generateCorrectionField($mistake['text'], $mistake['sugg'], $mistake['desc'], 'grammar');
			$text = utf8_substr_replace($text, $replacement_text, $mistake['start']+$offset, $mistake['end'] - $mistake['start'] + 1);
			$offset += utf8_strlen($replacement_text) - utf8_strlen($mistake['text']);
		}
	}
	
	//run spell checking (sgml filter activated)
	$spellchecking_result = $spell->getSpellCorrection($language, $text, false, '--add-filter=sgml');
	
	if(!empty($spellchecking_result))
	{
		//$offset will remember the new position as replacements are done
		$offset = 0;
		foreach($spellchecking_result as $mistake)
		{
			$replacement_text = generateCorrectionField($mistake['text'], $mistake['sugg'], $mistake['desc'], 'spelling');
			$text = utf8_substr_replace($text, $replacement_text, $mistake['start']+$offset, $mistake['end'] - $mistake['start'] + 1);
			$offset += utf8_strlen($replacement_text) - utf8_strlen($mistake['text']);
		}
	}
	
	return $text;
}

/*--------------------------------------
Template
--------------------------------------*/

function generateCorrectionField($original, $suggestions, $message, $type)
{
	//generate the correction field with all suggestions for a given mistake.
	//$type is either "grammar" or "spelling" depending on the mistake
	$output = '<span class="' . $type . '_mistake" title="' . escape_attribute($message, false) . '" data-suggestions="' . escape_attribute(implode('#', $suggestions), false) . '" >' . $original . '</span>';
	
	return str_replace("\n", ' ', $output);
}

?>