<?php
	
	header('Cache-Control: no-cache, must-revalidate');

	require_once("include/simpledix.php");
	
	$name = $_REQUEST['name'];
	$lang = $_REQUEST['lang'];
	
	$entries = glob("output/$name/config.$lang.xml");
	
	$ret = array();
	
	foreach ($entries as $entry)
	{
		$ret[] =  $entry;
	}

    echo json_encode(array("result" => $ret));
?>
