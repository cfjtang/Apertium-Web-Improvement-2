<?//coding: utf-8
/*
	Apertium Web Post Editing tool
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús
*/

include_once('includes/language.php');
include_once('includes/template.php');
include_once('includes/strings.php');
include_once('includes/format.php');


init_environment();

$data = escape($_POST);


//begin input document handling
if($_FILES["in_doc"] AND !($_FILES["in_doc"]["error"] > 0))
{
	$data['input_doc_name'] = $_FILES["in_doc"]["name"];
	$data['input_doc_type'] = getFileFormat($data['input_doc_name'], $data['in_doc_type']);
	$data['text_input'] = convertFileToHTML($_FILES["in_doc"]["tmp_name"], $data['input_doc_type']);
	$data['input_doc'] = base64_encode(file_get_contents($_FILES["in_doc"]["tmp_name"]));
}
//end input document handling


if(isset($data['language_pair']) and is_installed($data['language_pair']))
{
	$source_language = explode('-', $data['language_pair']);
	$target_language = $source_language[1];
	$source_language = $source_language[0];
}

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
	
	$data['text_output'] = getApertiumTranslation($source_language, $target_language, 'html', $data['text_input'] /*, $data['pretrans_src'], $data['pretrans_dst']*/);
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
	$tmx = generateTmxOutput($source_language, $target_language, strip_tags($data['text_input']), strip_tags($data['text_output']));
	send_file('out.tmx', $tmx);
}
elseif(isset($data['submit_output']))
{
	$output_file = rebuildFileFromHTML($data['text_output'], $data['input_doc_type'], base64_decode($data['input_doc']));
	send_file('Translation-' . $data['language_pair'] . '-' . $data['input_doc_name'], $output_file);
}


page_header("Apertium translation", array(
	'javascript/browser_support.js',
	'javascript/textEditor.js',
	'javascript/paste_event.js',
	'javascript/ajax.js',
	'javascript/dictionaries.js',
	'javascript/main.js',
	'javascript/logging_lowlevel.js',
	'javascript/logging.js',
	'javascript/nodes.js',
	'CSS/textEditor.css'
));
?>

<form name="mainform" action="" method="post">

	<div id="left">		
		<div id="language_select">
			Select languages for the translation : 
			<select name="language_pair">
<?	foreach($language_pairs_list as $pair)
	{
?>				<option value="<?echo $pair;?>"<? if(isset($data['language_pair']) AND $data['language_pair'] == $pair) {?> selected="selected"<?} ?>><?echo str_replace('-', ' → ', $pair);?></option>
<?	}
	?>
			</select>
		</div>
		
		<div class="run_check">
			<input type="submit" name="check_input" value="Check for mistakes" />
		</div>
		
		<div class="input_box">
			<textarea id="text_in_js_off" name="text_input"><?echo strip_tags($data['text_input']);?></textarea>
			<div id="text_in_js_on" contentEditable="true" style="display:none;"><?echo nl2br_r($data['text_input']);?><br class="nodelete" contenteditable="false" /></div>
		</div>
		
		<div class="submit_text">
			<input type="submit" name="replace_input" value="Apply replacements" />
			<input type="submit" name="submit_input" value="Translate text" />
		</div>
		
		<div class="more_options">
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
				Manual replacements : 
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
			
			<div style="display: none;">Dictionary :
				<select id="dictionary_src"><option value=""></option></select>
			</div>
		</div>
	</div>
	
	<div id="right">
		<div class="run_check">
			<input type="submit" name="check_output" value="Check for mistakes" />
		</div>
		
		<div class="input_box">
			<textarea id="text_out_js_off" name="text_output"><?echo strip_tags($data['text_output']);?></textarea>
			<div id="text_out_js_on" contentEditable="true" style="display:none;"><?echo nl2br_r($data['text_output']);?><br class="nodelete" contenteditable="false" /></div>
		</div>
		
		<div class="submit_text">
			<input type="submit" name="replace_output" value="Apply replacements" />
			<input type="submit" name="submit_output_tmx" value="Generate TMX" />
			<input type="submit" name="submit_output" value="Get translation result" />
			<input id="get_logs" type="submit" name="get_logs" value="Get logs" style="display:none;" />
		</div>
		
		<div class="more_options">
			<div>
				Manual replacements : 
				<ul id="posttrans_list">
<?
	if(is_array($data['posttrans_del']))
	{
		foreach($data['posttrans_del'] as $index => $nothing)
		{
			unset($data['posttrans_src'][$index]);
			unset($data['posttrans_dst'][$index]);
		}
	}
	
	if(is_array($data['posttrans_src']))
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
?>				</ul>
				<input id="posttrans_add" name="posttrans_add" type="submit" value="+" />
			</div>
			
			<div style="display: none;">Dictionary :
				<select id="dictionary_dst"><option value=""></option></select>
			</div>
		</div>
	</div>
	
	<input type="hidden" name="input_doc" value="<?echo $data['input_doc'];?>" />
	<input type="hidden" name="input_doc_type" value="<?echo $data['input_doc_type'];?>" />
	<input type="hidden" name="input_doc_name" value="<?echo $data['input_doc_name'];?>" />

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