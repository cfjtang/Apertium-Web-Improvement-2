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
	header ('Content-type: text/html; charset=utf-8'); ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="es">
  <head>
    <link rel="shortcut icon" href="http://xixona.dlsi.ua.es/apertium-www/favicon.ico"/>
    <meta http-equiv="Content-type" content="text/html; charset=UTF-8"/>
    <title><?echo $title;?></title>
 <?	foreach($includes as $file) {
	     if(str_endswith($file, '.js'))  {
?>
   <script type="text/javascript" src="<?echo $file . '?v=' . @filemtime($file); ?>"></script>
<?	     }
	     elseif(str_endswith($file, '.css')) { 
?>
   <link rel="stylesheet" type="text/css" href="<?echo $file . '?v=' . @filemtime($file);?>" />
<?	     }
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

function display_ajax_loader()
{
	/* Display the gif for ajax_loading
	 * By default, it isn't visible
	 */
	echo "<img alt='' id='ajax_loader' src='images/ajax-loader.gif' style='display:none;' />";
}

/* Interface language */

function avalaible_languages()
{
	/* Return an array of languages avalaible, according to 
	 * templates/avalaible_languages
	 */
	$templates_dir = 'templates/';
	
	return array_map("trim", file($templates_dir . 'avalaible_languages'));
}

function get_language()
{
	/* Return the current language of the interface
	 * If no language is set, retrieve him from the browser
	 * and set $_COOKIE['lang']
	 * If no language is send by the browser, 'en' is default'
	 */
	global $_COOKIE;
		
	if (!isset($_COOKIE['lang']) or !in_array($_COOKIE['lang'], avalaible_languages())) {
		if (isset($_SERVER['HTTP_ACCEPT_LANGUAGE']))
			$browser_lang = substr($_SERVER['HTTP_ACCEPT_LANGUAGE'], 0, 2);
		else
			$browser_lang = 'en';

		if (in_array($browser_lang, avalaible_languages())) {
			setcookie('lang', $browser_lang);
			$_COOKIE['lang'] = $browser_lang;
		}
		else {
			setcookie('lang','en');
			$_COOKIE['lang'] = 'en';
		}
	}
		
	return $_COOKIE['lang'];
}

function choose_language()
{
	/* Write the language menu */
	$current_language = get_language();
	?>
	<form action = "" method = "post" style='text-align:right;' >
		 <div>
		 <select name = 'new_lang' onchange='this.form.submit()'>
		 <?
		 foreach (avalaible_languages() as $lang) {
		echo "<option label = '".$lang."' value = '".$lang."' ";
		if ($lang == $current_language)
			echo "selected = 'selected'";
		echo ">".$lang."</option>\n";
	}
	?>
	</select>
        <noscript><input type="submit" name="submit_language" value="Change" /></noscript>
	</div>
	</form>
<?
}

/* Apertium.org translation system */

function display_streamer()
{
	/* Write the streamer */
	?>
	<div id='streamer'>
<?	
	choose_language();
	?>
	</div>
<?
}

/* Interface language */

function retrieve_info($page, $line)
{
	/* Return the line $line of the file templates/$get_language()/$page */
	$current_language = get_language();
	$templates_dir = 'templates/';

	$source = array_map("trim", file($templates_dir . $current_language . '/' . $page));
	return $source[$line];
}

function get_text($page, $info)
{
	/* Return the text of $info of the page $page
	 * in the language get_language();
	 */
	switch ($page) {
	case 'index':
		switch ($info) {
		case 'title':
			return retrieve_info('index', 0);
		case 'menu_title':
			return retrieve_info('index', 1);
		case 'menu1':
			return retrieve_info('index', 2);
		case 'menu2':
			return retrieve_info('index', 3);
		case 'menu3':
			return retrieve_info('index', 4);
		case 'supported_format':
			return retrieve_info('index', 5);
		case 'translate' :
			return retrieve_info('index', 6);
		case 'columns_name' :
			return retrieve_info('index', 7);
		case 'TMX_menu_title' :
			return retrieve_info('index', 8);
		case 'TMX_menu1' :
			return retrieve_info('index', 9);
		case 'TMX_menu2' :
			return retrieve_info('index', 10);
		case 'TMX_menu3' :
			return retrieve_info('index', 11);
		default:
			return '';
		break;
		}

	case 'translate':
		switch ($info) {
		case 'title':
			return retrieve_info('translate', 0);
		case 'select_language':
			return retrieve_info('translate', 1);
		case 'button_translate':
			return retrieve_info('translate', 2);
		case 'manual_replacement':
			return retrieve_info('translate', 3);
		case 'dictionary':
			return retrieve_info('translate', 4);
		case 'gen_TMX':
			return retrieve_info('translate', 5);
		case 'add_TMX':
			return retrieve_info('translate', 6);	
		default:
			return '';
		}
		break;
	default:
		/* This is a module
		 * $page = module_modulename
		 */
		switch ($info) {
		case 'name':
			return retrieve_info($page, 0);
		case 'description':
			return retrieve_info($page, 1);
		case 'button_in':
			return retrieve_info($page, 2);
		case 'button_out':
			return retrieve_info($page, 3);
		default:
			return '';
		break;
		}

		/* $module_name = str_replace("module_", '', $page); */
		break;
	}	
}

function write_text($page, $info)
{
	/* Write the text of $info of the page $page
	 * in the language get_language();
	 */
	echo get_text($page, $info);
}

/* If the user wants to change the language */
if (isset($_POST['new_lang']) && in_array($_POST['new_lang'], avalaible_languages())) {
	setcookie('lang', $_POST['new_lang']);
	$_COOKIE['lang'] = $_POST['new_lang'];
}

?>