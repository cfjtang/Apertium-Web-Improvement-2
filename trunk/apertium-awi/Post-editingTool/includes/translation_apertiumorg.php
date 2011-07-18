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
			return urldecode($result[1]);
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