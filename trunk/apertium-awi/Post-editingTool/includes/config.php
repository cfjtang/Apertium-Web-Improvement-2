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

	'spellcheckingtool' => 'aspell',

	'grammarproofingtool' => 'languagetool',
	
	'unzip_command' => 'unzip',
	'zip_command' => 'zip'
);

/* List of modules to load
 * Existing modules : see modules.php
 * If you change this list, do not forget to uncomment 
 * gen_templateJS
 * in modules.php (at the end of file)
 */
$modules_to_load = array('FormattedDocumentHandling', 'SpellGrammarChecking', 'LinkExternalDictionnaries', 'SearchAndReplace', 'Logs');

?>