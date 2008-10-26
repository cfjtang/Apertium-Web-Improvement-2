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
 <p/>
 <div style="width: 80%; margin-left: 10%; margin-right: 10%;">
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
				<input  type="radio" 
					name="direccion" 
					value="br-nl" 
					title="Breton pages with Dutch glosses"
					/>Breton → Dutch
				<input  type="radio" 
					name="direccion" 
					value="cy-en"
					title="Welsh pages with English glosses"
					/>Welsh → English
				</label>
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
  </body>
</html>
