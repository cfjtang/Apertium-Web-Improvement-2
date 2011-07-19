<?//coding: utf-8
/* Apertium Web Post Editing Tool
 * Abstract class for integration of Apertium.ORG as translate system
 *
 * Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
 * Mentors : Arnaud Vié, Luis Villarejo
 *
 */

class Translate_ApertiumORG extends Translate
{
	public function get_language_pairs_list()
	{
		/* Return an array of language pair on Apertium.org */
		$source = file_get_contents($this->config['apertiumorg_homeurl']);
		preg_match('#<select id="direction" name="direction" title="Select the translation type">(.*?)</select>#s', $source, $select_content);
		preg_match_all("#<option value='(.*?)' [selected=true ]*>#s", $select_content[1], $matches);
		
		return $matches[1];		
	}

	private function getApertiumORGTranslation($text)
	{
		/* Return the translation of $text, using the FILE translation 
		 * system of ApertiumORG, passing $text as a HTML file
		 */
		$boundary = rand();
		
		/* direction: $this->source_language . '-' . $this->target_language */
		$content = "-----------------------------" . $boundary . "\r\nContent-Disposition: form-data; name='direction'\r\n\r\n";
		$content .= $this->source_language . '-' . $this->target_language;

		/* doctype: html */
		$content .= "\r\n-----------------------------" . $boundary . "\r\nContent-Disposition: form-data; name='doctype'\r\n\r\n";
		$content .= "html";

		/* userfile: $text */
		$content .= "\r\n-----------------------------" . $boundary . "\r\nContent-Disposition: form-data; name='userfile'; filename='to_translate.html'\r\nContent-Type: text/html\r\n\r\n";
		$content .= $text;
		$content .= "\r\n-----------------------------" . $boundary . "--\r\n";
		$context = array('http' => array(
					 'method' => 'POST',
					 'header' => "Connection: Close\r\nContent-Type: multipart/form-data, boundary=---------------------------" . $boundary . "\r\nContent-Length: " . strlen($content) . "\r\n",
					 'content' => $content));
		$source = file_get_contents($this->config['apertiumorg_traddoc'], false, stream_context_create($context));
				
		if (isset($source))
			return $source;
		else
			return "Request Failed !";
	}
	
	public function getTranslation($text, $pretrans_src='', $pretrans_dst='')
	{
		/* Return the translation of $text using Apertium.ORG server  */
		return $this->getApertiumORGTranslation($text);
	}
}

?>