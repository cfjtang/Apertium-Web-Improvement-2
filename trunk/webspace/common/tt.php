<?php
	include_once("../config/apertium-config.php");

	$text = $_GET["text"];
	$dir = $_GET["dir"];
	$mark = $_GET["mark"];

	if($mark == 1) {
		$markUnknown = "";
	} else {
		$markUnknown = "-u";
	}
	
	$enc = mb_detect_encoding($text,'UTF-8,ISO-8859-1,ASCII');

        if(($enc != 'ASCII')||($enc != 'UTF-8')) {
                $text = mb_convert_encoding($text,'UTF-8',$enc);
        }


	$trad = translate($text, $dir, $markUnknown);
	
	if(isset($_GET['response'])) {
		$trad = $_GET['response'] . "('" . str_replace("'","\\'",$trad) . "');";
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
