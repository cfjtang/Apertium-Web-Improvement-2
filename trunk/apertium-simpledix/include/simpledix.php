<?php
function listParadigms($word, $xsl, $configFile, $field, $selectedParadigm)
{
    if ($word != "")
    {
/*
        echo "LC_ALL=es_ES.UTF-8 xsltproc --stringparam word \"$word\" $xsl $configFile";
*/
        $output = shell_exec("LC_ALL=es_ES.UTF-8 xsltproc --stringparam word \"$word\" $xsl $configFile");
        echo "<select name=\"".$field."\" id=\"".$field."\">";
        $value=0;
        foreach (split("\n", $output) as $language)
        {
            $aux = (trim($language));
            if ($aux != "")
            {
                if ($value%2 == 0)
                {
                    echo "<option value = \"".$aux."\"";
                    if ($aux == $selectedParadigm)
                    {
                        echo " selected=\"selected\" ";
                    }
                }
                else
                {
                    echo ">".$aux."</option>";
                }
                $value++;
            }
        }
        echo "</select>";
    }
}

function flex($word, $paradigm, $xsl, $configFile)
{
    $root = "";
    if ($word != "" && $paradigm != "")
    {

/*
        echo "LC_ALL=es_ES.UTF-8 xsltproc --stringparam word \"$word\" --stringparam paradigm \"$paradigm\" $xsl $configFile";
*/
        $output = shell_exec("LC_ALL=es_ES.UTF-8 xsltproc --stringparam word \"$word\" --stringparam paradigm \"$paradigm\" $xsl $configFile");
        /*
            echo "|||".$output."|||";
        */
        $first = true;

        foreach (split("\n", $output) as $language)
        {
            $aux = (trim($language));
            if ($aux != "")
            {
                if ($first == true)
                {
                    $first = false;
                    $root = $aux;
                }
                else
                {
                    echo "$aux <br/>";
                }
            }
        }
    }
    return $root;
}

function inDictionary($word, $paradigm, $xsl, $dictionary, $rootOfWord, $lang)
{
    if ($word != "" && $paradigm != "")
    {
        $session = new Session("localhost", 1984, $_SESSION["username"], $_SESSION["baseXPass"]);
        $session->execute("open apertium-es-ca");
/*
        echo "XQUERY " . "xquery for \$aux in /$lang/dictionary/section/e/i[text()=\"$rootOfWord\"][following-sibling::par[@n=\"$paradigm\"]] return \$aux";
*/
        $output = ($session->execute("xquery for \$aux in /$lang/dictionary/section/e/i[text()=\"$rootOfWord\"][following-sibling::par[@n=\"$paradigm\"]] return \$aux"));

        if (trim($output) != "")
        {
            return -1;
        }
        else
        {
            return "<e lm=\"$word\"><i>$rootOfWord</i><par n=\"$paradigm\"/></e>";
            //return "&lt;e lm=\"$word\"&gt;&lt;i&gt;$rootOfWord&lt;/i&gt;&lt;par n=\"$paradigm\"/&gt;&lt;/e&gt;";
        }
    }
    return -2;
}

function hasTraduction($lWord, $rWord, $xsl, $dictionary, $lang)
{
    if ($lWord != "" && $rWord != "")
    {
        $session = new Session("localhost", 1984, $_SESSION["username"], $_SESSION["baseXPass"]);
        $session->execute("open apertium-es-ca");
/*
        echo "XQUERY " . "xquery for \$aux in /$lang/dictionary/section/e[not(@r=RL)][p[l[not(child::b) and not(child::g)][text()=\"$lWord\"]][r[not(child::b) and not(child::g)][text()=\"$rWord\"]]] return \$aux";
*/
        $output = ($session->execute("xquery for \$aux in /$lang/dictionary/section/e[not(@r=RL)][p[l[not(child::b) and not(child::g)][text()=\"$lWord\"]][r[not(child::b) and not(child::g)][text()=\"$rWord\"]]] return \$aux"));
      
        if (trim($output) != "")
        {
            return false;
        }
        else
        {
            return "<e><p><l>$lWord</l><r>$rWord</r></p></e>";
            //return "&lt;e&gt;&lt;p&gt;&lt;l&gt;$lWord&lt;/l&gt;&lt;r&gt;$rWord&lt;/r&gt;&lt;/p&gt;&lt;/e&gt;";
        }   
    }
    return false;
}

function putInDictionary($lEntry, $rEntry, $trad, $lLang, $rLang)
{
    $session = new Session("localhost", 1984, $_SESSION["username"], $_SESSION["baseXPass"]);
    $session->execute("open apertium-es-ca");
    if ($lEntry != -1 && $lEntry != -2)
    {
        echo "<br/>Lenguage $lLang: ".str_replace("<", "&lt;", str_replace(">", "&gt;",  "xquery insert node $lEntry into /$lLang/dictionary/section[1]")) .  "<br/>";
/*
        $output = ($session->execute("xquery insert node $lEntry into /$lLang/dictionary/section[1]"));
*/
        echo $output;
    }
    if ($rEntry != -1 && $rEntry != -2)
    {
        echo "<br/>Lenguage $rLang: ".str_replace("<", "&lt;", str_replace(">", "&gt;", "xquery insert node $rEntry into /$rLang/dictionary/section[1]")) .  "<br/>";
/*
        $output = ($session->execute("xquery insert node $rEntry into /$rLang/dictionary/section[1]"));
*/
        echo $output;
    }
    echo "<br/>Traducción: ".str_replace("<", "&lt;", str_replace(">", "&gt;",  "xquery insert node $trad into /$lLang-$rLang/dictionary/section[1]")) .  "<br/>";
/*
    $output = ($session->execute("xquery insert node $trad into /$lLang-$rLang/dictionary/section[1]"));
*/
    echo $output;   
    
}

function fileExists($file, $tempname)
{
	$session = new Session("localhost", 1984, "admin", "admin");
    $session->execute("open apertium-es-ca");
    
    $output = $session->execute("xquery if (/$tempname/$file) then 'ok' else ''");
    
    return ($output == "ok");
}

?>
