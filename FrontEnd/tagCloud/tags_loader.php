<?php

function loadTags( ) {
	global $TAGS_FILE_NAME;
	global $TAGS_ARRAY;
	global $MAX_WEIGHT, $MIN_WEIGHT;
	
	$TAG_REGEXP = "/([\S]+)\s+([0-9]+)/i";
	
	// Ouverture du fichier de mots
	$file_handle = fopen($TAGS_FILE_NAME, 'r');

	if ( $file_handle ) {
		while ( !feof( $file_handle ) ) {
			// Recuperation du tag courant
			$current_tag_line = fgets( $file_handle );
			
			// Application de la regexp
			if ( preg_match ( $TAG_REGEXP , $current_tag_line, $matches ) ) {
				$tag = $matches[1];
				$weight = $matches[2];
				
				// Bornes de poids
				if ( $MAX_WEIGHT = 0 ) $MAX_WEIGHT = $weight;
				if ( $weight > $MAX_WEIGHT) $MAX_WEIGHT = $weight;
				if ( $MIN_WEIGHT = 0 ) $MIN_WEIGHT = $weight;
				if ( $weight < $MIN_WEIGHT) $MIN_WEIGHT = $weight;
				
				$TAGS_ARRAY[] = array(
					"tag" => $tag,
					"weight" => $weight
				);
			}
		}
	}
	
	// Fermeture du fichier de mots
	fclose( $file_handle );
}
?>