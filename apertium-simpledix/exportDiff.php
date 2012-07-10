<?php
    header('Cache-Control: no-cache, must-revalidate');
    header('Content-type: application/json');

    require_once("./include/membersite_config.php");

	set_time_limit(120);

    $lang1 = $_REQUEST['lang1'];
    $lang2 = $_REQUEST['lang2'];
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

	@mkdir("$ROOT/output");
	@mkdir("$ROOT/output/$name");
	$f1 = fopen("$ROOT/output/$name/apertium-$lang1-$lang2.$lang1.dix.diff", "w");
	$f2 = fopen("$ROOT/output/$name/apertium-$lang1-$lang2.$lang2.dix.diff", "w");
	$fb = fopen("$ROOT/output/$name/apertium-$lang1-$lang2.$lang1-$lang2.dix.diff", "w");

	$session = new Session("localhost", 1984, $user, $pass);
    $session->execute("open $name");
    
/*
    $output =  $session->execute("xquery /$lang1-$lang2/$lang1/dictionary");
*/
/*
    fwrite($f1, '<?xml version="1.0" encoding="UTF-8"?>');
*/
    fwrite($f1, $session->execute("xquery /$lang1-$lang2/History/$lang1"));
    
/*
    $output =  $session->execute("xquery /$lang1-$lang2/$lang2/dictionary");
*/
/*
    fwrite($f2, '<?xml version="1.0" encoding="UTF-8"?>');
*/
    fwrite($f2, $session->execute("xquery /$lang1-$lang2/History/$lang2"));
    
/*
    $output =  $session->execute("xquery /$lang1-$lang2/$lang1-$lang2/dictionary");
*/
/*
    fwrite($fb, '<?xml version="1.0" encoding="UTF-8"?>');
*/
    fwrite($fb, $session->execute("xquery /$lang1-$lang2/History/$lang1-$lang2"));
    

    echo json_encode(array("result" => array(
		"output/$name/apertium-$lang1-$lang2.$lang1.dix.diff",
		"output/$name/apertium-$lang1-$lang2.$lang2.dix.diff",
		"output/$name/apertium-$lang1-$lang2.$lang1-$lang2.dix.diff")));
?>
