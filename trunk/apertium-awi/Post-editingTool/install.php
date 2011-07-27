<?//coding: utf-8
/* Apertium Web Post Editing Tool
 * Script for installing Post-EditingTool (configure config.php, and download extern package)
 *
 * Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
 * Mentors : Arnaud Vié, Luis Villarejo
 */
set_time_limit(0);
include_once('includes/config.php');
include_once('includes/template.php');
include_once('modules.php');

/* Change config.php */


/* Do some test */
$recommendation = array();
$test_result = array();

function display_result($array) {
	/* Display the array $array as a list */
	foreach ($array as $entry) {
		if ($entry != '')
			echo "<li>" . $entry . "</li>\n";
	}
}

function add_test($condition, $result_true, $recommend_true, $result_false, $recommend_false) 
{
	/* If $condition, add $result_true and $recommend_true in $test_result and 
	 * $test_result
	 * Else $result_false and $recommend_false
	 */
	global $recommendation, $test_result;
	
	if ($condition) {
		$test_result[] = $result_true;
		$recommendation[] = $recommend_true;
	}
	else {
		$test_result[] = $result_false;
		$recommendation[] = $recommend_false;
	}
}

function test_command($command) {
	/* Return true if $command is avalaible 
	 * False otherwise
	 * Warning: this function launch the command */
	if (exec($command) == '')
		return TRUE;
	else
		return FALSE;
}

function test_apertium_command() {
	/* Test the existence of command apertium, apertium-unformat, apertium-re and apertium-des
	 * Return true if all of these commands are accessible, false otherwise */
	global $config;
	
	$command_to_test = array($config['apertium_command'], $config['apertium_command'], $config['apertium_command'], $config['apertium_command']);
	$return = TRUE;
	foreach ($command_to_test as $command) {
		if (test_command($command)) {
			$return = FALSE;
			break;
		}
	}
				
	return $return;
}

function test_temp_dir() 
{
	/* Return true if the temp directory is writable, false otherwise */
	global $config;

	return is_writable($config['temp_dir']);
}

function test_externTM() 
{
	/* Return true if externTM_url is achievable */
	global $config;
	
	return file_get_contents($config['externTM_url']);
}

add_test(test_apertium_command(), "Your apertium installation is correctly detected", "You should set 'use_apertiumorg' on FALSE", "Your apertium installation isn't detected", "You should set 'use_apertiumorg' on TRUE");

add_test(test_command('java'), "Your installation of JAVA is correctly detected", "You should use languagetool, yuicompressor and TMXMerger", "No JAVA installation are detected.", "You should set 'spellcheckingtool' on ATD, and 'grammarproofingtool' too. Or install JAVA to dispose of all functionnality.");

add_test(test_temp_dir(), "Your temp directory is writable", "", "Your temp directory isn't writable. Some functionnality are disabled.", "Change the right of your temp directory, or change the temp directory to a directory writable.");

add_test(test_command('pdftohtml'), "PdftoHtml program is correctly installed on your system.", "You should set 'pdf2html_command' on 'pdftohtml -c -noframes'", "PdftoHtml isn't detected on your system. PDF file management will not work.", "You should install pdftohtml program.");

add_test(test_command($config['ocr_command']), "The OCR program is correctly installed.", "", "The convert program and the OCR program aren't correctly installed. Picture management will not work.", "You should install 'convert' program and 'tesseract' program.");

add_test(test_externTM(), "An extern translation memory as been detected.", "You should set 'externTM_type' on 'TMServer', and keep 'externTM_url' as current.", "No extern translation memory is detected.", "You should set 'externTM_type' on ''. Or install a Translation Memory server, such as TMServer.");

page_header('Configure', array('CSS/style.css'));

?>

<div id='content'>
  <div id='header'>
    <h1>Apertium</h1>
    <p>Post-edition Interface : configure script</p>
  </div>
  <div id='frame'>
    <p>This script allow you to have a easily configure your Apertium Post-edition interface installation.<br /><br />
      Result of the test of your installation:
      <ul>
<?
	display_result($test_result);
?>
      </ul>
      Recommendations:
      <ul>
<?
	display_result($recommendation);
?>
      </ul>
      Choose your configuration:
    </p>
    <form action="" method="post">
      <table>
	<tr><td>Configuration</td><td>Avalaible Option</td></tr>
      </table>
    </form>
    <br />
    <p>Do not forget to use the <a href='publish.php'>Publish script</a> to finalize your installation.</p> 
  </div>
</div>


<?
    page_footer();
?>