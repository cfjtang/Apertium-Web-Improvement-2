<?php
	include_once("../config/apertium-config.php");

	$text = $HTTP_GET_VARS["text"];
	$dir = $HTTP_GET_VARS["dir"];
	$mark = $HTTP_GET_VARS["mark"];

	if($mark == 1) {
		$markUnknown = "";
	} else {
		$markUnknown = "-u";
	}
	
	$trad = translate($text, $dir, $markUnknown);
	
	if(isset($HTTP_GET_VARS['response'])) {
		$trad = $HTTP_GET_VARS['response'] . "('" . str_replace("'","\\'",$trad) . "');";
	}

	print $trad;


/*
  **************************
	   TRANSLATE
	**************************
*/
function translate($text, $dir, $markUnknown) {
	// Imports global vars (from config/apertium-config.php)
	global $APERTIUM_TRANSLATOR;
	global $LING_DATA_DIR;
	
	$text = stripslashes($text);
	$tempfile = tempnam("tmp","tradtext");
	
  $fd = fopen($tempfile,"w");
  fputs($fd, $text);
  fclose($fd);
  
  if ($dir == "ro-es" ) {
	$cmd = "cat $tempfile | LANG=en_GB.UTF-8 sed 's/ţ/ț/g' | LANG=en_GB.UTF-8 sed 's/ş/ș/g' | LANG=en_GB.UTF-8 $APERTIUM_TRANSLATOR -f txt $markUnknown $dir";
  } else {
  	$cmd = "LANG=es_ES.UTF-8 $APERTIUM_TRANSLATOR -f txt $markUnknown $dir $tempfile";
  	}  
  
	$trad = shell_exec($cmd);
	unlink($tempfile);
	
	return $trad;
}


?>
