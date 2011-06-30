<?//coding: utf-8
/*
  Apertium Web Post Editing tool
  Functions to handle deformatting and reformatting of documents
	
  Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
  Mentors : Luis Villarejo, Mireia Farrús

  Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
  Mentors : Arnaud Vié, Luis Villarejo
*/

include_once('config.php');
include_once('system.php');
include_once('files.php');

function getFormatName($inputFormat)
{
	//returns the format name to input to apertium depending on the format asked
	//returns false if no corresponding format exists
	
	/* If you had a format here, do not forget to add it in
	 * config.php: $config['supported_format']
	 */
	switch($inputFormat)
	{
	case "rtf":
		return "rtf";
		break;
	case "wxml":
		return "wxml";
		break;
	case "docx":
		return "docx";
		break;
	case "pptx":
		return "pptx";
		break;
	case "xlsx":
		return "xlsx";
		break;
	case "odt": case "ods": case "odp":
		return "odt";
		break;
	case "html":
		return "html";
		break;
	case 'mediawiki':
		return "mediawiki";
		break;
	case 'pdf':
		return "pdf";
		break;
	case 'png' : case 'jpg': case 'jpeg': case 'tiff': case 'tif':
		return 'tif';
		break;
	case "txt" :
		return "txt";
		break;
	}
	
	return false;
}

function getFileFormat($filename, $inputFormat="")
{
	//get the file format that will be used to analyse the file
	//user can force analysis according to a given valid format
	//if no format is found, txt will be used
	
	if($inputFormat != "" AND $format = getFormatName($inputFormat))
	{
		return $format;
	}
	else
	{
		$extension_begin = utf8_strrpos($filename, '.');
		if($extension_begin !== false)
		{
			$extension = utf8_substr($filename, $extension_begin + 1);
			if($format = getFormatName($extension))
			{
				return $format;
			}
		}
		
		return "txt";
	}
}

/*
  FUNCTIONS FOR FORMATTED FILE READING
*/

function apertium_extract($input_doc, $format)
{
	//given the binary data of a file, create the file in the temporary directory, extracted if needed
	//returns the path of the resulting file or directory
	global $config;
	
	//rebuild the input file into a temporary file, $input_document
	$input_document = tempnam($config['temp_dir'], 'APRE_');
	$input_document = $config['temp_dir'] . basename($input_document);
	$handle = fopen($input_document, "w");
	fwrite($handle, $input_doc);
	fclose($handle);
	
	switch($format)
	{
	case 'odt' : case 'docx' : case 'pptx' : case 'xlsx' :
			
		//extract it to a temporary directory, $input_document_directory
		$input_document_directory = dirname($input_document) . '/DIR_' . basename($input_document);
		mkdir($input_document_directory);
		$command = $config['unzip_command'] . ' -qq -o -d ' . $input_document_directory . ' ' . $input_document;
		executeCommand($command, '', $return_value, $return_status);
			
		unlink($input_document);
			
		return $input_document_directory;
			
		break;
	case 'pdf':
		/* Convert the file into html with pdf2html_command*/
		$command = $config['pdf2html_command'] . ' -c -noframes -i ' . $input_document . ' ' . $input_document . '.html';
		executeCommand($command, '', $return_value, $return_status);
		unlink($input_document);
		/* Remove png file generates by pdf2html
		 * Uncomment if you don't use the -i option (ignore image)
		 * unlink($input_document . '001.png');
		 */
		
		/* Replacing the HTML Title */
		$contents = file_get_contents($input_document . '.html');
		$contents = str_replace('<TITLE>' . $input_document . '</TITLE>', '', $contents);
		$handle = fopen($input_document . '.html', 'w');
		fwrite($handle, $contents);
		fclose($handle);
		
		$input_document .= '.html';
		
		return $input_document;
		
		break;
	case 'tif':
		/* Try to use ocr_command on the file
		 * If error, try to convert the file into tif (with Bpp <= 8) 
		 * with convert_command
		 * then convert into txt using ocr_command 
		 */
		
		$command = $config['ocr_command'] . ' ' . $input_document . ' ' . $input_document . '.txt';
		executeCommand($command, '', $return_value, $return_status);

		if ($return_status) {
			/* Any image format => tif in 256 colors*/

			$command = $config['convert_command'] . ' -type Truecolor ' . $input_document . ' ' . $input_document . '.tif';
			executeCommand($command, '', $return_value, $return_status);
			unlink($input_document);
			$input_document .= '.tif';
		
			/* tif => text */
			$command = $config['ocr_command'] . ' ' . $input_document . ' ' . $input_document . '.txt';
			executeCommand($command, '', $return_value, $return_status);
		}
		
		unlink($input_document);
		
		$input_document .= '.txt.txt';

		return $input_document;

		break;
	default :
			
		return $input_document;
			
		break;
	}
}

function apertium_unformat($format, $input_doc_path)
{
	//replacement for apertium-unformat : takes an input file and returns the text with format info escaped
	//Useless when unformat is present
	
	global $config; 
	
	$document = apertium_extract(file_get_contents($input_doc_path), $format);

	$old_dir = getcwd();
	
	//select files to edit
	switch($format)
	{
	case 'odt' :
			
		chdir($document);
			
		$command = 'find . | grep content\\\\.xml';
		executeCommand($command, '', $files, $return_status);
		$files = explode("\n", trim($files));
	       
		foreach($files as $ind => $file)
		{
			$files[$ind] = array('path' => $file, 'type' => 'odt');
		}
		
		break;
		
	case 'docx' :
			
		chdir($document);
			
		$command = 'find . | grep \\\\.xml$ | grep -v -i \\(settings\\|theme\\|styles\\|font\\|rels\\)';
		executeCommand($command, '', $files, $return_status);
		$files = explode("\n", trim($files));
			
		foreach($files as $ind => $file)
		{
			$files[$ind] = array('path' => $file, 'type' => 'wxml');
		}
			
		break;

	case 'tif':
		/* The image can now be considered as a text */
		$files = array(array('path' => '', 'type' => 'txt'));
		break;

	default :
			
		$files = array(array('path' => '', 'type' => $format));
			
		break;
	}
	
	$output = '';
	
	foreach($files as $file)
	{
		if($file['path'] == "")
		{
			$file['path'] = $document;
		}
		else
		{
			$output .= '[<file name="' . addcslashes(ltrim($file['path'], '.'), '\\"[]') . '" type="' . $file['type'] . '"/>].[]';
		}
		/* In the case of a pdf document(transform into html) */
		if ($file['type'] == 'pdf')
			$command = $config['apertium_des_commands'].'html';
		else
			$command = $config['apertium_des_commands'].$file['type'];

		executeCommand($command, file_get_contents($file['path']), $contents, $return_status);
		$output .= $contents;
	}
	
	chdir($old_dir);
	
	//clean the file
	if(is_dir($document))
		rmdir_recursive($document);
	else
		unlink($document);
		
	return $output;
}

function convertFileToHTML($filepath, $format)
{
	//unformats the file, then replace the formatting sequences [ ... ] 
	//by hidden html tags <hr data-format="..." contenteditable="false" />
	
	global $config;
	
	/* Security issues */
	if (!ctype_alpha($format))
		return false;
	
	/*/ Using apertium-unformat
	  $command = $config['apertium_unformat_command'] . ' -f ' . $format . ' "' . $filepath . '"';
	  executeCommand($command, '', $unformatted_text, $return_status);
	//*/
	
	// Using a php function
	$unformatted_text = apertium_unformat($format, $filepath);
	//*/
	$unformatted_text = str_replace(".[]", "", $unformatted_text);
	
	$opening_bracket = utf8_strpos_unescaped($unformatted_text, '[');
	
	while($opening_bracket !== false)
	{
		$closing_bracket = utf8_strpos_unescaped($unformatted_text, ']', $opening_bracket + 1);
		
		if($closing_bracket === false)
		{
			die('No closing bracket after opening at pos ' . $opening_bracket . ' in ' . var_dump($unformatted_text));
		}
		
		$source = utf8_substr($unformatted_text, $opening_bracket + 1, $closing_bracket - $opening_bracket - 1);
		
		if( ($at_pos = utf8_strpos(ltrim($source), '@' )) === 0 )
		{
			//if it begins by @, it is an inclusion string
			$included_file = file_get_contents(utf8_substr($source, $at_pos + 1));
			$replacement = '<hr data-format="' . escape_attribute($included_file) . '" contenteditable="false" />';
		}
		else
		{
			$replacement = '<hr data-format="' . escape_attribute($source) . '" contenteditable="false" />';
		}
		
		$unformatted_text = utf8_substr_replace($unformatted_text, $replacement, $opening_bracket, $closing_bracket - $opening_bracket + 1);
		
		$opening_bracket = utf8_strpos_unescaped($unformatted_text, '[', $opening_bracket + utf8_strlen($replacement));
	}
	
	return $unformatted_text;
}

/*
  FUNCTIONS FOR FORMATTED FILE GENERATION
*/

function apertium_rebuild($input_path, $format)
{
	//given a path, returns the binary data of the input file, rebuilding it if it's an extracted directory
	global $config;
	
	switch($format)
	{
	case 'odt' : case 'docx' : case 'pptx' : case 'xlsx' :
			
		//re-zip the $input_path into a file, and read its content into the $output variable
		$old_dir = getcwd();
		chdir($input_path);
		$command = $config['zip_command'] . ' -q -r "' . $old_dir . '/' . $input_path . '.zip" .'; //the annoying zip CLI adds .zip extension...
		executeCommand($command, '', $return_value, $return_status);
		chdir($old_dir);
		$output = file_get_contents($input_path . '.zip');
		rmdir_recursive($input_path);
		unlink($input_path . '.zip');
			
		return $output;
			
		break;		
	default :
			
		$output = file_get_contents($input_path);
		unlink($input_path);
		return $output;
			
		break;
	}
}

function apertium_reformat($unformatted_text, $format, $input_doc)
{
	//apply all changes from the $unformatted_text to the $input_doc (given as binary data) 
	//return the binary contents of the generated file
	
	global $config;
	
	//build the array of files to replace. '' represents the input document itself
	$file_replacements = array();
	$current_file = ''; 
	$current_offset = 0;
	$content_start = 0;
	while(($current_offset = utf8_strpos($unformatted_text, '<file name="', $content_start)) !== false)
	{
		//store previous file if not empty
		$file_contents = utf8_substr($unformatted_text, $content_start, $current_offset - $content_start);
		if($file_contents != "")
		{
			$file_replacements[$current_file] = $file_contents;
		}
		
		//find next file start
		$next_quote = utf8_strpos($unformatted_text, '"', $current_offset + strlen('<file name="') + 1);
		$content_start = utf8_strpos($unformatted_text, '/>', $next_quote + 1) + 2;
//get the name of the current file
		
		$current_file = utf8_substr($unformatted_text, $current_offset + strlen('<file name="'), $next_quote - ($current_offset + strlen('<file name="')));
		//for some reason, slashes are sometimes escaped, sometimes not...
		$current_file = stripslashes($current_file);
		//make path relative to the root of the archive (they are given absolute to the OS root...)
		$current_file = preg_replace('#^.*?/\\d+'.$format.'dir/#', '/', $current_file);
	}
	//store last file + indicates the begin of file
	$file_replacements[$current_file] = '.[]' . utf8_substr($unformatted_text, $content_start);
	
	$document = apertium_extract($input_doc, $format);
	
	foreach($file_replacements as $filename => $contents)
	{
		if(!is_dir($document . $filename))
		{
			switch($format)
			{
			case 'odt' :
				$command = $config['apertium_re_commands'].'odt';
				executeCommand($command, $contents, $reformatted_text, $return_status);
					
				break;
				
			case 'docx' :
					
				$extension = utf8_substr($filename, utf8_strrpos($filename, '.') + 1);
					
				switch($extension)
				{
				case 'xml' :
					$command = $config['apertium_re_commands'].'wxml';
					executeCommand($command, $contents, $reformatted_text, $return_status);
							
					break;
						
				case 'xlsx' :
					$command = $config['apertium_re_commands'].'xlsx';
					executeCommand($command, $contents, $reformatted_text, $return_status);
							
					break;
				}
					
				break;

			case 'pdf':
				/* In case of a pdf file, use apertium_rehtml, 
				 * and then html2pdf
				 */
				$command = $config['apertium_re_commands'] . 'html';
				executeCommand($command, $contents, $reformatted_text, $return_status);
			
				$command = $config['html2pdf_command'] . ' - -';
				executeCommand($command, $reformatted_text, $return_value, $return_status);
				$reformatted_text = $return_value;
				break;

			case 'tif':
				/* Tif file are managed as TXT file after the translation */
				$format = 'txt';

			default:
					
				//execute apertium-re$format to get the contents of the translated file into the $reformatted_text variable
				$command = $config['apertium_re_commands'].$format;
				executeCommand($command, $contents, $reformatted_text, $return_status);
					
				break;
			}
			$handle = fopen($document . $filename, "w");
			fwrite($handle, $reformatted_text);
			fclose($handle);
		}
	}
	
	return apertium_rebuild($document, $format);
}

function rebuildFileFromHTML($text_data, $format, $input_doc)
{
	/* Security issues */
	if (!ctype_alpha($format))
		return false;
	
	/* replace the hr tags by the style information they contain */
	$unformatted_text = preg_replace('#<hr.+?data-format="(.*?)".+?>#ie', 'unescape_attribute("$1")', $text_data);
	/* Managed apertium's data format */	
	$unformatted_text = str_replace(array("\n","\t","\r"), array("[\n]", "[\t]", "[\r]"), $unformatted_text);

	return apertium_reformat($unformatted_text, $format, $input_doc);
}

?>