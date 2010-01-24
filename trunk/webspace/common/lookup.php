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

	//include_once("available_pairs.php");
	//print "<option class='header' value='' disabled='true'>" . _("Select direction") . "</option>";
	print "<option class='header' value='' disabled='true'>" . _("Catalan") . "</option>";
	print "<option value='es-ca-rl' " . ($dir == 'ca-es' ? ' selected=true' : '') . ">" . _("Catalan") . " &rarr; " . _("Spanish") . "</option>";
	print "<option value='en-ca-rl' " . ($dir == 'ca-en' ? ' selected=true' : '') . ">" . _("Catalan") . " &rarr; " . _("English") . "</option>";
	// ca-eo not working!	
	//print "<option value='eo-ca-rl' " . ($dir == 'ca-eo' ? ' selected=true' : '') . ">" . _("Catalan") . " &rarr; " . _("Esperanto") . "</option>";
	print "<option value='fr-ca-rl' " . ($dir == 'ca-fr' ? ' selected=true' : '') . ">" . _("Catalan") . " &rarr; " . _("French") . "</option>";
	print "<option value='oc-ca-rl' " . ($dir == 'ca-oc' ? ' selected=true' : '') . ">" . _("Catalan") . " &rarr; " . _("Occitan") . "</option>";
	print "<option value='pt-ca-rl' " . ($dir == 'ca-pt' ? ' selected=true' : '') . ">" . _("Catalan") . " &rarr; " . _("Portuguese") . "</option>";
	print "<option class='beta' value='ca-ro-lr' " . ($dir == 'ca-ro' ? ' selected=true' : '') . ">" . _("Catalan") . " &rarr; " . _("Romanian") . " (beta)</option>";

	print "<option class='header' value='' disabled='true'>" . _("English") . "</option>";
	print "<option value='en-af-lr' " . ($dir == 'en-af' ? ' selected=true' : '') . ">" . _("English") . " &rarr; " . _("Afrikaans") . "</option>";
	print "<option value='en-ca-lr' " . ($dir == 'en-ca' ? ' selected=true' : '') . ">" . _("English") . " &rarr; " . _("Catalan") . "</option>";
	print "<option value='cy-en-rl' " . ($dir == 'en-cy' ? ' selected=true' : '') . ">" . _("English") . " &rarr; " . _("Welsh") . "</option>";		
	print "<option class='beta' value='en-es-lr' " . ($dir == 'en-es' ? ' selected=true' : '') . ">" . _("English") . " &rarr; " . _("Spanish") . " (beta)</option>";
	print "<option value='en-pl-lr' " . ($dir == 'en-pl' ? ' selected=true' : '') . ">" . _("English") . " &rarr; " . _("Polish") . "</option>";
	print "<option value='eo-en-rl' " . ($dir == 'en-eo' ? ' selected=true' : '') . ">" . _("English") . " &rarr; " . _("Esperanto") . "</option>";
	print "<option value='ht-en-rl' " . ($dir == 'ht-eo' ? ' selected=true' : '') . ">" . _("English") . " &rarr; " . _("Haitian Creole") . "</option>";

	print "<option class='header' value='' disabled='true'>" . _("Esperanto") . "</option>";
	print "<option value='eo-es-lr' " . ($dir == 'eo-es' ? ' selected=true' : '') . ">" . _("Esperanto") . " &rarr; " . _("Spanish") . "</option>";
	print "<option value='eo-ca-lr' " . ($dir == 'eo-ca' ? ' selected=true' : '') . ">" . _("Esperanto") . " &rarr; " . _("Catalan") . "</option>";
	print "<option value='eo-en-lr' " . ($dir == 'eo-en' ? ' selected=true' : '') . ">" . _("Esperanto") . " &rarr; " . _("English") . "</option>";
	
	print "<option class='header' value='' disabled='true'>" . _("Spanish") . "</option>";
	print "<option value='es-ca-lr' " . ($dir == 'es-ca' ? ' selected=true' : '') . ">" . _("Spanish") . " &rarr; " . _("Catalan") . "</option>";
	print "<option class='beta' value='en-es-rl' " . ($dir == 'es-en' ? ' selected=true' : '') . ">" . _("Spanish") . " &rarr; " . _("English") . " (beta)</option>";	
	//print "<option value='eu-es-rl' " . ($dir == 'es-eu' ? ' selected=true' : '') . ">" . _("Spanish") . " &rarr; " . _("Basque") . "</option>";	
	print "<option value='es-pt-lr' " . ($dir == 'es-pt' ? ' selected=true' : '') . ">" . _("Spanish") . " &rarr; " . _("Portuguese") . "</option>";
	print "<option value='oc-es-rl' " . ($dir == 'es-oc' ? ' selected=true' : '') . ">" . _("Spanish") . " &rarr; " . _("Occitan") . "</option>";
	print "<option value='es-gl-lr' " . ($dir == 'es-gl' ? ' selected=true' : '') . ">" . _("Spanish") . " &rarr; " . _("Galician") . "</option>";
	print "<option value='fr-es-rl' " . ($dir == 'es-fr' ? ' selected=true' : '') . ">" . _("Spanish") . " &rarr; " . _("French") . "</option>";	
	print "<option value='es-it-lr' " . ($dir == 'es-it' ? ' selected=true' : '') . ">" . _("Spanish") . " &rarr; " . _("Italian") . "</option>";
	print "<option value='eo-es-rl' " . ($dir == 'es-eo' ? ' selected=true' : '') . ">" . _("Spanish") . " &rarr; " . _("Esperanto") . "</option>";
	print "<option value='es-ro-lr' " . ($dir == 'es-ro' ? ' selected=true' : '') . ">" . _("Spanish") . " &rarr; " . _("Romanian") . "</option>";

	print "<option class='header' value='' disabled='true'>" . _("French") . "</option>";
	print "<option value='fr-ca-lr' " . ($dir == 'fr-ca' ? ' selected=true' : '') . ">" . _("French") . " &rarr; " . _("Catalan") . "</option>";
	print "<option value='fr-es-lr' " . ($dir == 'fr-es' ? ' selected=true' : '') . ">" . _("French") . " &rarr; " . _("Spanish") . "</option>";	

	print "<option class='header' value='' disabled='true'>" . _("Haitian Creole") . "</option>";
	print "<option value='ht-en-lr' " . ($dir == 'ht-en' ? ' selected=true' : '') . ">" . _("Haitian Creole") . " &rarr; " . _("English") . "</option>";

	print "<option class='header' value='' disabled='true'>" . _("Occitan") . "</option>";
	print "<option value='oc-ca-lr' " . ($dir == 'oc-ca' ? ' selected=true' : '') . ">" . _("Occitan") . " &rarr; " . _("Catalan") . "</option>";	
	print "<option value='oc-es-lr' " . ($dir == 'oc-es' ? ' selected=true' : '') . ">" . _("Occitan") . " &rarr; " . _("Spanish") . "</option>";	

	print "<option class='header' value='' disabled='true'>" . _("Romanian") . "</option>";	
	print "<option value='es-ro-rl' " . ($dir == 'ro-es' ? ' selected=true' : '') . ">" . _("Romanian") . " &rarr; " . _("Spanish") . "</option>";
	print "<option class='beta' value='ca-ro-rl' " . ($dir == 'ro-ca' ? ' selected=true' : '') . ">" . _("Romanian") . " &rarr; " . _("Catalan") . " (beta)</option>";		
	
	print "<option class='header' value='' disabled='true'>" . _("More") . "</option>";
	//print "<option disabled='true' value='eu-es-lr' " . ($dir == 'eu-es' ? ' selected=true' : '') . ">" . _("Basque") . " &rarr; " . _("Spanish") . "</option>";
	print "<option value='en-af-rl' " . ($dir == 'af-en' ? ' selected=true' : '') . ">" . _("Afrikaans") . " &rarr; " . _("English") . "</option>";
	print "<option value='cy-en-lr' " . ($dir == 'cy-en' ? ' selected=true' : '') . ">" . _("Welsh") . " &rarr; " . _("English") . "</option>";
	print "<option value='sv-da-rl' " . ($dir == 'da-sv' ? ' selected=true' : '') . ">" . _("Danish") . " &rarr; " . _("Swedish") . "</option>";
	print "<option value='es-gl-rl' " . ($dir == 'gl-es' ? ' selected=true' : '') . ">" . _("Galician") . " &rarr; " . _("Spanish") . "</option>";
	print "<option value='es-it-rl' " . ($dir == 'it-es' ? ' selected=true' : '') . ">" . _("Italian") . " &rarr; " . _("Spanish") . "</option>";
	print "<option value='sh-mk-rl' " . ($dir == 'mk-sh' ? ' selected=true' : '') . ">" . _("Macedonian") . " &rarr; " . _("Serbo-Croatian") . "</option>";
	print "<option value='en-pl-rl' " . ($dir == 'pl-en' ? ' selected=true' : '') . ">" . _("Polish") . " &rarr; " . _("English") . "</option>";
	print "<option value='pt-ca-lr' " . ($dir == 'pt-ca' ? ' selected=true' : '') . ">" . _("Portuguese") . " &rarr; " . _("Catalan") . "</option>";
	print "<option value='es-pt-rl' " . ($dir == 'pt-es' ? ' selected=true' : '') . ">" . _("Portuguese") . " &rarr; " . _("Spanish") . "</option>";

	print "<option value='sh-mk-lr' " . ($dir == 'sh-mk' ? ' selected=true' : '') . ">" . _("Serbo-Croatian") . " &rarr; " . _("Macedonian") . "</option>";
	print "<option value='sv-da-lr' " . ($dir == 'sv-da' ? ' selected=true' : '') . ">" . _("Swedish") . " &rarr; " . _("Danish") . "</option>";
	print "<option value='tg-fa-rl' " . ($dir == 'fa-	tg' ? ' selected=true' : '') . ">" . _("Persian") . " &rarr; " . _("Tajik") . "</option>";
	print "<option value='tg-fa-lr' " . ($dir == 'tg-fa' ? ' selected=true' : '') . ">" . _("Tajik") . " &rarr; " . _("Persian") . "</option>";
	//print "<option class='beta' value='de-en-lr' " . ($dir == 'de-en' ? ' selected=true' : '') . ">" . _("German") . " &rarr; " . _("English") . "</option>";
	//print "<option class='beta' value='de-en-rl' " . ($dir == 'en-de' ? ' selected=true' : '') . ">" . _("English") . " &rarr; " . _("German") . "</option>";

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
