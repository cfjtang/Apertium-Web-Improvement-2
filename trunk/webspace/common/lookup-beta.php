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

   print "<option class='beta' value='en-es-lr' " . ($dir == 'en-es' ? ' selected=true' : '') . ">" . _("English") . " &rarr; " . _("Spanish") . "</option>";
   print "<option class='beta' value='en-es-rl' " . ($dir == 'es-en' ? ' selected=true' : '') . ">" . _("Spanish") . " &rarr; " . _("English") . "</option>";	
   print "<option class='beta' value='de-en-lr' " . ($dir == 'de-en' ? ' selected=true' : '') . ">" . _("German") . " &rarr; " . _("English") . "</option>";
   print "<option class='beta' value='de-en-rl' " . ($dir == 'en-de' ? ' selected=true' : '') . ">" . _("English") . " &rarr; " . _("German") . "</option>";
   print "<option class='beta' value='es-de-lr' " . ($dir == 'es-de' ? ' selected=true' : '') . ">" . _("Spanish") . " &rarr; " . _("German") . "</option>";
   print "<option class='beta' value='es-de-rl' " . ($dir == 'de-es' ? ' selected=true' : '') . ">" . _("German") . " &rarr; " . _("Spanish") . "</option>";
   print "<option class='beta' value='ca-de-lr' " . ($dir == 'ca-de' ? ' selected=true' : '') . ">" . _("Catalan") . " &rarr; " . _("German") . "</option>";
   print "<option class='beta' value='ca-de-rl' " . ($dir == 'de-ca' ? ' selected=true' : '') . ">" . _("German") . " &rarr; " . _("Catalan") . "</option>";
   /*
   print "<option class='beta' value='en-gr-lr' " . ($dir == 'en-gr' ? ' selected=true' : '') . ">" . _("English") . " &rarr; " . _("Greek") . "</option>";
   print "<option class='beta' value='en-gr-rl' " . ($dir == 'gr-en' ? ' selected=true' : '') . ">" . _("Greek") . " &rarr; " . _("English") . "</option>";
   print "<option class='beta' value='ru-en-lr' " . ($dir == 'ru-en' ? ' selected=true' : '') . ">" . _("Russian") . " &rarr; " . _("English") . "</option>";
   print "<option class='beta' value='ru-en-rl' " . ($dir == 'en-ru' ? ' selected=true' : '') . ">" . _("English") . " &rarr; " . _("Russian") . "</option>";
   print "<option class='beta' value='en-la-lr' " . ($dir == 'en-la' ? ' selected=true' : '') . ">" . _("English") . " &rarr; " . _("Latin") . "</option>";
   print "<option class='beta' value='en-la-rl' " . ($dir == 'la-en' ? ' selected=true' : '') . ">" . _("Latin") . " &rarr; " . _("English") . "</option>";
   */


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
