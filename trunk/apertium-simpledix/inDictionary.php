<?php
    header('Cache-Control: no-cache, must-revalidate');
    header('Content-type: application/json');

    require_once("./include/membersite_config.php");

    $word = $_REQUEST['word'];
    $rootOfWord = $_REQUEST['rootOfWord'];
    $lang1 = $_REQUEST['lang1'];
    $lang2 = $_REQUEST['lang2'];
    $currLang = $_REQUEST['currLang'];
    $paradigm = $_REQUEST['paradigm'];
    $name = $_REQUEST['name'];
    $user = "admin";
/*
    $_REQUEST['user'];
*/
    $pass = "admin";
/*
    $_REQUEST['pass'];
*/
    $ret = "";
   
    if ($word != "" && $paradigm != "")
    {
        $session = new Session("localhost", 1984, $user, $pass);
        $session->execute("open $name");
        $output = ($session->execute("xquery for \$aux in /$lang1-$lang2/$currLang/dictionary/section/e/i[text()=\"$rootOfWord\"][following-sibling::par[@n=\"$paradigm\"]] return \$aux"));
        if (trim($output) != "")
        {
            $ret = "-1";
        }
        else
        {
            $ret = "<e lm=\"$word\"><i>$rootOfWord</i><par n=\"$paradigm\"/></e>";
        }
    }

    echo json_encode(array("result" => $ret, "node" => $currLang));

?>
