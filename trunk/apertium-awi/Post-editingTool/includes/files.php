<?//coding: utf-8
/*
	Apertium Web Post Editing tool
	Functions for file manipulation
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús
*/

include_once('includes/strings.php');

function scandirPhp4($dir)
{
	$files = array();
	
	$dh  = opendir($dir);
	while (false !== ($filename = readdir($dh))) 
	{
		$files[] = $filename;
	}
	
	sort($files);
	return $files;
}

function scanDirForFileExt($dir, $extension)
{
	//$dirList = scandir($dir);
	$dirList = scandirPhp4($dir);
	$result = array();
	
	foreach($dirList as $entry)
	{
		if(is_file($dir . '/' . $entry) AND str_endswith($entry, '.' . $extension))
		{
			$result[] = $dir . '/' . $entry;
		}
	}
	
	return $result;
}

function scanPathArray($path_array, $callback, $extension = '', $prefix_path = '')
{
	/*scans a path given as an array
		First element is the directory to scan
		Second element is the subdirectory to scan. '*' means all first-level subdirectories will be scanned.
		Next elements are in queue and will be passed for recursive called as we move on in the path
	For each find, executes the $callback function. Must take the file path as argument
	If $extension is set, files with a different file extension will be ignored
	$prefix_path is the path to the root direcory of the search
	*/
	
	$current_dir = $path_array[0];
	array_shift($path_array);
	$prefix_path .= $current_dir.'/';
	$next_dir = $path_array[0];
	
	
	$scan_dir = opendir($prefix_path);
	while($current_entry = readdir($scan_dir))
	{
		if($current_entry != '.' AND $current_entry != '..')
		{
			$current_entry_path = $prefix_path.$current_entry;
			if(is_file($current_entry_path) AND str_endswith($current_entry, '.' . $extension))
			{
				//matching file : run callback
				$callback($current_entry_path);
			}
			elseif(is_dir($current_entry_path) AND ($next_dir == '*' OR $next_dir == $current_entry))
			{
				//matching directory : recursive call
				$path_array[0] = $current_entry;
				scanPathArray($path_array, $callback, $extension, $prefix_path);
			}
		}
	}
}

function rmdir_recursive($dir, $unsecured=false)
{
	if(!$unsecured)
	{
		//prevent deleting high level paths : /, /dir, /dir/, /dir/subdir, /dir/subdir/
		$rp = realpath($dir);
		$split = explode('/', $dir);
		if(strpos($dir, '/') === 0 AND count($split) <= 3 + ($split[count($split) - 1] == "" ? 1 : 0))
		{
			return;
		}
	}
	//http://www.commentcamarche.net/faq/12255-warning-rmdir-directory-not-empty
	//Liste le contenu du répertoire dans un tableau
	$dir_content = scandir($dir);
	//Est-ce bien un répertoire?
	if($dir_content !== FALSE){
		//Pour chaque entrée du répertoire
		foreach ($dir_content as $entry)
		{
			//Raccourcis symboliques sous Unix, on passe
			if(!in_array($entry, array('.','..'))){
				//On retrouve le chemin par rapport au début
				$entry = $dir . '/' . $entry;
				//Cette entrée n'est pas un dossier: on l'efface
				if(!is_dir($entry)){
					unlink($entry);
				}
				//Cette entrée est un dossier, on recommence sur ce dossier
				else{
					rmdir_recursive($entry);
				}
			}
		}
	}
	//On a bien effacé toutes les entrées du dossier, on peut à présent l'effacer
	rmdir($dir);
}

?>