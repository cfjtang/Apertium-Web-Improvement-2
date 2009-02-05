<?php
	$sections = array("whatisapertium", "whodevelopsit", "testdrive", "documentation", "interact", "software", "funding", "users", "contact");
	$text = array("what is apertium", "who develops it", "test drive", "documentation", "interact!", "software", "funding", "users", "contact");

$subsections = array
(
/*
"whatisapertium" =>array
   (
   "langpairs"=>"Language pairs",
   ),
   */
   "testdrive"=>array
   (
   	"translatetext"=>"Text translation",
   	"translatedoc"=>"Document translation",
   	"surfandtranslate"=>"Surf &amp; translate",
	"lookup"=>"DicLookUp",   	
/*   	"link@Apertium unstable"=>"http://xixona.dlsi.ua.es/apertium-unstable/", */
   	"link@Apertium alpha-testing"=>"http://xixona.dlsi.ua.es/testing/"   	
   ),
/*
"documentation" => array
   (
   "documents"=>"Documents",
   "link@publications"=>"http://wiki.apertium.org/wiki/Publications",
   "link@Wiki" => "http://wiki.apertium.org/"
   ),*/
"interact" =>array
	(
		"link@cgi::irc"=>"http://xixona.dlsi.ua.es/cgi-bin/cgiirc/irc.cgi",
		"link@Forum"=>"http://sourceforge.net/forum/forum.php?forum_id=481030",
		"link@Mailing list"=>"https://lists.sourceforge.net/lists/listinfo/apertium-stuff"
	),
"software" =>array
	(
		"apertium-tagger-training-tools"=>"Tagger training tools",
		"apertium-transfer-tools"=>"Transfer tools",
		"lttoolbox"=>"Lexical transformation",
		"apertium-eval-translator"=>"Evaluation",
		"apertium-tinylex"=>"TinyLex"
	),
);

?>

