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

<ul>
	<li><? write_text('index', 'menu1'); ?></li>
	<?php
	if (module_is_load('FormattedDocumentHandling'))
		echo "<li>" . get_text('index', 'menu2') . "</li>";
?>
</ul>
<?php
if (module_is_load('FormattedDocumentHandling')) {
	echo get_text('index', 'supported_format') . ' :<br /><i>';
	foreach ($config['supported_format'] as $extension)
		echo $extension . ' ';
	echo "</i><br />";
	echo '<form action="translate.php" method="post" enctype="multipart/form-data">';
	echo '<div>';
	echo '<input type="file" name="in_doc" />';
	echo '<input type="text" name="in_doc_type" />';
	echo '<input type="submit" value="' . get_text('index', 'translate') . '" />';
	echo '</div>';
	echo '</form>';
	
}
?>
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
	if (module_is_load($module_name))
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