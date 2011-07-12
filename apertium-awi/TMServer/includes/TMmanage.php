<?//coding: utf-8
/* Apertium Web TMServer
 * Abstract Class for TM management
 *
 * Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
 * Mentors : Arnaud Vié, Luis Villarejo
 */

include('config.php');

abstract class TMmanage
{
	/* This class provide a system for manage Translation Memory segment */
	protected $config;
	protected $source_language;
	protected $target_language;
	
	public function __construct($config, $source_language = '', $target_language = '')
	{
		/* Initialise the Object */
		$this->config = $config;
		$this->source_language = $source_language;
		$this->target_language = $target_language;
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
	
	public abstract function add_TM($source);
	/* Add the Translation Memory segment to the database */
	
	public abstract function get_TM($language_pair = '');
	/* If $language_pair = ''
	 * Return a file corresponding to the Translation Memory having 
	 * $source_language as source, and $target_language as target 
	 * Else
	 * Return a file corresponding to the Translation Memory 
	 * $language_pair
	 * If no file exists, return False
	 */

	public abstract function get_language_pairs();
	/* Return an array of avalaible language pairs */

}

switch ($config['TM_type']) {
case 'TMX':
	include_once('TMmanage_TMX.php');
	$tm = new TMmanage_TMX($config, 'en', 'fr');
	break;
default:
	break;
}

?>