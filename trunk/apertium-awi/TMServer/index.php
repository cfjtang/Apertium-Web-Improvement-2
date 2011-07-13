<?//coding: utf-8
/* Apertium Web TMServer
 * Main page
 *
 * Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
 * Mentors : Arnaud Vié, Luis Villarejo
 */

include_once('includes/TMmanage.php');

/* Get avalaible language pairs list */
if (isset($_GET['action']) && $_GET['action'] == 'get_language_pairs') {
	foreach ($tm->get_language_pairs() as $lang_pair)
		echo $lang_pair . "\n";
	exit();
}

/* Get Translation Memory */
if (isset($_GET['language_pair'])) {
	$file = $tm->get_TM($_GET['language_pair']);
	header('Location: ' . $file);
}
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="es">
  <head>
    <link rel="shortcut icon" href="http://xixona.dlsi.ua.es/apertium-www/favicon.ico"/>
    <meta http-equiv="Content-type" content="text/html; charset=UTF-8"/>
    <title>TMServer</title>
    <link rel="stylesheet" type="text/css" href="CSS/style.css?v=<? echo time(); ?>" />
  </head>
  <body>

    <div id='content'>
      <div id='header'>
	<h1>Apertium</h1>
	<p>Translation Memory Server</p>
      </div>

      <div id='frame'>

	<p>What would you like to do ?</p>

	<ul>
	  <li>Add a translation memory</li>
	  <form action="" method="post" enctype="multipart/form-data">
	    <input type="file" name="in_doc" />
	    <input type="submit" name="add_tm" value="Add TM" />
	  </form>
<?
/* Add Translation Memory */
if (isset($_FILES["in_doc"]) && !($_FILES["in_doc"]["error"] > 0)) {
	$source = file_get_contents($_FILES["in_doc"]["tmp_name"]);
	if ($tm->add_TM($source))
		echo '<p>Translation memory correctly added !</p>';
	else
		echo '<p>An error occured</p>';
}
?>
	  <li>Get a translation memory:<br />
	    <p>Avalaible language pairs: 
	      <form action = "" method = "get">
		<select name='language_pair' onchange='this.form.submit()'>
<?
						foreach ($tm->get_language_pairs() as $pair)
						echo '<option value="' . $pair . '" label="' . $pair . '">'.$pair.'</option>' . "\n";
?>
		</select>
		<input type='submit' name='get_language_pairs' value='Get the TM!' />
		</form>
	    </li>
	  </ul>
	  <p>Want to use TMSever in your web application ?</p>
	  <ul>
	    <li><a href="index.php?action=get_language_pairs">Get the language pairs list</a></li>
	    <li><a href="index.php?language_pair=[HERE YOUR LANGUAGE PAIR]">Get an record from the database, by indicating the language pair</a>(in URL, replace [HERE YOUR LANGUAGE PAIR] with the language pair, for example fr-es)</li>
	  </ul>
	</div>
      </div>

  </body>
</html>
