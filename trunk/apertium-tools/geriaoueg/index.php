<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="es">
<head>
  <meta http-equiv="content-type" content="application/xhtml+xml; charset=UTF-8" />
  <link rel="stylesheet" href="styles/common.css" type="text/css"/>
  <title>Geriaoueg</title>
</head>
<body>
 <h1 id="web" style="border-top: none; padding-top: 0;">Geriaoueg</h1>
 <?
	if(strstr($_SERVER["HTTP_USER_AGENT"], "MSIE")) {
		echo '<div style="width: 60%; margin-left: 20%; margin-right: 20%; text-align: justify;">';
		echo "This site doesn't work in Microsoft Internet Explorer™ and I don't have a computer running Microsoft Windows™ to test it on. Sorry :( ";
		echo "<p/>";
		echo 'In the meantime you can always try <a href="http://www.mozilla-europe.org/en/firefox/">Firefox</a>, or submit a patch to Microsoft Internet Explorer™ to make it support CSS2.';
		echo '</div>';
	}

 ?>
 <p/>
 <div style="width: 60%; margin-left: 20%; margin-right: 20%;">
   <form action="navegador.php" method="post" style="padding: 3px">
     <fieldset>
<!--
	  <legend></legend>
-->	
       <label for="inurl"><b>URL</b>:<br/>
			<input title="URL to be translated" 
		       	       name="inurl" 
			       type="text" 
			       size="70" 
			       value="http://"/></label>

		<br/>
		<label for="direccion"><b>Direction:</b><br/>
				<input  type="radio" 
					name="direccion" 
					value="br-fr" 
					title="Breton pages with French glosses"
					checked/>Breton → French
<!--
				<input  type="radio" 
					name="direccion" 
					value="br-nl" 
					title="Breton pages with Dutch glosses"
					/>Breton → Dutch -->
				<input  type="radio" 
					name="direccion" 
					value="cy-en"
					title="Welsh pages with English glosses"
					/>Welsh → English
				</label>
<!--				<input  type="radio" 
					name="direccion" 
					value="is-ca"
					title="Icelandic pages with Catalan glosses"
					/>Icelandic → Catalan
				</label> -->

                <input type="hidden" value="vocab" name="funcion"/>

	  </fieldset>
	 	<br/>
	    <div>
	  	<input type="submit" 
		       value="Browse" 
		       class="submit"
		       title="Click here to browse"/>
		       
		<input type="reset" 
		       value="Reset"  
		       class="reset"
		       title="Click here to reset"/>

           </div>

      </form>
      </div>
    </div>
 <div style="width: 60%; margin-left: 20%; margin-right: 20%; text-align: justify">
 
 <h3>Acknowledgements</h3>
 The data for Breton come from Tomaz Jacquet's <a href="http://www.freelang.com/dictionnaire/breton.html">dictionary</a>.
 For Welsh, the data come from the <a href="http://www.cymraeg.org.uk">apertium-cy</a> machine translation system, with thanks
 to Kevin Donnelly. The morphological
 analysers for Breton and Welsh can be found online <a href="http://xixona.dlsi.ua.es/~fran/breton/">here</a> and 
 <a href="http://xixona.dlsi.ua.es/~fran/welsh/">here</a>. 
 The source code for this site is freely available under the
 <a href="http://www.gnu.org/licences/gpl.html">GNU GPL</a> and may be downloaded from the Apertium 
 <a href="http://wiki.apertium.org/wiki/SVN">subversion repository</a> as the module <code>apertium-tools/geriaoueg</code>.
 <p/>
 Please report any bugs to <a href="mailto:apertium-stuff@lists.sourceforge.net">apertium-stuff@lists.sourceforge.net</a>.
</div>
 <div style="width: 60%; margin-left: 20%; margin-right: 20%; font-size: 80%; text-align: center; margin-top: 10em; color: #bbbbbb;">
Copyright &copy; 2007&mdash;2008 Francis Tyers 
</div>
  </body>
</html>
