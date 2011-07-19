<?//coding: utf-8
/* Apertium Web Post Editing Tool
 * Functions for TMServer Module
 *
 * Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
 * Mentors : Arnaud Vié, Luis Villarejo
 */

class TMServer {
	
	private $server_url;

	public function __construct($server_url) {
		/* Load the PHP TMServer Object */
		$this->server_url = $server_url;
	}

	public function set_server_url($server_url) {
		/* Set the TMServer URL */
		$this->server_url = $server_url;
	}
	
	public function get_server_url() {
		/* Return the current TMServer's URL */
		return $this->server_url;
	}

	public function get_language_pairs_list() {
		/* Return an array of language pairs avalaible on the server */
		$source = file_get_contents($this->server_url . 'index.php?action=get_language_pairs');
		return explode("\n", trim($source));
	}

	public function get_real_URL($language_pair) {
		/* Return the real URL of the record in the TMServer Database for the
		 * $language_pair 
		 * return FALSE if no URL exists
		 */
		$source = file_get_contents($this->server_url . 'index.php?action=get_record&language_pair=' . urlencode($language_pair));
		if (empty($source))
			return FALSE;
		else
			return $this->server_url . $source;
	}

	public function send_TM($source) {
		/* Send the TM file to the TMServer */
		$boundary = rand();
		
		$content = "-----------------------------" . $boundary . "\r\nContent-Disposition: form-data; name='in_doc'; filename='to_add.tmx'\r\nContent-Type: application/octet-stream\r\n\r\n";
		$content .= $source;
		$content .= "\r\n-----------------------------" . $boundary . "--\r\n";
		
		$context = array('http' => array(
					 'method' => 'POST',
					 'header' => "Connection: Close\r\nContent-Type: multipart/form-data, boundary=---------------------------" . $boundary . "\r\nContent-Length: " . strlen($content) . "\r\n",
					 'content' => $content));
		$source = file_get_contents($this->server_url . 'index.php', false, stream_context_create($context));
		
		if (strstr($source, 'Translation memory correctly added !'))
			return TRUE;
		else
			return FALSE;
	}
	
}

?>