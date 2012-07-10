<?php

    require_once("./include/membersite_config.php");

	function rrmdir($dir) {
		if (is_dir($dir)) {
			$objects = scandir($dir);
			foreach ($objects as $object) {
				if ($object != "." && $object != "..") {
					if (filetype($dir."/".$object) == "dir") rrmdir($dir."/".$object); else unlink($dir."/".$object);
				}
			}
			reset($objects);
			rmdir($dir);
		}
	}
	header('Cache-Control: no-cache, must-revalidate');

	$name = $_GET["name"];
	
	$session = new Session("localhost", 1984, "admin", "admin");
	echo $session->execute("drop database $name");

	rrmdir("output/$name");

	echo "Successfully deleted<br/>";
?>
