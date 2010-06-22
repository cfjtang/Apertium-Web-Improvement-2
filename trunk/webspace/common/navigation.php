<div id="links">
        <?php 
            //include_once("sections/sections_ids.php");
            $file = "content/" . $lang . "/sections.php";
            if (file_exists($file)) {
                include_once("content/" . $lang . "/sections.php");
            } else {
                include_once("content/en/sections.php");
            }
            
                         
            $num = count($sections);
            
            for ($i = 0; $i < $num; $i++) {
                //echo '<br/>';
                $numSub = count($subsections[$sections[$i]]);
                $thisSection = false;    
                if($i != 0)
                  print " | ";
                if (!$thisSection) {
                    if ($sections[$i] == $id) {
                        print '<a class="selected" title="' . $description[$i] . '" href="?id=' . $sections[$i] . '&amp;lang=' . $lang . '">' . $text[$i] . '</a>  ' . "\n";
                        $numSub = count($subsections[$id]);
                        if ($numSub > 0) {
                            foreach ($subsections[$id] as $key=>$value) {
                                        $islink = strstr($key, '@');
                                        $islink = substr($islink,1, strlen($islink)-1);
                                        if( strcmp($islink,"") != 0 ) {
                                            print '<a class="submenu" href="' . $value . '">' . $islink . '</a> ';
                                        } else {
                                            print '<a class="submenu" href="?id=' . $key . '">' . $value . '</a> ';
                                        }
                                
                            }
                        }        
                    } else {
                        print '<a title="' . $description[$i] . '" href="?id=' . $sections[$i] . '&amp;lang=' . $lang . '">' . $text[$i] . '</a> ';
                        echo $description[$i];
                    }
                }
            }

            print "\n";
        ?>
 </div> 
