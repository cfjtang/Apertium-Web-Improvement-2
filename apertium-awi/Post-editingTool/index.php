<?//coding: utf-8
/*
	Apertium Web Post Editing tool
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús
	
	Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
	Mentors : Arnaud Vié, Luis Villarejo
*/
include_once('includes/config.php');
include_once('includes/template.php');
include_once('modules.php');

//init_environment();


page_header(get_text('index', 'title'), array());
choose_language();
?>

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
	echo '<input type="file" name="in_doc" />';
	echo '<input type="text" name="in_doc_type" />';
	echo '<input type="submit" value="' . get_text('index', 'translate') . '" />';
	echo '</form>';
}
?>

<table>
<tr><? write_text('index', 'columns_name'); ?></tr>
<?php
	foreach ($modules as $module_name => $module_data) {
	echo '<tr><td>' . $module_data['name'] . '</td><td><p>' . $module_data['description'] . '</p></td><td><center>';
	if ($module_data['default'])
		echo 'YES';
	else
		echo 'NO';
	echo '</center></td><td><center>';
	if (CheckModule($module_name))
		echo '<span style="color:green;">OK</span></center></td>';
	else
		echo '<span style="color:red;">NO</span></td>';
	echo '<td><center><input type="checkbox" name="'.$module_name.'" ';
	if (module_is_load($module_name))
		echo ' checked="1"';	
	echo ' /></center></td></tr>';
}
?>       
</table>

<?	
page_footer();

?>