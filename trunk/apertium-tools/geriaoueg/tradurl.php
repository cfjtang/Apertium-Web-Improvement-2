<?
	include("cabecera.php");

	$direccion = $_GET["direccion"];
	$inurl = urldecode($_GET["inurl"]);

	// Where all the files are
	$ROOT = "/var/www/geriaoueg";
	$TMP = "/tmp/";

	$relpath = "/geriaoueg/";
	$infile = tempnam("/tmp","Geriaoueg.in.");
 	$outfile = tempnam("/tmp", "Geriaoueg.out."); 

	// Path to lt-proc
	$proc = "/build/local/default/bin/lt-proc";

	// Path to the analysers, the name should be in the format of
	// xx-yy.bin where xx is the source language code and yy is the 
	// target language code
	$transducer = $ROOT . "/analysers/" . $direccion . ".bin";

	// Path to the wordlists, naming scheme as above
	$bidix = $ROOT . "/wordlists/" . $direccion . ".txt";

	// Path to turl
	$turl = "/var/www/geriaoueg/turl";

	// la variable $autorizado si es distinto de 0 se puede usar sin límites
	if(strlen($inurl) >= 500){
		echo("Error");
		exit;
	}

	$autorizado = 0;

	error_reporting(E_USER_ERROR);

	$archivo = tempnam($TMP, "URL");

	error_reporting(0);

	// We set the user agent of PHP to the same as the user's browser
	// this fixes some problems with Wikipedia (it doesn't like PHP)
	ini_set('user_agent', $_SERVER['HTTP_USER_AGENT'] . "\r\n");
	$pagetext = file_get_contents($inurl);


/*
	// This code inserts the Javascript and base url thing
	$insertion = '<head>' . "\n";
	$insertion = $insertion . '<base href="' . $inurl . '" target="_top"/>' . "\n";
	$insertion = $insertion . '<script src="http://elx.dlsi.ua.es/geriaoueg/js/boxover.js"></script>' . "\n";
	$pagetext = str_replace("<head>", $insertion, $pagetext);
*/
	// This code inserts the CSS and base url thing
	$insertion = '<head>' . "\n";
	$insertion = $insertion . '<base href="' . $inurl . '" target="_top"/>' . "\n";
	$insertion = $insertion . '<link rel="stylesheet" href="http://elx.dlsi.ua.es/geriaoueg/styles/hover.css" type="text/css"/>' . "\n";
	$pagetext = str_replace("<head>", $insertion, $pagetext);

	if(strlen($pagetext) == 0) {
		error("Zero size page returned");
		exit;
	}

	limite($pagetext, 16384 * 4 * 4 * 4, "Maximum size excedeed");
	
	// Shove the page in a temporary file.
	$fd = fopen($archivo, "w");

	if(!$fd){
		error("File creation failure");
		exit;
	}

	fputs($fd, $pagetext);
	fclose($fd);

	$encoding = "utf8";
  
	// Are we running on a weird port?
	$puerto = getenv("SERVER_PORT") == 80 ? "" : ":" . getenv("SERVER_PORT");

	// This is the base url that we use for navigation
	$dirbase = "http://" . getenv("SERVER_NAME"); 
	$dirbase = $dirbase . $puerto . "/" . $relpath . "/navegador.php?";
	$dirbase = $dirbase . "&direccion=" . $direccion . "&inurl=";

	$ejecutable = "LC_ALL=es_ES.utf8 apertium-deshtml $archivo | LC_ALL=es_ES.utf8 $proc $transducer > $infile";

	// Deformat and analyse the text.
	shell_exec($ejecutable);

	unlink($archivo);

	// Create a translation lookup table from a tab-separated file.
	$lookup = array();
	$bfile = file($bidix);
	foreach($bfile as $line) {
		$row = explode("\t", $line);
		// Some entries contain plurals attached to the word e.g. yezh(où)
		$key = $row[0];
		$key = str_replace("(", " ", $key);
		$key = explode(" ", $key);
		$key = $key[0];
		$lookup[$key] = $lookup[$key] . " " . trim($row[1]);
	}

	// Open the input file (the analysed/deformatted text)	
	$fd = fopen($infile, "r");
	$c = fread($fd, 1);

	// Open the output file (the text with hoverboxes and not analyses)
	$fdo = fopen($outfile, "w");


	//
	// Read a word, words are structured like: ^kant/gant<pr>/kant<adj>/kant<num><mf><pl>/kant<n><m><sg>$
	//
	// So we read until the first '/' to get the surface form, then we go reading 
	// until each '/' to get the possible lemmata.
	//
	function readWord($_fd, $lookup) {
		$word = "";
		$lemmata = array();

		// Get the surface form
		while($c != '/') {
			$word = $word . $c;
			$c = fread($_fd, 1);
		}
	
		
		if(ctype_punct($word)) {
			return $word;
		}
		
		$lemma = "";
		$c = fread($_fd, 1);
		// Pull the rest of the lemmata out of the analysis
		while($c != '$') {
			$lemma = $lemma . $c;

			$c = fread($_fd, 1);	

			if($c == '<') {
				$lemma = str_replace('/', '', $lemma);
				$lemmata[$lemma] = $lemma;
				$lemma = "";
				while($c != '/' && $c != '$') {
					$c = fread($_fd, 1);	
				}
			}
		}

		// Some of the lemmata might be duplicates
		$lemmata = array_unique($lemmata);

		// For each lemma, print out the lemma + what we find in the translation table
		foreach(array_keys($lemmata) as $lemma) {
			$body = $body . "(<b>" . $lemma . "</b>) " . $lookup[strtolower($lemma)] . " <b>·<\/b> ";
		}

/*
		// This inserts the Javascript span class and the definition etc.
		$output = '[<span class="tooltip" title="header=\[' . $word . '\] body=\[' . $body . '\]">]';	
		$output = $output . $word;
		$output = $output . "[<\/span>] ";
*/	

		// This inserts the CSS span class and the definition etc.
		$output = '[<span class="word-H">]' . $word . '[<span class="definition-H">]' . $body . '[<\/span><\/span> ]';

		return $output;
	}

	$body = false;
	$escaped = false;
	$line = "";

	// Remove the first SENT symbol
	$c = fread($fd, 1);
	while($c != '$') {
		$c = fread($fd, 1);
	}	
	$c = "";

	while(!feof($fd)) {
		// We don't want to translate stuff in the header of the HTML document
		// e.g. the title, so we skip the body
		while($body == false) {

			$line = $line . $c;
			if(strstr($line, "<body>") || strstr($line, "<body ")) {
				fwrite($fdo, $c);
				$body = true;
				while($c != ']') {
					fwrite($fdo, $c);
					$c = fread($fd, 1);
				}
				break;
			}

			$c = fread($fd, 1);

			// Although it has been analysed, so we want to remove the analyses
			if($c == '^') {
				$c = fread($fd, 1);
				while($c != '/') {
					fwrite($fdo, $c);
					$c = fread($fd, 1);
				}
				while($c != '$') {
					$c = fread($fd, 1);
				}
				$c = fread($fd, 1);
			}

			fwrite($fdo, $c);
		}	
		
		// Superblanks are formatting, just send them on their way
		if($c == '[') {
			$end = false;
			$escaped = false;
			while($end == false) {
				$c = fread($fd, 1);

				// This is an escaped character, e.g. [, so we 
				// just read past it. Removing this causes problems like
				// inserting popups inside tags
				if($c == '\\') {
					$escaped = true;
					$c = fread($fd, 1);
				}

				if($c == ']' && $escaped == false) {
					fwrite($fdo, $c);
					$end = true;
				}
				fwrite($fdo, $c);
				$escaped = false;
			}
		}

		// We've reached the start of a word.
		if($c == '^') {
			fwrite($fdo, readWord($fd, $lookup));
		}


		$escaped = false;
		$c = fread($fd, 1);
		
		if($c == '\\') {
			$escaped = true;
			$c = fread($fd, 1);
		}
	}


	// Reformat the HTML and insert the hoverover javascript,
	// Then re-write the links
	$cmd = "LC_ALL=es_ES.utf8 apertium-rehtml " . $outfile;
	$cmd = $cmd . ' | LANG=es_ES.UTF-8 ' . $turl . ' "' . $dirbase . '" "' . $inurl . '"';
	
	header("Content-Type: text/html; charset=\"$encoding\"");

	// Print out the mangled page
	echo shell_exec($cmd);


	// Cleanup

	fclose($fdo);
	fclose($fd);

	unlink($infile);
	unlink($outfile);
?>
