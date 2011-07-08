<?//coding: utf-8
/*
	Apertium Web Post Editing tool
	Functions for interaction with language tools
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús

	Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
	Mentors : Arnaud Vié, Luis Villarejo
*/

include('config.php');
include_once('strings.php');
include_once('system.php');
include_once('template.php');

/* Get the translation object */
include_once('includes/translation.php');

$language_pairs_list = array();

function init_environment()
{
	//Function to initialise environment variables. For now, it sets up the list of language pairs installed for Apertium and creates the xml parser
	
	global $config, $language_pairs_list, $xml_parser, $trans;
	
	putenv('LANG=en_GB.UTF-8');

	$language_pairs_list = $trans->get_language_pairs_list();      	
}

function is_installed($language_pair)
{
	global $language_pairs_list;
	return in_array($language_pair, $language_pairs_list);
}
?>