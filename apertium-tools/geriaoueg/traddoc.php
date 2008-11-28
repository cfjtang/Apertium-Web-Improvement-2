<?
	include("cabecera.php");

	$direccion = $_POST["direccion"];
	$funcion = $_POST["funcion"];
	$inurl = urldecode($_GET["inurl"]);

	// Where all the files are
	$ROOT = "/var/www/geriaoueg";
	$TRADUCTOR = "/build/local/default/bin/apertium";
	$TMP = "/tmp/";
	$LOG = $ROOT . "/log/access.log";

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

	$pagetext = file_get_contents($_FILES['userfile']['tmp_name']); 

	// We detect between two encodings, UTF-8 and ISO-8859-15 (latin1),
	// if the page is in latin1, we use the latin1 locale, if the page
	// is in UTF-8 we use the UTF-8 locale for external processing.
	$enc = mb_detect_encoding($pagetext, "UTF-8, ISO-8859-15");
	if($enc == "UTF-8") {
		$encoding = "utf8";
	} else {
		$encoding = "iso885915@euro";
	}

	# Guardar información de uso
	$today = date("Y-m-d, G:i:s T");               // 2001-10-01, 15:16:08 MST
	$fd = fopen($LOG, 'a+');
	fputs($fd, $today . " { " . $_REQUEST['direccion'] . "; " . $_REQUEST['funcion'] . " } { " . $_SERVER["REMOTE_ADDR"] . " } { WordPress plugin } { " . $_FILES['userfile']['tmp_name'] . " } \n");
	fclose($fd);

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

  
	// Are we running on a weird port?
	$puerto = getenv("SERVER_PORT") == 80 ? "" : ":" . getenv("SERVER_PORT");

	// This is the base url that we use for navigation
	$dirbase = "http://" . getenv("SERVER_NAME"); 
	$dirbase = $dirbase . $puerto . "/" . $relpath . "/navegador.php?";
	$dirbase = $dirbase . "&direccion=" . $direccion . "&inurl=";

	if($funcion == "off") {
	        // Send the page back in the encoding that it came in
	        header("Content-Type: text/html; charset=\"$encoding\"");

	        // Print out the mangled page
	        echo shell_exec("cat $archivo");

		exit;

	} else if($funcion == "translate") {
	        // Send the page back in the encoding that it came in
	        header("Content-Type: text/html; charset=\"$encoding\"");

	        // Print out the mangled page
	        echo shell_exec("LANG=es_ES.$encoding $TRADUCTOR -f html $direccion $archivo");

		exit;
	}


	$ejecutable = "LANG=es_ES.$encoding $ROOT/apertium-deshtml $archivo | LANG=es_ES.$encoding $proc $transducer > $infile";

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
		$key = str_replace("_", " ", $key[0]);
		$key = str_replace("'", "’", $key);
		$lookup[$key] = $lookup[$key] . " " . trim(str_replace("_", " ", $row[1]));
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
	function readWord($_fd, $lookup, $encoding) {
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

		if(sizeof($lemmata) == 0) {
			return $word . " ";
		}

		$count = 0;
		$vacio = 0;
		// For each lemma, print out the lemma + what we find in the translation table
		foreach(array_keys($lemmata) as $lemma) {
			if($encoding != "utf8") {
				if(array_key_exists(iconv("latin1","utf-8",strtolower($lemma)), $lookup)) {
					$vacio++;
				}
			} else {
				if(array_key_exists(strtolower($lemma), $lookup)) {
					$vacio++;
				}
			}

			// The wordlists are stored in UTF-8 but we might be trying to look up a word in latin1
			if($encoding != "utf8") {
				$add = iconv("utf8", "latin1", $lookup[iconv("latin1","utf-8",strtolower($lemma))]);
				$body = $body . "(<b>" . $lemma . "</b>) " . $add;
			} else { 
				$body = $body . "(<b>" . $lemma . "</b>) " . $lookup[strtolower($lemma)];
			}
			if($count < (sizeof($lemmata) - 1)) {
				$body = $body . " <b>&middot;<\/b> ";
			}
			$count++;
		}

		if($vacio == 0) {
			return $word . " ";
		}

		// This inserts the CSS span class and the definition etc.
		$output = '[<span class="word-H">]' . $word . '[<span class="definition-H">]' . $body . '[<\/span><\/span> ]';

		return $output;
	}

	$body = false;
	$escaped = false;
	$line = "";

	while(!feof($fd)) {
		
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
			fwrite($fdo, readWord($fd, $lookup, $encoding));
		}


		$escaped = false;
		$c = fread($fd, 1);
		
		if($c == '\\') {
			$escaped = true;
			$c = fread($fd, 1);
			$c = fread($fd, 1);
		}
	}


	// Reformat the HTML 
	// Then re-write the links
	$cmd = "LANG=es_ES.$encoding apertium-rehtml " . $outfile;
	$cmd = $cmd . ' | LANG=es_ES.$encoding ' . $turl . ' "' . $dirbase . '" "' . $inurl . '"';
	
	// Send the page back in the encoding that it came in
	header("Content-Type: text/html; charset=\"$encoding\"");

	// Print out the mangled page
	echo shell_exec($cmd);


	// Cleanup

	fclose($fdo);
	fclose($fd);

	unlink($infile);
	unlink($outfile);
?>
