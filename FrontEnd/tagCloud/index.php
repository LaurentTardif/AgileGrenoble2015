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
$array_tweets=loadTags("tweets.txt");
$array_tweeters=loadTags("tweeters.txt");


?>
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
				TagCanvas.weightSize = 2;
				TagCanvas.textFont = 'Verdana';
				TagCanvas.initial = [0.1,-0.1];
				try {
					TagCanvas.Start('myTweetsCanvas','tag_tweets',{
						// textColour: '#00f',
						weightMode: 'both',
						outlineColour: '#ff00ff',
						reverse: true,
						depth: 0.8,
						maxSpeed: 0.1
					});
					TagCanvas.Start('myTweetersCanvas','tag_tweeters',{
						// textColour: '#00f',
						weightMode: 'both',
						outlineColour: '#ff00ff',
						reverse: true,
						depth: 0.8,
						maxSpeed: 0.1
					});

				} catch(e) {
					// something went wrong, hide the canvas container
					document.getElementById('myTweetsContainer').style.display = 'none';
				}
			};
		</script>
	</head>
	<body>
		<h1>Nuages de tags</h1>
		<div id="myTweetsContainer">
			<canvas width="400" height="600" id="myTweetsCanvas">
				<p>Anything in here will be replaced on browsers that support the canvas element</p>
			</canvas>
		</div>
		<?php echo ( getCloudHTML("tag_tweets",$array_tweets ) ); ?>


		<div id="myTweetersContainer">
			<canvas width="400" height="600" id="myTweetersCanvas">
				<p>Anything in here will be replaced on browsers that support the canvas element</p>
			</canvas>
		</div>
		<?php echo ( getCloudHTML("tag_tweeters",$array_tweeters ) ); ?>


	</body>
</html>
