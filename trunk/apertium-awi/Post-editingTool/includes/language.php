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
$trans = new translate($config, 'en', 'es', 'html');

$language_pairs_list = array();

function init_environment()
{
	//Function to initialise environment variables. For now, it sets up the list of language pairs installed for Apertium and creates the xml parser
	
	global $config, $language_pairs_list, $xml_parser;
	
	putenv('LANG=en_GB.UTF-8');

	executeCommand($config['apertium_command']." fr-fr", "", $cmd_return, $return_status);
	
	$cmd_return = explode("\n", $cmd_return);
	foreach($cmd_return as $line)
	{
		$matches = array();
		if(preg_match("#^\s*([a-z]+-[a-z]+)\s*$#", $line, $matches))
		{
			$language_pairs_list[] = $matches[1];
		}
	}
}

function is_installed($language_pair)
{
	global $language_pairs_list;
	return in_array($language_pair, $language_pairs_list);
}