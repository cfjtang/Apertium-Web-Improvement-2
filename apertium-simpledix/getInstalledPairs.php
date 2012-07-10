<?php
    header('Cache-Control: no-cache, must-revalidate');
    header('Content-type: application/json');

    require_once("./include/simpledix.php");

    $ROOT = dirname($_SERVER['SCRIPT_FILENAME']);
    $wordRoot;

    $ret = array();
   

	$output = shell_exec("LC_ALL=es_ES.UTF-8 xsltproc $ROOT/simpledix/getInstalledPairs.xsl $ROOT/simpledix/installedPairs.xml");

	$first = true;

	foreach (split("\n", $output) as $out)
	{
		$aux = (trim($out));
		if ($aux != "")
		{
			$var["value"] = $aux;
			$ret[] = $var;
		}
	}
    

    echo json_encode(array("result" => $ret));
?>
