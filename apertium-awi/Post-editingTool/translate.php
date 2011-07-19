<?//coding: utf-8
/*
  Apertium Web Post Editing tool
	
  Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
  Mentors : Luis Villarejo, Mireia Farrús

  Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
  Mentors : Arnaud Vié, Luis Villarejo
*/

include_once('includes/config.php');
include('includes/language.php');
include_once('modules.php');
include_once('includes/template.php');
include_once('includes/strings.php');

init_environment();

$data = escape($_POST);

function define_var($array) {
	/* Foreach element of $array, if $data[element] isn't define, define it */
	global $data;
	
	foreach ($array as $name_var) {
		if (!array_key_exists($name_var, $data))
			$data[$name_var] = '';
	}
}

function get_action($array) {
	/* Return the name of the first element of $array corresponding to a $data[element] set */
	global $data;
	
	$action = '';
	foreach ($array as $name_var) {
		if (isset($data[$name_var])) {
			$action = $name_var;
			break;
		}
	}
			
	return $action;
}

function do_action($action_name) {
	/* Do the action for the corresponding $action_name */
	global $data;
	
	switch ($action_name) {
	case 'check_input':
		/* run grammar and spell checking on input
		 * changes $data['text_input'] */
		global $source_language;
		$data['text_input'] = checkForMistakes($data['text_input'], $source_language);
		break;
	case 'submit_input':
		/* translate input
		 * changes $data['text_output'] */
		global $trans;
		$data['text_output'] = $trans->getTranslation($data['text_input']);
		break;
	case 'replace_input':
		$data['text_input'] = str_replace_words($data['pretrans_src'], $data['pretrans_dst'], $data['pretrans_case'], $data['text_input']);
		break;
	case 'replace_output':
		$data['text_output'] = str_replace_words($data['posttrans_src'], $data['posttrans_dst'], $data['posttrans_case'], $data['text_output']);
		break;
	case 'check_output':
		/* run grammar and spell checking on output */
		global $target_language, $source_language;
		$data['text_output'] = checkForMistakes($data['text_output'], $target_language, $source_language);
		break;
	case 'submit_output_tmx':
		/* generate translation memory */
		global $trans;
		$tmx = $trans->generateTmxOutput(strip_tags($data['text_input']), strip_tags($data['text_output']));
		send_file('out.tmx', $tmx);
		break;
	case 'submit_output':
		if ($data['input_doc_type'] == 'tif')
			/* Tif file are managed as TXT file after the translation */
			$data['input_doc_name'] .= '.txt';
		$output_file = rebuildFileFromHTML($data['text_output'], $data['input_doc_type'], base64_decode($data['input_doc']));
		send_file('Translation-' . $data['language_pair'] . '-' . $data['input_doc_name'], $output_file);
		break;
	default:
		break;
	}
}

$to_define = array('text_output', 'text_input', 'input_doc', 'input_doc_type', 'input_doc_name');
define_var($to_define);

/* Define $source_language, $target_language for security issues */
$target_language = '';
$source_language = '';

$avalaible_action = array('check_input', 'submit_input', 'replace_input', 'replace_output', 'check_output', 'submit_output_tmx', 'submit_output');
$action = get_action($avalaible_action);
do_action($action);

if(isset($_FILES["in_doc"]) AND !($_FILES["in_doc"]["error"] > 0))
{
	//if handling formatted document
	$data['input_doc_name'] = $_FILES["in_doc"]["name"];
	$data['input_doc_type'] = getFileFormat($data['input_doc_name'], $data['in_doc_type']);
	$data['text_input'] = convertFileToHTML($_FILES["in_doc"]["tmp_name"], $data['input_doc_type']);
	$data['input_doc'] = base64_encode(file_get_contents($_FILES["in_doc"]["tmp_name"]));
}

if(isset($data['language_pair']) and is_installed($data['language_pair']))
{
	$source_language = explode('-', $data['language_pair']);
	$target_language = $source_language[1];
	$source_language = $source_language[0];
	$trans->set_source_language($source_language);
	$trans->set_target_language($target_language);
}

/* TMX Input management */
if (isset($_FILES["inputTMXFile"]))
	$trans->set_inputTMX($_FILES["inputTMXFile"]["tmp_name"]);

if (isset($data['inputTMX'])) {
	if (!(isset($data['inputTMXtype']) && ($data['inputTMXtype'] == 'FILE')))
		$trans->set_inputTMX($data['inputTMX']);
}

/* Page display */
$javascript_header = array(
	'CSS/textEditor.css',
	'CSS/style.css',
	'javascript/browser_support.js',
	'javascript/textEditor.js',
	'javascript/ajax.js',
	'javascript/main.js');
$javascript_header = AddJSDependencies($javascript_header);

page_header(get_text('translate', 'title'), $javascript_header);
display_streamer();

?>
<form name="mainform" action="" method="post" style='border: 1px solid silver; padding: 10px;' enctype="multipart/form-data">
<div class="language_select">
	<? write_text('translate', 'select_language'); ?> : 
</div>
	<table>
	<tr><td style = 'width:45%;vertical-align:top;'>
	<div class="input_box">
	<textarea id="text_in_js_off" name="text_input"><?echo strip_tags($data['text_input']);?></textarea>
	<div id="text_in_js_on" contenteditable="true" style="display:none;"><?echo nl2br_r($data['text_input']);?><br class="nodelete" contenteditable="false" /></div>
	</div>
		
	<div class="submit_text">
<?
foreach (LoadModules() as $module_name)
	WriteButtonInput($module_name);
?>
		
<input type="submit" name="submit_input" value="<? write_text('translate', 'button_translate'); ?>" />
	</div>
		
	<div class="more_options">
<?
if (module_is_load('SearchAndReplace')) {
	?>
	<div>
<? write_text('translate', 'manual_replacement'); ?> : 
		<ul id="pretrans_list">
<?
	if(isset($data['pretrans_del']) AND is_array($data['pretrans_del'])) {
		foreach($data['pretrans_del'] as $index => $nothing) {
			unset($data['pretrans_src'][$index]);
			unset($data['pretrans_dst'][$index]);
		}
	}
	
	if(isset($data['pretrans_src']) AND is_array($data['pretrans_src'])) {
		foreach($data['pretrans_src'] as $ind => $val)
			generateReplacementLine('pretrans', $val, $data['pretrans_dst'][$ind], $data['pretrans_case'][$ind], $ind);
	}
	
	if(isset($data['pretrans_add']))
		generateReplacementLine('pretrans', '', '', 'apply', count($data['pretrans_src']));
?>
		</ul>
		<input id="pretrans_add" name="pretrans_add" type="submit" value="+" />
	</div>
<?
	}
if (module_is_load('LinkExternalDictionnaries')) {		     
	echo '<div style="display: none;">' . get_text('translate', 'dictionary') . ' :';
	echo '<select id="dictionary_src"><option value=""></option></select>';
	echo '</div>';
}
?>
<table>
<?
/* Extern Translation Memory Server */
switch ($config['externTM_type']) {
case 'TMServer':
	include('includes/TMServer.php');
	$TM = new TMServer($config['externTM_url']);
	$avalaible_pair_list = $TM->get_language_pairs_list();
	$new_pair = FALSE;
	
	if (isset($data['inputTMXtype']) && ($data['inputTMXtype'] == 'extern') && isset($data['TM_pair']) && in_array($data['TM_pair'], $avalaible_pair_list)) {
		$trans->set_inputTMX($TM->get_real_URL($data['TM_pair']));
		$new_pair = $data['TM_pair'];
	}
	
	?>
	<tr>
	<td><p>Extern Translation Memory Server:
<?
		 echo '<h4>' . $TM->get_server_url() . '</h4>';
?>
	Avalaible language on this server: <select name='TM_pair'>
<?
        foreach ($avalaible_pair_list as $pair) {
		echo '<option label="' . $pair . '" value = "' . $pair . '" ';
		if ($pair == $new_pair)
			echo 'selected = "selected"';
		echo '>' . $pair . '</option>' . "\n";
	}
?>
</select></td>
<td><input type="radio" name="inputTMXtype" value="extern" <? if (isset($data['inputTMXtype']) && ($data['inputTMXtype'] == 'extern')) echo 'checked="1"';?>/></td></tr>
<?
	break;
default:
	break;
}

?>
<tr>
  <td><input type="text" name="inputTMX" value="<? echo $trans->get_inputTMX(); ?>" /><input type="submit" name="useTMX" value="Use TMX" /></td>
  <td><input type="radio" name="inputTMXtype" value="URL" <? if (isset($data['inputTMXtype']) && ($data['inputTMXtype'] == 'URL')) echo 'checked="1"';?>/></td>
</tr>
<tr>
  <td><input type="file" name="inputTMXFile" /></td>
  <td><input type="radio" name="inputTMXtype" value="FILE" <? if (isset($data['inputTMXtype']) && ($data['inputTMXtype'] == 'FILE')) echo 'checked="1"';?>/></td>
</tr>
</table>
</div>
</td>
<td>
<div class="language_select">
	<select name="language_pair">
<?	
foreach($language_pairs_list as $pair) {
	?>				<option value="<?echo $pair;?>"<? if(isset($data['language_pair']) AND $data['language_pair'] == $pair) {?> selected="selected"<?} ?>><?echo str_replace('-', ' → ', $pair);?></option>
<?	}
?>
	</select>
        <br /><br /><br />
<?
	display_ajax_loader();
?>	
</div>
</td>
<td style = 'width:45%;vertical-align:top;'>		
<div class="input_box">
	<textarea id="text_out_js_off" name="text_output"><?echo strip_tags($data['text_output']);?></textarea>
	<div id="text_out_js_on" contentEditable="true" style="display:none;"><?echo nl2br_r($data['text_output']);?><br class="nodelete" contenteditable="false" /></div>
	</div>
		
	<div class="submit_text">
<?
	foreach (LoadModules() as $module_name)
	     WriteButtonOutput($module_name);
?>
		
	<input type="submit" name="submit_output_tmx" value="<? write_text('translate', 'gen_TMX'); ?>" />
<?
/* Extern Translation Memory Server */
switch ($config['externTM_type']) {
case 'TMServer':
	if (isset($data['add_TM'])) {
		$tmx = $trans->generateTmxOutput(strip_tags($data['text_input']), strip_tags($data['text_output']));
		if ($TM->send_TM($tmx))
			echo "<br />TMX file successfully added !<br />";
		else
			echo "<br />An error occured...<br />";
	}
?>
        <input type="submit" name="add_TM" value="Add TMX to the TMServer" />
<?
	break;
default:
	break;
}
?>
	</div>
		
	<div class="more_options">
<?
if (module_is_load('SearchAndReplace')) {
?>
		<div>
<? 
	write_text('translate', 'manual_replacement'); ?> : 
		<ul id="posttrans_list">
<?
	if(isset($data['posttrans_del']) AND is_array($data['posttrans_del'])) {
		foreach($data['posttrans_del'] as $index => $nothing)
		{
			unset($data['posttrans_src'][$index]);
			unset($data['posttrans_dst'][$index]);
		}
	}
	
	if(isset($data['posttrans_src']) AND is_array($data['posttrans_src'])) {
		foreach($data['posttrans_src'] as $ind => $val)
			generateReplacementLine('posttrans', $val, $data['posttrans_dst'][$ind], $data['posttrans_case'][$ind], $ind);
	
		if(isset($data['posttrans_add']))
			generateReplacementLine('posttrans', '', '', 'apply', count($data['posttrans_src']));
?>
		</ul>
		<input id="posttrans_add" name="posttrans_add" type="submit" value="+" />
		</div>
<?
	}
}
if (module_is_load('LinkExternalDictionnaries')) {
	echo '<div style="display: none;">' . get_text('translate', 'dictionary') . ' :';
	echo '<select id="dictionary_dst"><option value=""></option></select>';
	echo '</div>';
}
?>
</div>
</td></tr>
</table>
<div>
  <input type="hidden" name="input_doc" value="<?echo $data['input_doc'];?>" />
  <input type="hidden" name="input_doc_type" value="<?echo $data['input_doc_type'];?>" />
  <input type="hidden" name="input_doc_name" value="<?echo $data['input_doc_name'];?>" />
</div>
</form>

<ul style="display:none;" id="list_elements_models">
<? 
generateReplacementLine('pretrans', '', '', 'apply', 'NUM');
generateReplacementLine('posttrans', '', '', 'apply', 'NUM');
?>
</ul>

<?	
page_footer();
?>
