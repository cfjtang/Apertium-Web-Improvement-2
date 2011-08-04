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
include_once('includes/template.php');
include_once('modules.php');

/* Content to display with javascript */
$textsource_content_file = get_text('index', 'supported_format') . ' :<br /><i>';
foreach ($config['supported_format'] as $extension)
	$textsource_content_file .=  $extension . ' ';
$textsource_content_file .=  "</i><br />";
$textsource_content_file .= '<div>';
$textsource_content_file .= '<input type="file" name="in_doc" />';
$textsource_content_file .= '<input type="text" name="in_doc_type" />';
$textsource_content_file .= '</div>';	

$textsource_content_wiki = "<p>Insert article edit URL (Example: http://example.com/wiki/index.php?title=foo&action=edit): </p><input type='text' name='wiki_url' />";

$TMX_content_URL = '<label for="inputTMX">URL: </label><input type="text" id="inputTMX"name="inputTMX" value="" />';
$TMX_content_file = '<label for="inputTMXFile"></label><input type="file" name="inputTMXFile" id="inputTMXFile" />';
$TMX_content_externTM = '';
/* Extern Translation Memory Server */
switch ($config['externTM_type']) {
case 'TMServer':
	include('includes/TMServer.php');
	$TM = new TMServer($config['externTM_url']);
	$avalaible_pair_list = $TM->get_language_pairs_list(); 
        $TMX_content_externTM .= "<label for='TM_pair'>TMServer ";
        $TMX_content_externTM .= "(" . $TM->get_server_url() . ")</label> ";
        $TMX_content_externTM .= "<select name='TM_pair' id='TM_pair' \">";
        $TMX_content_externTM .= "<option label='' value='' selected='selected'></option>";
        foreach ($avalaible_pair_list as $pair)
		$TMX_content_externTM .= '<option label="' . $pair . '" value = "' . $pair . '">' . $pair . '</option>' . "\n";
        $TMX_content_externTM .= "</select>";
        break;
default:
        break;
}

page_header(get_text('index', 'title'), array('javascript/index.js', 'CSS/style.css'));
display_streamer();
?>
<div id='content'>
	<div id='header'>
	<h1>Apertium</h1>
	<p>Post-edition Interface</p>
	</div>
	<div id='frame'>
<form action="translate.php" method="post" enctype="multipart/form-data">
<fieldset><legend>Source text</legend>
<p><? write_text('index', 'menu_title'); ?>
<select id="input_type" name="input_type" style='float: right;' onchange='javascript:loadTextSourceContent();'>
<option label='<? write_text('index', 'menu1'); ?>' value='none' selected="selected" ><? write_text('index', 'menu1'); ?></option>
<?
if (module_is_loaded('FormattedDocumentHandling'))
?>
	<script type="text/javascript">document.write("<option label='<? write_text('index', 'menu2'); ?>' value='file' ><? write_text('index', 'menu2'); ?></option>");</script>
<script type='text/javascript'>document.write("<option label='<? write_text('index', 'menu3'); ?>' value='wiki' ><? write_text('index', 'menu3'); ?></option>");</script>
</select></p>
<span style='display: none;' id="textsource_content_file"><? echo $textsource_content_file; ?></span>
<span style='display: none;' id="textsource_content_wiki"><? echo $textsource_content_wiki; ?></span>
<div id="textsource_content"></div>
</fieldset>
<fieldset><legend>TMX</legend>
<p><? write_text('index', 'TMX_menu_title'); ?>
<select id="use_TMX" name="use_TMX" style='float: right;' onchange='javascript:loadTMXContent();'>
<option label='No' value='none' selected="selected" >No</option>
<option label='<? write_text('index', 'TMX_menu1'); ?>' value='URL' ><? write_text('index', 'TMX_menu1'); ?></option>
<option label='<? write_text('index', 'TMX_menu2'); ?>' value='file' ><? write_text('index', 'TMX_menu2'); ?></option>
<?
if ($config['externTM_type'] == 'TMServer') {
?>
	<option label='<? write_text('index', 'TMX_menu3'); ?>' value='externTM' ><? write_text('index', 'TMX_menu3'); ?></option>
<?
	}
?>
</select></p>
<div id="TMX_content_URL"><? echo $TMX_content_URL; ?></div>
<div id="TMX_content_file"><? echo $TMX_content_file; ?></div>
<div id="TMX_content_externTM"><? echo $TMX_content_externTM; ?></div>
<div id="TMX_content" style='clear: both;'></div>
<script type="text/javascript">
	document.getElementById('TMX_content_URL').style.display = 'none';
	document.getElementById('TMX_content_file').style.display = 'none';
	document.getElementById('TMX_content_externTM').style.display = 'none';
</script>
</fieldset>
<input type="submit" name="load_input" value="<? write_text('index', 'translate'); ?>" id='traduce_button' />
</form>
</div>
</div>
<?	
page_footer();
?>