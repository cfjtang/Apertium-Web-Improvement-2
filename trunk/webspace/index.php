<?php
 session_start()
?>

<?php include_once("common/meta.php") ?>
<body>
<div style="width:inherit;background-color:orange;text-align:center;border:0px;padding:4px;">

<?
//    include '/home/fran/public_html/basque/berria/rss_php.php';
    include 'rss_php.php';

    $rss = new rss_php;
    $rss->load('http://sourceforge.net/export/rss2_projnews.php?group_id=143781');

    $items = $rss->getItems(); #returns all rss items

    print "<b>" . date("d-m-Y", strtotime($items[0]["pubDate"])) . "</b>: " . $items[0]["title"] . ", <a href=\"" . $items[0]["link"] . "\">read more...</a>";
?>
</div>

<?php
	$id = $HTTP_GET_VARS["id"];
	if ($id == "") {
		$id = "translatetext";
	} 

?>
        <div id="content">
	<?php 
		$headerFile = "content/" . $lang . "/header.html";
		if( file_exists($headerFile) ) {
			include_once($headerFile);
		} else {
			include_once("content/en/header.html");
		}
	?>
<div id="common_header">

       	<?php include_once("common/navigation.php") ?>
	<?php include_once("php/lang_menu.php") ?>
</div>
        <div id="body">
            	<!-- Navigation -->
            	<?php $file = "content/" . $lang . "/" . $id . ".html"; ?>

					<?php
						if( file_exists($file) ) {
							include_once("content/" . $lang . "/" . $id . ".html");
						} else {
							if (file_exists("content/en/" . $id . ".html") ) {
								include_once("content/en/" . $id . ".html");
							
							} else {
								print '<br/><br/>The page does not exist!<br/><br/>';
							}
						}
					?>
            </div>
            <!-- footer -->
            <br/>
            <?php include_once("common/footer.php") ?>
        </div>
    </body>
</html>
