<html>
<head>
<title>analyser.php</title>
</head>
<body>
<?php
include('config.php');
$dir=scandir($root_lang);
$langs=array("en","es","bn","cy","fr","gl");
$list;
//print_r($dir);
foreach($dir as $value)
{
	$pieces=explode("-",$value);
	if($pieces[0]=="apertium")
	{		
		if(array_search($pieces[1],$langs)!==FALSE)
		{
			if(array_search($pieces[2],$langs)!==FALSE)
			$list[]=rtrim($value);
		}
	}
}
//print_r($list);
//print($lang_list."<br><br>");
$listed=file($lang_list);
foreach($listed as $key => $value)
{
	$listed[$key]=rtrim($value);
}
//print_r($listed)
?>
<form method="POST" action="generator.php">
<?php
foreach($list as $value)
{
	if(array_search($value,$listed)!==FALSE)
		print("<br><input checked ");
	else
		print("<br><input unchecked ");
	print("type=\"checkbox\" name=\"langs[]\" value=\"$value\" />$value");
}
?>

<input type="submit" value="submit" name="submit">
</form>
</body>
</html>
