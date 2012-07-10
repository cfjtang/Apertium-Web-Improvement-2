<?php
    header('Cache-Control: no-cache, must-revalidate');
    header('Content-type: application/json');

    require_once("./include/membersite_config.php");

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

	$session = new Session("localhost", 1984, $user, $pass);
    $session->execute("open $name");
    
    $output =  $session->execute("xquery if (/$lang1-$lang2) then 'ok' else ''");

    echo json_encode(array("result" => ($output=="ok")));
?>
