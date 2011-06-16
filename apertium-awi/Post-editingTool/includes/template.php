<?//coding: utf-8
/*
	Apertium Web Post Editing tool
	Functions for page display
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús

	Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
	Mentors : Arnaud Vié, Luis Villarejo
*/

include_once('strings.php');

function page_header($title, $includes)
{
	header ('Content-type: text/html; charset=utf-8');
	
?><!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<title><?echo $title;?></title>
<?	foreach($includes as $file)
	{
		if(str_endswith($file, '.js'))
		{
?>	<script type="text/javascript" src="<?echo $file . '?v=' . @filemtime($file); ?>"></script>
<?		}
		elseif(str_endswith($file, '.css'))
		{
?>	<link rel="stylesheet" type="text/css" href="<?echo $file . '?v=' . @filemtime($file);?>" />
<?		}
	}
?>
</head>
<body>
<?
}

function page_footer()
{
?></body>
</html>
<?
}
?>