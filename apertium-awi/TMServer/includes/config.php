<?//coding: utf-8
/* Apertium Web TMServer
 * Configuration file
 *
 * Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
 * Mentors : Arnaud Vié, Luis Villarejo
 *
 * Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
 * Mentors : Luis Villarejo, Mireia Farrús
 */

$config = array(
	'TM_type' => 'TMX',
	'database_dir' => 'db/',
	'temp_dir' => 'temp/',
	
	/* TMX */
	'TMXsort_command' => 'python ../TmxTools/tmx-sort.py',
	'TMXmerge_command' => 'java -jar ../TmxTools/TMXMerger-1.1.jar'
	);

?>