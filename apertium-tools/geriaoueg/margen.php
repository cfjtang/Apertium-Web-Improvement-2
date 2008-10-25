<?
	$inurl = $_GET["inurl"]; 
	$inurl = urldecode($inurl); 
?>
<base target="_top">
<html>
<head>
  <title>Geriaoueg</title>
</head>
<body style="border-bottom: black 2px solid; margin: 0px; padding: 2px; background-color: #bbbbff;">

<div style="text-align: center; background-color: #bbbbff; margin: 0px; padding: 0px;">
  <div style="float: left; font-family: Sans;">
    <? echo $_GET["direccion"]; ?>
  </div>
  <div style="float: right; font-family: Sans, Helvetica;">
    <a href="http://elx.dlsi.ua.es/geriaoueg/">Home</a>
  </div>
  <?
    $inurl = $_GET["inurl"];
    $direccion = $_GET["direccion"];
  ?>
  <form action="navegador.php" method="post" style="padding: 3px; font-family: Sans, Helvetica;">
    <b>Page</b>:
    <input title="URL to be translated" name="inurl" type="text" size="70" value="<? echo $inurl; ?>"/>
    <input type="hidden" value="<? echo $direccion; ?>" name="direccion"/>
    <input type="submit" value="Browse" class="submit" title="Click here to browse"/>
  </form>

</div>
</body>
</html>
