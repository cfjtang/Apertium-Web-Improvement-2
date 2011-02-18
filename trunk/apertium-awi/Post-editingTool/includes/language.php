<?//coding: utf-8
/*
	Apertium Web Post Editing tool
	Functions for interaction with language tools
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús
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
	
	
	$language_pairs_list = array();
	
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

/*	In both proofing and spell checking, mistakes in the text are represented as an array
	$mistakes = array(
		0 => array('text' => original text, 'start' => mistake pos begin, 'end' => mistake pos end, 'desc' => mistake desc, 'sugg' => array of mistake suggestions),
		1 => array(...))
*/

/*--------------------------------------
Functions for grammar proofing
--------------------------------------*/


function mergeMistakes($mistake1, $mistake2, $text)
{
	$result = array();
	
	//size of the merging result
	$result['start'] = min($mistake1['start'], $mistake2['start']);
	$result['end'] = max($mistake1['end'], $mistake2['end']);
	$result_length = $result['end'] - $result['start'] + 1;
	
	//new suggestions :
	//for each suggestion, we take the original string, replace the mistake by the suggestion and then crop it to have only the segment that matters
	foreach($mistake2['sugg'] as $n => $sugg)
	{
		$mistake_length = $mistake2['end'] - $mistake2['start'] + 1;
		$mistake2['sugg'][$n] = utf8_substr(utf8_substr_replace($text, $sugg, $mistake2['start'], $mistake_length), $result['start'], $result_length + (utf8_strlen($sugg) - $mistake_length));
	}
	foreach($mistake1['sugg'] as $n => $sugg)
	{
		$mistake_length = $mistake1['end'] - $mistake1['start'] + 1;
		$mistake1['sugg'][$n] = utf8_substr(utf8_substr_replace($text, $sugg, $mistake1['start'], $mistake_length), $result['start'], $result_length + (utf8_strlen($sugg) - $mistake_length));
	}
	$result['sugg'] = array_merge($mistake2['sugg'], $mistake1['sugg']);
	
	//rebuild the complete text
	$result['text'] = utf8_substr($text, $result['start'], $result['end'] - $result['start'] + 1);
	
	//new description
	$result['desc'] = $mistake1['desc'] . "\n". $mistake2['desc'];
	
	return $result;
}

function insertMistake($mistake_info, &$mistakeslist, $text, $merge_colliding)
{
	//insert a new mistake ($mistake_info) in the list, preserving the order (on the start position)
	//if necessary, merge colliding mistakes into only one
	
	$to_insert = $mistake_info;
	
	for($i = 0; $i < count($mistakeslist); $i++)
	{
		$mistake_elt = $mistakeslist[$i];
		
		if($merge_colliding
			AND $to_insert['start'] <= $mistake_elt['end']
			AND $to_insert['end'] >= $mistake_elt['start'])
		{
			//merge block $i into $to_insert
			$to_insert = mergeMistakes($to_insert, $mistake_elt, $text);
			
			//and then delete block $i from the list
			$mistakeslist = array_merge(array_slice($mistakeslist, 0, $i), array_slice($mistakeslist, $i+1));
			
			//decrement i since we just deleted an element
			$i--;
		}
		else
		{
			if($to_insert['start'] <= $mistake_elt['start'])
			{
				//just insert at position i
				$mistakeslist = array_merge(array_slice($mistakeslist, 0, $i), array_merge(array($to_insert), array_slice($mistakeslist, $i)));
				return;
			}
		}
	}
	
	//we reached the end : this new mistake is last
	$mistakeslist[] = $to_insert;
}

function analyseLanguageToolReport($text, $correction_result, $merge_colliding)
{
	//$text is the text we're working on
	//$correction_result is the result of getLanguageToolCorrection on this text with $apply = false
	//generates the table of mistakes
	
	$mistakes = array();
	
	$correction_result = simplexml_load_string($correction_result);
	
	foreach($correction_result->children() as $error)
	{
		insertMistake(array('text' => utf8_substr($text, intval($error['fromx']), intval($error['errorlength'])), 'start' => intval($error['fromx']), 'end' => intval($error['fromx']) + intval($error['errorlength']) - 1, 'desc' => (string)$error['msg'], 'sugg' => explode('#', $error['replacements'])), $mistakes, $text, $merge_colliding);
	}
	
	return $mistakes;
}

function getLanguageToolCorrection($language, $format, $text, $motherlanguage, $apply, $merge_colliding)
{
	//runs a LanguageTool grammar checking on the $text in the $language, using $motherlanguage for false-friends
	//if $apply, then return the corrected text; else return the table of mistakes
	global $config;
	
	if($format == 'html') //replace html code by underscores to avoid it being analysed
	{
		$text = preg_replace('#\<([-:\w]+?)( +[-:\w]+?\=\"[^"]*\")* *\/?\>#eu', 'str_pad("", utf8_strlen("$0"), "#")', $text);
	}
	
	if($apply)
	{
		$tempname = tempnam($config['temp_dir'], 'ap_temp_');
		$temp = fopen($tempname, "w");
		fwrite($temp, $text);
		fclose($temp);
		
		$command = $config['languagetool_command'].' -l '.$language.' '.($motherlanguage != '' ? '-m ' . $motherlanguage : '').' '.($apply ? '-a ' : '').'"'.$tempname.'"';
		executeCommand($command, '', $correction_result, $return_status);
		
		unlink($tempname);
		
		return $correction_result;
		
	}
	else
	{
		runLTserver();
		
		//don't forget to replace new lines by underscores to avoid the problems with line count in LT
		$correction_result = @file_get_contents('http://localhost:'.$config['languagetool_server_port'].'/?language='.$language.'&text='.str_replace("\n", "#", $text).($motherlanguage != '' ? '&motherTongue=' . $motherlanguage : ''));
		
		return analyseLanguageToolReport($text, $correction_result, $merge_colliding);
	}
}



/*--------------------------------------
Functions for spell checking
--------------------------------------*/



function getAspellCorrection($language, $text, $apply, $additional_argument='')
{
	//runs Aspell to spellcheck $text in the given $language
	//if $apply, replace all unknown words with their closest match
	//else, generate the table of mistakes
	
	global $config;
	
	$command = $config['aspell_command'] . ' -l '. $language .' -a ' . $additional_argument . ' | grep ^[\&#] '; //only lines beginning with & and # indicate a mistake
	executeCommand($command, str_replace("\n", " ", $text), $mistakes, $return_status);
	
	if($apply)
	{
		$final_output = $text;
	}
	else
	{
		$final_mistakes_list = array();
	}
	
	$mistakes = trim($mistakes);
	
	if($mistakes != '')
	{
		$mistakes_list = explode("\n", $mistakes);
		
		foreach($mistakes_list as $line)
		{
			//parse the line to get the faulty word and aspell's suggestions
			
			$firstchar = utf8_substr($line, 0, 1);
			
			switch($firstchar)
			{
				case '#' :
					//it looks like : # word offset
					$word = utf8_substr($line, 2); //skip the first 2 characters : "# "
					$spacepos = utf8_strpos($word, " ");
					
					$offset = intval(utf8_substr($word, $spacepos));
					$suggestions = '';
					$word = utf8_substr($word, 0, $spacepos);
					
					break;
				
				case '&' :
					//it looks like : & word nb_sugg offset: sugg1, sugg2
					$word = utf8_substr($line, 2); //skip the first 2 characters : "& "
					$firstspacepos = utf8_strpos($word, " ");
					$secondspacepos = utf8_strpos($word, " ", $firstspacepos + 1);
					$columnpos = utf8_strpos($word, ":", $secondspacepos + 1);
					
					$offset = intval(utf8_substr($word, $secondspacepos + 1, $columnpos - $secondspacepos - 1));
					$suggestions = utf8_substr($word, $columnpos + 2);
					$word = utf8_substr($word, 0, $firstspacepos);
					
					break;
			}
			
			if($apply)
			{
				//basic replacement by the best match (first word of $suggestions)
				$replacement_string = utf8_strpos($suggestions, ',');
				$replacement_string = utf8_substr($suggestions, 0, $replacement_string);
				
				//replace the faulty word with the replacement string
				$final_output = utf8_substr_replace($final_output, $replacement_string, $offset, utf8_strlen($word));
			}
			else
			{
				//append mistake to the table
				$final_mistakes_list[] = array(
					'text' => $word, 
					'start' => $offset, 
					'end' => $offset + utf8_strlen($word) - 1, 
					'desc' => '', 
					'sugg' => explode(', ', $suggestions)
				);
			}
		}
	}
	
	if($apply)
	{
		return $final_output;
	}
	else
	{
		return $final_mistakes_list;
	}
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


/*--------------------------------------
General functions
--------------------------------------*/

function checkForMistakes($input_text, $language, $motherlanguage='')
{
	//generate the correction fields for a given text
	
	$text = $input_text;
	
	//run grammar proofing
	$correction_result = getLanguageToolCorrection($language, 'html', $text, $motherlanguage, false, true);
	
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
	$spellchecking_result = getAspellCorrection($language, $text, false, '--add-filter=sgml');
	
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

?>