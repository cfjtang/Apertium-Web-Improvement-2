<?php
global $langtext;

/*
	PRINT LANGUAGE LINK
*/
function printLanguageLink_previous($code, $text, $url, $newLang) {
	if ($newLang==$code) {
		$code = '&nbsp;<b>' . $text . '</b>&nbsp;';
		$GLOBALS["langtext"] = $code;
	} else {
	print '<a title="' . $text . '" href="' . $url . 'lang=' . $code . '">' . $code . '</a>&nbsp;&nbsp;';
	}
}

function printLanguageLink($code, $text, $url, $newLang) {
	if ($newLang==$code) {
		//$text = '&nbsp;<b>' . $text . '</b>&nbsp;';
		print '<option class="selected" selected="true" value="' . $url . 'lang=' . $code . '">' . $text . ' (' . $code . ')</option>';
		$GLOBALS["langtext"] = $code;
	} else {
	print '<option value="' . $url . 'lang=' . $code . '">' . $text . ' (' . $code . ')</option>';
	}
}

?>

<div id="langselect">
	<?php
		print '<select id="langSelection" name="langSelection" class="langSelection" onchange="window.location.href=document.getElementById(\'langSelection\').value">';
		printLanguageLink("af","afrikaans", $url, $newLang, $currentLang);
		printLanguageLink("ca","catal&agrave;", $url, $newLang, $currentLang);
		//printLanguageLink("cy","cymraeg", $url, $newLang, $currentLang);
		//printLanguageLink("da","dansk", $url, $newLang, $currentLang);
		//printLanguageLink("de","deutsch", $url, $newLang, $currentLang);		
		printLanguageLink("en","english", $url, $newLang, $currentLang);
		printLanguageLink("eo","esperanto", $url, $newLang, $currentLang);
		printLanguageLink("es","espa&ntilde;ol", $url, $newLang, $currentLang);
		printLanguageLink("it","italiano", $url, $newLang, $currentLang);
		//printLanguageLink("eu","euskara", $url, $newLang, $currentLang);		
		printLanguageLink("fr","fran&ccedil;ais", $url, $newLang, $currentLang);
		printLanguageLink("gl","galego", $url, $newLang, $currentLang);
		//printLanguageLink("id","indonesian", $url, $newLang, $currentLang);
		//printLanguageLink("mk","македонски", $url, $newLang, $currentLang);		
		//printLanguageLink("ms","bahasa Melayu (malay)", $url, $newLang, $currentLang);		
		//printLanguageLink("nl","nederlands (	dutch)", $url, $newLang, $currentLang);
		printLanguageLink("nn","norsk (nynorsk)", $url, $newLang, $currentLang);
		printLanguageLink("nb","norsk (bokmål)", $url, $newLang, $currentLang);
		printLanguageLink("oc","occit&agrave;", $url, $newLang, $currentLang);
		printLanguageLink("pt","portugu&ecirc;s", $url, $newLang, $currentLang);
		printLanguageLink("ro","rom&acirc;nă", $url, $newLang, $currentLang);
		//printLanguageLink("sh","srpskohrvatski", $url, $newLang, $currentLang);
		//printLanguageLink("sv","svenska", $url, $newLang, $currentLang);
		//printLanguageLink("tg","тоҷикӣ", $url, $newLang, $currentLang);		
		print '<a title="" href="' . $url . 'lang=' . $newLang . '">-&nbsp;<b>'. $langtext .'</b>&nbsp;</a>';
		print '</select>';
	?>
</div>
