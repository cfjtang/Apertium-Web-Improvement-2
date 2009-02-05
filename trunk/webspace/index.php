<?php
 session_start()
?>

<?php include_once("common/meta.php") ?>
<div style="width:inherit;background-color:orange;text-align:center;border:0px;padding:4px;">

<?
    include '/home/fran/public_html/basque/berria/rss_php.php';

    $rss = new rss_php;
    $rss->load('http://sourceforge.net/export/rss2_projnews.php?group_id=143781');

    $items = $rss->getItems(); #returns all rss items

    print "<b>" . date("d-m-Y", strtotime($items[0]["pubDate"])) . "</b>: " . $items[0]["title"] . ", <a href=\"" . $items[0]["link"] . "\">read more...</a>";
?>
</div>
<body>
<?php
	$id = $HTTP_GET_VARS["id"];
	if ($id == "") {
		$id = "translatetext";
#		$id = "whatisapertium";
	} 

?> 
        <div id="content">
        <!--
            <div id="search">
                <form action="" method="get">
                    <p>
                        search:&nbsp;<input type="text" />&nbsp;
                        <input type="image" src="images/zoom.jpg" value="Submit" alt="Search" title="Search" />
                    </p>
                </form>
            </div>
            -->
            
				<?php 
					$headerFile = "content/" . $lang . "/header.html";
						if( file_exists($headerFile) ) {
							include_once($headerFile);
						} else {
							include_once("content/en/header.html");
						}
				?>
				<?php include_once("php/lang_menu.php") ?>
				
            <div id="body">
            	<!-- Navigation -->
            	<?php include_once("common/navigation.php") ?>
            	<?php $file = "content/" . $lang . "/" . $id . ".html"; ?>

					<?php
						if( file_exists($file) ) {
							include_once("content/" . $lang . "/" . $id . ".html");
						} else {
							if (file_exists("content/en/" . $id . ".html") ) {
							include_once("content/en/" . $id . ".html");
							
							//echo '<div id="translateit"><p>This page is not available in' . $langtext. '</p>';
							//echo '<p>Do you want to <a href="?id=howto&amp;src=' . $id . '&amp;lang=' . $lang . '">help us to translate it</a>?</p>';
							//echo '</div>';
							} else {
								print '<br/><br/>';
								print 'The page does not exist!';
								print '<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>';
								print '<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>';
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
