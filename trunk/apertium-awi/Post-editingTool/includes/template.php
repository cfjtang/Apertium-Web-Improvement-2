<?//coding: utf-8
/*
	Apertium Web Post Editing tool
	Functions for page display
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús
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




function generateCorrectionField($original, $suggestions, $message, $type)
{
	//generate the correction field with all suggestions for a given mistake.
	//$type is either "grammar" or "spelling" depending on the mistake
	
	$output = '<span class="' . $type . '_mistake" title="' . escape_attribute($message, false) . '" data-suggestions="' . escape_attribute(implode('#', $suggestions), false) . '" >' . $original . '</span>';
	
	return str_replace("\n", ' ', $output);
}



function generatePretransLine($source, $target, $index)
{
?>
	<li>
		<input type="text" name="pretrans_src[]" value="<?echo $source;?>" /> -> <input type="text" name="pretrans_dst[]" value="<?echo $target;?>" /> 
		<input name="pretrans_del[<?echo $index;?>]" type="submit" value="-" class="delete_row" />
	</li>
<?
}

function generateReplacementLine($name, $source, $target, $case, $index)
{
?>
	<li>
		<input type="text" name="<?echo $name;?>_src[]" value="<?echo $source;?>" /> → <input type="text" name="<?echo $name;?>_dst[]" value="<?echo $target;?>" />
		<select name="<?echo $name;?>_case[]">
			<option value="apply" <?if($case == 'apply'){?>selected="selected"<?}?>>Apply source case</option>
			<option value="no" <?if($case == 'no'){?>selected="selected"<?}?>>Case-insensitive</option>
			<option value="" <?if($case === ''){?>selected="selected"<?}?>>Case-sensitive</option>
		</select>
		<input name="<?echo $name;?>_del[<?echo $index;?>]" type="submit" value="-" class="delete_row" />
	</li>
<?
}

?>