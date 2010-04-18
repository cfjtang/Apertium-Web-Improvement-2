
<?php
// Things to watch out for
// 1. Change the config.php file appropriately
// 2. Making shell_exec() work with root permissions.

// To Do
// 1. Combine all the text blocks into one string, with a special character in between. This would require only 1 call to lt-proc, hopefully speeding up the process. I would only require 1 call to explode() to extract an array of text blocks from this string.
include('config.php');
include('functions.php');
include('../simplehtmldom/simple_html_dom.php');
// Set the URL of the page or file to download.
$trans=$_GET['link187'];
$prev=$_GET['prev187'];
$direction=$_GET['lang187'];
echo"test1 + trans=$trans + prev=$prev";
//Fixing up the link
$link = createLink($trans,$prev,$direction);

echo "<br>test7=".$link;

//Getting the page using cURL
//The GET and POST variables are also forwarded if they are part of the original request.
$page = getPage($_GET, $_POST, $link);
if($page=="")
{
	echo "ZERO SIZED REPLY";
	exit;
}
//echo "page=$page";

//Fixing the links so that all requests are redirected through Geriaoueg.
$html = fixLinks($page, $link, $homeurl, $direction);
//echo $html;

//Getting the number of text nodes from the <body> of the page.
$es = $html->find('body',0);
$count = count($es->find('text'));

//print_r($html->find('body',0)->find('script'));
//Creating a lookup table from the analyser (e.g. cy-en.bin)
$lookup = createLookup($bidix);

print("<br>TEXT BLOCKS<br><br>");

//Going through all the text blocks
for($i=0;$i<$count;$i++)
{
	//We don't want to process inline scripts	
	if($es->find('text',$i)->parent()->tag=="script")
	continue;	
	echo "<br>".$es->find('text',$i)->innertext;
	//Get the text block with index $i
	$line=$es->find('text',$i)->innertext;
	//print("<br>ooo000---<br>".$es->find('text',$i)->outertext);
	//Lemmatise the text block using lt-proc
	$line=lemmatise(trim($line), $transducer);
	
	//Get an array of lexemes from the block of text
	$lexemes=explode("^", $line);
	$count2=count($lexemes);
	
	$newline = "";
	for($j=1;$j<$count2;$j++)
	{
		$temp = readWord(trim($lexemes[$j]),$lookup, $encoding);
		//print(" $temp");
		$newline = $newline.$temp;
	}
	//Replace the old text with the new text with hoverbox <span>
	$es->find('text', $i)->innertext=$newline;
}

//echo $html;

?>
