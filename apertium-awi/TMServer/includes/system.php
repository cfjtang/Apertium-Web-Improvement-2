<?//coding: utf-8
/*
	Apertium Web TMServer
	Functions handling interaction between the script and system commands
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús
*/

function executeCommand($command, $stdin, &$stdout, &$returnstatus)
{
	//A simple wrapper for proc_open to make command calls a little shorter
	
	$descriptorspec = array(
		0 => array("pipe", "r"),  //pipe 0 will be stdin for the process
		1 => array("pipe", "w"),  //pipe 1 will be stdout
	);
	
	$pipes = array();
	$process = proc_open($command, $descriptorspec, $pipes);

	if (is_resource($process)) 
	{
		fwrite($pipes[0], $stdin);
		fclose($pipes[0]);
		
		$stdout = stream_get_contents($pipes[1]);
		fclose($pipes[1]);
		
		$returnstatus = proc_close($process);		
	}
	else
	{
		$stdout='';
		$returnstatus=1;
	}
}

function send_file($filename, $bin_content)
{
	$size = strlen($bin_content);
	header("Content-Type: application/force-download; name=\"" . addslashes($filename) . "\"");
	header("Content-Transfer-Encoding: binary");
	header("Content-Length: $size");
	header("Content-Disposition: attachment; filename=\"" . addslashes($filename) . "\"");
	header("Expires: 0");
	header('Cache-Control: must-revalidate, post-check=0, pre-check=0');
	header('Cache-Control: private',false);
	header("Pragma: public");
	header('Connection: close');
	die($bin_content);
}

?>