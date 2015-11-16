<?php

function loadTags($tags_file_name) {
	//global $TAGS_FILE_NAME;
	//global $TAGS_ARRAY;
	
	
	$TAG_REGEXP = "/([\S]+)\s+([0-9]+)/i";
	
	// Ouverture du fichier de mots
	$file_handle = fopen($tags_file_name, 'r');
	$tags_array = array();
	if ( $file_handle ) {
		while ( !feof( $file_handle ) ) {
			// Recuperation du tag courant
			$current_tag_line = fgets( $file_handle );
			
			// Application de la regexp
			if ( preg_match ( $TAG_REGEXP , $current_tag_line, $matches ) ) {
				$tag = $matches[1];
				$weight = $matches[2];			

				$tags_array[] = array(
					"tag" => $tag,
					"weight" => $weight
				);
			}
		}
	}
	
	// Fermeture du fichier de mots
	fclose( $file_handle );
	return $tags_array ;
}
?>
