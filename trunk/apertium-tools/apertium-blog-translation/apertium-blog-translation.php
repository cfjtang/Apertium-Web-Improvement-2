<?php
/*
Plugin Name: Apertium Blog Translation
Version: 0.1 Beta
Plugin URI: http://www.ebenimeli.org
Author: Enrique Benimeli Bofarull
Author URI: http://www.ebenimeli.org
Description: Blog post translation with Apertium.
*/ 
/*  Copyright 2008  Enrique Benimeli Bofarull  (email : ebenimeli@gmail.com)

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.
                
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
            
    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
/* Changelog
	2008-10-12
		- Version 0.1:
			- 
*/

// source language code
$sl = "es";
// soruce language name
$slName = "Español";

// target languages (codes and full names)
$languages = array(
	"ca" => "Catalan",
	"en" => "English",
	"fr" => "French",
	"pt" => "Portuguese"	
);

add_action('wp_head', 'addHeaderCode');
function addHeaderCode() {
	echo '<link type="text/css" rel="stylesheet" href="' . get_bloginfo('wpurl') . '/wp-content/plugins/apertium/css/apertium.css" media="screen" />' . "\n";
	echo '<script language="JavaScript" type="text/javascript" src="' . get_bloginfo('wpurl') . '/wp-content/plugins/apertium/js/apertium.js"></script>' . "\n";

	if (function_exists('wp_enqueue_script')) {
		//wp_enqueue_script('apertium', get_bloginfo('wpurl') . '/wp-content/plugins/apertium/js/apertium.js', array('prototype'), '0.1');
	}
   
   //$devOptions = $this->getAdminOptions();
   if ($devOptions['show_header'] == "false") { return; }
}

function apertiumPostTranslation($id) {
	print_translation_menu($id);
	print_translation($id);
}

function print_translation_menu($id) {
	global $languages;
	global $sl,$slName;

	$codeStr = getLanguageCodesAsString();	
	
	echo '<div id="translateButton'.$id.'" class="languages">';
	echo '<div id="showListButton" onclick="showListOfLanguages(\''.$id.'\');">translate this post</div>';
	echo '</div>';
	
	echo '<div id="listOfLanguages'.$id.'" class="languages hidden">';
	$code = $sl;
	$name = $slName;
	echo '<div id="'.$code.'-button'.$id.'" class="unselectedLang" onclick="translateto(\''.$code.'\',\''.$codeStr.'\',\''.$id.'\');" title="'.$name.'">'.$code.'</div>'."\n";
	foreach ($languages as $code => $name) {
		echo '<div id="'.$code.'-button'.$id.'" class="unselectedLang" onclick="translateto(\''.$code.'\',\''.$codeStr.'\',\''.$id.'\');" title="'.$name.'">'.$code.'</div>'."\n";
	}
	echo '<div class="unselectedLang" onclick="hideListOfLanguages(\''.$codeStr.'\',\''.$id.'\');">&raquo;</div>';
	echo '</div>';
}

function getLanguageCodesAsString() {
	global $languages;
	$codeStr = "";	
	$i = 1;
	$nLanguages = count($languages);
	foreach ($languages as $code => $name) {
		$codeStr .= $code;
		if( $i<$nLanguages) {
			$codeStr .= ",";
		}
		$i++;
	}
	return $codeStr;
}

function print_translation($id) {
	global $languages;	
	global $sl,$slName;
	
	$content = get_the_content();
	$content = apply_filters('the_content', $content);
	$thetitle = get_the_title();
	echo '<div id="'.$sl.$id.'" class="hidden">';
	echo "$content";
	echo '</div>';
	echo '<div id="'.$sl.'-title'.$id.'" class="hidden">';
	echo "$thetitle";
	echo '</div>';			
	
	foreach ($languages as $code => $name) {
		// content translation to target language
		$dir = $sl."-".$code;
		$t_tl_content = apertium_translate($content, $dir, '');
		// title translation to target language
		$t_tl_title = apertium_translate($thetitle, $dir, '');

		echo '<div id="note-'.$code.$id.'" class="apertiumNote hidden">translation to <b>'.$name.'</b> by <a href="http://www.apertium.org">Apertium</a></div>';
		echo '<div id="'.$code.$id.'" class="hidden">';
		echo "$t_tl_content";
		echo '</div>';
		echo '<div id="'.$code.'-title'.$id.'" class="hidden">';
		echo "$t_tl_title";
		echo '</div>';			
	}
}

function apertium_translate($text, $dir, $markUnknown) {
	$trad = apertium_translate_local($text, $dir, $markUnknown);
	//apertium_translate_online($text, $dir, $markUnknown);
	return $trad;
}

function apertium_translate_online($text, $dir, $markUnknown) {

}

function apertium_translate_local($text, $dir, $markUnknown) {
	$APERTIUM_TRANSLATOR = "apertium";

	$text = stripslashes($text);
	$tempfile = tempnam("tmp","tradtext");

	$fd = fopen($tempfile,"w");
	fputs($fd, $text);
	fclose($fd);
	$cmd = "LANG=en_US.UTF-8 apertium -f html $dir $tempfile";
 
	$trad = shell_exec($cmd);
	$trad = replaceUnknownWords($trad);
	unlink($tempfile);
	return $trad;
}
                  
function replaceUnknownWords($trad) {
	$result = "";
	for($i = 0; $i < strlen($trad); $i++) {
		if($trad[$i]=='*') {
			//$result = $result.'<span class="unknownWord"><a href="http://xixona.dlsi.ua.es/apertium/webform/index.php?direccion='.$direccion.'&word=';
			$result = $result.'<span class="unknownWord">';
			$myword ="";
			for(; $trad[$i] != ' ' && $trad[$i] != '<' && $trad[$i] != '\n' && $i < strlen($trad); $i++) {
				if( $trad[$i] != '*' ) {
					$myword = $myword.$trad[$i];
				}
			}
			//$result .= substr($myword,1).'">'.$myword."</a></span>".$trad[$i];
			$result .= $myword."</span>".$trad[$i];
		} else {
			$result.=$trad[$i];
		}
	}
	return $result;
}
                                                                                                                                                         
?>
