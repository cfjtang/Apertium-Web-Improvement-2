<?//coding: utf-8
/* Apertium Web Post Editing Tool
 * Abstract class for grammar proofing management
 *
 * Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
 * Mentors : Arnaud Vié, Luis Villarejo
 */
include("config.php");

abstract class Grammar
{
	public abstract function getGrammarCorrection($language, $format, $text,
					       $motherlanguage, $apply, $merge_colliding);
}

/* Includes the right spell checking tool, whichever $config['spellcheckingtool'] 
 * Create the $grammar object
 */
global $grammar;

switch ($config['grammarproofingtool']) {
case 'languagetool':
	include_once("grammar_LT.php");
	$grammar = new Grammar_LT($config);
	break;
case 'ATD':
	include_once("gramproof_ATD.php");
	$grammar = new Gramproof_ATD($config);
	break;
default:
	include_once("grammar_LT.php");
	$grammar = new Grammar_LT($config);
	break;
}
?>