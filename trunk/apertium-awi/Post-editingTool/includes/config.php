<?//coding: utf-8
/*
	Apertium Web Post Editing tool
	Configuration variables
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús
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
	
	'unzip_command' => 'unzip',
	'zip_command' => 'zip'
);

?>