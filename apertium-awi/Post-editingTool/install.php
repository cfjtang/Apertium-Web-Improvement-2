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
$avalaible_config = array(
	array('name' => 'temp_dir',
	      'type' => 'text'),
	array('name' => 'apertium_command',
	      'type' => 'text'),
	array('name' => 'apertium_unformat_command',
	      'type' => 'text'),
	array('name' => 'apertium_re_commands',
	      'type' => 'text'),
	array('name' => 'apertium_des_commands',
	      'type' => 'text'),
	array('name' => 'languagetool_command',
	      'type' => 'text'),
	array('name' => 'languagetool_server_command',
	      'type' => 'text'),
	array('name' => 'languagetool_server_port',
	      'type' => 'text'),
	array('name' => 'aspell_command',
	      'type' => 'text'),
	array('name' => 'maligna_command',
	      'type' => 'text'),
	array('name' => 'ATD_command',
	      'type' => 'text'),
	array('name' => 'ATD_link',
	      'type' => 'text'),
	array('name' => 'spellcheckingtool',
	      'type' => 'select',
	      'options' => array('aspell', 'ATD')),
	array('name' => 'grammarproofingtool',
	      'type' => 'select',
	      'options' => array('languagetool', 'ATD')),
	array('name' => 'unzip_command',
	      'type' => 'text'),
	array('name' => 'zip_command',
	      'type' => 'text'),
	array('name' => 'pdf2html_command',
	      'type' => 'text'),
	array('name' => 'html2pdf_command',
	      'type' => 'text'),
	array('name' => 'convert_command',
	      'type' => 'text'),
	array('name' => 'ocr_command',
	      'type' => 'text'),
	array('name' => 'yuicompressor_command',
	      'type' => 'text'),
	array('name' => 'tmxmerger_command',
	      'type' => 'text'),
	array('name' => 'use_apertiumorg',
	      'type' => 'boolean'),
	array('name' => 'apertiumorg_homeurl',
	      'type' => 'text'),
	array('name' => 'apertiumorg_traddoc',
	      'type' => 'text'),
	array('name' => 'externTM_type',
	      'type' => 'select',
	      'options' => array('TMServer', '')),
	array('name' => 'externTM_url',
	      'type' => 'text'),
	array('name' => 'supported_format',
	      'type' => 'array')
	);

function set_new_config()
{
	/* Return an array of the new configuration */
	global $avalaible_config, $_POST;

	$new_config = array();
	foreach ($avalaible_config as $element) {
		switch ($element['type']) {
		case 'array':
			$new_config[$element['name']] = explode(',', $_POST[$element['name']]);
			break;
		case 'boolean':
			if ($_POST[$element['name']] == 'TRUE')
				$new_config[$element['name']] = TRUE;
			else
				$new_config[$element['name']] = FALSE;
			break;
		default:
			$new_config[$element['name']] = $_POST[$element['name']];
		}
	}
		
	return $new_config;
}

function display_choice($array)
{
	/* Display a menu depending on the 'type' field */
	global $config;
	
	switch ($array['type']) {
	case 'text':
		echo '<input type="text" name="' . $array['name'] . '" value="' . $config[$array['name']] . '" />';
		break;
	case 'select':
		echo '<select name="' . $array['name'] . '">';
		foreach ($array['options'] as $option) {
			echo "<option label='" . $option . "' value='" . $option . "' ";
			if ($option == $config[$array['name']])
				echo "selected='selected'";
			echo ">" . $option . "</option>\n";
		}
		echo '</select>';
		break;

	case 'array':
		echo '<input type="text" name="' . $array['name'] . '" value="' . implode(',',$config[$array['name']]) . '" />';
		break;

	case 'boolean':
		echo '<select name="' . $array['name'] . '">';
		echo "<option label='TRUE' value='TRUE' ";
		if ($config[$array['name']])
			echo "selected='selected'";
		echo ">TRUE</option>\n";
		echo "<option label='FALSE' value='FALSE' ";
		if (!$config[$array['name']])
			echo "selected='selected'";
		echo ">FALSE</option>\n";		
		echo '</select>';
		break;
		
	default:
		break;
	}
}

function display_menu()
{
	/* Display the menu as a table */
	global $avalaible_config;
	
	foreach ($avalaible_config as $choice) {
		echo "<tr><td>" . $choice['name'] . "</td><td>";
		display_choice($choice);
		echo "</td></tr>\n";
	}
}

function replace_in_config($name_var, $value)
{
	/* Replace the value of the variable $name_var by $value 
	 * in includes/config.php 
	 * Return true if operation is a success, false otherwise
	 */
	
	$source = file_get_contents('includes/config.php');
	$value_formatted = var_export($value, true);
	$replace = "\n\$" . $name_var . " = " . $value_formatted . ";";
	
	$new_source = preg_replace('#\n\$' . $name_var . '(.*?)\;#s', 
				   "$replace", $source);

	if ($handle = @fopen('includes/config.php', 'w')) {
		fwrite($handle, $new_source);
		fclose($handle);
		return TRUE;
	}
	else
		return FALSE;
}

if (isset($_POST['apply_config'])) {
	$new_config = set_new_config();
	$config_replacement = false;
	if (replace_in_config('config', $new_config)) {
		$config = $new_config;
		$config_replacement = true;
	}
}

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

function add_test($condition, $result_true, $recommend_true, $result_false, $recommend_false, $recommend_condition = FALSE) 
{
	/* If $condition, add $result_true in $test_result 
	 * In addition, if not $recommend_condition, add $recommend_true to $recommendation 
	 * Else $result_false and $recommend_false
	 */
	global $recommendation, $test_result;
	
	if ($condition) {
		$test_result[] = "<span style='color: green;'>" . $result_true . "</span>";
		if (!$recommend_condition)
			$recommendation[] = $recommend_true;
	}
	else {		
		$test_result[] = "<span style='color: red;'>" . $result_false . "</span>";
		$recommendation[] = $recommend_false;
	}
}

function test_command($command) {
	/* Return true if $command is avalaible 
	 * False otherwise
	 * Warning: this function launch the command */
	$return = explode(':', trim(exec('whereis ' . $command)));
	if (isset($return[1]) and trim($return[1]) != '')
		return TRUE;
	else
		return FALSE;
}

function test_apertium_command() {
	/* Test the existence of command apertium, apertium-unformat, apertium-re and apertium-des
	 * Return true if all of these commands are accessible, false otherwise */
	global $config;
	
	$command_to_test = array($config['apertium_command'], $config['apertium_unformat_command']);
	$return = TRUE;
	foreach ($command_to_test as $command) {
		if (!test_command($command)) {
			echo $command;
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
	
	return (file_get_contents($config['externTM_url']) != '');
}

function test_config()
{
	/* Return true if includes/config.php is writable */
	return is_writable('includes/config.php');
}

add_test(test_apertium_command(), "Your apertium installation is correctly detected", "You should set 'use_apertiumorg' on FALSE", "Your apertium installation isn't detected", "You should set 'use_apertiumorg' on TRUE", $config['use_apertiumorg'] == FALSE);

add_test(test_command('java'), "Your installation of JAVA is correctly detected", "You should use languagetool, yuicompressor and TMXMerger", "No JAVA installation are detected.", "You should set 'spellcheckingtool' on ATD, and 'grammarproofingtool' too. Or install JAVA to dispose of all functionnality.");

add_test(test_temp_dir(), "Your temp directory is writable", "", "Your temp directory isn't writable. Some functionnality are disabled.", "Change the right of your temp directory, or change the temp directory to a directory writable.");

add_test(test_command('pdftohtml'), "PdftoHtml program is correctly installed on your system.", "You should set 'pdf2html_command' on 'pdftohtml -c -noframes'", "PdftoHtml isn't detected on your system. PDF file management will not work.", "You should install pdftohtml program.", $config['pdf2html_command'] == 'pdftohtml -c -noframes');

add_test(test_command($config['convert_command']) && test_command($config['ocr_command']), "The convert program and the OCR program are correctly installed.", "", "The convert program and the OCR program aren't correctly installed. Picture management will not work.", "You should install 'convert' program and 'tesseract' program.");

add_test(test_externTM(), "An extern translation memory has been detected.", "You should set 'externTM_type' on 'TMServer', and keep 'externTM_url' as current.", "No extern translation memory is detected.", "You should set 'externTM_type' on ''. Or install a Translation Memory server, such as TMServer.", $config['externTM_type'] == 'TMServer');

add_test(test_config(), "Your configuration file is writable.", "", "Your configuration file isn't writable. You cannot save a new configuration.", "You should change the right of includes/config.php to enable the write on it.");

add_test(test_command($config['aspell_command']), "Aspell program is correctly installed", "You should set 'spellcheckingtool' on 'aspell'.", "Aspell program isn't installed.", "You should set 'spellcheckingtool' on 'ATD', or install aspell.", $config['spellcheckingtool'] == 'aspell');

/* Module management */

function display_module()
{
	/* Show informations on modules installed */
	global $modules, $modules_to_load;
	
	echo "<tr><td>Module</td><td>Description</td><td>Recommended</td><td>Dependencies</td><td>To load</td></tr>";
	$img_yes = 'images/yes.png';
	$img_no = 'images/no.png';
	foreach ($modules as $module_name => $module_data) {
		echo '<tr><td>' . $module_data['name'] . '</td><td><p>' . $module_data['description'] . '</p></td><td style="text-align: center;">';
	if ($module_data['default'])
		echo '<img src="'.$img_yes.'" alt="YES" style="width: 40px;" />';
	else
		echo '<img src="'.$img_no.'" alt="NO" style="width: 40px;" />';
	echo '</td><td style="text-align: center;">';
	if (CheckModule($module_name))
		echo '<img src="'.$img_yes.'" alt="YES" style="width: 40px;" />';
	else
		echo '<img src="'.$img_no.'" alt="NO" style="width: 40px;" />';
	echo '</td><td style="text-align: center;"><input type="checkbox" name="' . $module_name . '" ';
	if (in_array($module_name, $modules_to_load))
		echo 'checked="1" ';
	echo '/></td></tr>';
	}
}

function set_new_module()
{
	/* Return an array of the new list of modules to load */
	global $modules, $_POST;

	$return = array();
	foreach ($modules as $name => $value) {
		if (isset($_POST[$name]))
			$return[] = $name;
	}

	return $return;
}

if (isset($_POST['apply_modules'])) {
	$new_module = set_new_module();
	$module_replacement = false;
	if (replace_in_config('modules_to_load', $new_module)) {
		$modules_to_load = $new_module;
		$module_replacement = true;
	}
}
/* Page display */
page_header('Configure', array('CSS/style.css'));

?>

<div id='content'>
  <div id='header'>
    <h1>Apertium</h1>
    <p>Post-edition Interface : configure script</p>
  </div>
  <div id='frame'>
<?
	if (isset($config_replacement)) {
		if ($config_replacement)
			echo "<h3>The new config file has been successfully write !</h3>";
		else
			echo "<h3>The write of the new config file failed. Please verify includes/config.php right.</h3>";
	}
       if (isset($module_replacement)) {
	       if ($module_replacement)
			echo "<h3>The new list of modules has been successfully write !</h3>";
		else
			echo "<h3>The write of the new modules list failed. Please verify includes/config.php right.</h3>";
       }
	
?>
    <p>This script allow you to have a easily configure your Apertium Post-edition interface installation.<br /><br />
      Result of the test of your installation:
      <ul>
<?
	display_result($test_result);
?>
      </ul>
      Recommendations:
      <ul><span style='color: blue;'>
<?
	display_result($recommendation);
?>
      </span></ul>
      Choose your configuration:
    </p>
    <form action="" method="post">
      <table>
	<tr><td>Configuration</td><td>Avalaible Option</td></tr>
<?
	display_menu();
?>
      <tr><td></td><td><input type="submit" name="apply_config" value="Apply changes" /></td></tr>
      </table>
    </form>
    <p>Choose which modules to load:</p>
    <form action="" method="post">    
      <table>
<?
	display_module();
?>
        <tr><td></td><td></td><td></td><td></td><td><input type="submit" name="apply_modules" value="Apply changes" /></td></tr> 
      </table>
    </form>
    <br />
    <p>Do not forget to use the <a href='publish.php'>Publish script</a> to finalize your installation.</p> 
  </div>
</div>

<?
    page_footer();
?>