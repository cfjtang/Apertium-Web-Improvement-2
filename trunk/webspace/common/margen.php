<?
	$inurl = $_GET["inurl"]; 
	$inurl = urldecode($inurl); 

	$funcion = $_GET["funcion"];
?>
<base target="_top">
<html>
<head>
  <title>Apertium</title>
</head>
<body style="border-bottom: black 2px solid; margin: 0px; padding: 2px; background-color: #bbbbff;">

<div style="text-align: center; background-color: #bbbbff; margin: 0px; padding: 0px;">
  <div style="float: left; font-family: Sans, Helvetica;">
    <a href="http://www.apertium.org/">
      <img src="http://www.apertium.org/apertium-www/images/Apertium_logo_225x48.png" height="44" border="0"/>
    </a>
  </div>
  <?
    $inurl = $_GET["inurl"];
    $direccion = $_GET["dir"];
  ?>
  <form action="browser.php" method="post" style="padding: 12px; font-family: Sans, Helvetica;" name="nav">
    <b>Page</b>:
    <input title="URL to be translated" name="inurl" type="text" size="60" value="<? echo $inurl; ?>"/>
    <input type="hidden" value="<? echo $direccion; ?>" name="dir"/>
    <input type="submit" value="<? echo _('Translate'); ?>" class="submit" title="Click here to browse"/>
   <br/>
  </form>

</div>
</body>
</html>
