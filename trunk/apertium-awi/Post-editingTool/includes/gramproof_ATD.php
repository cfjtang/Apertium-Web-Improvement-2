<?//coding: utf-8
/* Apertium Web Post Editing Tool
 * Abstract class for integration of After The DeadLine
 *
 * Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
 * Mentors : Arnaud Vié, Luis Villarejo
 *
 * Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
 * Mentors : Luis Villarejo, Mireia Farrús
 */
class Gramproof_ATD
{
	private $config;
	private $ATDsupport_lang;

	public function __construct($config)
	{
		/* Initialise the Object */
		$this->config = $config;
		$this->ATDsupport_lang = array('en', 'fr', 'de', 'pt', 'es');
	}
	
	private function launch_ATD()
	{
		/* Launch the webserver ATD */
		executeCommand($this->config['ATD_command'], '', $result, $return_status);
	}

	private function stop_ATD()
	{
		/* Stop the webserver ATD */
		executeCommand('killall run-lowmem.sh', '', $result, $return_status);
	}

	private function getATDresult($lang, $data, $online_server = TRUE)
	{
		/* Return the XML file generate by ATD for the data $data and
		 * the language $lang, if the language is in $ATDsupport_lang
		 * Use the online server if $online_server
		 */
		if (!in_array($lang, $this->ATDsupport_lang))
			$lang = 'en';
		
		$url = $this->config['ATD_link'];
		if ($online_server)
			$url = $lang . '.' . $url;
		
		return file_get_contents('http://' . $url . '/checkDocument?data=' . urlencode($data));
	}
	
	private function analyseATDresult($XML, $type_error, $text)
	{
		/* Analyse the XML file generaye by ATD 
		 * Return the table of mistakes
		 */
		$mistakes = array();
		
		$data = simplexml_load_string($XML);

		foreach ($data->error as $error) {
			if ($error->type == $type_error) {
				if ((string) $error->{'precontext'} != '')
					$start = strpos($text,
							(string) $error->precontext . ' ' . (string) $error->string)
						+ strlen($error->precontext) + 1;
				else
					$start = strpos($text, (string) $error->string);
				       
				$sugg = array();
				/* Get the list of suggestions */
				foreach ($error->children() as $child1) {
					foreach ($child1->children() as $child2)
						$sugg[] = $child2;
				}
				
				$one_mistake = array(
					'text' => $error->string,
					'desc' => $error->description,
					'sugg' => $sugg,
					'start' => $start,
					'end' => $start + strlen($error->string) - 1);
				$mistakes[] = $one_mistake;
			}
		}
		
		return $mistakes;
	}
	
	private function getATDCorrection($language, $text, $apply, $type_error)
	{
		/* Return a table of mystake for error $type_error (spelling, grammar)
		 * Using AfterTheDeadLine
		 */
		return $this->analyseATDresult($this->getATDresult($language, $text), $type_error, $text);
	}


	public function getSpellCorrection($language, $text, $apply, $additional_argument = '')
	{
		/* Run SpellCheking
		 * If $apply, return $text with all unknow words replace with their
		 * closest match
		 * Else, return the table of mistakes
		 */

		return $this->getATDCorrection($language, $text, $apply, 'spelling');
	}
	
	public function getGrammarCorrection($language, $format, $text,
					       $motherlanguage, $apply, $merge_colliding)
	{
		/* Run Grammar Profing, with the tool $grammarproofingtool
		 * If $apply, return the corrected text
		 * Else return the table of mistakes
		 */

		return $this->getATDCorrection($language, $text, $apply, 'grammar');
	}
	
}
?>