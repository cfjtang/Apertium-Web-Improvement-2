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
	$f1R = fopen("$ROOT/output/$name/apertium-$lang1-$lang2.$lang1.dix", "w");
	$f2R = fopen("$ROOT/output/$name/apertium-$lang1-$lang2.$lang2.dix", "w");
	$fbR = fopen("$ROOT/output/$name/apertium-$lang1-$lang2.$lang1-$lang2.dix", "w");

	$session = new Session("localhost", 1984, $user, $pass);
    $session->execute("open $name");
    

    fwrite($f1, $session->execute("xquery /$lang1-$lang2/History/$lang1"));
    
/*
    $output = shell_exec("LC_ALL=es_ES.UTF-8 xsltproc --stringparam file \"$ROOT/output/$name/apertium-$lang1-$lang2.$lang1.dix.diff\" $ROOT/simpledix/joinDiff.xsl $ROOT/output/$name/apertium-$lang1-$lang2.$lang1.dix.orig");
*/

    $output = shell_exec("LC_ALL=es_ES.UTF-8 $ROOT/simpledix/mergeDiff.sh $ROOT/output/$name/apertium-$lang1-$lang2.$lang1.dix.orig $ROOT/output/$name/apertium-$lang1-$lang2.$lang1.dix.diff $lang1" );
    fwrite($f1R, $output);


    fwrite($f2, $session->execute("xquery /$lang1-$lang2/History/$lang2"));
    
/*
    $output = shell_exec("LC_ALL=es_ES.UTF-8 xsltproc --stringparam file \"$ROOT/output/$name/apertium-$lang1-$lang2.$lang2.dix.diff\" $ROOT/simpledix/joinDiff.xsl $ROOT/output/$name/apertium-$lang1-$lang2.$lang2.dix.orig");
*/
    $output = shell_exec("LC_ALL=es_ES.UTF-8 $ROOT/simpledix/mergeDiff.sh $ROOT/output/$name/apertium-$lang1-$lang2.$lang2.dix.orig $ROOT/output/$name/apertium-$lang1-$lang2.$lang2.dix.diff $lang2" );
    fwrite($f2R, $output);    


    fwrite($fb, $session->execute("xquery /$lang1-$lang2/History/$lang1-$lang2"));
        
/*
    $output = shell_exec("LC_ALL=es_ES.UTF-8 xsltproc --stringparam file \"$ROOT/output/$name/apertium-$lang1-$lang2.$lang1-$lang2.dix.diff\" $ROOT/simpledix/joinDiff.xsl $ROOT/output/$name/apertium-$lang1-$lang2.$lang1-$lang2.dix.orig");
*/
    $output = shell_exec("LC_ALL=es_ES.UTF-8 $ROOT/simpledix/mergeDiff.sh $ROOT/output/$name/apertium-$lang1-$lang2.$lang1-$lang2.dix.orig $ROOT/output/$name/apertium-$lang1-$lang2.$lang1-$lang2.dix.diff $lang1-$lang2" );

    fwrite($fbR, $output);

    echo json_encode(array("result" => array(
		"output/$name/apertium-$lang1-$lang2.$lang1.dix",
		"output/$name/apertium-$lang1-$lang2.$lang2.dix",
		"output/$name/apertium-$lang1-$lang2.$lang1-$lang2.dix")));
?>
