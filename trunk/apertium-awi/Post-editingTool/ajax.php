<?//coding: utf-8
/*
  Apertium Web Post Editing tool
  Ajax Interface
	
  Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
  Mentors : Luis Villarejo, Mireia Farrús

  Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
  Mentors : Arnaud Vié, Luis Villarejo

*/

include('includes/language.php');
include_once('includes/strings.php');
include_once('modules.php');

putenv('LANG=en_GB.UTF-8');

$data = escape($_POST);

if(isset($data['language_pair'])) {
	$source_language = explode('-', $data['language_pair']);
	$target_language = $source_language[1];
	$source_language = $source_language[0];
	$trans->set_source_language($source_language);
	$trans->set_target_language($target_language);
	
	if(!(ctype_alpha($source_language) AND ctype_alpha($target_language)))
		die("Incorrect languages");
}
else
	die("No language given");

//Define all variables, for strictness...
$variables_name = array('text_input', 'text_output', 'pretrans_src', 'pretrans_dst', 'pretrans_case', 'posttrans_src', 'posttrans_dst', 'posttrans_case', 'inputTMX');

foreach ($variables_name as $name) {
	if(!isset($data[$name]))
		$data[$name] = null;
}

function check_entry($text)
{
	/* Run test on $text :
	 * Test 1: just 'br', 'span' and 'hr' tags are allowed
	 * Test 2: span tag have to be the form defined in includes/gramproof.php
	 * Test 3: hr tag have to be the form defined in includes/format.php
	 *
	 * Return false if a test fail
	 */
	
	
	preg_match_all('#<(.*?)>#',strtolower($text),$matches);
	$safe_tags = array('/span', 'br', 'br/');
	foreach ($matches[1] as $tag) {
		if (in_array($tag, $safe_tags))
			continue;
		elseif (substr($tag, 0, 4) == 'span') {
			/* Test2 */
			preg_match_all('#span class\=\"[^\"]*\" title\=\"[^\"]*\" data-suggestions\=\"[^\"]*\" #s', $tag, $matche);
			if ($matche[0][0] != $tag)
				return false;
		}
		elseif (substr($tag, 0, 2) == 'hr') {
			/* Test3 */
			preg_match_all('#hr data-format="[^\"]*" contenteditable="false"#s', $tag, $matche);
			if ($matche[0][0] != $tag)
				return false;
		}
		else
			/* Test1 */
			return false;
	}

	return true;

}

if (isset($data['inputTMX_content']))
	$trans->set_inputTMX(base64_decode($data['inputTMX_content']));
else
	$trans->set_inputTMX('');

switch($data['action_request'])
{
case 'proof_input' :
	if(check_entry($data['text_input']))
		echo nl2br_r(checkForMistakes($data['text_input'], $source_language));
	break;
	
case 'proof_output' :
	if(check_entry($data['text_output']))
		echo nl2br_r(checkForMistakes($data['text_output'], $target_language, $source_language));
	break;
	
case 'translate' :
	if(check_entry($data['text_input']))
		echo nl2br_r($trans->getTranslation($data['text_input'], $data['pretrans_src'], $data['pretrans_dst']));
	break;
	
case 'replace_input' :
	echo nl2br_r(str_replace_words($data['pretrans_src'], $data['pretrans_dst'], $data['pretrans_case'], $data['text_input']));
	break;
	
case 'replace_output' :
	echo nl2br_r(str_replace_words($data['posttrans_src'], $data['posttrans_dst'], $data['posttrans_case'], $data['text_output']));
	break;
}

?>