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
	
	<div class = "languageChooser">
		<a href = "?locale=es_ES"> Español </a>
		<a href = "?locale=en_US"> English </a>
	</div>	
		
	<div class = "header">
		<h1> Simpledix </h1>
	</div>

	<span>
		<input type="checkbox" name="showInstalledCB" id = "showInstalledCB"
			onchange = "showInstalled()" checked="selected">
			<?= T_gettext("Show only installed pairs")?>
		</input>
	</span>
	
	<span style="margin-left:10px">
		<input type= "radio" name = "fileSource" value = "file" 
			checked="true" onclick="changeSource()" onchange="changeSource()"> 
			<?= T_gettext("From disk")?>
		</input>
		<input type= "radio" name = "fileSource" value = "text" 
			onclick="changeSource()" onchange="changeSource()"> 
			<?= T_gettext("From url")?>
		</input>	</span>

	<span style="margin-left:10px">
		<a href = "confList.php" target = "_blank" ><?= T_gettext("Configuration files:")?></a>
	</span> 
	<span style="margin-left:10px">
		<a href = "http://wiki.apertium.org/wiki/User:Dtr5" target = "_blank" ><?= T_gettext("Tutorial")?></a>
	</span> 

	<div class = "upload">
		
		<form id = "form" action="upload_dix.php" method="post" enctype="multipart/form-data">
			<div>
				<div class = "leftUpload">
					<div id = "lang1holder" class = "langHolder">
						<?= T_gettext('Dictionary:')?> <select id = "lang1"  name = "lang1" onchange = "searchPairs()"></select>
					</div>
					
					<div class = "dictUpload">
						<input type="file" name="leftDix" id="leftDix" 
							onclick = "this.className = ''; this.value = ''"
							onchange = "check_file(this, ['.dix', '.metadix'])" /> 
					</div>
					<div class = "confUpload">
						<span id = "opt1">
						<?= T_gettext('Configuration file (Optional):')?>
						</span>
						<span id = "req1" class = "hidden" style = "color:red">
							<?= T_gettext('Configuration file:')?>
						</span>
						<br/>
						<input type="file" name="leftConf" id="leftConf" class = "big"/> 
						<input type="button" value ="<?= T_gettext('Clear');?>" class="tiny" onclick = "clearInput('leftConf')"/>
					</div>
				</div>
				
				<div class = "rightUpload">
					<div id = "lang2holder" class = "langHolder">
						<?= T_gettext('Dictionary:')?> <select id = "lang2" name = "lang2"></select>
					</div>
					<div class = "dictUpload">
						<input type="file" name="rightDix" id="rightDix" 
							onclick = "this.className = ''; this.value = ''"
							onchange = "check_file(this, ['.dix', '.metadix'])"/> 
					</div>
					<div class = "confUpload">
						<span id = "opt2">
							<?= T_gettext('Configuration file (Optional):')?> 
						</span>
						<span id = "req2" class = "hidden" style = "color:red">
							<?= T_gettext('Configuration file:')?> 
						</span>
						<br/>
						<input type="file" name="rightConf" id="rightConf" class = "big"/> 
						<input type="button" value ="<?= T_gettext('Clear');?>" class="tiny" onclick = "clearInput('rightConf')" width = "40px"/>
					</div>			
				</div>
				<div style="clear:both"></div>
			</div>

			<div class = "biUpload">
				<div class = "langHolder">
					<?= T_gettext('Bilingual:')?>
				</div>
				<div class = "dictUpload">
					<input type="file" name="biDix" id="biDix" class="w70" 
						onclick = "this.className = ''; this.value = ''"
						onchange = "check_file(this, ['.dix', '.metadix'])"/> 
				</div>
			</div>
			
			<input type="button" name="button" value="<?= T_gettext('Upload')?>" onclick="checkUpload()" class="uploadButton"/>
		</form>
	</div>
	


	</body>
	
	<script type="text/javascript">
		DELETE_ALL_CONFIRM = '<?= T_gettext("Delete all?")?>';
		EXTENSION_ERROR = '<?= T_gettext("Extension should be in ")?>';
		MISSING = '<?= T_gettext("Missing files")?>';
		MISSING_CONF = '<?= T_gettext("Missing configuration")?>';
		CONFCHECK_ERROR = '<?= T_gettext("Error while checking if a conf file exists")?>';
		UNDEF_LANG = '<?= T_gettext("Both languages should be defined")?>';
		SAME_LANGUAGES = '<?= T_gettext("Same language in both dictionaries")?>';
		DICTIONARY = '<?= T_gettext("Dictionary:")?>';
		getInstalledPairs();
	</script>
	
</html>
