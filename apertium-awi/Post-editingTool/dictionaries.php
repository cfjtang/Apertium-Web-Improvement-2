<?//coding: utf-8
/*
	Apertium Web Post Editing tool
	System to generate the list of search engines for dictionary search
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús
*/

include_once('includes/files.php');

$engines_list_src = array();
$engines_list_dst = array();
/*Each engine will be represented as an array:
	array(
		'name' => blah blah,
		'query_string' => blah blah
	);
*/

function getSearchEngineFromFile($file_path)
{
	$openSearchDescription = new SimpleXMLElement(file_get_contents($file_path));
	
	foreach($openSearchDescription->Url as $Url)
	{
		if(!$Url['rel'] OR (string) $Url['rel'] == 'results')
		{
			$query_string = (string) $Url['template'];
		}
	}
	
	return array(
		'name' => (string) $openSearchDescription->ShortName,
		'query_string' => $query_string
	);
}

//set the paths of dirs to scan
$scan_src = (isset($_GET['src_language']) AND ctype_alpha($_GET['src_language']));
$scan_dst = (isset($_GET['dst_language']) AND ctype_alpha($_GET['dst_language']));

if($scan_src AND is_dir('dictionaries/' . $_GET['src_language']))
{
	$engines_list_src = array_map('getSearchEngineFromFile', scanDirForFileExt('dictionaries/' . $_GET['src_language'], 'xml'));
	if($scan_dst AND is_dir('dictionaries/' . $_GET['src_language'] . '/' . $_GET['dst_language']))
	{
		$engines_list_src = array_merge(
			$engines_list_src, 
			array_map('getSearchEngineFromFile', scanDirForFileExt('dictionaries/' . $_GET['src_language'] . '/' . $_GET['dst_language'], 'xml'))
		);
	}
	
?>
	dictionary_src.innerHTML = '';
<?
	foreach($engines_list_src as $engine)
	{
?>
	dictionary_src.options[dictionary_src.options.length] = new Option("<?echo addcslashes($engine['name'], '\\"');?>", "<?echo addcslashes($engine['query_string'], '\\"');?>", false, false);
<?
	}
}

if(empty($engines_list_src))
{
?>	dictionary_src.options[0] = new Option("No dictionary available", "", false, false);
<?	
}

if($scan_dst AND is_dir('dictionaries/' . $_GET['dst_language']))
{
	$engines_list_dst = array_map('getSearchEngineFromFile', scanDirForFileExt('dictionaries/' . $_GET['dst_language'], 'xml'));
	if($scan_src AND is_dir('dictionaries/' . $_GET['dst_language'] . '/' . $_GET['src_language']))
	{
		$engines_list_dst = array_merge(
			$engines_list_dst, 
			array_map('getSearchEngineFromFile', scanDirForFileExt('dictionaries/' . $_GET['dst_language'] . '/' . $_GET['src_language'], 'xml'))
		);
	}
	
?>
	dictionary_dst.innerHTML = '';
<?
	foreach($engines_list_dst as $engine)
	{
?>
	dictionary_dst.options[dictionary_dst.options.length] = new Option("<?echo addcslashes($engine['name'], '\\"');?>", "<?echo addcslashes($engine['query_string'], '\\"');?>", false, false);
<?
	}
}

if(empty($engines_list_dst))
{
?>	dictionary_dst.options[0] = new Option("No dictionary available", "", false, false);
<?	
}

?>