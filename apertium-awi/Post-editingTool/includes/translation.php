<?//coding: utf-8
/*
  Apertium Web Post Editing tool
  PHP Object for translation system
	
  Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
  Mentors : Luis Villarejo, Mireia Farrús

  Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
  Mentors : Arnaud Vié, Luis Villarejo
*/

include_once("system.php");

class translate
{
	/* This class provide a system for translation using Apertium */
	private $config;
	private $source_language;
	private $target_language;
	private $format;
	private $inputTMX;
	private $useapertiumorg;

	public function __construct($config, $src_language = '',
				    $tgt_language = '', $format = '')
	{
		/* Initialise the Object */
		$this->config = $config;
		$this->source_language = $src_language;
		$this->target_language = $tgt_language;
		$this->format = $format;
		$this->inputTMX = false;
		$this->useapertiumorg = false;
	}

	public function set_source_language($src_language)
	{
		/* Set the Source language */
		if (ctype_alpha($src_language))
			$this->source_language = $src_language;
		else
			return false;
	}

	public function set_target_language($tgt_language)
	{
		/* Set the Target language */
		if (ctype_alpha($tgt_language))
			$this->target_language = $tgt_language;
		else
			return false;
	}

	public function set_format($format)
	{
		/* Set the Format */
		if (ctype_alpha($format))
			$this->format = $format;
		else
			return false;
	}

	public function set_useapertiumorg($value)
	{
		/* Set the Use Apertium.ORG */
		if ($value)
		        $this->useapertiumorg = TRUE;
		else
			$this->useapertiumorg = FALSE;
	}

	public function get_useapertiumorg()
	{
		/* Return the value of Use Apertium.ORG */
		return $this->useapertiumorg;
	}

	public function set_inputTMX($inputTMX)
	{
		/* Set the input TMX file */
		if (is_readable($inputTMX))
			$this->inputTMX = $inputTMX;
		else
			$this->inputTMX = htmlspecialchars(filter_var($inputTMX, FILTER_VALIDATE_URL, FILTER_FLAG_PATH_REQUIRED), ENT_QUOTES, 'UTF-8');
	}

	public function get_inputTMX()
	{
		/* Return the value of $inputTMX */
		return $this->inputTMX;
	}

	private function mergeTMX($files_array)
	{
		/* Merge TMX file in $files_array using tmxmerger_command, 
		 * and language source: $this->source_language
		 * and return the name of the file generated 
		 */
		$tempname = tempnam($this->config['temp_dir'], 'ap_temp_');
		
		$command = $this->config['tmxmerger_command'] . ' source=' . $this->source_language;
		foreach ($files_array as $file_name)
			$command .= ' ' . $file_name;
		
		$command .= ' ' . $tempname;
		executeCommand($command, '', $return_value, $return_status);
		
		return $tempname;
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

	public function getApertiumTranslation($text, $pretrans_src='', $pretrans_dst='')
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

		if ($this->inputTMX) {
			if ($generate_tmx) {
				/* Merging pretrans and external TMX */
				$tempname =  $this->generateTMXApertium($pretrans_src, $pretrans_dst);	
				$tempname_tmx = $this->mergeTMX(array($tempname, $this->inputTMX));
				$command .= ' -m "'.$tempname_tmx.'"';
			}
			else	
				$command .= ' -m "'.$this->inputTMX.'"';
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
	
		/* $translation_result = html_entity_decode($translation_result, ENT_COMPAT, 'UTF-8'); 
		 * only useful if  the"-f html" option is enabled in the apertium command
		 */
	
		return $translation_result;
	}

	private function getApertiumORGTranslation($text)
	{
		/* Return the translation of $text, using the translation system
		 * of ApertiumORG
		 */
		$data = http_build_query(array(
						 'direction' => $this->source_language . '-' . $this->target_language,
						 'textbox' => urlencode($text)));
		$context = array('http' => array(
					 'method' => 'POST',
					 'header' => "Content-type: application/x-www-form-urlencoded\r\nConnection: close\r\nContent-Length: " . strlen($data) . "\r\n",
					 'content' => $data));
		$source = file_get_contents($this->config['apertiumorg_homeurl'], false, stream_context_create($context));
		preg_match('#<p class="transresult">(.*?)</p>#s', $source, $result);

		if (isset($result[1]))
			return $result[1];
		else
			return "Request Failed !";
	}

	public function getTranslation($text, $pretrans_src='', $pretrans_dst='')
	{
		/* Return the translation of $text using Apertium local install or
		 * Apertium.org, depending on $useapertiumorg
		 */
		if ($this->useapertiumorg)
			return $this->getApertiumORGTranslation($text);
		else
			return $this->getApertiumTranslation($text, $pretrans_src, $pretrans_dst);
	}

	public function generateTmxOutput($source_text, $target_text)
	{
		/* Return a TMX file representing the translation of $source_text 
		 * in $target_text 
		 */
	
		$source_language_file = tempnam($this->config['temp_dir'],
						'ma_temp_src_');
		$target_language_file = tempnam($this->config['temp_dir'],
						'ma_temp_tar_');
		$output_file = tempnam($this->config['temp_dir'],
				       'ma_temp_out_');
	
		/* maligna seems to fail when there are spaces in directory names : 
		 * let's minimise the risk by indicating a relative path to the temp directory
		 */
		$source_language_file = $this->config['temp_dir'] . basename($source_language_file);
		$target_language_file = $this->config['temp_dir'] . basename($target_language_file);
		$output_file = $this->config['temp_dir'] . basename($output_file);
	
		$temp = fopen($source_language_file, "w");
		fwrite($temp, $source_text);
		fclose($temp);
	
		$temp = fopen($target_language_file, "w");
		fwrite($temp, $target_text);
		fclose($temp);
	
		$command = $this->config['maligna_command'] . ' parse -c txt "'.$source_language_file.'" "'.$target_language_file.'" | ' .
			$this->config['maligna_command'] . ' modify -c split-sentence | ' .
			$this->config['maligna_command'] . ' modify -c trim | ' . 
			$this->config['maligna_command'] . ' align -c viterbi -a poisson -n word -s iterative-band | ' .
			$this->config['maligna_command'] . ' select -c one-to-one | ' . 
			$this->config['maligna_command'] . ' format -c tmx -l ' . $this->source_language . ',' . $this->target_language . ' ' . $output_file . ' ; ' . 
			'rm -f "'.$source_language_file.'" "'.$target_language_file.'"';
	
		executeCommand($command, '', $return_value, $return_status);
	
		$output = file_get_contents($output_file);
		unlink($output_file);
	
		return $output;
	}
}
?>