<?//coding: utf-8
/*
	Apertium Web Post Editing tool
	Configuration variables
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús

	Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
	Mentors : Arnaud Vié, Luis Villarejo
*/
$config = array(
	'temp_dir' => 'temp/',

	'apertium_command' => 'apertium',
	'apertium_unformat_command' => 'apertium-unformat',
	'apertium_re_commands' => 'apertium-re',
	'apertium_des_commands' => 'apertium-des',
	
	'languagetool_command' => 'java -jar external/LanguageTool/LanguageTool.jar',
	'languagetool_server_command' => 'java -cp external/LanguageTool/jaminid.jar:external/LanguageTool/LanguageTool.jar de.danielnaber.languagetool.server.HTTPServer',
	'languagetool_server_port' => 8081,
	
	'aspell_command' => 'aspell',
	
	'maligna_command' => 'external/maligna-2.5.5/bin/maligna',

	'ATD_command' => './external/atd/run-lowmem.sh',
	'ATD_link' => 'service.afterthedeadline.com',

	'spellcheckingtool' => 'ATD', /* aspell, ATD */

	'grammarproofingtool' => 'ATD', /* languagetool, ATD */
	
	'unzip_command' => 'unzip',
	'zip_command' => 'zip',

	'pdf2html_command' => 'pdftohtml -c -noframes',
	'html2pdf_command' => 'external/wkhtmltopdf-i386',

	'convert_command' => 'convert',
	'ocr_command' => 'tesseract',

	'yuicompressor_command' => 'java -jar external/yuicompressor-2.4.6/build/yuicompressor-2.4.6.jar',

	'tmxmerger_command' => 'java -jar ../TmxTools/TMXMerger-1.1.jar',
	
	'use_apertiumorg' => FALSE,
	'apertiumorg_homeurl' => 'http://www.apertium.org/index.php',
	'apertiumorg_traddoc' => 'http://www.apertium.org/common/traddoc.php',
	
	'externTM_type' => 'TMServer', /* 'TMServer', ''(to use nothing) */
	'externTM_url' => 'http://localhost/TMServer/',
	
	'supported_format' => array("rtf", "wxml", "docx", "pptx", "xlsx", "odt", "ods", "odp", "html", "mediawiki", "pdf", "png", "jpg", "jpeg", "tiff", "tif", "txt")
);

/* List of modules to load
 * Existing modules : see modules.php
 * If you change this list, do not forget to uncomment 
 * gen_templateJS
 * in modules.php (at the end of file)
 *
 * or use publish.php
 */
$modules_to_load = array('FormattedDocumentHandling', 'SpellGrammarChecking', 'LinkExternalDictionnaries', 'SearchAndReplace', 'Logs');

?>