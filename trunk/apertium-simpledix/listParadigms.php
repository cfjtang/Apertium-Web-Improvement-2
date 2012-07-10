<?php
    header('Cache-Control: no-cache, must-revalidate');
    header('Content-type: application/json');

    require_once("./include/simpledix.php");

    $word = $_REQUEST['word'];
    $lang = $_REQUEST['lang'];
    $confFile = $_REQUEST['confFile'];
    $ROOT = dirname($_SERVER['SCRIPT_FILENAME']);  

    $ret = array();
   
    if ($word != "")
    {
        $output = shell_exec("LC_ALL=es_ES.UTF-8 xsltproc --stringparam word \"$word\" $ROOT/simpledix/candidates.xsl $ROOT/$confFile");
        foreach (split("\n", $output) as $out)
        {
            if (trim($out) != "")
            {
                $var["value"] = trim($out);
                $ret[] = $var;
            }
        }
    }

    echo json_encode(array("result" => $ret));
?>
