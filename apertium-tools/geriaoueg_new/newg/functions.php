<?php

//Fixing up the link so that it can be used by cURL.
function createLink($trans,$prev,$direction)
{
	if($prev=="gerihome")
	{
		//When the request is coming from the index page
		if(substr($trans,0,7)!="http://" && substr($trans,0,8)!="https://" )
		{$link="http://".$trans;}
		else
		{$link=$trans;}
		//echo"<br>test2=$link";
	}
	else
	{
		if($trans[0]=='/')
		{
			//When the URL is absolute, e.g. /abc/pqa.html
			$temp=parse_url($prev);	
			$trans=$temp["scheme"]."://".$temp["host"].$trans;
			$link=$trans;
			//echo"<br>test3=$link";
		}
		else
		if(substr($trans,0,7)=="http://" || substr($trans,0,8)=="https://")
		{	
			//when the URL is complete, e.g. http://www.abc.com
			$link=$trans; 
			//echo"<br>test4=$link";	
		}
		else
		{	
			//when the url is relative, e.g. abc/pqr.html
			$temp=parse_url($prev);
			if(basename($prev)==$temp['host'])
			{$link=$prev."/".$trans; 
			//echo"<br>test5=$link";
			}
			else
			{$link=dirname($prev)."/".$trans; 
			//echo"<br>test6=$link";
			}
		}
	}
	return $link;
}

//Execute the cURL session and return the page.
//Arguments: ($_GET array, $_POST array, link to be opened)
function getPage($GET,$POST,$link)
{
	//Initialize the cURL session
	$ch = curl_init();
	// Initialize GET and POST variables if present
	if(count($POST)>2)  //checking for POST
	{
		curl_setopt($ch, CURLOPT_POST, 1);
		foreach($POST as $vblname => $value)
		{
			if($vblname=="prev187" || $vblname=="link187" || $vblname=="lang187")
			continue;
			else
			{
				$temppost=$temppost.$vblname."=".$value."&";
			}
		}
		$temppost=substr($temppost,0,-1);
		curl_setopt($ch, CURLOPT_POSTFIELDS, $temppost);
		//echo "<br>test post=$temppost";
	}
	else
	if(count($GET)>2)   //checking for GET
	{
		$link=$link."?";
		foreach($GET as $vblname => $value)
		{
			if($vblname=="prev187" || $vblname=="link187" || $vblname=="lang187")
			continue;
			else
			{
				$link=$link.$vblname."=".$value."&";
			}
		}
		$link=substr($link,0,-1);
		//echo "<br>test get=$link";
	}

	//echo "<br>test8=".$link."<br>";
	curl_setopt($ch, CURLOPT_URL,"$link");
	//Ask cURL to return the contents in a variable
	//   instead of simply echoing them to the browser.
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt($ch, CURLOPT_PROXY, "172.30.3.3");
	curl_setopt($ch, CURLOPT_PROXYPORT, "8080");
	curl_setopt($ch, CURLOPT_FOLLOWLOCATION, TRUE);
	curl_setopt($ch, CURLOPT_MAXREDIRS, 10);
	// Execute the cURL session
	$page = curl_exec($ch);
	// Close cURL session
	curl_close ($ch);
	return $page;
}
// Change the links so that all requests are routed through are server.
// Also includes the base URL for header includes.
// Returns an HTML DOM object.
function fixLinks($page, $link, $homeurl, $direction)
{
	$html = str_get_html($page);
	$e=$html->find('head',0);
	$temp=parse_url($link);
	if(basename($link)==$temp['host'])
	$temp2=$link;
	else
	$temp2=dirname($link);
	//Adding the base URL (so that relative links for included files are not broken)
	$e->innertext="<base href='$temp2' />\n<link rel='stylesheet' type='text/css' href='styles/hover.css' />".$e->innertext;
	//Modify forms for redirection
	foreach($html->find('form') as $form)
	{
	//	echo "<br>old: ".$form->action;
		$form->innertext="<input type=\"hidden\" name=\"lang187\" value=\"".$direction."\">".$form->innertext;		
		$form->innertext="<input type=\"hidden\" name=\"prev187\" value=\"$link\">".$form->innertext;
		$form->innertext="<input type=\"hidden\" name=\"link187\" value=\"".$form->action."\">".$form->innertext;
		$form->action=$homeurl;
	//	echo "<br>new: ".$form->action;
	}
	//Modify links for redirection
	foreach($html->find('a') as $anchor)
	{
		//echo "<br>old: ".$anchor->href;
		$anchor->href=$homeurl."?&link187=".$anchor->href."&prev187=$link&lang187=$direction";
		//echo "<br>new: ".$anchor->href;
	}
	return $html;
}

function createLookup($bidix)
{
	$lookup = array();
	$bfile = file($bidix);
	foreach($bfile as $line) {
		$row = explode("\t", $line);
		// Some entries contain plurals attached to the word e.g. yezh(où)
		//Depending on the direction of translation, $key = $row[0] or $row[1]		
		$key = $row[1];
		$key = str_replace("(", " ", $key);
		$key = explode(" ", $key);
		$key = str_replace("_", " ", $key[0]);
		$key = str_replace("'", "’", $key);
		if(isset($lookup[$key]))		
		$lookup[$key] = $lookup[$key] . " " . trim(str_replace("_", " ", $row[0]));
		else
		$lookup[$key] = trim(str_replace("_", " ", $row[0]));
		//print("<br>$key  --->  ".$lookup[$key]);
	}
	return $lookup;
}
function getSurfaceForm($line)
{
	return explode("/", $line, 1);
}
// Read a word, words are structured like: ^kant/gant<pr>/kant<adj>/kant<num><mf><pl>/kant<n><m><sg>$
//
// So we read until the first '/' to get the surface form, then we go reading 
// until each '/' to get the possible lemmata.
//
function readWord($lexeme, $lookup, $encoding) 
{
	error_reporting(0);
	//Remove the $ at the end
	$lexeme = substr($lexeme, 0, -1);
	//print("<br><br>readWord: $lexeme");
	$word = "";
	$lemmata = array();
	// Get the surface form
	$temp = explode("/", $lexeme, 2);
	$word = $temp[0];
	$lemma1 = $temp[1];
	//print("<br>Surface Form: $word<br>Rest: $lemma1");
	
	if(ctype_punct($word)) {
		return $word;
	}

	//split into separate lemmata
	$lemmata = explode("/",$lemma1);

	//print_r($lemmata);
	// Some of the lemmata might be duplicates
	foreach($lemmata as $key => $value)
	{
		//print("<br>--$value ---");		
		$temp = explode("<", $value, 2);
		$lemmata[$key] = $temp[0];		
		//print("$lemmata[$key]");
	}
	
	$lemmata = array_unique($lemmata);
	//if(sizeof($lemmata) == 0) {
	//return $word . " ";
	//}
	if($lemmata[0][1]=="*")
	{
	return " ".$word;
	}
	$count = 0;
	$vacio = 0;
	$body = "";
	// For each lemma, print out the lemma + what we find in the translation table
	foreach($lemmata as $lemma) {
		//$temp = explode("<",$lemma,2);
		//$lemma = $temp[0];
		if($encoding != "utf8") {
			if(array_key_exists(iconv("latin1","utf-8",strtolower($lemma)), $lookup)) {
				$vacio++;
			}
		} 
		else {
			if(array_key_exists(strtolower($lemma), $lookup)) {
				$vacio++;
			}
		}
		//print("<br>----------foreach$vacio: -$lemma-".$lookup[strtolower($lemma)]);
		// The wordlists are stored in UTF-8 but we might be trying to look up a word in latin1
		if($encoding != "utf8") {
			$add = iconv("utf8", "latin1", $lookup[iconv("latin1","utf-8",strtolower($lemma))]);
			$body = $body . "(<b>" . $lemma . "</b>) " . $add;
		} 
		else { 
			$body = $body . "(<b>" . $lemma . "</b>) " . $lookup[strtolower($lemma)];
		}
		if($count < (sizeof($lemmata) - 1)) {
			$body = $body . " <b>&middot;</b> ";
		}
		$count++;
	}
	if($vacio == 0) {
	return " ".$word;
	}
	// This inserts the CSS span class and the definition etc.
	//$output = '[<span class="word-H">]' . $word . '[<span class="definition-H">]<br>' . $body . '[<\/span><\/span> ]';
	$output=" <a class='info' href=''>".$word."<span>$body</span></a>";
	return $output;
}

//Lemmatise a given string using ltproc
function lemmatise($line, $transducer)
{
	$line=shell_exec("echo $line | ltproc $transducer");
	return $line;
}

?>

