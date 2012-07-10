<?php
    header('Cache-Control: no-cache, must-revalidate');
    header('Content-type: application/json');

    require_once("./include/simpledix.php");

	$configFile = $_REQUEST['configFile'];
	$configFile2 = $_REQUEST['configFile2'];
    $ROOT = dirname($_SERVER['SCRIPT_FILENAME']);
    $wordRoot;

    $ret = array();
    $acum = array();
   

	$output = shell_exec("LC_ALL=es_ES.UTF-8 xsltproc $ROOT/simpledix/getKinds.xsl $ROOT/$configFile | sort | uniq ");

	foreach (split("\n", $output) as $out)
	{
		$aux = (trim($out));
		if ($aux != "")
		{
			$acum[$aux] = $aux;
		}
	}
	
	$output = shell_exec("LC_ALL=es_ES.UTF-8 xsltproc $ROOT/simpledix/getKinds.xsl $ROOT/$configFile2 | sort | uniq ");

	foreach (split("\n", $output) as $out)
	{
		$aux = (trim($out));
		if ($aux != "" && in_array($aux, $acum))
		{
			$var["value"] = $aux;
			$ret[] = $var;
		}
	}

    echo json_encode(array("result" => $ret));
?>
