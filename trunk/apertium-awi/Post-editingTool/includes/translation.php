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

abstract class Translate
{
	/* This class provide a system for translation using Apertium */
	protected $config;
	protected $source_language;
	protected $target_language;
	protected $format;
	protected $inputTMX;

	public function __construct($config, $src_language = '',
				    $tgt_language = '', $format = '')
	{
		/* Initialise the Object */
		$this->config = $config;
		if (ctype_alpha($src_language))
			$this->source_language = $src_language;
		else
			$this->source_language = 'en';
		if (ctype_alpha($tgt_language))
			$this->target_language = $tgt_language;
		else
			$this->target_language = 'en';
		$this->format = $format;
		$this->inputTMX = false;
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

	public function set_inputTMX($inputTMX)
	{
		/* Set the input TMX content */
		$this->inputTMX = $inputTMX;
	}

	public function get_inputTMX()
	{
		/* Return the value of $inputTMX */
		return $this->inputTMX;
	}

	protected function mergeTMX($files_array)
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

	public abstract function get_language_pairs_list();
	/* Return an array of the language pairs list, 
	 * depending on $config['use_apertiumorg']
	 */

	public abstract function getTranslation($text, $pretrans_src='', $pretrans_dst='');
		/* Return the translation of $text using Apertium local install or
		 * Apertium.org, depending on $config['use_apertiumorg']
		 */
	

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

if (!$config['use_apertiumorg']) {
	include_once('translation_apertium.php');
	$trans = new Translate_Apertium($config, 'en', 'es', 'html');
}
else {
	include_once('translation_apertiumorg.php');
	$trans = new Translate_ApertiumORG($config, 'en', 'es', 'html');
}

?>