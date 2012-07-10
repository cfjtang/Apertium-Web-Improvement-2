<?php
    header('Cache-Control: no-cache, must-revalidate');
    header('Content-type: application/json');

	require_once("include/simpledix.php");
	
	$lang = $_REQUEST['lang'];
	
	$entries = glob("simpledix/config.$lang.xml");

    echo json_encode(array("result" => count($entries)>0));
?>
