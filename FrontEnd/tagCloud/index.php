<?php
/**
 * Bibliotheques
 */
	// Variables globales
	require_once( "config.php");

	// Lecteur de mots
	require_once( "tags_loader.php");

	// Generation du code HTML
	require_once( "cloud_generator.php");

/**
 * Preparation des donnees
 */
loadTags();

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<link rel="stylesheet" type="text/css" href="cloud.css" charset="UTF-8" />
		<title>Nuages de tags</title>
	</head>
	<body>
		<h1>Nuages de tags</h1>
<?php echo ( getCloudHTML( ) ); ?>
	</body>
</html>