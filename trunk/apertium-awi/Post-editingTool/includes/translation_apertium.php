<?//coding: utf-8
/* Apertium Web Post Editing Tool
 * Abstract class for integration of Apertium as translate system
 *
 * Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
 * Mentors : Arnaud Vié, Luis Villarejo
 *
 * Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
 * Mentors : Luis Villarejo, Mireia Farrús
 */
include_once('system.php');

class Translate_Apertium extends Translate
{
	public function get_language_pairs_list()
	{
		/* Return an array of language pairs installed on the server */
		$language_pairs_list = array();

		executeCommand($this->config['apertium_command']." fr-fr", "", $cmd_return, $return_status);
	
		$cmd_return = explode("\n", $cmd_return);
		foreach($cmd_return as $line)
		{
			$matches = array();
			if(preg_match("#^\s*([a-z]+-[a-z]+)\s*$#", $line, $matches))
			{
				$language_pairs_list[] = $matches[1];
			}
		}
		
		return $language_pairs_list;
	}


	private function generateTMXApertium($pretrans_src, $pretrans_dst)
	{
		/* Generate a TMX file for the need of Apertium translation '-m' 
		 * Return the TMX file name
		 */
		$tmx_input = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<tmx version="1.4b">
	<header srclang="'.$this->source_language.'" segtype="phrase" o-tmf="al" datatype="plaintext" creationtoolversion="1" creationtool="unas" adminlang="en"/>
	<body>' . "\n";
		
		foreach($pretrans_src as $ind => $value)
		{
			if(trim($value) != '')
			{
				$tmx_input .= '	<tu>
		<tuv xml:lang="'.$this->source_language.'">
			<seg>'. htmlspecialchars($value, ENT_QUOTES, 'UTF-8') .'</seg>
		</tuv>
		<tuv xml:lang="'.$this->target_language.'">
			<seg>'. htmlspecialchars($pretrans_dst[$ind], ENT_QUOTES, 'UTF-8') .'</seg>
		</tuv>
	</tu>' . "\n";
			}
		}
		
		$tmx_input .= '	</body>
</tmx>';
		
		$tempname = tempnam($this->config['temp_dir'], 'ap_temp_');
		$tempname = $this->config['temp_dir'] . basename($tempname);
		
		$tmx_file = fopen($tempname, "w");
		fwrite($tmx_file, $tmx_input);
		fclose($tmx_file);
		
		return $tempname;
	}

	private function getApertiumTranslation($text, $pretrans_src='', $pretrans_dst='')
	{
		/* Runs a translation of the $text using the $language_pair with
		 * Apertium, and returns the result text
		 * If $pretrans_src is an array, then translate all elements in 
		 * $pretrans_src as their counterpart in $pretrans_dst
		 *
		 * Return the translation result
		 */		
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
	
		$command = $this->config['apertium_command'].' -u -f '.$this->format.' '.$this->source_language.'-'.$this->target_language;

		if ($this->inputTMX != '') {
			$tempname_inputTMX = tempnam($this->config['temp_dir'], 'ap_temp_');
			$tempname_inputTMX = $this->config['temp_dir'] . basename($tempname_inputTMX);
			$tmx_file = fopen($tempname_inputTMX, "w");
			fwrite($tmx_file, $this->inputTMX);
			fclose($tmx_file);
		}
			

		if (isset($tempname_inputTMX)) {
			if ($generate_tmx) {
				/* Merging pretrans and external TMX */
				$tempname =  $this->generateTMXApertium($pretrans_src, $pretrans_dst);	
				$tempname_tmx = $this->mergeTMX(array($tempname, $tempname_inputTMX));
				$command .= ' -m "'.$tempname_tmx.'"';
			}
			else	
				$command .= ' -m "'.$tempname_inputTMX.'"';
		}
		elseif ($generate_tmx) {
			$tempname =  $this->generateTMXApertium($pretrans_src, $pretrans_dst);	
			$command .= ' -m "'.$tempname.'"';
		}

		executeCommand($command, $text, $translation_result, $return_status);

		if (isset($tempname))
			unlink($tempname);
		if (isset($tempname_tmx))
			unlink($tempname_tmx);
		if (isset($tempname_inputTMX))
			unlink($tempname_inputTMX);
	
		/* $translation_result = html_entity_decode($translation_result, ENT_COMPAT, 'UTF-8'); 
		 * only useful if  the"-f html" option is enabled in the apertium command
		 */
	
		return $translation_result;
	}
	
	public function getTranslation($text, $pretrans_src='', $pretrans_dst='')
	{
		/* Return the translation of $text using Apertium local install */
		return $this->getApertiumTranslation($text, $pretrans_src, $pretrans_dst);
	}
}

?>