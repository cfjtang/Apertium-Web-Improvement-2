<?php
	
	header('Cache-Control: no-cache, must-revalidate');
	$ROOT = dirname($_SERVER['SCRIPT_FILENAME']);
	
	require_once("include/BaseXClient.php");
	require_once("include/simpledix.php");

	if (!
		(($_FILES["leftDix"] || $_REQUEST['leftDixUrl']) &&
		($_FILES["rightDix"] || $_REQUEST['rightDixUrl']) &&
		($_FILES["biDix"] || $_REQUEST['biDixUrl'])))
	{
		echo "Not enough files";
		exit();
	}

	$error = false;
	
	if ($_FILES["leftDix"]["error"] > 0)
	{
		echo "Error with left dictionary. Code : ".$_FILES["leftDix"]["error"]." <br/>";
		$error = true;
	}
	
	if ($_FILES["rightDix"]["error"] > 0)
	{
		echo "Error with right dictionary. Code : ".$_FILES["rightDix"]["error"]."<br/>";
		$error = true;
	}
		
	if ($_FILES["biDix"]["error"] > 0)
	{
		echo "Error with the bilingual dictionary. Code : ".$_FILES["biDix"]["error"]."<br/>";
		$error = true;
	}
	
	if ($_FILES["leftConf"]["error"] > 0 && $_FILES["leftConf"]["error"] != 4)
	{
		echo "Error with the left configuration file. Code: : ".$_FILES["leftConf"]["error"]."<br/>";
		$error = true;
	}
	
	if ($_FILES["rightConf"]["error"] > 0 && $_FILES["rightConf"]["error"] != 4)
	{
		echo "Error with the right configuration file. Code: : ".$_FILES["rightCond"]["error"]."<br/>";
		$error = true;
	}			

	if ($error)
	{
		exit();
	}
	
	$session = new Session("localhost", 1984, "admin", "admin");
	
	//Sección crítica. 
	$fp = @fopen("/tmp/simpledix_lock.txt", "w");
	if (!flock($fp, LOCK_EX)) 
	{
		echo "Error de sincronización";
		exit();
	}
	while (true)
	{
		$tempName="_".md5(rand());
		try
		{
			//Si ya existe la base de datos, volvemos a intentar
			$session->execute("open $tempName");
		}
		catch (Exception $ex)
		{
			//Si no existe, tenemos un id único y creamos la db
			$session->execute("create database $tempName") . "<br/>";
			break;
		}		
	} 

	flock($fp, LOCK_UN); // release the lock
	fclose($fp);
	//Fin de seccion crítica
	
	//Carpetas de trabajo (deberían estar creadas)
	@mkdir("output");
	@mkdir("output/$tempName");

	$lang1 = $_POST["lang1"];
	$lang2 = $_POST["lang2"];

	//Creamos el fichero que nos indica el par del usuario
	fclose(fopen("output/$tempName/current_$lang1"."_"."$lang2", "w"));

	//Inicialización de la base de datos
	$session->execute("xquery insert node <$lang1-$lang2/> into doc (\"$tempName\")");
	
	$session->execute("xquery insert node <$lang1/> into /$lang1-$lang2");
	$session->execute("xquery insert node <$lang2/> into /$lang1-$lang2");
	$session->execute("xquery insert node <$lang1-$lang2/> into /$lang1-$lang2");
	
	$session->execute("xquery insert node <History/> into /$lang1-$lang2");
	$session->execute("xquery insert node <$lang1/> into /$lang1-$lang2/History");
	$session->execute("xquery insert node <$lang2/> into /$lang1-$lang2/History");
	$session->execute("xquery insert node <$lang1-$lang2/> into /$lang1-$lang2/History");
	
	//Copia de diccionarios originales (.orig) e inserción en la base de datos
	if ($_FILES["leftDix"])
	{
		$LFileName = $_FILES["leftDix"]["name"].".orig";
		copy($_FILES["leftDix"]["tmp_name"], "$ROOT/output/$tempName/".$LFileName);
	}
	else
	{
		$LFileName = "apertium-".$lang1."-".$lang2.".".$lang1.".dix.orig";
		shell_exec("wget ".$_REQUEST['leftDixUrl']. " -q -O $ROOT/output/$tempName/" .$LFileName);
	}
	
	chmod("$ROOT/output/$tempName/".$LFileName, 0666);
	$session->execute("xquery insert node doc(\"$ROOT/output/$tempName/"
		. $LFileName
		. "\") into /$lang1-$lang2/$lang1");	
		
	if ($_FILES["rightDix"])
	{
		$RFileName = $_FILES["rightDix"]["name"].".orig";
		copy($_FILES["rightDix"]["tmp_name"], "$ROOT/output/$tempName/".$RFileName);
	}
	else
	{
		$RFileName = "apertium-".$lang1."-".$lang2.".".$lang2.".dix.orig";
		shell_exec("wget ".$_REQUEST['rightDixUrl']. " -q -O $ROOT/output/$tempName/" .$RFileName);
	}
	
	chmod("$ROOT/output/$tempName/".$RFileName, 0666);
	$session->execute("xquery insert node doc(\"$ROOT/output/$tempName/"
		. $RFileName
		. "\") into /$lang1-$lang2/$lang2");
		
	if ($_FILES["biDix"])
	{
		$BiFileName = $_FILES["biDix"]["name"].".orig";
		copy($_FILES["biDix"]["tmp_name"], "$ROOT/output/$tempName/".$BiFileName);
	}
	else
	{
		$BiFileName = "apertium-".$lang1."-".$lang2.".".$lang1."-".$lang2.".dix.orig";
		shell_exec("wget ".$_REQUEST['biDixUrl']. " -q -O $ROOT/output/$tempName/" .$BiFileName);
	}
	chmod("$ROOT/output/$tempName/".$BiFileName, 0666);
	$session->execute("xquery insert node doc(\"$ROOT/output/$tempName/"
		. $BiFileName
		. "\") into /$lang1-$lang2/$lang1-$lang2");
		
	//Creada la bd con todos los datos

	
	
	
	  
	if ($_FILES["leftConf"]["error"] == 0)
	{
		copy($_FILES["leftConf"]["tmp_name"], "output/$tempName/config.$lang1-$lang2.$lang1.xml");
	}
  
	if ($_FILES["rightConf"]["error"] == 0)
	{
		copy($_FILES["rightConf"]["tmp_name"], "output/$tempName/config.$lang1-$lang2.$lang2.xml");
	}
	header("Location: index.php?user=$tempName");
?>
