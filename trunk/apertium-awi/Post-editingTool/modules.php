<?//coding: utf-8
/* Apertium Web Post Editing Tool
 * List of modules with object to load
 * Functions for modules management
 *
 * Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
 * Mentors : Arnaud Vié, Luis Villarejo
 */

/* Module structure :
 * name : Module's name
 * description : Short description of the module
 * default : Recommanded module
 * javascript : an array of javascript dependencies (with path javascript/)
 * php : an array of php dependencies (with path includes/)
 * button_in : HTML code for the interface, under the input, when the module is activated
 * button_out : HTML code for the interface, under the output, when the module is activated
 */

$modules = array(
	'FormattedDocumentHandling' => array(
		'name' => 'Formatted document handling',
		'description' => 'Allow the text editor to handle formatted documents through the apertium-unformat and apertium-reformat modules.',
		'default' => TRUE,
		'javascript' => array(),
		'php' => array('format.php', 'system.php', 'files.php'),
		'button_in' => '',
		'button_out' => '<input type="submit" name="submit_output" value="Get translation result" />'
		),
	'SpellGrammarChecking' => array(
		'name' => 'Spell and Grammar checking',
		'description' => 'Integrate the ability to check both input and output texts for mistakes, with a button “Check for mistakes”.
When pressed, it runs spell checking and grammar checking on the text and underlines mistakes in different colours (red for spelling, blue for grammar).',
		'default' => TRUE,
		'javascript' => array('gramproof.js', 'main.js'),
		'php' => array('gramproof.php', 'strings.php', 'system.php'),
		'button_in' => '<input type="submit" name="check_input" value="Check for mistakes" />',
		'button_out' => '<input type="submit" name="check_output" value="Check for mistakes" />'
		),
	'LinkExternalDictionnaries' => array(
		'name' => 'Link to external dictionaries',
		'description' => 'Include links to dictionaries next to all suggestions on mistakes, so you may easily find which one corresponds to the expected meaning.
Whenever a dictionary for the language is available, a dictionary selection list is
displayed under the text editing field.',
		'default' => TRUE,
		'javascript' => array('dictionaries.js'),
		'php' => array('files.php'),
		'button_in' => '',
		'button_out' => ''
		),
	'SearchAndReplace' => array(
		'name' => 'Search and Replace',
		'description' => 'Inlcude search and replace forms in interface to instantly search for a word throughout the whole text and replace it, according to a specific pattern.',
		'default' => TRUE,
		'javascript' => array('main.js'),
		'php' => array('searchreplace.php', 'strings.php'),
		'button_in' => '<input type="submit" name="replace_input" value="Apply replacements" />',
		'button_out' => '<input type="submit" name="replace_output" value="Apply replacements" />'
		),
	'Logs' => array(
		'name' => 'Logs changes',
		'description' => 'Log the changes made by you in the target text after the translation.
This isn’t directly reusable in other translation processes, but it can help language pair maintainers get an idea of the changes to make.',
		'default' => TRUE,
		'javascript' => array('logs.js'),
		'php' => array('language.php', 'strings.php'),
		'button_in' => '',
		'button_out' => ''
		)
	);

function CheckModule($name)
{
	/* Check the existence of the modules :
	 * - $name is in $modules
	 * - javascript dependencies
	 * - php dependencies
	 * Return TRUE if all is ok, FALSE otherwise
	 */
	global $modules;

	if (array_key_exists($name, $modules)) {
		foreach ($modules[$name]['javascript'] as $name_file) {
			if (!file_exists('javascript/' . $name_file))
				return FALSE;
		}
		foreach ($modules[$name]['php'] as $name_file) {
			if (!file_exists('includes/' . $name_file))
				return FALSE;
		}
		return TRUE;
	}
	else
		return FALSE;
}

function LoadPHPDependencies($name)
{
	/* Load PHP dependencies of the module $name */
	global $modules;

	foreach ($modules[$name]['php'] as $name_file)
		include_once('includes/' . $name_file);
}

function WriteButtonInput($name)
{
	/* Write the button_in field of module $name */
	global $modules;
	
	echo $modules[$name]['button_in'];
}

function WriteButtonOutput($name)
{
	/* Write the button_out field of module $name */
	global $modules;
	
	echo $modules[$name]['button_out'];
}

function DefaultModules()
{
	/* Return an array of the modules name with field default = TRUE */
	global $modules;
	
	$return = array();
	foreach ($modules as $name => $data) {
		if ($data['default'])
			$return[] = $name;
	}

	return $return;
}

function module_is_load($name)
{
	/* Return TRUE if the module $name is load, FALSE otherwise */
	return in_array($name, $_SESSION['modules_load']);
}

function LoadModules()
{
	/* Return an array of the modules loaded */
	return $_SESSION['modules_load'];
}

function LoadAModule($name)
{
	/* Load the module $name */
	if (!in_array($name, LoadModules())) {
		$_SESSION['modules_load'][] = $name;
		LoadPHPDependencies($name);
	}
}

function UnloadAModule($name)
{
	/* Unload the module $name */
	if (in_array($name, LoadModules())) {
		unset($_SESSION['modules_load'][array_search($name, $_SESSION['modules_load'])]);
		$_SESSION['modules_load'] = array_values($_SESSION['modules_load']);
	}
}

function AddJSPath($file)
{
	/* Concatenates 'javascript/' and $file */
	return 'javascript/' . $file;
}
	

function AddJSDependencies($js_header) 
{
	/* Return $js_header with JS dependencies of loaded modules added, without doubloon */
	global $modules;

	foreach (LoadModules() as $name)
		$js_header = array_merge($js_header, array_map("AddJSPath", $modules[$name]['javascript']));
	
	return array_values(array_unique($js_header));
}

function gen_templateJS($url_source, $url_dst)
{
	/* Generate the javascript/main.js file, according to javascript/main.tmpl
	 * and modules loaded
	 */
	$source = file_get_contents($url_source);
	$file = fopen($url_dst, "w");

	preg_match_all("|\#BEGIN\#(.*?)\#END\#|s",$source,$matche);
	$to_treat = str_replace(array("#BEGIN#\n", "#END#"), '', $matche[0][0]);
	
	preg_match_all("|\#(.*?)\#(.*?)\#\#|s", $to_treat, $matches);
	
	$i = 0;
	foreach ($matches[1] as $ModuleName) {
		if ($ModuleName == 'all' || module_is_load($ModuleName)) 
			fwrite($file, $matches[2][$i]);
		$i++;
	}
       
	fclose($file);
}
	

session_start();
if (!isset($_SESSION['modules_load']))
	$_SESSION['modules_load'] = DefaultModules();
else {
	foreach (LoadModules() as $module_name)
		LoadPHPDependencies($module_name);
}

?>
		
