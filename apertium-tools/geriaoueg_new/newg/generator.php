<!--This is where the wordlist & analyser files are generated-->
<?php
include('config.php');
$langs=$_POST[langs];
//print_r($langs);
foreach($langs as $value)
{
	$post_items[]="langs[]=".$value;	
	$piece=explode("-",$value);
	$com="lt-comp lr $root_lang/$value/$value.$piece[1].dix $root/analysers/$piece[1]-$piece[2].bin";
	$com2="lt-expand $root_lang/$value/$value.$piece[1]-$piece[2].dix $root/wordlists/$piece[1]-$piece[2].txt";
	print("<br>$com");
	$output = shell_exec("$com");
	if($output=="")
	{
		$com="lt-comp lr $root_lang/$value/$value.$piece[2].dix $root/analysers/$piece[1]-$piece[2].bin";	
		$output=shell_exec("$com");
	}
	print("<pre>$output</pre>");
	print("<br>$com2");
	$output = shell_exec("$com2");	
	print("<pre>$output</pre>");
}
?>
