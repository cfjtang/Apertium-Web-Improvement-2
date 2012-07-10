<?php
    header('Cache-Control: no-cache, must-revalidate');
    header('Content-type: application/json');

    require_once("./include/membersite_config.php");

    $lword = $_REQUEST['lword'];
    $rword = $_REQUEST['rword'];
    $lang1 = $_REQUEST['lang1'];
    $lang2 = $_REQUEST['lang2'];
    $name = $_REQUEST['name'];
    $both = $_REQUEST['both'];
    $ltor = $_REQUEST['lr'];
    $rtol = $_REQUEST['rl'];
    $lcat = $_REQUEST['lcat'];
    $rcat = $_REQUEST['rcat'];
    
    $user = "admin"; /*  $_REQUEST['user'];  */
    $pass = "admin"; /*  $_REQUEST['pass'];  */
    $ROOT = dirname($_SERVER['SCRIPT_FILENAME']);
    
    $ret = array();
   
    if ($rword != "" && $lword != "")
    {
        $session = new Session("localhost", 1984, $user, $pass);
        $session->execute("open $name");
        
        if ($both == 'true')
        {
			$output = ($session->execute("xquery for \$aux in /$lang1-$lang2/$lang1-$lang2/dictionary/section/e[not(@r)][p[l[not(child::b) and not(child::g)][text()=\"$lword\"]][r[not(child::b) and not(child::g)][text()=\"$rword\"]]] return \$aux"));
			if (trim($output) == "")
			{
				$ret[] = array("lang"=>"$lang1 &harr; $lang2", "node"=> "$lang1-$lang2", "query" => "<e><p><l>$lword<s n='$lcat'/></l><r>$rword<s n='$rcat'/></r></p></e>");
			}  
		}
		if ($rtol == 'true')
		{ 
			$output = ($session->execute("xquery for \$aux in /$lang1-$lang2/$lang1-$lang2/dictionary/section/e[@r='RL'][p[l[not(child::b) and not(child::g)][text()=\"$lword\"]][r[not(child::b) and not(child::g)][text()=\"$rword\"]]] return \$aux"));
			if (trim($output) == "")
			{
				$ret[] = array("lang"=>"$lang1 &larr; $lang2", "node"=> "$lang1-$lang2", "query" => "<e r='RL'><p><l>$lword<s n='$lcat'/></l><r>$rword<s n='$rcat'/></r></p></e>");
			}  
		} 
		if ($ltor == 'true')
		{
			$output = ($session->execute("xquery for \$aux in /$lang1-$lang2/$lang1-$lang2/dictionary/section/e[@r='LR'][p[l[not(child::b) and not(child::g)][text()=\"$lword\"]][r[not(child::b) and not(child::g)][text()=\"$rword\"]]] return \$aux"));
			if (trim($output) == "")
			{
				$ret[] = array("lang"=>"$lang1 &rarr; $lang2", "node"=> "$lang1-$lang2", "query" => "<e r='LR'><p><l>$lword<s n='$lcat'/></l><r>$rword<s n='$rcat'/></r></p></e>");
			}  
		} 
    }

    echo json_encode(array("result" => $ret));
?>
