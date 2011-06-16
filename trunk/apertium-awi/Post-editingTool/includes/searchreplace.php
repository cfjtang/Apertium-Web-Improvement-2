<?//coding: utf-8
/*
	Apertium Web Post Editing tool
	Functions for SearchAndReplace Module
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús

	Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
	Mentors : Arnaud Vié, Luis Villarejo
*/

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