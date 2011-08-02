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

	case 'load_input':
	       	/* Load the entry */
		
		switch ($data['input_type']) {
		case 'none':
			break;
		case 'file':
			/* File management */
			global $_FILES;
			
			if(isset($_FILES["in_doc"]) AND !($_FILES["in_doc"]["error"] > 0)) {
				//if handling formatted document
				$data['input_doc_name'] = $_FILES["in_doc"]["name"];
				$data['input_doc_type'] = getFileFormat($data['input_doc_name'], $data['in_doc_type']);
				$data['text_input'] = convertFileToHTML($_FILES["in_doc"]["tmp_name"], $data['input_doc_type']);
				$data['input_doc'] = base64_encode(file_get_contents($_FILES["in_doc"]["tmp_name"]));
			}
			break;
		case 'wiki':
			/* WikiPage */
			global $wiki_form, $config;
			$url = $data['wiki_url'];
		
			$wiki_form = array();
			if (!empty($url) and filter_var($url, FILTER_VALIDATE_URL, FILTER_FLAG_PATH_REQUIRED)) {
				/* Security issue */
				$source = file_get_contents($url, false, stream_context_create(array('http' => array('method' => 'GET', 'header' => 'User-Agent: Apertium AWI <commial@gmail.com>\r\n'))));
				
				/* Get informations for the future POST, stocked in $wiki_form */
				preg_match('#<form id="editform" name="editform" method="post" action="(.*?)" enctype="multipart/form-data">(.*?)</form>#s', $source, $match_form);
				unset($source);
				$wiki_form['action'] = 'http://' . parse_url($url, PHP_URL_HOST) . '/' . parse_url($url, PHP_URL_PATH) . '/' . $match_form[1];
				$form_content = $match_form[2];
				unset($match_form);
			
				preg_match_all('#<input (.*?)>#s', $form_content, $match_input);
				$wiki_form['input'] = $match_input[0];
				unset($match_input);
				
				preg_match('#<textarea (.*?)>(.*?)</textarea>#s', $form_content, $match_textarea);
				$wiki_form['textarea'] = $match_textarea[1];
				$content = $match_textarea[2];
				unset($match_textarea);

				/* write the main content in a file, 
				 * to use convertFileToHTML method */
				$tempname = tempnam($config['temp_dir'], 'ap_temp_');
				$tempname = $config['temp_dir'] . basename($tempname);
				$file = fopen($tempname, "w");
				fwrite($file, $content);
				fclose($file);
				
				$data['text_input'] = convertFileToHTML($tempname, 'mediawiki');
				unlink($tempname);
				break;
			}
		default:
			break;
		}
		
		break;

	case 'wiki_end_submit':
		$wiki_todo = 'wpSave';
	case 'wiki_end_preview':
		if (!isset($wiki_todo))
			$wiki_todo = 'wpPreview';
		
		$wiki_form = unserialize(base64_decode($data['wiki_form']));
		
		echo '<form id="tosubmit" name="tosubmit" action="' . $wiki_form['action'] . '" method="post">';
		foreach ($wiki_form['input'] as $input)
			echo $input;
		
		echo '<textarea ' . $wiki_form['textarea'] . '>';
		
		/* Remove hr tag using rebuildFileFromHTML method */
		echo rebuildFileFromHTML($data['text_output'], 'mediawiki','');
		
		echo '</textarea>';
		
		echo '<input type="hidden" name="' . $wiki_todo . '" value="' . $wiki_todo . '"/>';
		echo '<input type="submit" name="' . $wiki_todo . '" value="' . $wiki_todo . '"/>';
		echo '</form>';
		
		/* Auto-submit */
		echo "<script type='text/javascript'>document.tosubmit.submit();</script>";
		exit();
	default:
		break;
	}
}

$to_define = array('text_output', 'text_input', 'input_doc', 'input_doc_type', 'input_doc_name', 'pretrans_src', 'posttrans_src');
define_var($to_define);

/* Define $source_language, $target_language for security issues */
$target_language = '';
$source_language = '';

/* Do the right action */
$avalaible_action = array('check_input', 'submit_input', 'replace_input', 'replace_output', 'check_output', 'submit_output_tmx', 'submit_output', 'load_input', 'wiki_end_submit', 'wiki_end_preview');
$action = get_action($avalaible_action);
do_action($action);

/* Specific cases */
if(isset($data['language_pair']) and is_installed($data['language_pair']))
{
	$source_language = explode('-', $data['language_pair']);
	$target_language = $source_language[1];
	$source_language = $source_language[0];
	$trans->set_source_language($source_language);
	$trans->set_target_language($target_language);
}

/* TMX Input management */
if (isset($_FILES["inputTMXFile"]) && !($_FILES["inputTMXFile"]["error"] > 0))
	$trans->set_inputTMX(file_get_contents($_FILES["inputTMXFile"]["tmp_name"]));

if (isset($data['inputTMX']) && $data['inputTMX'] != '')
		$trans->set_inputTMX($data['inputTMX']);

if (isset($data['TM_pair']) && $data['TM_pair'] != '') {
	switch ($config['externTM_type']) {
	case 'TMServer':
		include('includes/TMServer.php');
		$TM = new TMServer($config['externTM_url']);
		if (in_array($data['TM_pair'], $TM->get_language_pairs_list()))
			$trans->set_inputTMX(file_get_contents($TM->get_real_URL($data['TM_pair'])));
		break;
	default:
		break;
	}
}

if (isset($data['inputTMX_content']))
	$trans->set_inputTMX(base64_decode($data['inputTMX_content']));

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
if (module_is_loaded('SearchAndReplace')) {
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
if (module_is_loaded('LinkExternalDictionnaries')) {		     
	echo '<div style="display: none;">' . get_text('translate', 'dictionary') . ' :';
	echo '<select id="dictionary_src"><option value=""></option></select>';
	echo '</div>';
}
?>
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
        <input type="submit" name="add_TM" value="<? write_text('translate', 'add_TMX'); ?>" />
<?
	break;
default:
	break;
}
?>
	</div>
		
	<div class="more_options">
<?
if (module_is_loaded('SearchAndReplace')) {
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
	}

       if(isset($data['posttrans_add']))
	       generateReplacementLine('posttrans', '', '', 'apply', count($data['posttrans_src']));
?>
	</ul>
	<input id="posttrans_add" name="posttrans_add" type="submit" value="+" />
</div>
<?
}
if (module_is_loaded('LinkExternalDictionnaries')) {
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
  <input type="hidden" name="inputTMX_content" value="<? echo base64_encode($trans->get_inputTMX()); ?>" />
</div>
<?
if (isset($_POST['wiki_url']) && $_POST['wiki_url'] != '') {
	/* In the case of a wiki page */
	echo '<input type="hidden" name="wiki_form" value="' . base64_encode(serialize($wiki_form)) . '" />';
	echo '<input type="submit" style="font-weight : bolder;" name="wiki_end_submit" value="Save wiki page" />';
	echo '<input type="submit" style="font-weight : bolder;" name="wiki_end_preview" value="Preview wiki page" />';
}
?>
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
