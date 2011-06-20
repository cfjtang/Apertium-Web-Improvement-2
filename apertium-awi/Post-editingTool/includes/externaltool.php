<?//coding: utf-8
/*
 * Apertium Web Post Editing tool
 * PHP Object for link with external tool
 *
 * Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
 * Mentors : Arnaud Vié, Luis Villarejo
 */

include_once("system.php");
include_once("strings.php");

class externtool
{
	/* This class provide a system for link with external tool */
	private $config;
	private $spellcheckingtool;
	private $grammarproofingtool;
	
	public function __construct($config)
	{
		/* Initialise the Object */
		$this->config = $config;
		$this->spellcheckingtool = $config['spellcheckingtool'];
		$this->grammarproofingtool = $config['grammarproofingtool'];
	}

	private function getAspellCorrection($language, $text, $apply, $additional_argument='')
	{
		//runs Aspell to spellcheck $text in the given $language
		//if $apply, replace all unknown words with their closest match
		//else, generate the table of mistakes
	
		$command = $this->config['aspell_command'] . ' -l '. $language .' -a ' . $additional_argument . ' | grep ^[\&#] '; //only lines beginning with & and # indicate a mistake
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
	
	private function mergeMistakes($mistake1, $mistake2, $text)
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

	private function insertMistake($mistake_info, &$mistakeslist, $text, $merge_colliding)
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
				$to_insert = $this->mergeMistakes($to_insert, $mistake_elt, $text);
			
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
	
	private function analyseLanguageToolReport($text, $correction_result, $merge_colliding)
	{
		//$text is the text we're working on
		//$correction_result is the result of getLanguageToolCorrection on this text with $apply = false
		//generates the table of mistakes
	
		$mistakes = array();
	
		$correction_result = simplexml_load_string($correction_result);
	
		if($correction_result)
		{
			//If LanguageTool ran fine and produced an XML result, fetch its contents
			foreach($correction_result->children() as $error)
			{
				$this->insertMistake(array('text' => utf8_substr($text, intval($error['fromx']), intval($error['errorlength'])), 'start' => intval($error['fromx']), 'end' => intval($error['fromx']) + intval($error['errorlength']) - 1, 'desc' => (string)$error['msg'], 'sugg' => explode('#', $error['replacements'])), $mistakes, $text, $merge_colliding);
			}
		}
	
		return $mistakes;
	}

	private function getLanguageToolCorrection($language, $format, $text, $motherlanguage, $apply, $merge_colliding)
	{
		//runs a LanguageTool grammar checking on the $text in the $language, using $motherlanguage for false-friends
		//if $apply, then return the corrected text; else return the table of mistakes	
		if($format == 'html') //replace html code by underscores to avoid it being analysed
		{
			$text = preg_replace('#\<([-:\w]+?)( +[-:\w]+?\=\"[^"]*\")* *\/?\>#eu', 'str_pad("", utf8_strlen("$0"), "#")', $text);
		}
	
		if($apply)
		{
			$tempname = tempnam($this->config['temp_dir'], 'ap_temp_');
			$temp = fopen($tempname, "w");
			fwrite($temp, $text);
			fclose($temp);
		
			$command = $this->config['languagetool_command'].' -l '.$language.' '.($motherlanguage != '' ? '-m ' . $motherlanguage : '').' '.($apply ? '-a ' : '').'"'.$tempname.'"';
			executeCommand($command, '', $correction_result, $return_status);
		
			unlink($tempname);
		
			return $correction_result;
		
		}
		else
		{
			runLTserver();
		
			//don't forget to replace new lines by underscores to avoid the problems with line count in LT
			$correction_result = @file_get_contents('http://localhost:'.$this->config['languagetool_server_port'].'/?language='.$language.'&text='.str_replace("\n", "#", $text).($motherlanguage != '' ? '&motherTongue=' . $motherlanguage : ''));
		
			return $this->analyseLanguageToolReport($text, $correction_result, $merge_colliding);
		}
	}

	public function SpellChecking($language, $text, $apply
				      , $additional_argument = '') 
	{
		/* Run SpellCheking, with the tool $spellcheckingtool
		 * If $apply, return $text with all unknow words replace with their
		 * closest match
		 * Else, return the table of mistakes
		 */
		if ($this->spellcheckingtool == 'aspell')
			return $this->getAspellCorrection($language, $text, $apply, $additional_argument);
		else
			return $text;
	}

	public function GrammarProofing($language, $format, $text, $motherlanguage
					, $apply, $merge_colliding)
	{
		/* Run Grammar Profing, with the tool $grammarproofingtool
		 * If $apply, return the corrected text
		 * Else return the table of mistakes
		 */
		if ($this->grammarproofingtool == 'languagetool')
			return $this->getLanguageToolCorrection($language, $format, $text, $motherlanguage, $apply, $merge_colliding);
		else
			return $text;
	}
}
			