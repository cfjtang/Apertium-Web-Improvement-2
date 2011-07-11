<?//coding: utf-8
/* Apertium Web TMServer
 * Abstract class for the management of TMX format as Translation memory
 *
 * Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
 * Mentors : Arnaud Vié, Luis Villarejo
 *
 * Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
 * Mentors : Luis Villarejo, Mireia Farrús
 */

include('system.php');

class TMmanage_TMX extends TMmanage
{
	private function mergeTMX($files_array)
	{
		/* Merge TMX file in $files_array using tmxmerger_command, 
		 * and language source: $this->source_language
		 * and return the name of the file generated 
		 */
		$tempname = tempnam($this->config['temp_dir'], 'TM_temp_');
		
		$command = $this->config['TMXmerge_command'] . ' source=' . $this->source_language;
		foreach ($files_array as $file_name)
			$command .= ' ' . $file_name;
		
		$command .= ' ' . $tempname;
		executeCommand($command, '', $return_value, $return_status);
		
		return $tempname;
	}
	
	private function write_tmx_file($source, $src_language, $trg_language)
	{
		/* Write the tmx file $src_language-$trg_language.tmx with
		 * content $source
		 * Return true if success, false otherwise
		 */
		$db_filename = $this->config['database_dir'] . $src_language . '-' . $trg_language . '.tmx';

		if (file_exists($db_filename)) {
			
			$tempname = tempnam($this->config['temp_dir'], 'TM_temp_');
			$file = fopen($tempname, 'w');
			fwrite($file, $source);
			fclose($file);
			
			if (!$tempname)
				return FALSE;
			$new_tmx = $this->mergeTMX(array($tempname, $db_filename));
			unlink($tempname);
			
			$command = 'mv ' . $new_tmx . ' ' . $db_filename;
			executeCommand($command, '', $return_value, $return_status);
			if (!$return_status)
				return FALSE;

			return TRUE;
		}
		else {
			$file = fopen($db_filename, 'w');
			
			if (fwrite($file, $source))
				fclose($file);
			else
				return FALSE;
			return TRUE;
		}
	}
	
	

	public function get_TM($language_pair = '')
	{
		/* If $language_pair = ''
		 * Return a file corresponding to the Translation Memory having 
		 * $source_language as source, and $target_language as target 
		 * Else
		 * Return a file corresponding to the Translation Memory 
		 * $language_pair
		 * If no file exists, return False
		 */
		$name = '';
		
		if ($language_pair != '' && in_array($language_pair, $this->get_language_pairs()))
			$name = $this->config['database_dir'] . $language_pair . '.tmx';
		elseif (file_exists($this->config['database_dir'] . $this->source_language . '-' . $this->target_language . '.tmx'))
			$name = $this->config['database_dir'] . $this->source_language . '-' . $this->target_language . '.tmx';
		
		if ($name == '')
			return FALSE;
		else
			return $name;
	}

	public function get_language_pairs()
	{
		/* Return an array of avalaible language pairs */
		$to_return = array();

		$command = 'cd ' . $this->config['database_dir'] . ';ls *.tmx' ;
		executeCommand($command, '', $return_value, $return_status);
		$tmx_files = explode("\n", $return_value);

		foreach ($tmx_files as $name) {
			if ($name != '')
				$to_return[] = substr($name, 0, strpos($name, '.'));
		}

		return $to_return;
	}
}

?>