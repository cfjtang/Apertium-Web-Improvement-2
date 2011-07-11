<?//coding: utf-8
/* Apertium Web Post Editing Tool
 * Script for publishing (to pass on a public site)
 *
 * Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
 * Mentors : Arnaud Vié, Luis Villarejo
 */
set_time_limit(0);
include_once('includes/config.php');
include_once('includes/template.php');
include_once('modules.php');

//init_environment();

page_header('Publish', array('CSS/style.css'));
?>

<div id='content'>
  <div id='header'>
    <h1>Apertium</h1>
    <p>Post-edition Interface : publishing script</p>
  </div>
  <div id='frame'>
    <p>This script allow you to have a script ready to be published on the internet.<br />
      Choose what you want to do:
    </p>
    <form action="" method="post">
      <table>
	<tr><td>Task</td><td>Do it</td></tr>
	<tr><td>Regenerate Javascript according to loaded modules</td><td><input type="checkbox" checked="1" name="regen_JS" /></td></tr>
	<tr><td>Compress Javascript files to speed-up page loading</td><td><input type="checkbox" checked="1" name="compress_JS" /></td></tr>
	<tr><td>Compress CSS files to speed-up page loading</td><td><input type="checkbox" checked="1" name="compress_CSS" /></td></tr>
	<tr><td>Delete this file to avoid its use by malicious user</td><td><input type="checkbox" checked="1" name="delete_myself" /></td></tr>
	<tr><td>Uncompress CSS files to allow the edition</td><td><input type="checkbox" name="uncompress_CSS" /></td></tr>
	<tr><td>Uncompress Javascript files to allow the edition</td><td><input type="checkbox" name="uncompress_JS" /></td></tr>
	<tr><td></td><td><input type="submit" name="Apply" value="Apply" /></td></tr>
      </table>
    </form>
    <br />
    <div style='color:red;'>
<?
if (isset($_POST['regen_JS'])) {
      echo "Modules loaded : ";
      foreach ($modules_to_load as $mod)
	      echo $mod . ' ';
      echo '<br />';
      echo "Generate javascript/main.js...<br />";
      gen_templateJS('javascript/main.tmpl', 'javascript/main.js');
      echo "Generate javascript/textEditor.js...<br />";
      gen_templateJS('javascript/textEditor.tmpl', 'javascript/textEditor.js');
      echo "<br />";
}

if (isset($_POST['compress_JS'])) {
	echo "Javascript files to compress : ";
	$js_files = scanDirForFileExt('javascript', 'js');
	foreach ($js_files as $name)
		echo $name . ' ';
	echo '<br />';
	echo "Backup non-compressed files...<br />";
	foreach ($js_files as $name) {
		if (!file_exists($name . '-max'))
			executeCommand('cp ' . $name . ' ' . $name . '-max', '', $return_value, $return_status);
	}
	echo "Compress files...<br />";
	foreach ($js_files as $name) {
		$command = $config['yuicompressor_command'] . ' --type js --charset utf-8 -o ' . $name . ' ' . $name .'-max';
		executeCommand($command, '', $return_value, $return_status);
		echo $name . " compressed !<br />";
	}
	echo "<br />";
}

if (isset($_POST['uncompress_JS'])) {
	echo "Javascript files to uncompress : ";
	$js_files = scanDirForFileExt('javascript', 'js-max');
	foreach ($js_files as $name)
		echo $name . ' ';
	echo '<br />';
	echo "Uncompress files...<br />";
	foreach ($js_files as $name) {
		$name = substr($name, 0, -4);
		executeCommand('mv '. $name . '-max ' . $name, '', $return_value, $return_status);
		echo $name . " uncompressed !<br />";
	}
	echo "<br />";
}

if (isset($_POST['compress_CSS'])) {
	echo "CSS files to compress : ";
	$css_files = scanDirForFileExt('CSS', 'css');
	foreach ($css_files as $name)
		echo $name . ' ';
	echo '<br />';
	echo "Backup non-compressed files...<br />";
	foreach ($css_files as $name) {
		if (!file_exists($name . '-max'))
			executeCommand('cp ' . $name . ' ' . $name . '-max', '', $return_value, $return_status);
	}
	echo "Compress files...<br />";
	foreach ($css_files as $name) {
		$command = $config['yuicompressor_command'] . ' --type css --charset utf-8 -o ' . $name . ' ' . $name .'-max';
		executeCommand($command, '', $return_value, $return_status);
		echo $name . " compressed !<br />";
	}
	echo "<br />";
}

if (isset($_POST['uncompress_CSS'])) {
	echo "CSS files to uncompress : ";
	$css_files = scanDirForFileExt('CSS', 'css-max');
	foreach ($css_files as $name)
		echo $name . ' ';
	echo '<br />';
	echo "Uncompress files...<br />";
	foreach ($css_files as $name) {
		$name = substr($name, 0, -4);
		executeCommand('mv '. $name . '-max ' . $name, '', $return_value, $return_status);
		echo $name . " uncompressed !<br />";
	}
	echo "<br />";
}

if (isset($_POST['delete_myself'])) {
	$pagename = 'publish.php';
	echo "Delete " . $pagename . "...<br />";
	unlink($pagename);
}

?>
    </div> 
  </div>
</div>


<?
    page_footer();
?>