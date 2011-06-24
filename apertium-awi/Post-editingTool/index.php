<?//coding: utf-8
/*
	Apertium Web Post Editing tool
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús
	
	Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
	Mentors : Arnaud Vié, Luis Villarejo
*/
include_once('includes/config.php');
include_once('modules.php');
include_once('includes/template.php');

//init_environment();


page_header("Apertium translation", array());

?>

<p>What would you like to translate ?</p>

<ul>
	<li>A short text ? <a href="translate.php">Click here</a></li>
	<?php
	if (module_is_load('FormattedDocumentHandling'))
		echo "<li>A document ? Use the form below !</li>";
?>
</ul>
<?php
if (module_is_load('FormattedDocumentHandling')) {
	echo 'Formats supported :<br /><i>';
	foreach ($config['supported_format'] as $extension)
		echo $extension . ' ';
	echo "</i><br />";
	echo '<form action="translate.php" method="post" enctype="multipart/form-data">';
	echo '<input type="file" name="in_doc" />';
	echo '<input type="text" name="in_doc_type" />';
	echo '<input type="submit" value="Translate !" />';
	echo '</form>';
}
?>

<table>
<tr><td>Module</td><td>Description</td><td>Recommended</td><td>Dependencies</td><td>Load</td></tr>
<?php
	foreach ($modules as $module_name => $module_data) {
	echo '<tr><td>' . $module_data['name'] . '</td><td><pre>' . $module_data['description'] . '</pre></td><td><center>';
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