<?php
    header('Cache-Control: no-cache, must-revalidate');
    header('Content-type: application/json');

	require_once("include/simpledix.php");
	
	$lang = $_REQUEST['lang'];
	$lang1 = $_REQUEST['lang1'];
	$lang2 = $_REQUEST['lang2'];
	
	$entries = glob("simpledix/config.$lang1-$lang2.$lang.xml");

    echo json_encode(array("result" => count($entries)>0));
?>
