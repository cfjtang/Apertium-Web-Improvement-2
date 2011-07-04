<?//coding: utf-8
/* Apertium Web Post Editing Tool
 * Abstract class for integration of Aspell
 *
 * Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
 * Mentors : Arnaud Vié, Luis Villarejo
 *
 * Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
 * Mentors : Luis Villarejo, Mireia Farrús
 */
class Spelling_Aspell extends Spelling
{
	private $config;
	
	public function __construct($config)
	{
		/* Initialise the Object */
		$this->config = $config;
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

	public function getSpellCorrection($language, $text, $apply, $additional_argument = '')
	{
		/* Run SpellCheking
		 * If $apply, return $text with all unknow words replace with their
		 * closest match
		 * Else, return the table of mistakes
		 */
		return $this->getAspellCorrection($language, $text, $apply);
	}
}

?>