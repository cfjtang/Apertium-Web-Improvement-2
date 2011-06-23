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

	public function __construct($config, $src_language = '',
				    $tgt_language = '', $format = '')
	{
		/* Initialise the Object */
		$this->config = $config;
		$this->source_language = $src_language;
		$this->target_language = $tgt_language;
		$this->format = $format;
	}

	public function set_source_language($src_language)
	{
		/* Set the Source language */
		$this->source_language = $src_language;
	}

	public function set_target_language($tgt_language)
	{
		/* Set the Target language */
		$this->target_language = $tgt_language;
	}

	public function set_format($format)
	{
		/* Set the Format */
		$this->format = $format;
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
	
		if($generate_tmx)
			$tempname =  generateTMXApertium($pretrans_src, $pretrans_dst);	
	
		$command = $this->config['apertium_command'].' -u -f '.$this->format.' '.$this->source_language.'-'.$this->target_language . (isset($tempname) ? ' -m "'.$tempname.'"' : '');
		executeCommand($command, $text, $translation_result, $return_status);
	
		if(isset($tempname))
			unlink($tempname);
	
		/* $translation_result = html_entity_decode($translation_result, ENT_COMPAT, 'UTF-8'); 
		 * only useful if  the"-f html" option is enabled in the apertium command
		 */
	
		return $translation_result;
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