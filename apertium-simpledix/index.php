<?php
   
    if($_GET['user'] == '')
    {
        header("Location: upload.php");
        exit;
    }
	header("Content-Type: text/html; charset=UTF_8");
		
	require_once("include/membersite_config.php");
	require_once("include/simpledix.php");
	require_once("i18n/i18n.php");
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
    <link rel="stylesheet" type="text/css" href="style/style.css"/>
    <script type="text/javascript" src="scripts/simpledix.js"></script>
    <script type="text/javascript" src="scripts/jquery-1.6.4.js"></script>

	<title>Simpledix</title> 
	</head> 
	<body>

		<div class = "languageChooser">
			<a href = "?locale=es_ES&user=<?=$_GET['user']?>"> Español </a>
			<a href = "?locale=en_US&user=<?=$_GET['user']?>"> English </a>
		</div>	

		<div class = "header">
			<h1> Simpledix </h1>
			<div class = "floathidden">
				<select id = "lang1">
				</select>

				<select id = "lang2" >
				</select>
			</div>

		</div>
		
		<span style="margin-left:10px">
			<?= T_gettext('Generate directions:');?>
			<input type="checkbox" name = "B" id = "B" checked="checked" onclick="toggleClassCb(this, 'itemHiglight', 'dull', 'harr')"></input> 
			<span id = "harr" class = "itemHiglight">&harr;</span>
			<input type="checkbox" name = "LR" id = "LR" onclick="toggleClassCb(this, 'itemHiglight', 'dull', 'rarr')"></input> 	
			<span id = "rarr">&rarr;</span>
			<input type="checkbox" name = "RL" id = "RL" onclick="toggleClassCb(this, 'itemHiglight', 'dull', 'larr')"></input>
			<span id = "larr">&larr;</span>
		</span>	
		
        <span style="margin-left:10px">
			<?=T_gettext("Filter by lexical category:") ?>
			<select id = "categorias" onchange="filterCategories(this)"></select>
        </span>
		
		<div class="floathidden">
				Mode:<br/>
				<input type="radio" name="speed" value="N" /> Normal<br />
				<input type="radio" name="speed" value="F" checked="checked" /> Fast<br />
				<input type="radio" name="speed" value="E" /> Faster<br />
		</div>
		
		<div class="floathidden" id="configFiles">
			Lang 1 configuration:
			<table id = "cFiles1" class = "uline" ></table>
			Lang 2 configuration:
			<table id = "cFiles2"></table>
		</div>

        <div id = "theResult" class = "divResult">
			<input type = "button" value="X" class="closeButton"
				onClick = "toggleClass('Mostrar', SHOW_INSERT_QUERIES, HIDE_INSERT_QUERIES, 'divResult', 'extendedDivResult', 'theResult')"/>

			<ul class = "result" id = "result"></ul>
        </div>
		<div class="thetable">
			<div class = "tableHeader">
				Lang: <span id = "lang1Label"></span>
			</div>
			
			<div class = "wInsert ttip" id = "toInsert1" hidden="true">
				<?= T_gettext("Word will be inserted");?> 
				<span><?= T_gettext("Insert text");?> </span> 
			</div>
			<div class = "wAlready ttip" id = "already1" hidden="true"> 
				<?= T_gettext("Word is already inserted");?> 
				<span><?= T_gettext("Already text");?> </span>  
				</div>
			<div class = "wUndef ttip" id = "undef1" hidden="true"> 
				<?= T_gettext("Word or paradigm is undefined");?> 
				<span><?= T_gettext("Error text");?> </span>  
			</div>
			<div class = "wWarning ttip" id = "warn1" hidden="true"> 
				<?= T_gettext("Regenerate insertions");?> 
				<span><?= T_gettext("Warning text");?> </span> 
			</div>
			<table id ="table1" >
			<tbody/>
			</table>
			
			<div><?= T_gettext("Existing translations:");?></div>
			<ul id = "translations1"></ul>
			<div style="clear:both"></div>
		</div>
		
		<div class="thetable">
			<div class = "tableHeader">
				Lang: <span id = "lang2Label"></span>
			</div>
			<div class = "wInsert ttip" id = "toInsert2" hidden="true">
				<?= T_gettext("Word will be inserted");?> 
				<span><?= T_gettext("Insert text");?> </span> 
			</div>
			<div class = "wAlready ttip" id = "already2" hidden="true"> 
				<?= T_gettext("Word is already inserted");?> 
				<span><?= T_gettext("Already text");?> </span>  
				</div>
			<div class = "wUndef ttip" id = "undef2" hidden="true"> 
				<?= T_gettext("Word or paradigm is undefined");?> 
				<span><?= T_gettext("Error text");?> </span>  
			</div>
			<div class = "wWarning ttip" id = "warn2" hidden="true"> 
				<?= T_gettext("Regenerate insertions");?> 
				<span><?= T_gettext("Warning text");?> </span> 
			</div>
			
			<table id ="table2" class="table">
			<tbody/>            
			</table>
			
			<div><?= T_gettext("Existing translations:");?></div>
			<ul id = "translations2"></ul>
			<div style="clear:both"></div>
        </div>
        
        <div class = "footer">
			<input class = "configButton" type = "button" id = "generate" name = "Generar" 
				value="<?= T_gettext('Generate insertions');?>"  
				onClick = "genInsert()"
				onFocus = "checkFastMode1('1')"
				onBlur = "checkFastMode('1')"/>
			<input class = "configButton" type = "button" id ="insert" name = "Insertar" 
				value="<?= T_gettext('Insert into dix');?>" 
				onClick = "insert()"
				onBlur = "checkFastMode('2')"/>		
			<input class = "configButton" type = "button" 
				name = "Reiniciar" value= "<?= T_gettext('Clear form');?>" onClick = "init()"/>
			
			<input type = "button" class = "uptip" name = "Mostrar" value="<?= T_gettext('Show insert queries');?>" id = "Mostrar"
				onClick = "toggleClass(this, SHOW_INSERT_QUERIES, HIDE_INSERT_QUERIES, 'divResult', 'extendedDivResult', 'theResult')" />
			<span><?= T_gettext('Explain show insert');?></span>
			
			<input class = "uptip" type = "button" 
				name = "Eliminar" value = "<?= T_gettext('Close session');?>" onClick="deleteUser()"/>
			<span><?= T_gettext('Explain close insert');?></span>
			
			<span>
				<input type = "button" name = "Exportar" 
					value = "<?= T_gettext('¿Export dictionaries?');?>" onClick="exportDix()"/>
				<div class = "dictionaries"  hidden="true" id = "dictionaries">
					<input type="button" value = "X" class = "delButton" onclick="hide(this)"></input>
					<ul id = "links">
					</ul>
				</div>
			</span>
		</div>
        
    </p>

<script type="text/javascript">
	
	LIST_ERROR = '<?= T_gettext("Error while listing paradigms");?>'
	FLEX_ERROR = '<?= T_gettext("Error while flexing a word")?>'
	WORD_ERROR = '<?= T_gettext("Error while checking if a word exists in the dix");?>'
	WORD_UNDEF = '<?= T_gettext("Error: undefined word");?>'
	PARAD_UNDEF = '<?= T_gettext("Error: undefined paradigm");?>'
	INSERT = '<?= T_gettext("Insert into dix");?>'
	UNAVAILABLE = '<?= T_gettext("Unavailable pair");?>'
	TRANS_EXISTS = '<?= T_gettext("Translation exists");?>'
	TRANS_ERROR = '<?= T_gettext("Error while checking if the translation exists");?>'
	GENERATE  = '<?= T_gettext("Generate insertions");?>'
	GENERATING  = '<?= T_gettext("Generating...");?>'
	INSERT_OK = '<?= T_gettext("Successfully inserted");?>'
	INSERT_ERROR = '<?= T_gettext("Error while inserting");?>'
	INSTALLED_ERROR = '<?= T_gettext("Error while getting the installed pairs");?>'
	INSTALLED = '<?= T_gettext("Installed pair");?>'
	EXPORT = '<?= T_gettext("¿Export dictionaries?");?>'
	EXPORTING = '<?= T_gettext("Exporting...");?>'
	EXPORT_ERROR = '<?= T_gettext("Error while exporting dictionaries");?>'
	DELETE_CONFIRM = '<?= T_gettext("All your work will be lost. Make sure you have the dictionaries downloaded");?>'
	ERROR = '<?= T_gettext("Error");?>'
	EXPORT_OK = '<?= T_gettext("Dictionaries exported. You may now download them");?>'
	TRANSLATIONS = '<?= T_gettext("Existing translations:");?>'
	
	WORD_INSERT = '<?= T_gettext("Word will be inserted");?>'
	WORD_ALREADY_INSERTED = '<?= T_gettext("Word is already inserted");?>'
	WORD_NOT_DEFINED = '<?= T_gettext("Word or paradigm is undefined");?>'
	
	SHOW_INSERT_QUERIES = '<?= T_gettext("Show insert queries");?>';
	HIDE_INSERT_QUERIES = '<?= T_gettext("Hide insert queries");?>';
	FIRST_GENERATE = '<?= T_gettext("Generate the insertions before inserting");?>';
	
	ALL = '<?=T_gettext("all" )?>';
	
    name = '<?=$_GET ["user"] ?>';
    init();
    getUserPair();
</script>

</body>
</html>



