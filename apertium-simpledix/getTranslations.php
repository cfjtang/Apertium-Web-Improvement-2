<?php
    header('Cache-Control: no-cache, must-revalidate');
    header('Content-type: application/json');

    require_once("./include/membersite_config.php");

    $word = $_REQUEST['word'];
    $lang1 = $_REQUEST['lang1'];
    $lang2 = $_REQUEST['lang2'];
    $currLang = $_REQUEST['currLang'];
    $name = $_REQUEST['name'];
    $user = "admin";
    $pass = "admin";
    $ROOT = dirname($_SERVER['SCRIPT_FILENAME']);
    
    $ret = array();

	$session = new Session("localhost", 1984, $user, $pass);
	$session->execute("open $name");
	
	if ($lang1 == $currLang)
	{
		$output = ($session->execute("xquery for \$aux in /$lang1-$lang2/$lang1-$lang2/dictionary/section/e[p[l[text()=\"$word\"]][r[text()]]] | /$lang1-$lang2/$lang1-$lang2/dictionary/section/e[i[text()=\"$word\"]] return \$aux")); 
	}
	else
	{
		$output = ($session->execute("xquery for \$aux in /$lang1-$lang2/$lang1-$lang2/dictionary/section/e[p[l[text()]][r[text()=\"$word\"]]] | /$lang1-$lang2/$lang1-$lang2/dictionary/section/e[i[text()=\"$word\"]] return \$aux"));
	}

	foreach (split("</e>", $output) as $out)
	{
		if (trim($out) != "")
			$ret[] = str_replace("<", "&lt", str_replace(">", "&gt", $out."</e>"));
	}

    echo json_encode(array("result" => $ret, "currLang" => $currLang));
?>
