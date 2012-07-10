<?php
    header('Cache-Control: no-cache, must-revalidate');
    header('Content-type: application/json');

    require_once("./include/simpledix.php");

    $word = $_REQUEST['word'];
    $lang = $_REQUEST['lang'];
    $paradigm = $_REQUEST['paradigm'];
    $confFile = $_REQUEST['confFile'];
    $ROOT = dirname($_SERVER['SCRIPT_FILENAME']);
    $wordRoot = null;

    $ret = array();
   
    if ($word != "" && $paradigm != "")
    {
        $output = shell_exec("LC_ALL=es_ES.UTF-8 xsltproc --stringparam word \"$word\" --stringparam paradigm \"$paradigm\" $ROOT/simpledix/flex.xsl $ROOT/$confFile");

        $first = true;

        foreach (split("\n", $output) as $out)
        {
            $aux = (trim($out));
            if ($aux != "")
            {
                if ($first == true)
                {
                    $wordRoot = $aux;
                    $first = false;
                    $var = null;
                }
                else
                {
                    $var["value"] = $aux;
                    $ret[] = $var;
                }
            }
        }
    }

    echo json_encode(array("result" => $ret, "root" => $wordRoot));
?>
