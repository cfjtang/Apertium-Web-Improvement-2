<?php



	if(class_exists('WPlize')) {

		$aux_options = new WPlize('WP_Geriaoueg');


		$aux_options->update_option('language','cy');
		$aux_options->update_option('translation_languages','en');
		
		$aux_options->update_option('title_id','title-');
		$aux_options->update_option('content_id','entry-');

		//$aux_options->update_option('','');

	}


?>
