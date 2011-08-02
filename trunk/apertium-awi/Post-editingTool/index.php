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

//init_environment();


page_header(get_text('index', 'title'), array('CSS/style.css'));
display_streamer();
?>
<div id='content'>
	<div id='header'>
	<h1>Apertium</h1>
	<p>Post-edition Interface</p>
	</div>
	<div id='frame'>

<p><? write_text('index', 'menu_title'); ?></p>
<form action="translate.php" method="post" enctype="multipart/form-data">
<input type="radio" id="input_type_none" name="input_type" value="none" checked="checked"/><? write_text('index', 'menu1'); ?><br />
<?php
if (module_is_loaded('FormattedDocumentHandling'))
	echo "<script type='text/javascript'>document.write(\"<input type='radio' name='input_type' id='input_type_file' value='file' />" . get_text('index', 'menu2') . "\");</script>";
?>
<?php
if (module_is_loaded('FormattedDocumentHandling')) {
	$to_write = '<br /><br />' . get_text('index', 'supported_format') . ' :<br /><i>';
	foreach ($config['supported_format'] as $extension)
			$to_write .=  $extension . ' ';
	$to_write .=  "</i><br />";
	$to_write .= '<div>';
	$to_write .= '<input type="file" name="in_doc" onchange="javascript:document.getElementById(\\\'input_type_file\\\').checked=true;"/>';
	$to_write .= '<input type="text" name="in_doc_type" />';
	$to_write .= '</div>';	
	/* To avoid non-Javascript user */
	echo '<script type="text/javascript">document.write(\'' . $to_write . '\');</script>';
}
?>
<script type='text/javascript'>document.write("\n<p><input type='radio' name='input_type' id='input_type_wiki' value='wiki' />Want to translate a WikiPage ?</p>\nInsert article edit URL (http://example.com/wiki/index.php?title=foo&action=edit): <br /><input type='text' name='wiki_url' onclick=\"javascript:document.getElementById('input_type_wiki').checked=true;\" />");</script>
<p><? write_text('index', 'TMX_menu_title'); ?></p>
<input type="radio" id="use_TMX_none" name="use_TMX" value="" checked="checked" />None<br />
<input type="radio" id="use_TMX_yes" name="use_TMX" value="yes"/>
<table>
  <tr>
    <td><? write_text('index', 'TMX_menu1'); ?></td><td><input type="text" name="inputTMX" value="" onclick="javascript:document.getElementById('use_TMX_yes').checked=true;" /></td>
  </tr>
  <tr>
  <td><? write_text('index', 'TMX_menu2'); ?></td><td><input type="file" name="inputTMXFile" onchange="javascript:document.getElementById('use_TMX_yes').checked=true;" /></td>
  </tr>
  <tr>
    <td><? write_text('index', 'TMX_menu3'); ?></td><td>
<?
/* Extern Translation Memory Server */
switch ($config['externTM_type']) {
case 'TMServer':
	include('includes/TMServer.php');
	$TM = new TMServer($config['externTM_url']);
	$avalaible_pair_list = $TM->get_language_pairs_list(); 
        echo "(" . $TM->get_server_url() . ") ";
        echo "<select name='TM_pair' onchange=\"javascript:document.getElementById('use_TMX_yes').checked=true;\">";
        echo "<option label='' value='' selected='selected'></option>";
        foreach ($avalaible_pair_list as $pair)
		echo '<option label="' . $pair . '" value = "' . $pair . '">' . $pair . '</option>' . "\n";
        echo "</select>";
        break;
default:
        echo "No server set";
        break;
}
?>
    </td>
  </tr>
  <tr>
    <td></td><td><input type="submit" name="load_input" value="<? write_text('index', 'translate'); ?>" /></td>
  </tr>
</table>
</form>
<br />
<table>
<tr><? write_text('index', 'columns_name'); ?></tr>
<?php
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
	echo '</td><td style="text-align: center;">';
	if (module_is_loaded($module_name))
		echo '<img src="'.$img_yes.'" alt="YES" style="width: 40px;" />';
	else
		echo '<img src="'.$img_no.'" alt="NO" style="width: 40px;" />';
	echo '</td></tr>';
}
?>       
</table>
</div>
</div>
<?	
page_footer();

?>
