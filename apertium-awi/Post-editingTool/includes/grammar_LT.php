<?//coding: utf-8
/* Apertium Web Post Editing Tool
 * Abstract class for integration of LanguageTool
 *
 * Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
 * Mentors : Arnaud Vié, Luis Villarejo
 *
 * Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
 * Mentors : Luis Villarejo, Mireia Farrús
 */
class Grammar_LT extends Grammar
{
	private $config;
	
	public function __construct($config)
	{
		/* Initialise the Object */
		$this->config = $config;
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

	public function getGrammarCorrection($language, $format, $text,
					       $motherlanguage, $apply, $merge_colliding)
	{
		/* Run Grammar Profing, with the tool $grammarproofingtool
		 * If $apply, return the corrected text
		 * Else return the table of mistakes
		 */

		return $this->getLanguageToolCorrection($language, $format, $text, $motherlanguage, $apply, $merge_colliding);
	}
}
?>