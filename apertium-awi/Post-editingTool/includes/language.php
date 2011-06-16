<?//coding: utf-8
/*
	Apertium Web Post Editing tool
	Functions for interaction with language tools
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús

	Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
	Mentors : Arnaud Vié, Luis Villarejo
*/

include_once('config.php');
include_once('strings.php');
include_once('system.php');
include_once('template.php');

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



/*--------------------------------------
Functions for translation
--------------------------------------*/


function is_installed($language_pair)
{
	global $language_pairs_list;
	return in_array($language_pair, $language_pairs_list);
}

function getApertiumTranslation($source_language, $target_language, $format, $text, $pretrans_src='', $pretrans_dst='')
{
	//runs a translation of the $text using the $language_pair with Apertium, and returns the result text
	//if $pretrans_src is an array, then translate all elements in $pretrans_src as their counterpart in $pretrans_dst
	
	global $config;
	
	$generate_tmx = false;
	if(is_array($pretrans_src) AND !empty($pretrans_src))
	{
		foreach($pretrans_src as $ind => $value)
		{
			if(strlen(trim($value)) < 5) //for some reason, Apertium crashes when loading arguments shorter than 5 chars from tmx
			{
				unset($pretrans_src[$ind]);
				unset($pretrans_dst[$ind]);
			}
		}
		$generate_tmx = !empty($pretrans_src);
	}
	
	if($generate_tmx)
	{
		$tmx_input = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<tmx version="1.4b">
	<header srclang="'.$source_language.'" segtype="phrase" o-tmf="al" datatype="plaintext" creationtoolversion="1" creationtool="unas" adminlang="en"/>
	<body>' . "\n";
		
		foreach($pretrans_src as $ind => $value)
		{
			if(trim($value) != '')
			{
				$tmx_input .= '	<tu>
		<tuv xml:lang="'.$source_language.'">
			<seg>'. htmlspecialchars($value, ENT_QUOTES, 'UTF-8') .'</seg>
		</tuv>
		<tuv xml:lang="'.$target_language.'">
			<seg>'. htmlspecialchars($pretrans_dst[$ind], ENT_QUOTES, 'UTF-8') .'</seg>
		</tuv>
	</tu>' . "\n";
			}
		}
		
		$tmx_input .= '	</body>
</tmx>';
		
		$tempname = tempnam($config['temp_dir'], 'ap_temp_');
		$tempname = $config['temp_dir'] . basename($tempname);
		
		$tmx_file = fopen($tempname, "w");
		fwrite($tmx_file, $tmx_input);
		fclose($tmx_file);
		
	}
	
	
	
	$command = $config['apertium_command'].' -u -f '.$format.' '.$source_language.'-'.$target_language . (isset($tempname) ? ' -m "'.$tempname.'"' : '');
	executeCommand($command, $text, $translation_result, $return_status);
	
	if(isset($tempname))
	{
		unlink($tempname);
	}
	
	//$translation_result = html_entity_decode($translation_result, ENT_COMPAT, 'UTF-8'); //only useful if  the"-f html" option is enabled in the apertium command
	
	return $translation_result;
}

/*--------------------------------------
Functions for TMX generation
--------------------------------------*/


function generateTmxOutput($source_language, $target_language, $source_text, $target_text)
{
	global $config;
	
	$source_language_file = tempnam($config['temp_dir'], 'ma_temp_src_');
	$target_language_file = tempnam($config['temp_dir'], 'ma_temp_tar_');
	$output_file = tempnam($config['temp_dir'], 'ma_temp_out_');
	
	//maligna seems to fail when there are spaces in directory names : let's minimise the risk by indicating a relative path to the temp directory
	$source_language_file = $config['temp_dir'] . basename($source_language_file);
	$target_language_file = $config['temp_dir'] . basename($target_language_file);
	$output_file = $config['temp_dir'] . basename($output_file);
	
	$temp = fopen($source_language_file, "w");
	fwrite($temp, $source_text);
	fclose($temp);
	
	$temp = fopen($target_language_file, "w");
	fwrite($temp, $target_text);
	fclose($temp);
	
	$command = $config['maligna_command'] . ' parse -c txt "'.$source_language_file.'" "'.$target_language_file.'" | ' .
		$config['maligna_command'] . ' modify -c split-sentence | ' .
		$config['maligna_command'] . ' modify -c trim | ' . 
		$config['maligna_command'] . ' align -c viterbi -a poisson -n word -s iterative-band | ' .
		$config['maligna_command'] . ' select -c one-to-one | ' . 
		$config['maligna_command'] . ' format -c tmx -l ' . $source_language . ',' . $target_language . ' ' . $output_file . ' ; ' . 
		'rm -f "'.$source_language_file.'" "'.$target_language_file.'"';
	
	executeCommand($command, '', $return_value, $return_status);
	
	$output = file_get_contents($output_file);
	unlink($output_file);
	
	return $output;
}

?>