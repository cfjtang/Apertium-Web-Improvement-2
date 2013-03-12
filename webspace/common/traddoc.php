<?php
	include_once("../config/apertium-config.php");
	include_once("./logging.php");
	process_form();
?>

<?php
/*
	**************************
	   PROCESS FORM
	**************************
*/
function process_form() {
  $dir = escapeshellarg($_POST["direction"]);
  $mark = $_POST["mark"];       /* we only check if it's 1, no escaping needed */
  $doctype = $_POST["doctype"]; /* escape right before use in translate() */
	translate($doctype, $dir, $mark);
}

/*
  **************************
	   TRANSLATE DOCUMENT
	**************************
*/
function translate($doctype, $dir, $markUnknown) {
	global $APERTIUM_TRANSLATOR;
	global $APERTIUM_PATH;
	
	$log = new Logging();
        $log->lwrite( $dir . ' ' . $doctype . ' ' . $_SERVER [ 'REMOTE_ADDR' ] );

	$content_type = getContentType($doctype);
	$download_filetype = getDownloadFileType($doctype);



  if($_FILES['userfile']['size']>16384*4*4*4*4){
    print "<p>The file is too big!</p>";
    exit;
  }

	$tempfile = tempnam("/tmp","translate");
	$tempfile = $tempfile . "." . $doctype;

	$encoding = '';
	if($doctype == 'html' || $doctype == 'txt') {
		$enc = detect_encoding(file_get_contents($_FILES['userfile']['tmp_name']));
		$encoding = 'LC_ALL=es_ES.'.$enc[1];
	}

  if($markUnknown != 1) {
    $doctype = $doctype." -u ";
  }

  $doctype = escapeshellarg($doctype);
  $cmd = "PATH=$APERTIUM_PATH:\$PATH " . $encoding .' '. $APERTIUM_TRANSLATOR . " -f $doctype $dir " . $_FILES['userfile']['tmp_name'] . " $tempfile";
  
  
  $str = shell_exec($cmd);

  header('Content-Type: '.$content_type);
  header('Content-Disposition: attachment; filename="translation.'.$download_filetype.'"');
  header('Content-length: '.filesize($tempfile));
  readfile($tempfile);
  
  unlink ($tempfile);
  //echo "cmd = $cmd";  
}

/*
  **************************
	   GET CONTENT_TYPE
	**************************
*/
function getContentType($doctype) {
  switch($doctype)
  {
    case "txt":
      $content_type = "text/plain";
      break;
    case "rtf":
      $content_type = "text/rtf";
      break;
    case "html":
      $content_type = "text/html";
      break;
    case "odt":
      $content_type = "application/vnd.oasis.opendocument.text";
      break;
    case "sxw":
      $content_type = "application/vnd.sun.xml.writer";
      break;
    case "doc":
      $content_type = "application/rtf";
      break;
  }
  return $content_type;
}

/*
  **************************
	   GET DOWNLOAD FILE TYPE
	**************************
*/
function getDownloadFileType($doctype) {
  if($doctype != "doc") {
  	$download_filetype = $doctype;
  } else {
  	$download_filetype = "rtf";
  }
  return $download_filetype;
}

// Detect encoding
function detect_encoding($text) {
   $enc = mb_detect_encoding($text, "UTF-8, ISO-8859-15", "ISO-8859-2");
   $encoding = "";
   $e[0] = $enc;
   if($enc == "UTF-8") {
         $encoding = "utf8";
      } else {
         if($enc == "ISO-8859-2") {
               $encoding = "iso88592@euro";
            } else {
               $encoding = "iso885915@euro";
            }
      }

   $e[1] = $encoding;
   return $e;
}

?>
