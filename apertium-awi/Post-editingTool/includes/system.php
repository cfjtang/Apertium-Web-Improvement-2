<?//coding: utf-8
/*
	Apertium Web Post Editing tool
	Functions handling interaction between the script and system commands
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús
*/

include_once('config.php');
include_once('strings.php');

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

function executeBackgroundCommand($command, $stdin, $stdout_file=null)
{
	$descriptorspec = array();
	$descriptorspec[0] = array("pipe", "r");  //pipe 0 will be stdin for the process
	if($stdout_file == null)
	{
		$descriptorspec[1] = array("pipe", "w"); //pipe1 will be stdout
	}
	else
	{
		$descriptorspec[1] = array("file", $stdout_file, "w"); //$stdout_file will be stdout
	}

	
	$pipes = array();
	$process = proc_open($command . ' &', $descriptorspec, $pipes);

	if (is_resource($process)) 
	{
		fwrite($pipes[0], $stdin);
		fclose($pipes[0]);
	}
}

//make sure LanguageTool server is running
function runLTserver()
{
	global $config;
	//first, let's check that the LT listen port is really listened to and outputs XML, in which case it'll most probably be LT
	if( !($file = @file_get_contents('http://localhost:'.$config['languagetool_server_port'].'/?language=en&text=You%20can%20has%20cheeseburger.') AND (utf8_strpos($file, '<?xml version="1.0" encoding="UTF-8"?>') === 0)) )
	{
		//we've got to start LT server
		$out1 = ''; 
		$out2 = '';
		executeBackgroundCommand($config['languagetool_server_command'] . ' -p ' . $config['languagetool_server_port'], '', $out1, $out2);
		
		while(!($file = @file_get_contents('http://localhost:'.$config['languagetool_server_port'].'/?language=en&text=You%20can%20has%20cheeseburger.') AND (utf8_strpos($file, '<?xml version="1.0" encoding="UTF-8"?>') === 0)))
		{
			//wait until the server is loaded
			usleep(1000);
		}
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