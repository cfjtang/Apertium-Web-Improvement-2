<?php
    header('Cache-Control: no-cache, must-revalidate');
    header('Content-type: application/json');

    require_once("./include/membersite_config.php");

    $name = $_REQUEST['name'];
    $user = "admin";
/*
    $_REQUEST['name'];
*/
    $pass = "admin";
/*
    $_REQUEST['pass'];
*/
    $ROOT = dirname($_SERVER['SCRIPT_FILENAME']);

	$entries = glob("output/$name/current_*_*");
	$aux = explode("/", $entries[0]);
	$langs = explode("_", $aux[2]);

    echo json_encode(array("lang1" => $langs[1], "lang2" => $langs[2]));
?>
