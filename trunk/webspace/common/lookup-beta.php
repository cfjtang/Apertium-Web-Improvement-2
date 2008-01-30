<?php
	include_once("config/apertium-config.php");
	$dir = $HTTP_GET_VARS["dir"];
	if ($dir == "") {
		$dir = getPair($lang);
	}
	show_form("", $dir);
/*
  **************************
	   SHOW FORM
	**************************
*/
function show_form($textbox, $dir) {
	print '<form class="translation" action="" method="post">';
	print '<fieldset><legend></legend>';
	//print _("Translation type:");
	print ' <select onchange="ajaxFunction(this.value);" id="direction" name="direction" title="' . _("Select the translation type") . '">';

	print "<option class='beta' value='es-en-rl' " . ($dir == 'en-es' ? ' selected=true' : '') . ">" . _("English") . " &rarr; " . _("Spanish") . " (beta)</option>";
	print "<option class='beta' value='es-en-lr' " . ($dir == 'es-en' ? ' selected=true' : '') . ">" . _("Spanish") . " &rarr; " . _("English") . " (beta)</option>";	
	print "<option class='beta' value='de-en-lr' " . ($dir == 'de-en' ? ' selected=true' : '') . ">" . _("German") . " &rarr; " . _("English") . "</option>";
	print "<option class='beta' value='de-en-rl' " . ($dir == 'en-de' ? ' selected=true' : '') . ">" . _("English") . " &rarr; " . _("German") . "</option>";

	print '</select><br/><br/>';
	//print _("Show lexical information") . " ";
	//print '<input onchange="lexicalInfo(this);" id="lexical" value="1" name="lexical" type="checkbox" title="' . _("Check the box to show lexical information") . '"/>';
	print '<input id="word" name="word" type="text" onkeyup="delayLookUp(this.value,true, document.forms[0].direction.value);"/>';
	print '<br/>';
	print '</fieldset></form>';
	print '<div id="message"></div>';
	print '<div id="result" style="overflow:auto; width:500px; height:400px;"></div>';
	print '<script src="common/js/lookup.js" type="text/javascript"></script>';
}

?>
