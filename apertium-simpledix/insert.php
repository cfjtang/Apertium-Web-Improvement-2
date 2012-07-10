<?php
    header('Cache-Control: no-cache, must-revalidate');
    header('Content-type: application/json');

    require_once("./include/membersite_config.php");

	$name = $_REQUEST['name'];
	$node = $_REQUEST['node'];
	$lang1 = $_REQUEST['lang1'];
	$lang2 = $_REQUEST['lang2'];
	$query = str_replace("\\", "", $_REQUEST['query']);
	
	$aux = "";
	if ($query != "" && strpos($query, ";") == false)
	{
		$session = new Session("localhost", 1984, "admin", "admin");
		$session->execute("open $name");
		$session->execute("xquery insert node " . $query .  " into /$lang1-$lang2/$node/dictionary/section[1]");
		$session->execute("xquery insert node " . $query .  " into /$lang1-$lang2/History/$node");
	}

    echo json_encode(array("elem" => $_REQUEST['elem']));
?>
