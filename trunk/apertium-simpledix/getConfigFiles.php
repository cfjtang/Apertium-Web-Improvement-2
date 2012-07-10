<?php
    header('Cache-Control: no-cache, must-revalidate');
    header('Content-type: application/json');

	require_once("include/simpledix.php");
	
	$name = $_REQUEST['name'];
	$lang = $_REQUEST['lang'];
	
	$entries = glob("simpledix/config.*.xml");
	
	$ret = array();
	
	foreach ($entries as $entry)
	{
		$ret[] =  $entry;
	}

    echo json_encode(array("result" => $ret));
?>
