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

$to_define = array('text_output', 'text_input', 'input_doc', 'input_doc_type', 'input_doc_name');
foreach ($to_define as $name_var) {
	if (!array_key_exists($name_var, $data))
		$data[$name_var] = '';
}
if (!array_key_exists('text_input', $data))
	$data['text_input'] = '';

/* Define $source_language, $target_language for security issues */
$target_language = '';
$source_language = '';

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

if (isset($data['inputTMX']))
	$trans->set_inputTMX($data['inputTMX']);

if(isset($data['check_input']))
{
	//run grammar and spell checking on input
	//changes $data['text_input']
	
	$data['text_input'] = checkForMistakes($data['text_input'], $source_language);
}
elseif(isset($data['submit_input']))
{
	//translate input
	//changes $data['text_output']
	$data['text_output'] = $trans->getTranslation($data['text_input'] /*, $data['pretrans_src'], $data['pretrans_dst']*/);
}
elseif(isset($data['replace_input']))
{
	$data['text_input'] = str_replace_words($data['pretrans_src'], $data['pretrans_dst'], $data['pretrans_case'], $data['text_input']);
}
elseif(isset($data['replace_output']))
{
	$data['text_output'] = str_replace_words($data['posttrans_src'], $data['posttrans_dst'], $data['posttrans_case'], $data['text_output']);
}
elseif(isset($data['check_output']))
{
	//run grammar and spell checking on output
	
	$data['text_output'] = checkForMistakes($data['text_output'], $target_language, $source_language);
}
elseif(isset($data['submit_output_tmx']))
{
	//generate translation memory
	$tmx = $trans->generateTmxOutput(strip_tags($data['text_input']), strip_tags($data['text_output']));
	send_file('out.tmx', $tmx);
}
elseif(isset($data['submit_output']))
{
	if ($data['input_doc_type'] == 'tif')
		/* Tif file are managed as TXT file after the translation */
		$data['input_doc_name'] .= '.txt';
	$output_file = rebuildFileFromHTML($data['text_output'], $data['input_doc_type'], base64_decode($data['input_doc']));
	send_file('Translation-' . $data['language_pair'] . '-' . $data['input_doc_name'], $output_file);
}


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
<form name="mainform" action="" method="post" style='border: 1px solid silver; padding: 10px;'>
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
	<?php
	foreach (LoadModules() as $module_name)
	WriteButtonInput($module_name);
?>
		
<input type="submit" name="submit_input" value="<? write_text('translate', 'button_translate'); ?>" />
	</div>
		
	<div class="more_options">
	<?php
	if (module_is_load('SearchAndReplace')) {
		?>
		<div>
		<? /*/ Fields for manual translation, which doesn't work at the moment ?>
		     Manual translations : 
		     <ul id="pretrans_list">
		     <?	
		     if(is_array($data['pretrans_del']))
		     {
		     foreach($data['pretrans_del'] as $index => $nothing)
		     {
		     unset($data['pretrans_src'][$index]);
		     unset($data['pretrans_dst'][$index]);
		     }
		     }
	
		     if(is_array($data['pretrans_src']))
		     {
		     foreach($data['pretrans_src'] as $ind => $val)
		     {
		     generatePretransLine($val, $data['pretrans_dst'][$ind], $ind);
		     }
		     }
	
		     if(isset($data['pretrans_add']))
		     {
		     generatePretransLine('', '', count($data['pretrans_src']));
		     }
		     ?>				</ul>
		     <input id="pretrans_add" name="pretrans_add" type="submit"  value="+" />
		     <? //*/ ?>
		<? write_text('translate', 'manual_replacement'); ?> : 
		<ul id="pretrans_list">
		<?
		if(isset($data['pretrans_del']) AND is_array($data['pretrans_del']))
		{
			foreach($data['pretrans_del'] as $index => $nothing)
			{
				unset($data['pretrans_src'][$index]);
				unset($data['pretrans_dst'][$index]);
			}
		}
	
		if(isset($data['pretrans_src']) AND is_array($data['pretrans_src']))
		{
			foreach($data['pretrans_src'] as $ind => $val)
			{
				generateReplacementLine('pretrans', $val, $data['pretrans_dst'][$ind], $data['pretrans_case'][$ind], $ind);
			}
		}
	
		if(isset($data['pretrans_add']))
		{
			generateReplacementLine('pretrans', '', '', 'apply', count($data['pretrans_src']));
		}
		?>				</ul>
		<input id="pretrans_add" name="pretrans_add" type="submit" value="+" />
		</div>
		<?php
	}
if (module_is_load('LinkExternalDictionnaries')) {		     
	echo '<div style="display: none;">' . get_text('translate', 'dictionary') . ' :';
	echo '<select id="dictionary_src"><option value=""></option></select>';
	echo '</div>';
}
?>
<input type="text" name="inputTMX" value="<? echo $trans->get_inputTMX(); ?>" /><input type="submit" name="useTMX" value="Use TMX" />
</div>
</td>
<td>
<div class="language_select">
	<select name="language_pair">
	<?	foreach($language_pairs_list as $pair)
{
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
<td style = 'width:45%;'>		
<div class="input_box">
	<textarea id="text_out_js_off" name="text_output"><?echo strip_tags($data['text_output']);?></textarea>
	<div id="text_out_js_on" contentEditable="true" style="display:none;"><?echo nl2br_r($data['text_output']);?><br class="nodelete" contenteditable="false" /></div>
	</div>
		
	<div class="submit_text">
<?php
	foreach (LoadModules() as $module_name)
	     WriteButtonOutput($module_name);
?>
		
	<input type="submit" name="submit_output_tmx" value="<? write_text('translate', 'gen_TMX'); ?>" />
	</div>
		
	<div class="more_options">
<?php
	if (module_is_load('SearchAndReplace')) {
?>
		<div>
		<? write_text('translate', 'manual_replacement'); ?> : 
		<ul id="posttrans_list">
<?
		if(isset($data['posttrans_del']) AND is_array($data['posttrans_del']))
		{
			foreach($data['posttrans_del'] as $index => $nothing)
			{
				unset($data['posttrans_src'][$index]);
				unset($data['posttrans_dst'][$index]);
			}
		}
	
		if(isset($data['posttrans_src']) AND is_array($data['posttrans_src']))
		{
			foreach($data['posttrans_src'] as $ind => $val)
			{
				generateReplacementLine('posttrans', $val, $data['posttrans_dst'][$ind], $data['posttrans_case'][$ind], $ind);
			}
		}
	
		if(isset($data['posttrans_add']))
		{
			generateReplacementLine('posttrans', '', '', 'apply', count($data['posttrans_src']));
		}
?>
		</ul>
		<input id="posttrans_add" name="posttrans_add" type="submit" value="+" />
		</div>
<?php
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