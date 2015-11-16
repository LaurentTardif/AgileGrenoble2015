<?php
/** genere le code HTML du nuage */
function getCloudHTML($div_name, $TAGS_ARRAY ) {
	//global $TAGS_ARRAY;
	//global $NB_STYLES, $MAX_WEIGHT, $MIN_WEIGHT;
	//$NB_STYLES = 20;

	// Calcul du facteur d'echelle
	$scale = 1;
	
	// Generation du code
	$retour = "<div id=\"".$div_name."\">";
	$retour .= "\n\t<ul>";
	
	if ( count ( $TAGS_ARRAY ) ) {
		foreach ( $TAGS_ARRAY as $tag_element) {
			// Determination de la taille du tag
			$tag_size = round ( $tag_element["weight"] * $scale);
			$retour .= "\n\t<li><a href=\"#\"";
			$retour .= " data-weight=\"" . $tag_size . "\">";
			$retour .= htmlentities( $tag_element["tag"] );
			$retour .= "</a></li>";
		}
	}
	
	$retour .= "\n\t</ul>";
	$retour .= "\n</div>";
	
	return $retour;
}
?>
