<?php
require_once('gettext.inc');

$textdomain="simpledix";
if (empty($locale))
	$locale = 'es_ES.utf8';
if (isset($_GET['locale']) && !empty($_GET['locale']))
	$locale = $_GET['locale'].".utf8";
putenv('LANGUAGE='.$locale);
putenv('LANG='.$locale);
putenv('LC_ALL='.$locale);
putenv('LC_MESSAGES='.$locale);
T_setlocale(LC_ALL,$locale);
T_setlocale(LC_CTYPE,$locale);

$locales_dir = dirname(__FILE__).'/../i18n';
T_bindtextdomain($textdomain,$locales_dir);
T_bind_textdomain_codeset($textdomain, 'UTF-8'); 
T_textdomain($textdomain);
?>
