<?php
	
	header('Cache-Control: no-cache, must-revalidate');

	require_once("include/simpledix.php");
	
	$name = $_REQUEST['name'];
	$lang = $_REQUEST['lang'];
	$lang1 = $_REQUEST['lang1'];
	$lang2 = $_REQUEST['lang2'];
	
	$entries = glob("output/$name/config.$lang1-$lang2.$lang.xml");
	
	$ret = array();
	
	foreach ($entries as $entry)
	{
		$ret[] =  $entry;
	}

    echo json_encode(array("result" => $ret));
?>
