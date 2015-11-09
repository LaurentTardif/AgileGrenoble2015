<?php
/** genere le code HTML du nuage */
function getCloudHTML( ) {
	global $TAGS_ARRAY;
	global $NB_STYLES, $MAX_WEIGHT, $MIN_WEIGHT;
	
	// Calcul du facteur d'echelle
	$scale = round ( $NB_STYLES / ( $MAX_WEIGHT - $MIN_WEIGHT + 1 ) );
	
	// Generation du code
	$retour = "<div class=\"tags_box\">";
	
	if ( count ( $TAGS_ARRAY ) ) {
		foreach ( $TAGS_ARRAY as $tag_element) {
			// Determination de la taille du tag
			$tag_size = round ( $tag_element["weight"] * $scale);
			$retour .= "\n\t<a class=\"cloud" . $tag_size . "\">";
			$retour .= htmlentities( $tag_element["tag"] );
			$retour .= "</a>";
		}
	}
	
	$retour .= "\n</div>";
	
	return $retour;
}
?>
