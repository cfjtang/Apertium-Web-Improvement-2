<?php
	
	header('Cache-Control: no-cache, must-revalidate');

	require_once("include/BaseXClient.php");
	require_once("include/simpledix.php");
	require_once("i18n/i18n.php");
	
	$session = new Session("localhost", 1984, "admin", "admin");
	
	$output = $session->execute("list");
	$i = 0;
	$users = array();
	foreach (explode("\n", $output) as $line)
	{
		if ($i > 1 && trim($line) != "")
		{
			$aux = split(" ", $line);
			if (strlen($aux[0]) == 33)
				$users[] = $aux[0];
		}
		$i++;
	}
	
	$erase = array();
	foreach ($users as $user)
	{
		$session->execute("open $user");
		$output = $session->execute("info database");
		$session->execute("close");
		
		$lines = explode("\n", $output);
		$line = explode(" ", $lines[6]);
		$date = explode(".", $line[3]);
		$time = explode(":", $line[4]);
		
		$datetime = new DateTime(); 
		$datetime->setDate($date[2], $date[1], $date[0]); 
		$datetime->setTime($time[0], $time[1], $time[2]); 
		
		$now = new DateTime("now");
		
		if (date_diff($datetime, $now)->format('%a') > 0)
		{
			$aux = array();
			$aux[0] = $user;
			$aux[1] = date_diff($datetime, $now)->format('%a');
			$erase[] = $aux;
		}
	}	
?>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
	<head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
    <link rel="stylesheet" type="text/css" href="style/style.css"/>
    <script type="text/javascript" src="scripts/simpledix.js"></script>
    <script type="text/javascript" src="scripts/jquery-1.6.4.js"></script>

	<title>Simpledix deleter</title> 
	</head> 
	<body>
		<ul id = "deleteList">
			<?php
				foreach ($erase as $user)
				{
					echo "<li>";
					echo $user[0]." has been inactive for ".$user[1]." days "."<input type='button' onclick=\"fastDelete('$user[0]', this)\" value='delete'/>";
					echo "</li>";
				}
			?>
		
		</ul>
		<input type="button" onclick="deleteAll()" value = "Delete All"/>
		
		<script type="text/javascript">
				 ERROR = '<?= T_gettext("Error");?>';
				 DELETE_ALL_CONFIRM = '<?= T_gettext("Really delete all?");?>';
		</script>
	</body>
</html>
