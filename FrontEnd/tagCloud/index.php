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
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<link rel="stylesheet" type="text/css" href="cloud.css" charset="UTF-8" />
		<title>Nuages de tags</title>
		<!--[if lt IE 9]><script type="text/javascript" src="excanvas.js"></script><![endif]-->
		<script src="tagcanvas.min.js" type="text/javascript"></script>
		<script type="text/javascript">
			window.onload = function() {
				TagCanvas.weight = true;
				TagCanvas.weightFrom = 'data-weight';
				TagCanvas.weightSize = 4;
				TagCanvas.textFont = 'Verdana';
				TagCanvas.initial = [0.1,-0.1];
				try {
					TagCanvas.Start('myCanvas','tags',{
						// textColour: '#00f',
						weightMode: 'both',
						outlineColour: '#ff00ff',
						reverse: true,
						depth: 0.8,
						maxSpeed: 0.1
					});
				} catch(e) {
					// something went wrong, hide the canvas container
					document.getElementById('myCanvasContainer').style.display = 'none';
				}
			};
		</script>
	</head>
	<body>
		<h1>Nuages de tags</h1>
		<div id="myCanvasContainer">
			<canvas width="800" height="800" id="myCanvas">
				<p>Anything in here will be replaced on browsers that support the canvas element</p>
			</canvas>
		</div>
		<?php echo ( getCloudHTML( ) ); ?>
	</body>
</html>