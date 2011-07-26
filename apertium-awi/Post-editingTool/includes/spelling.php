<?//coding: utf-8
/* Apertium Web Post Editing Tool
 * Abstract class for spell cheking management
 *
 * Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
 * Mentors : Arnaud Vié, Luis Villarejo
 */
include("config.php");

abstract class Spelling
{
	public abstract function getSpellCorrection($language, $text, $apply, $additional_argument = '');
}

/* Includes the right spell checking tool, whichever $config['spellcheckingtool'] 
 * Create the $spell object
 */
global $spell;
switch ($config['spellcheckingtool']) {
case 'aspell':
	include_once("spelling_aspell.php");
	$spell = new Spelling_Aspell($config);
	break;
case 'ATD':
	include_once("gramproof_ATD.php");
	$spell = new Gramproof_ATD($config);
	break;
default:
	include_once("spelling_aspell.php");
	$spell = new Spelling_Aspell($config);
	break;
}

?>