<?//coding: utf-8
/*
	Apertium Web Post Editing tool
	Ajax Interface
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús

	Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
	Mentors : Arnaud Vié, Luis Villarejo

*/

include_once('includes/language.php');
include_once('includes/strings.php');

include_once('includes/gramproof.php');

putenv('LANG=en_GB.UTF-8');

$data = escape($_POST);

if(isset($data['language_pair']))
{
	$source_language = explode('-', $data['language_pair']);
	$target_language = $source_language[1];
	$source_language = $source_language[0];
	
	if(!(ctype_alpha($source_language) AND ctype_alpha($target_language)))
	{
		die("Incorrect languages");
	}
}
else
{
	die("No language given");
}

//Define all variables, for strictness...
$variables_name = array('text_input', 'text_output', 'pretrans_src', 'pretrans_dst', 'pretrans_case', 'posttrans_src', 'posttrans_dst', 'posttrans_case');

foreach ($variables_name as $name) {
	if(!isset($data[$name]))
		$data[$name] = null;
}

switch($data['action_request'])
{
	case 'proof_input' :
		echo nl2br_r(checkForMistakes($data['text_input'], $source_language));
		break;
	
	case 'proof_output' :
		echo nl2br_r(checkForMistakes($data['text_output'], $target_language, $source_language));
		break;
	
	case 'translate' :
		echo nl2br_r(getApertiumTranslation($source_language, $target_language, 'html', $data['text_input'], $data['pretrans_src'], $data['pretrans_dst']));
		break;
	
	case 'replace_input' :
		echo nl2br_r(str_replace_words($data['pretrans_src'], $data['pretrans_dst'], $data['pretrans_case'], $data['text_input']));
		break;
	
	case 'replace_output' :
		echo nl2br_r(str_replace_words($data['posttrans_src'], $data['posttrans_dst'], $data['posttrans_case'], $data['text_output']));
		break;
}

?>