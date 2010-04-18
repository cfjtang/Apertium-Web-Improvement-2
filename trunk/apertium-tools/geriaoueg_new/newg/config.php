<?php
//Setting up variables

//Stuff that should come from user.
$direction="es-en";

//newg root 
$root="/home/deepakjoy/public_html/newg";

//
$homeurl="http://localhost/xampp/public_html/newg/test.php";

//apertium root
$root_aper="/usr/local/bin";

//apertium trunk for language pairs
$root_lang="/home/pubuntu/Apertium";

//temporary files folder (files will be created here)
$tmp="/tmp/";
$log=$root."log/access.log";

//relative path of newg root
$relpath="xampp/newg";
$infile=tempnam($tmp,"newg.in.");
$outfile=tempnam($tmp,"newg.out.");

//file with list of valid language pairs
$lang_list=$root."/language_pairs";

//lt-proc
$ltproc=$root_aper."/lt-proc";

//lt-comp
$ltcomp=$root_aper."/lt-comp";

//lt-expand
$ltexpand=$root_aper."/lt-expand";

//analyser generator (rerlative)
$w_ana="analysers/generator.php";

//wordlist generator (rerlative)
$w_gen="wordlists/w_generator.php";

//Path to translator
$translator= $root_aper."/apertium";

// Path to the analysers, the name should be in the format of
// xx-yy.bin where xx is the source language code and yy is the 
// target language code
$transducer = $root . "/analysers/" . $direction . ".bin";

// Path to the wordlists, naming scheme as above
$bidix = $root . "/wordlists/" . $direction . ".txt";

?>
