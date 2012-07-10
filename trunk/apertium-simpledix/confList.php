<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<?php
	require_once("i18n/i18n.php");
?>        
        
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
	<head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
    <link rel="stylesheet" type="text/css" href="style/style.css"/>
    <script type="text/javascript" src="scripts/simpledix.js"></script>
    <script type="text/javascript" src="scripts/jquery-1.6.4.js"></script>

	<title>Simpledix</title> 
	</head> 

	<body>
		
		<div class = "configList">
			<div class = "configListTitle"><?= T_gettext("Configuration files:")?> </div>
			<ul id = "allConfs"/>
		</div>
	
	</body>
	<script type="text/javascript">
		getAllConfigs();
	</script>
	
</html>
